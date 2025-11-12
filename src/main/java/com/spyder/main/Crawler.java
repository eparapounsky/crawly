package com.spyder.main;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.net.URI;

public class Crawler {

    private final String url;
    private final Downloader downloader;

    public Crawler(String url, String saveLocation, Downloader downloader) {
        this.url = url;
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
                String currentLink = link.attr("abs:href");

                // prevent null or empty links
                if (currentLink == null || currentLink.isEmpty()) {
                    continue;
                }

                // prevent infinite loops
                if (visitedUrls.contains(currentLink)) {
                    continue;
                }

                // do not follow external links
                URI currentUri = new URI(currentLink);
                String currentDomain = currentUri.getHost();

                URI baseUri = new URI(this.url);
                String originalDomain = baseUri.getHost();
                if (!currentDomain.equals(originalDomain)) {
                    continue;
                }

                crawlHelper(currentLink, visitedUrls);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
