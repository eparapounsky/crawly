package com.spyder.main;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {

    private final String url;
    private final Downloader downloader;
    private static final Logger logger = System.getLogger(Crawler.class.getName());

    public Crawler(String url, Downloader downloader) {
        this.url = url;
        this.downloader = downloader;
    }

    public void crawl() {
        try {
            HashSet<String> visitedUrls = new HashSet<>();
            crawlHelper(url, visitedUrls);
        } catch (Exception e) {
            // use concatenation to include exception message
            logger.log(Level.ERROR, "Failed to start crawling from: " + url, e);
        }
    }

    private void crawlHelper(String url, HashSet<String> visitedUrls) {
        try {
            logger.log(Level.INFO, "Started crawling webpage: {0}", url);

            Document webpage = Jsoup.connect(url).get(); // store parsed html
            downloader.download(webpage, url);
            visitedUrls.add(url);

            // collect all links in current page
            Elements links = webpage.select("a[href]");

            if (links.isEmpty()) {
                logger.log(Level.DEBUG, "No links found on current page: {0}", url);
                return;
            }

            for (Element link : links) {
                String currentLink = link.attr("abs:href");

                // prevent null or empty links
                if (currentLink == null || currentLink.isEmpty()) {
                    logger.log(Level.DEBUG, "Encountered null or empty link on currentpage: {0}", currentLink);
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
                    logger.log(Level.DEBUG, "Skipping external link: {0}", currentLink);
                    continue;
                }
                // recursive crawl
                crawlHelper(currentLink, visitedUrls);
            }
        } catch (IOException | URISyntaxException e) {
            logger.log(Level.WARNING, "Failed to crawl page: " + url + " - continuing with other pages", e);
        }
    }

}
