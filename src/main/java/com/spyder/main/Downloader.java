package com.spyder.main;

import org.jsoup.nodes.Document;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.FileWriter;
import java.io.IOException;

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
        String basePath = saveLocation;
        if (siteName != null && !siteName.isEmpty()) {
            basePath = basePath + File.separator + siteName;
        }

        // build full file path that preserves directory structure from url
        String fullPath = basePath + File.separator + relativePath.replace("/", File.separator);

        // create output directory if it doesn't exist
        // include all nested directories before writing file
        try {
            Path outputFilepath = Paths.get(fullPath).getParent(); // ensure all parent directories are created
            if (outputFilepath != null) {
                Files.createDirectories(outputFilepath); // create nested directories
            }
        } catch (IOException e) {
            System.err.println("Error creating nested directories: " + e.getMessage());
        }

        // write entire page html to filepath specified by fullPath
        try (FileWriter myWriter = new FileWriter(fullPath)) {
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
            return "/";
        }
    }
}
