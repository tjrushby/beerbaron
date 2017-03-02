public class Product {
    private String productId;
    private String productName;
    private String productAvgPrice;

    public Product(String id, String name) {
        this.productId = id;
        this.productName = name;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductAvgPrice() {
        return productAvgPrice;
    }

    public String toString() {
        return "productId: " + productId + "\n" +
               "productName: " + productName + "\n" +
               "productAvgPrice: " + productAvgPrice;
    }
}