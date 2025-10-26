import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Download {

    public static void main(String[] args) {
        try {
            Document doc = Jsoup.connect("https://www.elenaparapounsky.com/").get(); // download the page HTML
            //System.out.println("Title: " + doc.title());

            Elements links = doc.select("a"); // extract all links

            // for each loop
            for (Element link : links) {
                System.out.printf("%s\n\t%s\n",
                        link.attr("link"), link.absUrl("href"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}