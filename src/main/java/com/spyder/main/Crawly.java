package com.spyder.main;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Scanner;

public class Crawly {

    private static final Logger logger = System.getLogger(Crawly.class.getName());

    public static void main(String[] args) {
        logger.log(Level.INFO, "Started Crawly");

        // Get user input
        String url;
        String saveLocation;

        if (args.length == 0) {
            try (Scanner scanner = new Scanner(System.in)) {
                url = getUrl(args, scanner);
                saveLocation = getSaveLocation(args, scanner);
            }
        } else {
            url = getUrl(args, null);
            saveLocation = getSaveLocation(args, null);
        }

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

        logger.log(Level.INFO, "Crawly finished");
    }

    private static String getUserInput(Scanner scanner, String userPrompt) {
        System.out.print(userPrompt);
        String userInput = scanner.nextLine();
        return userInput;
    }

    private static String getUrl(String[] args, Scanner scanner) {
        String url;

        if (scanner == null) {
            // command line mode
            url = args[0];
        } else {
            // interactive mode
            url = getUserInput(scanner, "Enter website: ");
        }

        // Basic input validation
        if (url == null || url.trim().isEmpty()) {
            logger.log(Level.ERROR, "URL cannot be empty");
            System.exit(1);
        }

        return url;
    }

    private static String getSaveLocation(String[] args, Scanner scanner) {
        String saveLocation;

        if (scanner == null) {
            // command line mode
            saveLocation = args.length > 1 ? args[1] : null;
        } else {
            // interactive mode
            saveLocation = getUserInput(scanner, "Enter save location (optional, hit Enter to skip): ");
        }

        // Set default if not provided
        if (saveLocation == null || saveLocation.trim().isEmpty()) {
            logger.log(Level.INFO, "Save location not specified; defaulting to ./output");
            saveLocation = "./output";
        }

        return saveLocation;
    }
}
