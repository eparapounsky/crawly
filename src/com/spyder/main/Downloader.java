package com.spyder.main;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;

public class Downloader {
    private String url;
    private String location;

    public Downloader(String url, String location) {
        this.url = url;
        this.location = location;
    }

    public void crawl() {
        try {
            Document webpage = Jsoup.connect(this.url).get(); // download the page HTML
            createFile(webpage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createFile(Document webpage) {
        // create a file to store the downloaded content
        try {
            String file_name = webpage.location().replace("https://www.", ""); // remove invalid file name syntax

            System.out.println(file_name);
            File page_file = new File(file_name); // create file object

            if (page_file.createNewFile()) {           // try to create the file
                System.out.println("File created: " + page_file.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
