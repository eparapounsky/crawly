package com.spyder.main;

public class Crawly {

    public static void main(String[] args) {
        new Crawler("https://www.elenaparapounsky.com/", "./output").crawl();
    }
}
