package com.spyder.test;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class MyHttpServerHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Define the path to your HTML file
        String path = exchange.getRequestURI().toASCIIString();
        if (path.equals("/")) {
            path = "./input/index.html";
        } else {
            path = "./input/" + path;
        }
        System.out.println("Test HttpServer URI: " + path);
        Path filePath = Paths.get(path); // Adjust this path to your file location
        if (Files.exists(filePath) && Files.isReadable(filePath)) {
            // Set response headers
            exchange.sendResponseHeaders(200, Files.size(filePath));
            exchange.getResponseHeaders().set("Content-Type", "text/html");

            // Write the file content to the response body
            try (OutputStream os = exchange.getResponseBody()) {
                Files.copy(filePath, os);
            }
        } else {
            // Handle file not found or unreadable
            String response = path + " - 404 (Not Found)";
            exchange.sendResponseHeaders(404, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}
