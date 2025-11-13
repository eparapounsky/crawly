package com.spyder.main;

import org.jsoup.nodes.Document;

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
            Path filepath = Paths.get(saveLocation);
            Files.createDirectories(filepath);
        } catch (IOException e) {
            System.err.println("Error creating nested directories: " + e.getMessage());
        }

        String filename = webpage.title().replace(".", "");

        try (FileWriter myWriter = new FileWriter(filename + ".html")) {
            myWriter.write(webpage.html());  // write entire html doc
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
