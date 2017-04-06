import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class ProductParser {
    private String url;

    public ProductParser(String url) {
        this.url = url;
        parseProductPrice();
    }

    private void parseProductPrice() {
        String productID = "";
        String priceCheckPrice = "";

        SQLiteInitializer sqLiteInitializer = new SQLiteInitializer();

        try {
            // attempt to connect to the requested url
            Document doc = Jsoup.connect(url).get();

            // select the product price
            Elements prices = doc.select(".prdtlewgt-priceval");

            if(prices.size() > 0) {
                // the carton price will be in the first selection unless there is a four
                // or six-pack special on the page
                priceCheckPrice = prices.get(0).text();

                // remove the $ character so we can check if we parsed the carton price
                String[] priceSplit = priceCheckPrice.split("\\$");
                priceCheckPrice = priceSplit[1];

                // a carton is incredibly unlikely to be under the 'if value', and a four or six-pack is unlikely
                // to be over it so we can tell what we've parsed
                if(Double.parseDouble(priceCheckPrice) < 31) {
                    // if we didn't parse the carton price from the first selection then it will be in
                    // the second selection
                    priceSplit = prices.get(1).text().split("\\$|\\s");
                    priceCheckPrice = priceSplit[1];
                }

                System.out.println(priceCheckPrice);

                // get the productID from the end of the url
                String[] idSplit = url.split("/");
                productID = idSplit[4];
            }

            sqLiteInitializer.addPriceCheck(productID, priceCheckPrice);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
