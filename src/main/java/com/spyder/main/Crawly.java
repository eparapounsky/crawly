package com.spyder.main;

public class Crawly {

    public static void main(String[] args) {
        String url = "https://toscrape.com/";
        String saveLocation = "./output";
        Downloader downloader = new Downloader(saveLocation); // create the dependency
        Crawler crawler = new Crawler(url, downloader); // inject dependency
        crawler.crawl();
    }
}
