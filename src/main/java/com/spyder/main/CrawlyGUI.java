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

        this.urlLabel = createUrlLabel(); // create url label
        this.panel.add(urlLabel); // add URL label to panel
        panel.add(javax.swing.Box.createVerticalStrut(10)); // add some vertical space

        this.urlField = createUrlField(); // create url field
        panel.add(urlField); // add URL field to panel
        panel.add(javax.swing.Box.createVerticalStrut(15)); // add some vertical space

        this.crawlButton = CrawlyGUI.createButton(); // create button
        panel.add(crawlButton); // add button to panel

        frame.add(panel); // add panel to frame
        frame.setVisible(true); // set frame to visible
    }

    // private void setupLayout() {}

    private static JButton createButton() {
        JButton button = new JButton("Go");
        button.setBounds(150, 200, 220, 50);
        button.setAlignmentX(JPanel.CENTER_ALIGNMENT); // center horizontally
        return button;
    }

    private static JTextField createUrlField() {
        JTextField urlField = new JTextField(30);
        urlField.setMaximumSize(urlField.getPreferredSize()); // prevent stretching, use size specified in constructor
        urlField.setAlignmentX(JPanel.CENTER_ALIGNMENT); // center horizontally
        return urlField;
    }

    private static JLabel createUrlLabel() {
        JLabel label = new JLabel("Enter URL to crawl:");
        label.setAlignmentX(JPanel.CENTER_ALIGNMENT); // center horizontally
        return label;
    }

    private static JPanel createPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // use box layout with vertical stacking
        panel.add(javax.swing.Box.createVerticalStrut(15)); // add some vertical space
        return panel;
    }

    private static JFrame createFrame() {
        JFrame frame = new JFrame("Crawly");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // exit application when window is closed
        frame.setSize(500, 200);
        frame.setLocationRelativeTo(null); // center window on screen
        return frame;
    }

}
