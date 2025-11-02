package com.spyder.main;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Crawler {

    private final String url;
    private String saveLocation;
    private final Downloader downloader;

    public Crawler(String url, String saveLocation, Downloader downloader) {
        this.url = url;
        this.saveLocation = saveLocation;
        this.downloader = downloader;
    }

    public void crawl() {
        try {
            Document webpage = Jsoup.connect(this.url).get(); // store parsed html
            downloader.download(webpage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
