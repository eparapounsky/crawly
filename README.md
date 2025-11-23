# Crawly

**This project is still under construction.**

A Java-based web crawler for downloading websites to browse offline.

## Usage

**Prerequisites:** Java 11 or higher must be installed and available in your system PATH or JAVA_HOME environment variable.

- **Entering a URL is required.**
- Entering a save location is optional; if you don't specify one, the app will default to `output` in the current directory.

### Download Options

You can download Crawly from the [Releases](../../releases) page in two formats:

1. **Crawly.jar** - Java JAR file (cross-platform)
2. **Crawly.exe** - Windows executable (Windows x86 users only)

### Option 1: Run the JAR File

Download `Crawly.jar` from Releases and run it using Java:

#### Interactive Mode

```bash
java -jar Crawly.jar
```

The application will prompt you to enter a website URL and save location.

#### Command Line Mode (with arguments)

```bash
java -jar Crawly.jar <url> [save-location]
```

Examples:

```bash
java -jar Crawly.jar http://example.com
java -jar Crawly.jar http://example.com ./downloads
```

### Option 2: Run the Windows Executable

Download `Crawly.exe` from Releases and double-click to launch. A console window will open and prompt you to enter a website URL and a save location.

#### Command Line Mode (with arguments)

You can also pass arguments directly to the executable:

```bash
Crawly.exe <url> [save-location]
```

Examples:

```bash
Crawly.exe http://example.com
Crawly.exe http://example.com ./downloads
```

### About the Windows Executable

The `crawly.exe` file was created using [Launch4j](https://launch4j.sourceforge.net/), a Java executable wrapper. Launch4j allows Java applications to be packaged as native Windows executables, providing a more user-friendly experience by eliminating the need to manually invoke Java from the command line. The executable automatically detects your Java installation and launches the application with the appropriate Java runtime.

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
