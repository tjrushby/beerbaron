package network;

import models.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

public class SearchParser {
    private static final String url = "https://m.danmurphys.com.au/mdm/search/dm_search_results.jsp?searchterm=";

    // parses all the products from the search results page
    public List<Product> parseSearchPage(String searchTerm) {
        List<Product> products = new LinkedList();

        try {
            // attempt to connect to the search results page
            Document doc = Jsoup.connect(url + searchTerm).get();

            if (doc != null) {
                // successfully connected to the search results page, select the elements containing the products
                Elements prodInfo = doc.select(".prdtlewgt-prodinfo");

                if(prodInfo.size() > 0) {
                    for(Element product : prodInfo) {
                        // String.split to obtain the product id
                        String productId = product.select("a").attr("href");
                        String idSplit[] = productId.split("/");
                        productId = idSplit[4];

                        String productName = product.select("p.prdtlewgt-prodname").text();

                        // String.split to obtain the product price
                        String productPriceString = product.select("span.prdtlewgt-priceval").text();
                        String[] priceSplit = productPriceString.split("\\$|\\s");
                        productPriceString = priceSplit[1];

                        BigDecimal productPrice = new BigDecimal(productPriceString);

                        // add a new Product to the List
                        products.add(new Product(productId, productName, productPrice, productPrice));
                    }
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

        return products;
    }
}
