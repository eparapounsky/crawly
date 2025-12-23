package com.spyder.main;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

public class Crawly {

    private static final Logger logger = System.getLogger(Crawly.class.getName());

    public static void main(String[] args) {
        logger.log(Level.INFO, "Started Crawly");

        if (args.length == 0) {
            // GUI Mode
            CrawlyGUI crawlyGUI = new CrawlyGUI();
        } else {
            // Command-Line Mode
            String url = getUrl(args);
            String saveLocation = getSaveLocation(args);
            logger.log(Level.INFO, "Initializing Crawly with URL: {0} and Save Location: {1}",
                    url, saveLocation);

            // Main application logic
            try {
                WebPageSaver downloader = new WebPageSaver(saveLocation); // create the dependency
                Crawler crawler = new Crawler(url, downloader); // inject dependency
                crawler.crawl();
            } catch (Exception e) {
                logger.log(Level.ERROR, "Error occurred during crawling: {0}", e.getMessage());
                logger.log(Level.ERROR, "Stack trace:", e);
            }

            logger.log(Level.INFO, "Crawling completed successfully");
        }

    }

    private static String getUrl(String[] args) {
        String url = args[0];

        // Basic input validation
        if (url == null || url.trim().isEmpty()) {
            logger.log(Level.ERROR, "URL cannot be empty");
            System.exit(1);
        }

        return url;
    }

    private static String getSaveLocation(String[] args) {
        String saveLocation = args.length > 1 ? args[1] : null;

        // Set default if not provided
        if (saveLocation == null || saveLocation.trim().isEmpty()) {
            logger.log(Level.INFO, "Save location not specified; defaulting to ./output");
            saveLocation = "./output";
        }

        return saveLocation;
    }
}
