import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Set;
import java.io.File;
import java.io.IOException;

public class Download {
    static void createFile(Document webpage) {
        // create a file to store the downloaded content
        try {
            String file_name = webpage.location().replace("https://www.", ""); // remove invalid file name syntax

            System.out.println(file_name);
            File page_file = new File(file_name); // create file object

            if (page_file.createNewFile()) {           // try to create the file
                System.out.println("File created: " + page_file.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    static void crawl() {
        try {
            Document webpage = Jsoup.connect("https://www.elenaparapounsky.com/").get(); // download the page HTML
            createFile(webpage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        crawl();
    }


//    public static void main(String[] args) {
//        try {
//            Document webpage = Jsoup.connect("https://www.elenaparapounsky.com/").get(); // download the page HTML
//            //System.out.println("Title: " + webpage.title());
//
//            Elements links = webpage.select("a"); // extract all links
//
//            // for each loop
//            for (Element link : links) {
//                System.out.printf("%s\n", link.absUrl("href"));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}