public class PriceCheck {
    private String productId;
    private String priceCheckDateTime;
    private String priceCheckPrice;

    public PriceCheck() {

    }

    public PriceCheck(String productId, String priceCheckDateTime, String priceCheckPrice) {
        this.productId = productId;
        this.priceCheckDateTime = priceCheckDateTime;
        this.priceCheckPrice = priceCheckPrice;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getPriceCheckDateTime() {
        return priceCheckDateTime;
    }

    public void setPriceCheckDateTime(String priceCheckDateTime) {
        this.priceCheckDateTime = priceCheckDateTime;
    }

    public String getPriceCheckPrice() {
        return priceCheckPrice;
    }

    public void setPriceCheckPrice(String priceCheckPrice) {
        this.priceCheckPrice = priceCheckPrice;
    }

    public String toString() {
        return "productID: " + productId + "\n" +
               "priceCheckDateTime: " + priceCheckDateTime + "\n" +
               "priceCheckPrice: " + priceCheckPrice;
    }
}
