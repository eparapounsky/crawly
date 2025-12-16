package com.spyder.main;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Crawly {

    private static final Logger logger = System.getLogger(Crawly.class.getName());

    public static void main(String[] args) {
        logger.log(Level.INFO, "Started Crawly");

        setUpGui();

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

    private static void setUpGui() {
        // create the main window
        JFrame frame = new JFrame("Crawly");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // exit application when window is closed
        frame.setSize(500, 200);
        frame.setLocationRelativeTo(null); // center window on screen

        // create panel (container for components)
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // use box layout with vertical stacking
        panel.add(javax.swing.Box.createVerticalStrut(15)); // add some vertical space

        // create and add label to panel
        JLabel label = new JLabel("Enter URL to crawl:");
        label.setAlignmentX(JPanel.CENTER_ALIGNMENT); // center horizontally
        panel.add(label);
        panel.add(javax.swing.Box.createVerticalStrut(10)); // add some vertical space

        // create and add text fields to panel
        JTextField urlField = new JTextField(30);
        urlField.setMaximumSize(urlField.getPreferredSize()); // prevent stretching, use size specified in constructor
        urlField.setAlignmentX(JPanel.CENTER_ALIGNMENT); // center horizontally
        panel.add(urlField);
        panel.add(javax.swing.Box.createVerticalStrut(15)); // add some vertical space

        // create and add button to frame
        JButton button = new JButton("Go");
        button.setBounds(150, 200, 220, 50);
        button.setAlignmentX(JPanel.CENTER_ALIGNMENT); // center horizontally
        panel.add(button);

        // add panel to frame
        frame.add(panel);

        // set frame to visible
        frame.setVisible(true);
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
