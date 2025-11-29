package com.spyder.test;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.spyder.main.Crawler;
import com.spyder.main.WebPageSaver;
import com.spyder.main.Utils;
import com.sun.net.httpserver.HttpServer;

public class CrawlerTest {

    private static final int TEST_PORT = 3333;
    private static final String TEST_URL = "http://localhost:" + TEST_PORT;
    private static File inputFile = null;
    private static File outputDirectory = null;
    private static HttpServer server = null;

    @BeforeAll
    public static void setup() throws Exception {

        server = HttpServer.create(new InetSocketAddress(TEST_PORT), 0);
        server.createContext("/", new MyHttpServerHandler()); // set handler to serve files from input directory
        server.start();

        // set input and output directories
        inputFile = new File("./input/index.html");
        System.out.println("Testing input at: " + inputFile.getAbsolutePath());
        Assertions.assertTrue(inputFile.exists(), inputFile.getAbsolutePath() + " doesn't exist!");

        outputDirectory = new File("./outputTest");
        Utils.deleteDirectory(outputDirectory); // clean in case test was killed and couldn't clean in @AfterAll
        outputDirectory.mkdirs();
        Assertions.assertTrue(outputDirectory.exists(),
                outputDirectory.getAbsolutePath() + " Failed to create output directory!");
    }

    @AfterAll
    public static void cleanUp() {
        if (server != null) {
            server.stop(0);
        }
        Utils.deleteDirectory(outputDirectory);
    }

    // recursively collect all files in a directory (relative to the root)
    private void getFiles(File directory, Set<String> collectedFiles) {
        File[] files = directory.listFiles();

        for (File file : files) {
            if (file.isDirectory()) {
                getFiles(file, collectedFiles);
            } else {
                String filePath = file.getPath();

                // skip "C:\" to get relative path from root
                int backslashIndex = filePath.indexOf('\\', 3);
                if (backslashIndex > 0) {
                    filePath = filePath.substring(filePath.indexOf('\\', 3));
                }

                // replace custom image folder name and input directory with "images" for
                // comparison
                filePath = filePath.replaceAll("\\\\(images|" + WebPageSaver.getImagesFolderName() + ")\\\\",
                        "\\\\images\\\\");

                // add relative file path to set
                collectedFiles.add(filePath);
            }
        }
    }

    @Test
    void testHttpServer() {
        String saveLocation = outputDirectory.getAbsolutePath();
        WebPageSaver downloader = new WebPageSaver(saveLocation);
        Crawler crawler = new Crawler(TEST_URL, downloader);
        crawler.crawl();

        Set<String> inputFiles = new HashSet<>();
        Set<String> outputFiles = new HashSet<>();
        getFiles(inputFile.getParentFile(), inputFiles);
        getFiles(outputDirectory, outputFiles);

        assertEquals(inputFiles, outputFiles, "Difference(s) found between input and output files.");
    }
}
