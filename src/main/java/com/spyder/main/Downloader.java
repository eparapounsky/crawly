package com.spyder.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Downloader {

    private final String saveLocation;
    private static final Logger logger = Logger.getLogger(Downloader.class.getName());

    public Downloader(String saveLocation) {
        this.saveLocation = saveLocation;
    }

    public void download(Document webpage, String url) {
        // get relative path to file within save location, so we can preserve directory
        // structure
        String relativePath = getRelativePath(url);

        // build full file path that preserves directory structure from url
        // replace forward slashes with file separator to maintain directory structure
        String fullFilePath = saveLocation + File.separator + relativePath.replace("/", File.separator);

        // for all parent directories of current file, create them if they don't exist
        // this creates any nested directories needed before writing the file
        try {
            Path parentDirectoryPath = Paths.get(fullFilePath).getParent();
            if (parentDirectoryPath != null) {
                Files.createDirectories(parentDirectoryPath);
            }
        } catch (IOException | InvalidPathException e) {
            System.err.println("Error creating nested directories: " + e.getMessage());
        }

        // download images & update their urls in the html before writing the html file
        downloadImages(webpage, saveLocation, relativePath);

        // write entire current page html to filepath specified by fullFilePath
        try (FileWriter myWriter = new FileWriter(fullFilePath)) {
            myWriter.write(webpage.html());
        } catch (IOException e) {
            System.err.println("Error writing webpage to file: " + e.getMessage());
        }
    }

    private String getRelativePath(String url) {
        // use path from url to determine file path within save location
        String relativePath = extractPathFromUrl(url);

        if (relativePath.equals("/")) {
            relativePath = "index.html"; // if root path, save as index.html
        } else if (!relativePath.endsWith(".html") && !relativePath.contains(".")) {
            // if path has no extension (.html, .jpg), append index.html (for directory
            // paths)
            // ex: when you visit http://example.com/about, the server is actually serving
            // http://example.com/about/index.html
            // handle both cases: with or without trailing slash (ensure proper path
            // separation)
            if (relativePath.endsWith("/")) {
                relativePath = relativePath + "index.html";
            } else {
                relativePath = relativePath + "/index.html";
            }
        }

        // remove leading slash to make it a relative path (avoid // in file path)
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }
        return relativePath;
    }

    /*
     * Uses java.net.URI to extract the path component from the URL. Handles URL
     * encoding/decoding, strips queries and fragments, accounts for
     * schemes/ports/authentication.
     * Notes on edge cases: if URL has no path, returns null, so we return "/" for
     * root. If any exception occurs (malformed URL, etc.), we also return "/" as a
     * fallback.
     */
    private String extractPathFromUrl(String url) {
        try {
            return new URI(url).getPath(); // returns null if no path, or "/" for root
        } catch (URISyntaxException e) {
            return "/"; // fallback
        }
    }

    private void downloadImages(Document webpage, String rootDirectory, String currentPagePath) {
        // collect all images in current page
        Elements images = webpage.select("img");

        // create images directory at root level (or do nothing if directory exists)
        String imagesDirectory = rootDirectory + File.separator + "images";
        try {
            Files.createDirectories(Paths.get(imagesDirectory));
        } catch (IOException e) {
            System.err.println("Error creating images directory: " + e.getMessage());
            return;
        }

        // download each image and update its url in the html
        for (Element img : images) {
            try {
                // get absolute url (src is relative by default) and we need absolute to
                // download
                String imageAbsoluteUrl = img.attr("abs:src");
                if (imageAbsoluteUrl == null || imageAbsoluteUrl.isEmpty()) {
                    continue;
                }

                // extract filename from the image url
                String imageFileName = createImageFileName(imageAbsoluteUrl);
                String imageFilePath = imagesDirectory + File.separator + imageFileName;

                // download the image:
                // imageUri.toURL.openStream converts URI to URL (since URI doesn't have
                // openStream method) and opens a network connection to the image url ...
                // ... it returns an InputStream we can read from to get image data.
                // Files.copy copies all bytes from InputStream to a file at imageFilePath,
                // replacing existing file if it exists
                URI imageUri = new URI(imageAbsoluteUrl); // convert to uri to open network stream
                try (InputStream imageInputStream = imageUri.toURL().openStream()) {
                    Files.copy(imageInputStream, Paths.get(imageFilePath), StandardCopyOption.REPLACE_EXISTING);
                }

                // update the image src in the html so it works locally
                String relativeImagePath = calculateRelativePath(currentPagePath, "images/" + imageFileName); // get
                // relative
                // path
                // from
                // current
                // page to
                // image
                // file
                img.attr("src", relativeImagePath); // update src attribute to relative path

            } catch (Exception e) {
                System.err.println("Error downloading image: " + e.getMessage());
            }
        }
    }

    private String createImageFileName(String imageUrl) {
        try {
            // extract original filename from image url
            String imagePath = new URI(imageUrl).getPath(); // convert to url so we can automatically parse path
            String originalFileName = imagePath.substring(imagePath.lastIndexOf('/') + 1); // extract after last "/"
            return originalFileName;
        } catch (URISyntaxException e) {
            return "image.jpg"; // fallback to default filename
        }
    }

    // calculates the relative path from the current html page to the destination
    // path
    // this ensures images can be referenced correctly regardless of page depth
    private String calculateRelativePath(String sourcePath, String destinationPath) {
        try {
            // convert path strings to Path objects to get parent directories for current
            // page
            Path from = Paths.get(sourcePath).getParent();
            if (from == null) {
                from = Paths.get(""); // empty path (root directory)
            }
            Path to = Paths.get(destinationPath);

            // calculate relative path and convert to forward slashes for html (Windows
            // compatibility)
            Path relativePath = from.relativize(to);
            return relativePath.toString().replace(File.separator, "/");
        } catch (Exception e) {
            // fallback: return simple path from root if path operations fail
            return destinationPath;
        }
    }
}
