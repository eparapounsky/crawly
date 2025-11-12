package com.spyder.main;

public class Crawly {

    public static void main(String[] args) {
        String url = "https://books.toscrape.com/";
        // String url = "C:\\git\\Crawly\\output\\index.html";
        String saveLocation = "./output";
        Downloader downloader = new Downloader(saveLocation); // create the dependency
        Crawler crawler = new Crawler(url, downloader); // inject dependency
        crawler.crawl();
    }
}
