package com.spyder.main;

import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;

public class Downloader {

    public void download(Document webpage) {
        createFile(webpage);
    }

    private void createFile(Document webpage) {

        try {
            String file_name = webpage.location().replace("https://www.", ""); // remove invalid file name syntax
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
