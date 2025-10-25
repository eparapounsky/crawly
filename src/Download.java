import java.io.*;
import java.net.URL;
import java.net.URI;
import java.net.MalformedURLException;

public class Download {

    // Method to download a webpage
    public static void downloadWebPage(String webpage) {
        try {
            // Create a URI object and convert it to a URL
            URI uri = new URI(webpage);
            URL url = uri.toURL();

            // Open a stream to read the webpage content
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

            // Specify the filename to save the downloaded content
            BufferedWriter writer = new BufferedWriter(new FileWriter("DownloadedPage.html"));

            // Read each line from the stream and write it to the file
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine(); // Add a new line for better formatting
            }

            // Close the streams
            reader.close();
            writer.close();

            System.out.println("Webpage downloaded successfully as 'DownloadedPage.html'.");
        }
        // Handle malformed URL or URI exceptions
        catch (MalformedURLException | IllegalArgumentException e) {
            System.out.println("Error: The URL is invalid.");
        }
        // Handle IO exceptions
        catch (IOException e) {
            System.out.println("Error: Unable to download the webpage.");
        }
        // Handle URI syntax exceptions
        catch (Exception e) {
            System.out.println("Error: Invalid URI syntax.");
        }
    }

    // Main method
    public static void main(String[] args) {

        // URL of the webpage to download
        String url = "https://www.geeksforgeeks.org/";

        // Call the method to download the webpage
        downloadWebPage(url);
    }
}