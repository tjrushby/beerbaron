import java.math.BigDecimal;

public class Product {
    private String productId;
    private String productName;
    private BigDecimal productAvgPrice;

    public Product(String productId, String productName) {
        this.productId = productId;
        this.productName = productName;
    }

    public Product(String productId, String productName, BigDecimal productAvgPrice) {
        this.productId = productId;
        this.productName = productName;
        this.productAvgPrice = productAvgPrice;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getProductAvgPrice() {
        return productAvgPrice;
    }

    public String toString() {
        return "productId: " + productId + "\n" +
               "productName: " + productName + "\n" +
               "productAvgPrice: " + productAvgPrice;
    }
}