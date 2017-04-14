import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class ProductParser {
    private static final String url = "https://www.danmurphys.com.au/product/";

    // returns a Document object for the requested page
    public Document parseProductPage(String productId) {
        Document doc = null;

        try {
            // attempt to connect to the url for this product, set timeout to 10s to ensure we connect
            doc = Jsoup.connect(url + productId).timeout(10*1000).get();
        } catch(IOException e) {
            e.printStackTrace();
        }

        return doc;
    }

    // parses the product name from the corresponding Document object. Used when creating a new product
    // from the File menu to avoid user error by inputting the wrong product name with the product id
    public String parseProductName(Document doc) {
        String productName = "";

        // select the product name from the doc
        Elements metaElements = doc.select("meta[property=og:title]");

        if(metaElements.size() > 0) {
            // if we selected anything at all there will only be one element so we can safely assume that
            // it is the data we're after
            productName = metaElements.get(0).text();
        }

        return productName;
    }

    // parses the product price from the corresponding Document object
    public String parseProductPrice(Document doc) {
        String productPrice = "";

        // select the product price from the doc
        Elements prices = doc.select(".prdtlewgt-priceval");

        if (prices.size() > 0) {
            // the carton price will be in the first selection unless there is a four
            // or six-pack special on the page
            productPrice = prices.get(0).text();

            // remove the $ character so we can check if we parsed the carton price
            String[] priceSplit = productPrice.split("\\$");
            productPrice = priceSplit[1];

            // a carton is incredibly unlikely to be under the 'if value', and a four or six-pack is unlikely
            // to be over it so we can tell what we've parsed
            if (Double.parseDouble(productPrice) < 31) {
                // we didn't parse the carton price from the first selection so it will be in
                // the second selection
                priceSplit = prices.get(1).text().split("\\$|\\s");
                productPrice = priceSplit[1];
            }
        }

        return productPrice;
    }
}
