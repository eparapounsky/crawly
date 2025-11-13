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

    public Downloader(String saveLocation) {
        this.saveLocation = saveLocation;
    }

    public void download(Document webpage) {
        // create output directory if it doesn't exist
        try {
            Path outputFilepath = Paths.get(saveLocation);
            Files.createDirectories(outputFilepath);
        } catch (IOException e) {
            System.err.println("Error creating nested directories: " + e.getMessage());
        }

        String pageTitle = sanitizePageTitle(webpage.title());
        String filename = saveLocation + File.separator + pageTitle + ".html"; // File.separator for platform independent filepath

        // write entire page html to filepath specified by filename
        try (FileWriter myWriter = new FileWriter(filename)) {
            myWriter.write(webpage.html());
        } catch (IOException e) {
            System.err.println("Error writing webpage to file: " + e.getMessage());
        }
    }

    private String sanitizePageTitle(String filename) {
        String sanitized = filename.replace(".", "").replace("!", "").replace("?", "").replace("", "").replace(":", "-").replace("|", "").trim();
        return sanitized;
    }
}
