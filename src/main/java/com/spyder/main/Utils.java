package com.spyder.main;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Utils {
    private static final int BUFFER_SIZE = 1024; // 1kb buffer size for downloadFile

    public static void downloadFile(String fileURL, String savePath) throws IOException {
        // use URI constructor for strict validation, then convert to URL to catch
        // malformed URLs early
        URL url;
        try {
            url = new URI(fileURL).toURL();
        } catch (URISyntaxException | IllegalArgumentException | MalformedURLException e) {
            throw new IOException("Invalid URL syntax: " + fileURL, e);
        }

        // try with resources to write data to file
        try (BufferedInputStream inputStream = new BufferedInputStream(url.openStream());
                FileOutputStream outputStream = new FileOutputStream(savePath)) {
            byte[] dataBuffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = inputStream.read(dataBuffer, 0, BUFFER_SIZE)) != -1) {
                outputStream.write(dataBuffer, 0, bytesRead);
            }
        }
    }

    // recursively delete a directory and its contents
    public static void deleteDirectory(File directory) {
        // safety check: prevent accidental deletion of external files
        String currentDirectory = System.getProperty("user.dir");
        if (!directory.getAbsolutePath().startsWith(currentDirectory)) {
            System.err.println(
                    "WARNING: Attempted to delete directory outside workspace: " + directory.getAbsolutePath());
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                deleteDirectory(file);
            }
            file.delete();
        }
    }

    // determines if a URL string likely ends with a filename
    public static boolean isFileURL(String urlString) {
        if (urlString == null || urlString.trim().isEmpty()) {
            return false;
        }

        try {
            // remove query and fragment
            String cleanUrl = urlString.split("[?#]")[0];

            // extract path
            String path;

            if (cleanUrl.contains("://")) {
                URL url = new URI(cleanUrl).toURL();
                path = url.getPath();
            } else {
                path = cleanUrl;
            }

            // empty path or just "/"
            if (path.isEmpty() || path.equals("/")) {
                return false;
            }

            // ends with slash indicates directory
            if (path.endsWith("/")) {
                return false;
            }

            String lastSegment = path.substring(path.lastIndexOf('/') + 1);

            // has file extension
            if (lastSegment.contains(".") && !lastSegment.endsWith(".")
                    && lastSegment.lastIndexOf('.') < lastSegment.length() - 1) {
                return true;
            }

            // no extension but doesn't look like common directory patterns
            return !lastSegment.matches("^(api|users|admin|public|static|assets|images|css|js|docs)$");

        } catch (URISyntaxException | IllegalArgumentException | MalformedURLException e) {
            // fallback: simple string analysis
            return urlString.matches(".*\\.[a-zA-Z0-9]{2,6}(?:[?#]|$)");
        }
    }

    public static boolean isValidUrl(String urlString) {
        // basic null/empty check
        if (urlString == null || urlString.trim().isEmpty()) {
            return false;
        }

        // use URI constructor for strict validation, then convert to URL to catch
        // malformed URLs early
        try {
            new URI(urlString).toURL();
            return true;
        } catch (URISyntaxException | MalformedURLException e) {
            return false;
        }
    }
}
