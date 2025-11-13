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
        // extract path from url to get file structure
        String urlPath = extractPathFromUrl(url);
        
        // use url path to determine file path within save location
        String relativePath = urlPath;
        if (relativePath.isEmpty() || relativePath.equals("/")) {
            relativePath = "index.html";
        } else if (!relativePath.endsWith(".html") && !relativePath.contains(".")) {
            // If it's a path without extension, append index.html
            relativePath = relativePath.endsWith("/") ? relativePath + "index.html" : relativePath + "/index.html";
        }
        
        // remove leading slash to make it a relative path
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
            Path outputFilepath = Paths.get(fullPath).getParent();
            if (outputFilepath != null) {
                Files.createDirectories(outputFilepath);
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
            int schemeEnd = url.indexOf("://");
            if (schemeEnd != -1) {
                url = url.substring(schemeEnd + 3);
            }
            
            int pathStart = url.indexOf('/');
            if (pathStart == -1) {
                return "/";
            }
            
            String path = url.substring(pathStart);
            
            // Remove query parameters and fragments
            int queryStart = path.indexOf('?');
            if (queryStart != -1) {
                path = path.substring(0, queryStart);
            }
            int fragmentStart = path.indexOf('#');
            if (fragmentStart != -1) {
                path = path.substring(0, fragmentStart);
            }
            
            return path;
        } catch (Exception e) {
            return "/";
        }
    }
}
