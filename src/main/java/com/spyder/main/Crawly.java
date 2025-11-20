package com.spyder.main;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Scanner;

public class Crawly {

    private static final Logger logger = System.getLogger(Crawly.class.getName());

    public static void main(String[] args) {
        logger.log(Level.INFO, "Started Crawly");

        // Get user input.
        // TO-DO: directly read command line arguments?
        String url;
        String saveLocation;
        try (Scanner scanner = new Scanner(System.in)) {
            // String url = "https://toscrape.com/";
            url = getUserInput(scanner, "Enter website: ");
            // String saveLocation = "./output";
            saveLocation = getUserInput(scanner, "Enter save location: ");
        }

        logger.log(Level.INFO, "Initializing Crawly with URL: {0} and Save Location: {1}",
                url, saveLocation);

        // Main application logic.
        try {
            Downloader downloader = new Downloader(saveLocation); // create the dependency
            Crawler crawler = new Crawler(url, downloader); // inject dependency
            crawler.crawl();
        } catch (Exception e) {
            logger.log(Level.ERROR, "Error occurred during crawling: {0}", e.getMessage());
            logger.log(Level.ERROR, "Stack trace:", e);
        }

        logger.log(Level.INFO, "Crawly finished");
    }

    private static String getUserInput(Scanner scanner, String userPrompt) {
        System.out.print(userPrompt);
        String userInput = scanner.nextLine();
        return userInput;
    }
}
