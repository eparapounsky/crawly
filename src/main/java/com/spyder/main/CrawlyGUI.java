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

    private static final Logger logger = System.getLogger(CrawlyGUI.class.getName());
    private static final int PANEL_SPACING = 15;
    private static final int GUI_WINDOW_HEIGHT = 230;
    private static final int GUI_WINDOW_WIDTH = 500;
    private JFrame frame;
    private JPanel panel;
    private JLabel urlLabel;
    private JLabel saveLocationLabel;
    private JTextField urlField;
    private JTextField saveLocationField;
    private JButton crawlButton;
    private String url;
    private String saveLocation;

    public CrawlyGUI() {
        buildUserInterface();
        addEventListeners();
    }

    private void addEventListeners() {
        this.crawlButton.addActionListener(e -> {
            System.out.println("Button was clicked!");
            CrawlyGUI.this.url = CrawlyGUI.this.urlField.getText();
            CrawlyGUI.this.saveLocation = CrawlyGUI.this.saveLocationField.getText();
            // Set default if not provided
            if (this.saveLocation == null || this.saveLocation.trim().isEmpty()) {
                logger.log(Level.INFO, "Save location not specified; defaulting to ./output");
                this.saveLocation = "./output";
            }
            WebPageSaver downloader = new WebPageSaver(CrawlyGUI.this.saveLocation); // create the dependency
            Crawler crawler = new Crawler(CrawlyGUI.this.url, downloader); // inject dependency
            crawler.crawl();
        });
    }

    private void buildUserInterface() {
        this.frame = createFrame(); // create the main window
        this.panel = createPanel(); // create panel (container for components)

        initializeUrlComponents();
        initializeSaveLocationComponents();
        initializeCrawlButton();

        this.frame.add(panel); // add panel to frame
        this.frame.setVisible(true); // set frame to visible
    }

    private static JFrame createFrame() {
        JFrame frame = new JFrame("Crawly");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // exit application when window is closed
        frame.setSize(GUI_WINDOW_WIDTH, GUI_WINDOW_HEIGHT); // set window size
        frame.setLocationRelativeTo(null); // center window on screen
        return frame;
    }

    private static JPanel createPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // use box layout with vertical stacking
        panel.add(javax.swing.Box.createVerticalStrut(PANEL_SPACING)); // add some vertical space
        return panel;
    }

    private void initializeUrlComponents() {
        // create url label
        this.urlLabel = createLabel("Enter URL to crawl:");
        this.panel.add(urlLabel); // add URL label to panel
        this.panel.add(javax.swing.Box.createVerticalStrut(PANEL_SPACING)); // add some vertical space

        // create url field
        this.urlField = createTextField();
        this.panel.add(urlField); // add URL field to panel
        this.panel.add(javax.swing.Box.createVerticalStrut(PANEL_SPACING)); // add some vertical space
    }

    private void initializeSaveLocationComponents() {
        // create save location label
        this.saveLocationLabel = createLabel("Enter save location (optional):");
        this.panel.add(saveLocationLabel); // add save location label to panel
        this.panel.add(javax.swing.Box.createVerticalStrut(PANEL_SPACING)); // add some vertical space

        // create save location field
        this.saveLocationField = createTextField();
        this.panel.add(saveLocationField); // add save location field to panel
        this.panel.add(javax.swing.Box.createVerticalStrut(PANEL_SPACING)); // add some vertical space
    }

    private void initializeCrawlButton() {
        this.crawlButton = createButton("Go"); // create button
        this.panel.add(crawlButton); // add button to panel
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
