package com.spyder.main;

import org.jsoup.nodes.Document;

import java.io.FileWriter;

public class Downloader {
    private final String saveLocation;

    public Downloader(String saveLocation) {
        this.saveLocation = saveLocation;
    }

    public void download(Document webpage) {
        String filename = webpage.title().replace(".", "");

        try (FileWriter myWriter = new FileWriter(filename + ".html")) {
            myWriter.write(webpage.html());  // write entire html doc
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
