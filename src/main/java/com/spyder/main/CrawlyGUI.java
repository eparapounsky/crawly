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
    private static final int WINDOW_HEIGHT = 240;
    private static final int WINDOW_WIDTH = 500;
    private static final int ELEMENT_SPACING = 15;

    // Instance fields
    // Logger
    private static final Logger logger = System.getLogger(CrawlyGUI.class.getName());

    // GUI Components
    private JFrame frame;
    private JPanel mainPanel;
    private JPanel buttonPanel;
    private JLabel urlLabel;
    private JLabel saveLocationLabel;
    private JTextField urlField;
    private JTextField saveLocationField;
    private JButton goButton;
    private JButton stopButton;

    // Application state
    private String url;
    private String saveLocation;
    private Thread crawlerThread;

    // Constructor
    public CrawlyGUI() {
        buildUserInterface();
        addEventListeners();
    }

    // UI setup methods
    private void buildUserInterface() {
        this.frame = createFrame(); // create the main window
        this.mainPanel = createVerticalPanel();
        this.buttonPanel = createHorizontalPanel();

        initializeUrlComponents();
        initializeSaveLocationComponents();
        initializeButtons();

        frame.add(mainPanel);
        mainPanel.add(buttonPanel); // keep buttons in vertical flow with input fields
        frame.setVisible(true); // set frame to visible
    }

    private void initializeUrlComponents() {
        // create url label
        this.urlLabel = createLabel("Enter URL to crawl:");
        this.mainPanel.add(urlLabel); // add URL label to panel
        this.mainPanel.add(javax.swing.Box.createVerticalStrut(ELEMENT_SPACING)); // add some vertical space

        // create url field
        this.urlField = createTextField();
        this.mainPanel.add(urlField); // add URL field to panel
        this.mainPanel.add(javax.swing.Box.createVerticalStrut(ELEMENT_SPACING)); // add some vertical space
    }

    private void initializeSaveLocationComponents() {
        // create save location label
        this.saveLocationLabel = createLabel("Enter save location (optional):");
        this.mainPanel.add(saveLocationLabel); // add save location label to panel
        this.mainPanel.add(javax.swing.Box.createVerticalStrut(ELEMENT_SPACING)); // add some vertical space

        // create save location field
        this.saveLocationField = createTextField();
        this.mainPanel.add(saveLocationField); // add save location field to panel
    }

    private void initializeButtons() {
        // create Go and Stop buttons
        this.goButton = createButton("Go");
        this.stopButton = createButton("Stop");
        this.stopButton.setEnabled(false); // disabled initially, enabled when crawling starts

        // add buttons to the button panel
        this.buttonPanel.add(this.goButton);
        this.buttonPanel.add(javax.swing.Box.createHorizontalStrut(ELEMENT_SPACING)); // add some horizontal space
        this.buttonPanel.add(this.stopButton);
    }

    private void addEventListeners() {
        this.goButton.addActionListener(e -> handleGoButtonClick());
        this.stopButton.addActionListener(e -> handleStopButtonClick());
    }

    private void handleGoButtonClick() {
        // Assign user-specified values to instance variables
        CrawlyGUI.this.url = CrawlyGUI.this.urlField.getText();
        CrawlyGUI.this.saveLocation = CrawlyGUI.this.saveLocationField.getText();

        // Set saveLocation to default if not provided
        if (this.saveLocation == null || this.saveLocation.trim().isEmpty()) {
            logger.log(Level.INFO, "Save location not specified; defaulting to ./output");
            this.saveLocation = "./output";
        }

        // Disable Go button and enable Stop button
        CrawlyGUI.this.goButton.setEnabled(false);
        CrawlyGUI.this.stopButton.setEnabled(true);

        // Run crawler in a separate thread to prevent GUI blocking
        runCrawlerInThread();
    }

    private void handleStopButtonClick() {
        // Although the thread can't be null here due to the button state, check to be safe
        if (this.crawlerThread != null && this.crawlerThread.isAlive()) {
            logger.log(Level.INFO, "Crawling stopped by user");
            this.crawlerThread.interrupt();

            // Enable Go button and disable Stop button
            CrawlyGUI.this.goButton.setEnabled(true);
            CrawlyGUI.this.stopButton.setEnabled(false);
        }
    }

    /**
     * Executes the web crawling operation in a separate background thread. This
     * method creates and starts a new thread that performs the main crawling
     * logic, allowing the GUI to remain responsive during the operation.
     */
    private void runCrawlerInThread() {
        this.crawlerThread = new Thread(() -> {
            try {
                // Main application logic
                WebPageSaver webPageSaver = new WebPageSaver(this.saveLocation); // create the dependency
                Crawler crawler = new Crawler(this.url, webPageSaver); // inject dependency
                crawler.crawl();
                logger.log(Level.INFO, "Crawling completed successfully");
            } catch (Exception ex) {
                // Check if error message matches the custom interruption message; if so, do not log as an error
                if (!ex.getMessage().contains("interrupted")) {
                    logger.log(Level.ERROR, "Error occurred during crawling: {0}", ex.getMessage());
                }
            } finally {
                // Enable Go button and disable Stop button in the EDT 
                // Use SwingUtilities.invokeLater to ensure thread safety
                javax.swing.SwingUtilities.invokeLater(() -> {
                    this.goButton.setEnabled(true);
                    this.stopButton.setEnabled(false);
                });
            }

        });

        this.crawlerThread.start();
    }

    // UI creation helper methods
    private static JFrame createFrame() {
        JFrame frame = new JFrame("Crawly");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // exit application when window is closed
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT); // set window size
        frame.setLocationRelativeTo(null); // center window on screen
        return frame;
    }

    private static JPanel createVerticalPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // use box layout with vertical stacking
        panel.add(javax.swing.Box.createVerticalStrut(ELEMENT_SPACING)); // add some vertical space
        return panel;
    }

    private static JPanel createHorizontalPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS)); // use box layout with horizontal stacking
        return panel;
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
