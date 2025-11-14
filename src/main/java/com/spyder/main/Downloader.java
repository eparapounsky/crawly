package com.spyder.main;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.InvalidPathException;
import java.nio.file.StandardCopyOption;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class Downloader {

    private final String saveLocation;
    private final String siteName; // for top-level directory (organizing downloaded files by site)

    // for cases where siteName is not provided
    public Downloader(String saveLocation) {
        this(saveLocation, null);
    }

    // for cases where siteName is provided - organize downloads by site
    public Downloader(String saveLocation, String siteName) {
        this.saveLocation = saveLocation;
        this.siteName = siteName;
    }

    // pass url to give method access to path information
    public void download(Document webpage, String url) {
        // use path from url to determine file path within save location
        String relativePath = extractPathFromUrl(url);
        if (relativePath.equals("/")) {
            relativePath = "index.html"; // if root path, save as index.html
        } else if (!relativePath.endsWith(".html") && !relativePath.contains(".")) {
            // if path has no extension (.html, .jpg), append index.html (for directory paths)
            // ex: when you visit http://example.com/about, the server is actually serving http://example.com/about/index.html
            // handle both cases: with or without trailing slash (ensure proper path separation)
            if (relativePath.endsWith("/")) {
                relativePath = relativePath + "index.html";
            } else {
                relativePath = relativePath + "/index.html";
            }
        }

        // remove leading slash to make it a relative path (avoid double slashes in file path)
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }

        // include site name as top level directory if given to organize by site
        String rootDirectory = saveLocation;
        if (siteName != null && !siteName.isEmpty()) {
            rootDirectory = rootDirectory + File.separator + siteName;
        }

        // build full file path that preserves directory structure from url
        // replace forward slashes with file separator to maintain directory structure
        String fullFilePath = rootDirectory + File.separator + relativePath.replace("/", File.separator);

        // for all parent directories of current file path, create them if they don't exist
        // this creates any nested directories needed before writing the file
        try {
            Path parentDirectoryPath = Paths.get(fullFilePath).getParent();
            if (parentDirectoryPath != null) {
                Files.createDirectories(parentDirectoryPath);
            }
        } catch (IOException | InvalidPathException e) {
            System.err.println("Error creating nested directories: " + e.getMessage());
        }

        // download images and update their urls in the html before writing the html file
        downloadImages(webpage, rootDirectory, relativePath);

        // write entire current page html to filepath specified by fullFilePath
        try (FileWriter myWriter = new FileWriter(fullFilePath)) {
            myWriter.write(webpage.html());
        } catch (IOException e) {
            System.err.println("Error writing webpage to file: " + e.getMessage());
        }
    }

    // extracts the path portion from the url, handles various url formats, removes query parameters and fragments and fragments, provides error handling
    private String extractPathFromUrl(String url) {
        try {
            int urlSchemeEndIndex = url.indexOf("://");
            if (urlSchemeEndIndex != -1) {
                url = url.substring(urlSchemeEndIndex + 3); // extract after "://"
            }

            int urlPathStartIndex = url.indexOf('/');
            if (urlPathStartIndex == -1) {
                return "/"; // no path, return root
            }

            String relativeUrlPath = url.substring(urlPathStartIndex); // extract after domain

            // remove query parameters and fragments
            int urlQueryStartIndex = relativeUrlPath.indexOf('?');
            if (urlQueryStartIndex != -1) {
                relativeUrlPath = relativeUrlPath.substring(0, urlQueryStartIndex); // extract before "?"
            }
            int fragmentStart = relativeUrlPath.indexOf('#');
            if (fragmentStart != -1) {
                relativeUrlPath = relativeUrlPath.substring(0, fragmentStart); // extract before "#"
            }

            return relativeUrlPath;
        } catch (Exception e) {
            return "/"; // on error, default to root path (best effort)
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
                // get absolute url (src is relative by default) and we need absolute to download
                String imageAbsoluteUrl = img.attr("abs:src");
                if (imageAbsoluteUrl == null || imageAbsoluteUrl.isEmpty()) {
                    continue;
                }

                // extract filename from the image url
                String imageFileName = createImageFileName(imageAbsoluteUrl);
                String imageFilePath = imagesDirectory + File.separator + imageFileName;

                // download the image
                URI imageUri = new URI(imageAbsoluteUrl); // convert to uri to open network stream
                try (InputStream in = imageUri.toURL().openStream()) {
                    Files.copy(in, Paths.get(imageFilePath), StandardCopyOption.REPLACE_EXISTING);
                }

                // update the image src in the html to use relative path so it works locally
                String relativeImagePath = calculateRelativePath(currentPagePath, "images/" + imageFileName);
                img.attr("src", relativeImagePath);

            } catch (Exception e) {
                System.err.println("Error downloading image: " + e.getMessage());
            }
        }
    }

    private String createImageFileName(String imageUrl) {
        try {
            // extract original filename from image url
            String imagePath = new URI(imageUrl).getPath(); // convert to url so we can parse path
            String originalFileName = imagePath.substring(imagePath.lastIndexOf('/') + 1); // extract after last "/"
            return originalFileName;
        } catch (Exception e) {
            return "image.jpg"; // fallback to default filename
        }
    }

    // calculates the relative path from the current page to the target file
    // this ensures images can be referenced correctly regardless of page depth
    private String calculateRelativePath(String fromPath, String toPath) {
        try {
            // convert paths to Path objects using forward slashes (web standard)
            Path from = Paths.get(fromPath).getParent();
            if (from == null) {
                from = Paths.get("");  // root level
            }
            Path to = Paths.get(toPath);

            // calculate relative path and convert to forward slashes for html
            Path relativePath = from.relativize(to);
            return relativePath.toString().replace(File.separator, "/");
        } catch (Exception e) {
            // fallback: return simple path from root if path operations fail
            return toPath;
        }
    }
}
