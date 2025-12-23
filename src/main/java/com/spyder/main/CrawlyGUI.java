package com.spyder.main;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CrawlyGUI {

    // Static constants for GUI dimensions and spacing
    private static final int WINDOW_HEIGHT = 230;
    private static final int WINDOW_WIDTH = 500;
    private static final int ELEMENT_SPACING = 15;

    // Instance fields
    private static final Logger logger = System.getLogger(CrawlyGUI.class.getName());
    private JFrame frame;
    private JPanel panel;
    private JLabel urlLabel;
    private JLabel saveLocationLabel;
    private JTextField urlField;
    private JTextField saveLocationField;
    private JButton goButton;
    private JButton stopButton;
    private String url;
    private String saveLocation;
    // private Crawler crawler;
    private Thread crawlerThread;

    // Constructor
    public CrawlyGUI() {
        buildUserInterface();
        addEventListeners();
    }

    // Private methods
    private void buildUserInterface() {
        this.frame = createFrame(); // create the main window
        this.panel = createPanel(); // create panel (container for components)

        initializeUrlComponents();
        initializeSaveLocationComponents();
        initializeGoButton();
        initializeStopButton();

        this.frame.add(panel); // add panel to frame
        this.frame.setVisible(true); // set frame to visible
    }

    private void addEventListeners() {
        this.goButton.addActionListener(e -> {
            // Assign user-specified values to instance variables
            CrawlyGUI.this.url = CrawlyGUI.this.urlField.getText();
            CrawlyGUI.this.saveLocation = CrawlyGUI.this.saveLocationField.getText();

            // Set saveLocation to default if not provided
            if (this.saveLocation == null || this.saveLocation.trim().isEmpty()) {
                logger.log(Level.INFO, "Save location not specified; defaulting to ./output");
                this.saveLocation = "./output";
            }

            // Disable Go button and enable Stop button
            this.goButton.setEnabled(false);
            this.stopButton.setEnabled(true);

            // Run crawler in a separate thread to prevent GUI blocking
            this.crawlerThread = new Thread(() -> {
                try {
                    // Main application logic
                    WebPageSaver downloader = new WebPageSaver(CrawlyGUI.this.saveLocation); // create the dependency
                    Crawler crawler = new Crawler(CrawlyGUI.this.url, downloader); // inject dependency
                    crawler.crawl();
                    logger.log(Level.INFO, "Crawling completed successfully");
                } catch (Exception ex) {
                    if (Thread.currentThread().isInterrupted()) {
                        logger.log(Level.INFO, "Crawling stopped by user");
                    } else {
                        logger.log(Level.ERROR, "Error occurred during crawling: {0}", ex.getMessage());
                    }
                } finally {
                    // Enable Go button and disable Stop button in the EDT 
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        CrawlyGUI.this.goButton.setEnabled(true);
                        CrawlyGUI.this.stopButton.setEnabled(false);
                    });
                }

            });
        });

        this.stopButton.addActionListener(e -> {

        });
    }

    private static JFrame createFrame() {
        JFrame frame = new JFrame("Crawly");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // exit application when window is closed
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT); // set window size
        frame.setLocationRelativeTo(null); // center window on screen
        return frame;
    }

    private static JPanel createPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // use box layout with vertical stacking
        panel.add(javax.swing.Box.createVerticalStrut(ELEMENT_SPACING)); // add some vertical space
        return panel;
    }

    private void initializeUrlComponents() {
        // create url label
        this.urlLabel = createLabel("Enter URL to crawl:");
        this.panel.add(urlLabel); // add URL label to panel
        this.panel.add(javax.swing.Box.createVerticalStrut(ELEMENT_SPACING)); // add some vertical space

        // create url field
        this.urlField = createTextField();
        this.panel.add(urlField); // add URL field to panel
        this.panel.add(javax.swing.Box.createVerticalStrut(ELEMENT_SPACING)); // add some vertical space
    }

    private void initializeSaveLocationComponents() {
        // create save location label
        this.saveLocationLabel = createLabel("Enter save location (optional):");
        this.panel.add(saveLocationLabel); // add save location label to panel
        this.panel.add(javax.swing.Box.createVerticalStrut(ELEMENT_SPACING)); // add some vertical space

        // create save location field
        this.saveLocationField = createTextField();
        this.panel.add(saveLocationField); // add save location field to panel
        this.panel.add(javax.swing.Box.createVerticalStrut(ELEMENT_SPACING)); // add some vertical space
    }

    private void initializeGoButton() {
        this.goButton = createButton("Go"); // create button
        this.panel.add(goButton); // add button to panel
    }

    private void initializeStopButton() {
        this.stopButton = createButton("Stop");
        this.stopButton.setEnabled(false); // disabled initially, enabled when crawling starts
        this.panel.add(stopButton);
    }

    private static JLabel createLabel(String labelText) {
        JLabel label = new JLabel(labelText);
        label.setAlignmentX(JPanel.CENTER_ALIGNMENT); // center horizontally
        return label;
    }

    private static JTextField createTextField() {
        JTextField textField = new JTextField(30);
        textField.setMaximumSize(textField.getPreferredSize()); // prevent stretching, use size specified in constructor
        textField.setAlignmentX(JPanel.CENTER_ALIGNMENT); // center horizontally
        return textField;
    }

    private static JButton createButton(String buttonText) {
        JButton button = new JButton(buttonText);
        button.setBounds(150, 200, 220, 50); // set position and size
        button.setAlignmentX(JPanel.CENTER_ALIGNMENT); // center horizontally
        return button;
    }
}
