# Crawly

**This project is still under construction.**

A Java-based web crawler for downloading websites to browse offline.

## Usage

**Prerequisites:** Java 11 or higher must be installed and available in your system PATH or JAVA_HOME environment variable.

### Run on Windows (Easiest)

Download the application from Releases, and double-click `Crawly.exe` to launch. A console window will open and prompt you to enter a website URL and a save location.

- **Entering a URL is required.**

- Entering a save location is optional; if you don't specify one, the app will default to `output` in the current directory.

### Command Line Mode (with arguments)

You can also pass arguments directly:

```bash
Crawly.exe <url> [save-location]
```

Examples:

```bash
Crawly.exe http://example.com
Crawly.exe http://example.com ./my-downloads
```

## Current Features

- Connect to and parse websites using JSoup
- Download HTML content to local files
- Recursive crawling with depth limit
- Download and save images locally
- Rewrite URLs in HTML to point to local files
- Domain-restricted crawling (stays within original domain)

## Planned Features

- Configurable crawl depth
- GUI

## Technologies Used

- **Java 25** - Core programming language
- **JSoup 1.17.2** - HTML parsing and web scraping
- **Maven** - Dependency management and build tool
- **Launch4j** - Java executable wrapper for Windows
