package com.spyder.main;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Scanner;

public class Crawly {

    private static final Logger logger = System.getLogger(Crawly.class.getName());
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        logger.log(Level.INFO, "Started Crawly");

        // String url = "https://toscrape.com/";
        String url = getUserInput("Enter website: ");
        System.out.println(url);
        // String saveLocation = "./output";
        String saveLocation = getUserInput("Enter save location: ");

        logger.log(Level.INFO, "Initializing Crawly with URL: {0} and Save Location: {1}",
                url, saveLocation);

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

    private static String getUserInput(String userPrompt) {
        System.out.print(userPrompt);
        String userInput = scanner.nextLine();
        return userInput;
    }
}
