package com.spyder.main;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CrawlyGUI {

    private JFrame frame;
    private JPanel panel;
    private JLabel urlLabel;
    private JLabel saveLocationLabel;
    private JTextField urlField;
    private JTextField saveLocationField;
    private JButton crawlButton;

    public CrawlyGUI() {
        initializeComponents();
        // setupLayout();
    }

    private void initializeComponents() {
        this.frame = createFrame(); // create the main window
        this.panel = createPanel(); // create panel (container for components)

        initializeUrlComponents();
        initializeSaveLocationComponents();
        initializeCrawlButton();

        this.frame.add(panel); // add panel to frame
        this.frame.setVisible(true); // set frame to visible
    }

    // private void setupLayout() {}
    private static JFrame createFrame() {
        JFrame frame = new JFrame("Crawly");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // exit application when window is closed
        frame.setSize(500, 230); // set window size
        frame.setLocationRelativeTo(null); // center window on screen
        return frame;
    }

    private static JPanel createPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // use box layout with vertical stacking
        panel.add(javax.swing.Box.createVerticalStrut(15)); // add some vertical space
        return panel;
    }

    private void initializeUrlComponents() {
        // create url label
        this.urlLabel = createLabel("Enter URL to crawl:"); 
        this.panel.add(urlLabel); // add URL label to panel
        this.panel.add(javax.swing.Box.createVerticalStrut(10)); // add some vertical space

        // create url field
        this.urlField = createTextField(); 
        this.panel.add(urlField); // add URL field to panel
        this.panel.add(javax.swing.Box.createVerticalStrut(15)); // add some vertical space
    }

    private void initializeSaveLocationComponents() {
        // create save location label
        this.saveLocationLabel = createLabel("Enter save location (optional):"); 
        this.panel.add(saveLocationLabel); // add save location label to panel
        this.panel.add(javax.swing.Box.createVerticalStrut(10)); // add some vertical space

        // create save location field
        this.saveLocationField = createTextField(); 
        this.panel.add(saveLocationField); // add save location field to panel
        this.panel.add(javax.swing.Box.createVerticalStrut(15)); // add some vertical space
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
