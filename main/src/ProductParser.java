import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class ProductParser {
    private String url;

    public ProductParser(String url) {
        this.url = url;
        parseProductPrice();
    }

    private String parseProductPrice() {
        String product = "";
        String price = "";

        try {
            // attempt to connect to the requested url
            Document doc = Jsoup.connect(url).get();
            //System.out.println(doc);

            // select the product name
            Elements names = doc.select(".prdtlewgt-prodname");
            //System.out.println(names.size());

            if(names.size() > 0) {
                for(int i = 0; i < names.size(); i++) {
                    product = names.get(i).text();
                    System.out.println(product);
                }
            }

            // select the product price
            Elements prices = doc.select(".price-large");
            //System.out.println(prices.size());

            if(prices.size() > 0) {
                for(int i = 0; i < prices.size(); i++) {
                    price = prices.get(i).text();
                    System.out.println(price);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return price;
    }
}
