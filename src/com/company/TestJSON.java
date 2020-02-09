import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class TestJSON {


    /**Get JSON data from API
     * https://dzone.com/articles/how-to-parse-json-data-from-a-rest-api-using-simpl
     *
     * GitHub of Search Engine Connect to web server and print results using BufferedReader and InputStreamReader
     * https://github.com/BrentLeeSF/SearchEngine/blob/master/src/HTTPFetcher.java
     *
     *
     * BufferedReader vs Scanner
     * https://javahungry.blogspot.com/2018/12/difference-between-bufferedreader-and-scanner-in-java-examples.html
     *
     * https://www.geeksforgeeks.org/difference-between-scanner-and-bufferreader-class-in-java/
     * */


    public static void main(String[] args) {
        TestJSON js = new TestJSON();
        js.printStuff("http://homework.ad-juster.com/api/creatives");
    }

    public void printStuff(String getStuff) {

        try {

            URL url = new URL(getStuff); // same
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // same

            conn.setRequestMethod("GET"); //same
            conn.connect(); // why no connect?

            int responsecode = conn.getResponseCode(); // same

            if (responsecode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responsecode);

            } else {
                Scanner sc = new Scanner(url.openStream());

                while (sc.hasNext()) {
                    String inline = sc.nextLine();
                    System.out.println(inline);
                }
                sc.close();
            }

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
