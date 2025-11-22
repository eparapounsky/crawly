package com.spyder.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebPageSaver {

    private final String saveLocation;
    private static final Logger logger = System.getLogger(WebPageSaver.class.getName());
    private final static String IMAGES_FOLDER_NAME = "images";

    public static String getImagesFolderName() {
        return IMAGES_FOLDER_NAME;
    }

    public WebPageSaver(String saveLocation) {
        // Ensure path is syntactically valid before assigning
        try {
            Paths.get(saveLocation);
        } catch (Exception e) {
            logger.log(Level.ERROR, "Invalid save location path: " + saveLocation, e);
            throw new IllegalArgumentException("Invalid save location path: " + saveLocation, e);
        }

        this.saveLocation = saveLocation;
    }

    public void saveWebPage(Document webpage, String url) {
        // Derive a relative file path from the URL.
        // (e.g. "/" -> index.html, "/about" -> about/index.html)
        String relativeFilePath = getRelativePath(url);

        // Build the full file path that preserves the directory structure of the URL.
        // Replace / with \ on Windows.
        // TO-DO: Replace with Path API (safer for file operations)
        String absoluteFilePath = saveLocation + File.separator + relativeFilePath.replace("/", File.separator);

        // Ensure the parent directories for the target file exist, creating any nested
        // directories needed to mirror the URL path before writing the file.
        Path parentDirectoryPath = Paths.get(absoluteFilePath).getParent();
        if (parentDirectoryPath != null) { // null if saving to root directory
            try {
                Files.createDirectories(parentDirectoryPath);
            } catch (IOException | InvalidPathException e) {
                // Use concatenation for directory path, so that we can pass the exception
                // as a throwable, preserving stack trace.
                logger.log(Level.ERROR, "Failed to create directories for " + parentDirectoryPath, e);
                return;
            }
        }

        // Download all images in the webpage, and update their src attributes to point
        // to the local copies, before writing the HTML file.
        downloadImages(webpage, saveLocation, relativeFilePath);

        // Write the modified HTML content (with updated image paths) to the file.
        try (FileWriter myWriter = new FileWriter(absoluteFilePath)) {
            myWriter.write(webpage.html());
        } catch (IOException e) {
            logger.log(Level.ERROR, "Failed to write webpage to file: " + absoluteFilePath, e);
        }
    }

    private String getRelativePath(String url) {
        // Use the URL path to determine the file path within the save location.
        String relativePath = extractPathFromUrl(url);

        if (relativePath.equals("/")) {
            relativePath = "index.html"; // if root path, save as index.html
        } else if (!relativePath.endsWith(".html") && !relativePath.contains(".")) {
            // Treat paths without an explicit file extension (.html, .jpg)
            // as directories, and save the resource as an index page.
            // Also, handle both "/about" and "/about/" by ensuring
            // the returned path ends with "/index.html".
            if (relativePath.endsWith("/")) {
                relativePath = relativePath + "index.html";
            } else {
                relativePath = relativePath + "/index.html";
            }
        }

        // Remove the leading slash to ensure a valid relative path (no //).
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }
        return relativePath;
    }

    /*
     * Uses java.net.URI to extract the path component from the URL. Handles URL
     * encoding/decoding, strips queries and fragments, accounts for
     * schemes/ports/authentication.
     */
    private String extractPathFromUrl(String url) {
        try {
            // returns null if no path, or "/" for root
            String path = new URI(url).getPath();
            return (path == null || path.isEmpty()) ? "/" : path;
        } catch (URISyntaxException e) {
            // malformed URL: fallback to root
            logger.log(Level.WARNING, "Malformed URL: " + url + "- defaulting to '/'", e);
            return "/";
        }
    }

    private void downloadImages(Document webpage, String rootDirectory, String currentPagePath) {
        // Collect all images in the current page in an Elements collection.
        Elements images = webpage.select("img");

        // Create the images directory at the root level (or do nothing if it exists).
        String imagesDirectory = rootDirectory + File.separator + IMAGES_FOLDER_NAME;
        try {
            Files.createDirectories(Paths.get(imagesDirectory));
        } catch (IOException e) {
            logger.log(Level.ERROR, "Failed to create images folder", e);
            return;
        }

        // Write each image to the images directory and update its src attribute in the
        // HTML to point to the local copy.
        for (Element img : images) {
            // Get absolute URL (src is relative by default).
            // We need the absolute URL to download the image because the relative URL won't
            // work outside the context of the webpage.
            String imageAbsoluteUrl = img.attr("abs:src");
            if (imageAbsoluteUrl == null || imageAbsoluteUrl.isEmpty()) {
                continue;
            }

            // Create the image filepath for saving the image locally.
            String imageFileName = extractImageNameFromUrl(imageAbsoluteUrl);
            String imageFilePath = imagesDirectory + File.separator + imageFileName;

            // Download the image.
            try {
                // convert to URI to open network stream
                URI imageUri = new URI(imageAbsoluteUrl);
                // read from stream to get image data and copy to local file,
                // replacing file if it exists
                try (InputStream imageInputStream = imageUri.toURL().openStream()) {
                    Files.copy(imageInputStream, Paths.get(imageFilePath), StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (URISyntaxException | IOException e) {
                logger.log(Level.ERROR, "Failed to download image: {0}", imageFileName, e);
                continue;
            }

            // Update the src attribute of the image in the HTML using new relative path.
            String relativeImagePath = calculateRelativePath(currentPagePath, IMAGES_FOLDER_NAME + imageFileName);
            img.attr("src", relativeImagePath);
        }
    }

    private String extractImageNameFromUrl(String imageUrl) {
        String imageFilePath;

        // Get the path of the image.
        try {
            // Convert to URL so we can automatically parse path with getPath().
            imageFilePath = new URI(imageUrl).getPath();

            // Handle cases where path is null or empty.
            if (imageFilePath == null || imageFilePath.isEmpty()) {
                return "image.jpg";
            }
        } catch (URISyntaxException e) {
            logger.log(Level.WARNING, "Failed to parse image path from URL: {0}, fallback to default filename",
                    imageUrl);
            return "image.jpg";
        }
        // Extract the image's filename from the path (after the last "/").
        String extractedImageName = imageFilePath.substring(imageFilePath.lastIndexOf('/') + 1);
        return extractedImageName;
    }

    /**
     * Calculates the relative path from a source HTML file to a destination file
     * (typically an image). This ensures that when the HTML file references the
     * destination file, the link will work regardless of how deeply nested the
     * HTML file is in the directory structure.
     * 
     * For example:
     * - Source: "about/team/index.html"
     * - Destination: "images/photo.jpg"
     * - Result: "../../images/photo.jpg"
     * 
     * This allows the HTML to correctly reference images using relative paths
     * rather than absolute paths, making the downloaded site portable.
     */
    private String calculateRelativePath(String sourcePath, String destinationPath) {
        try {
            // Get the path of the source file's parent directory.
            Path from = Paths.get(sourcePath).getParent();
            if (from == null) {
                from = Paths.get(""); // empty path (root directory)
            }

            // Get the destination file path.
            Path to = Paths.get(destinationPath);

            // Get the relative path from source to destination.
            // Convert to forward slashes for HTML (Windows compatibility).
            Path relativePath = from.relativize(to);
            return relativePath.toString().replace(File.separator, "/");
        } catch (Exception e) {
            // Fallback: return simple path from root if path operations fail.
            logger.log(Level.WARNING, "Failed to calculate relative path from {0} to {1}, fallback to root path",
                    sourcePath, destinationPath);
            return destinationPath;
        }
    }
}
