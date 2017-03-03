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
            Elements prices = doc.select(".price-large");

            if(prices.size() > 0) {
                for(int i = 0; i < prices.size(); i++) {
                    priceCheckPrice = prices.get(i).text();

                    // remove the $ symbol for insertion in to the database later
                    String[] priceSplit = priceCheckPrice.split("\\$");
                    priceCheckPrice = priceSplit[1];

                    // get the productID from the end of the url
                    String[] idSplit = url.split("/");
                    productID = idSplit[4];
                }
            }

            sqLiteInitializer.addPriceCheck(productID, priceCheckPrice);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
