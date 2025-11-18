package com.spyder.main;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Crawly {

    private static final Logger logger = Logger.getLogger(Crawly.class.getName());

    public static void main(String[] args) {
        logger.log(Level.INFO, "Started Crawly");

        String url = "https://toscrape.com/";
        String saveLocation = "./output";

        logger.log(Level.INFO, "Initializing Crawly with URL: {0} and Save Location: {1}",
                new Object[] { url, saveLocation });

        try {
            Downloader downloader = new Downloader(saveLocation); // create the dependency
            Crawler crawler = new Crawler(url, downloader); // inject dependency
            crawler.crawl();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error occurred during crawling: {0}", e.getMessage());
            logger.log(Level.SEVERE, "Stack trace:", e);
        }

        logger.log(Level.INFO, "Crawly finished");
    }
}
