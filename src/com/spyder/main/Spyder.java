package com.spyder.main;

public class Spyder {

    public static void main(String[] args) {
        new Downloader("https://www.elenaparapounsky.com/", "./output").crawl();
    }
}