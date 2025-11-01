package com.spyder.main;

public class Crawly {

    public static void main(String[] args) {
        String url = "https://www.elenaparapounsky.com/";
        String saveLocation = "./output";
        Downloader downloader = new Downloader(null); // create the dependency
        Crawler crawler = new Crawler(url, saveLocation, downloader); // inject dependency
        crawler.crawl();
    }
}
