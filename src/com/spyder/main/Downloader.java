package com.spyder.main;

import org.jsoup.nodes.Document;

import java.io.FileWriter;

public class Downloader {

    public void download(Document webpage) {
        try {
            FileWriter myWriter = new FileWriter(webpage.title());
            myWriter.write("File test");
            myWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
