package com.spyder.main;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {

    private final String url;
    private final Downloader downloader;
    private static final Logger logger = System.getLogger(Crawler.class.getName());
    private static final int MAX_CRAWL_DEPTH = 10;

    public Crawler(String url, Downloader downloader) {
        // Validate URL format before assignment
        if (!Utils.isValidUrl(url)) {
            throw new IllegalArgumentException("Invalid URL format: " + url);
        }

        this.url = url;
        this.downloader = downloader;
    }

    public void crawl() {
        try {
            Set<String> visitedUrls = new HashSet<>();
            crawlHelper(url, visitedUrls, MAX_CRAWL_DEPTH);
        } catch (Exception e) {
            // use concatenation to include exception message
            logger.log(Level.ERROR, "Failed to start crawling from: " + url, e);
        }
    }

    private void crawlHelper(String url, Set<String> visitedUrls, int maxDepth) {
        // Check depth limit
        if (maxDepth <= 0) {
            logger.log(Level.DEBUG, "Reached maximum depth, stopping crawl at: {0}", url);
            return;
        }

        try {
            logger.log(Level.DEBUG, "Started crawling webpage: {0}", url);

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

                // recursive crawl with decremented depth
                crawlHelper(currentLink, visitedUrls, maxDepth - 1);
            }
        } catch (IOException | URISyntaxException e) {
            logger.log(Level.WARNING, "Failed to crawl page: {0} - continuing with other pages", url, e);
        }
    }

}
