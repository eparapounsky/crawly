# Crawly

A Java web crawler and scraper for creating offline website archives. Recursively downloads pages and images within a domain, rewrites links for local browsing, and runs with either command-line arguments or a GUI interface.

## Usage

**Prerequisites:** Java 11 or higher must be installed and available in your system PATH or JAVA_HOME environment variable.

- **Entering a URL is required.**
- Entering a save location is optional; if you don't specify one, the app will default to `output` in the current directory.

### Download Options

You can download Crawly from the [Releases](../../releases) page in two formats:

1. **crawly-jar-with-dependencies.jar** - Java JAR file (cross-platform)
2. **Crawly.exe** - Windows executable (Windows x86 users only)

### Option 1: Run the JAR File

Download `crawly-jar-with-dependencies.jar` from Releases and run it using Java:

#### Command Line Mode (with arguments)

```bash
java -jar crawly-jar-with-dependencies.jar <url> [save-location]
```

Examples:

```bash
java -jar crawly-jar-with-dependencies.jar http://example.com
java -jar crawly-jar-with-dependencies.jar http://example.com ./downloads
```

### Option 2: Run the Windows Executable

Download `Crawly.exe` from Releases and double-click to launch the GUI application.

### About the Windows Executable

The `crawly.exe` file was created using [Launch4j](https://launch4j.sourceforge.net/), a Java executable wrapper. Launch4j allows Java applications to be packaged as native Windows executables, providing a more user-friendly experience by eliminating the need to manually invoke Java from the command line. The executable automatically detects your Java installation and launches the application with the appropriate Java runtime.

## Current Features

- **Dual Interface**: Command-line and GUI modes
- **Domain Restriction**: Only follows links within the original domain to prevent external crawling
- **Offline Browsing**: Downloads HTML content and rewrites URLs to point to local files
- **Image Handling**: Downloads and saves images locally with centralized organization
- **Path Preservation**: Maintains website directory structure in local filesystem
- **Interruption Support**: GUI allows graceful start/stop of crawling operations

## Planned Features

- Crawling progress indicators
- Configurable crawl depth limits
- Enhanced file type support

## Technologies Used

- **Java 11+** - Core programming language (compiled for Java 11, tested with newer versions)
- **JSoup 1.17.2** - HTML parsing and web scraping
- **Maven** - Dependency management and build tool
- **Swing** - GUI framework for desktop interface
- **JUnit 5** - Testing framework with embedded HTTP server
- **Launch4j** - Java executable wrapper for Windows
