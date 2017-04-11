import java.math.BigDecimal;

public class Product {
    private String productId;
    private String productName;
    private BigDecimal productCurrentPrice;
    private BigDecimal productAvgPrice;

    public Product(String productId, String productName) {
        this.productId = productId;
        this.productName = productName;
    }

    public Product(String productId, String productName, BigDecimal productCurrentPrice, BigDecimal productAvgPrice) {
        this.productId = productId;
        this.productName = productName;
        this.productCurrentPrice = productCurrentPrice;
        this.productAvgPrice = productAvgPrice;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getProductCurrentPrice() { return productCurrentPrice; }

    public BigDecimal getProductAvgPrice() {
        return productAvgPrice;
    }

    public String toString() {
        return "productId: " + productId + "\n" +
               "productName: " + productName + "\n" +
               "productCurrentPrice: " + productCurrentPrice + "\n" +
               "productAvgPrice: " + productAvgPrice;
    }
}
