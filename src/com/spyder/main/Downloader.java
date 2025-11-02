package com.spyder.main;

import org.jsoup.nodes.Document;

import java.io.FileWriter;

public class Downloader {

    public void download(Document webpage) {
        try {
            FileWriter myWriter = new FileWriter(webpage.title() + ".html");
            myWriter.write(webpage.html());  // write entire html doc
            myWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
