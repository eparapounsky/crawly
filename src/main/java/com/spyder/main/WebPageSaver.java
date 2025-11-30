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
        // Derive a relative file path from the URL for the webpage.
        // Needed to create the correct parent directory structure and file name.
        String relativeFilePath = getRelativePath(url);

        // Build the full file path that preserves the directory structure of the URL,
        // and uses the save location as the root.
        // Use Path API for proper cross-platform path handling (handle \ on Windows).
        Path targetFilePath = Paths.get(saveLocation).resolve(relativeFilePath);

        // Ensure the parent directories for the target file exist, creating any nested
        // directories needed to mirror the URL path before writing the file.
        Path parentDirectoryPath = targetFilePath.getParent();
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

        // Create a copy of the webpage to avoid modifying the original document when
        // updating internal links, which might still be used for crawling
        Document webpageForSaving = webpage.clone();

        // Download all images in the webpage, and update their src attributes to point
        // to the local copies, before writing the HTML file.
        downloadImages(webpageForSaving, saveLocation, relativeFilePath);

        // Update internal links to point to the correct local file paths
        updateInternalLinks(webpageForSaving, url, relativeFilePath);

        // Write the modified HTML content, with updated image and link paths,
        // to the file.
        try (FileWriter myWriter = new FileWriter(targetFilePath.toFile())) {
            myWriter.write(webpageForSaving.html());
        } catch (IOException e) {
            logger.log(Level.ERROR, "Failed to write webpage to file: " + targetFilePath, e);
        }
    }

    /**
     * Converts a URL to a relative file path for offline website storage.
     *
     * Follows standard web server conventions: - Root path "/" becomes
     * "index.html" - URLs without explicit file extensions are treated as file
     * requests (.html) - URLs with explicit extensions are preserved as-is
     * (.jpg, .png, .pdf, etc.)
     *
     * Examples: "https://example.com/" → "index.html" (root directory) 
     * "https://example.com/about" → "about.html" (file request) 
     * "https://example.com/news/sports" → "news/sports.html" (nested file) 
     * "https://example.com/file.pdf" → "file.pdf" (preserves actual files)
     *
     * @param url The complete URL to convert to a relative file path
     * @return A relative file path suitable for local filesystem storage
     *
     */
    private String getRelativePath(String url) {
        // Use the URL path to determine the file path within the save location.
        String relativePath = extractPathFromUrl(url);

        if (relativePath.equals("/")) {
            // Save the root path as index.html
            relativePath = "index.html";
        } else if (!relativePath.contains(".")) {
            // Handle paths without explicit file extensions
            // Remove trailing slash if present
            if (relativePath.endsWith("/")) {
                relativePath = relativePath.substring(0, relativePath.length() - 1);
            }
            // Append .html to treat as file request
            relativePath = relativePath + ".html";
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

            String path = new URI(url).getPath(); // returns null if no path, or "/" for root
            return (path == null || path.isEmpty()) ? "/" : path; // default to "/" if null
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
        Path imagesDirectory = Paths.get(rootDirectory).resolve(IMAGES_FOLDER_NAME);
        try {
            Files.createDirectories(imagesDirectory);
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
            Path imageFilePath = imagesDirectory.resolve(imageFileName);

            // Download the image.
            try {
                // convert to URI to open network stream
                URI imageUri = new URI(imageAbsoluteUrl);
                // read from stream to get image data and copy to local file,
                // replacing file if it exists
                try (InputStream imageInputStream = imageUri.toURL().openStream()) {
                    Files.copy(imageInputStream, imageFilePath, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (URISyntaxException | IOException e) {
                logger.log(Level.ERROR, "Failed to download image: {0}", imageFileName, e);
                continue;
            }

            // Update the src attribute of the image in the HTML using new relative path.
            String relativeImagePath = calculateRelativePath(currentPagePath, IMAGES_FOLDER_NAME + "/" + imageFileName);
            img.attr("src", relativeImagePath);
        }
    }

    private void updateInternalLinks(Document webpage, String baseUrl, String currentPagePath) {
        // Collect all links in the current page
        Elements links = webpage.select("a[href]");

        try {
            String baseDomain = new URI(baseUrl).getHost();

            for (Element link : links) {
                String href = link.attr("href"); // needed for skipping certain links
                String absoluteHref = link.attr("abs:href"); // needed for domain comparison

                // Skip empty or null hrefs
                if (href == null || href.isEmpty() || absoluteHref == null || absoluteHref.isEmpty()) {
                    continue;
                }

                // Skip external links, anchors, javascript, mailto, etc.
                if (href.startsWith("#") || href.startsWith("javascript:")
                        || href.startsWith("mailto:") || href.startsWith("tel:")) {
                    continue;
                }

                try {
                    String linkDomain = new URI(absoluteHref).getHost();

                    // Only process internal links (same domain)
                    if (linkDomain != null && linkDomain.equals(baseDomain)) {
                        // Convert the absolute URL to the local file path it should point to
                        String targetLocalPath = getRelativePath(absoluteHref);

                        // Calculate relative path from current page to target page
                        String relativeHref = calculateRelativePath(currentPagePath, targetLocalPath);

                        // Update the href attribute
                        link.attr("href", relativeHref);

                        logger.log(Level.DEBUG, "Updated link: {0} -> {1}", href, relativeHref);
                    }
                } catch (URISyntaxException e) {
                    logger.log(Level.DEBUG, "Skipping malformed link: {0}", href);
                }
            }
        } catch (URISyntaxException e) {
            logger.log(Level.WARNING, "Failed to parse base URL for link processing: {0}", baseUrl, e);
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
     * Calculates the relative path from a source HTML file to a destination
     * file (typically an image). This ensures that when the HTML file
     * references the destination file, the link will work regardless of how
     * deeply nested the HTML file is in the directory structure.
     *
     * For example: - Source: "about/team.html" - Destination:
     * "images/photo.jpg" - Result: "../images/photo.jpg"
     *
     * This allows the HTML to correctly reference images using relative paths
     * rather than absolute paths, making the downloaded site portable.
     */
    private String calculateRelativePath(String sourcePath, String destinationPath) {
        try {
            // Get the path of the source file's parent directory.
            Path from = Paths.get(sourcePath).getParent();
            if (from == null) {
                from = Paths.get(""); // empty path (root directory of save location)
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
