package com.spyder.main;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;

public class Crawler {

    private final String url;
    private String saveLocation;
    private final Downloader downloader;

    public Crawler(String url, String saveLocation, Downloader downloader) {
        this.url = url;
        this.saveLocation = saveLocation;
        this.downloader = downloader;
    }

    public void crawl() {
        try {
            HashSet<String> visitedURLS = new HashSet<>();

            crawlHelper(url, visitedURLS);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void crawlHelper(String url, HashSet<String> visitedUrls) {
        try {
            Document webpage = Jsoup.connect(url).get(); // store parsed html
            downloader.download(webpage);

            visitedUrls.add(url);

            // collect all links in current page
            Elements links = webpage.select("a[href]");

            if (links.isEmpty()) {
                return;
            }

            for (Element link : links) {
                if (visitedUrls.contains(link.attr("abs:href"))) {
                    continue;
                }
                crawlHelper(link.attr("abs:href"), visitedUrls);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
