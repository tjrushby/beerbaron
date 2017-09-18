package models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PriceCheck {
    private String productId;
    private LocalDate priceCheckDateTime;
    private BigDecimal priceCheckPrice;

    public PriceCheck(String productId, LocalDate priceCheckDateTime, BigDecimal priceCheckPrice) {
        this.productId = productId;
        this.priceCheckDateTime = priceCheckDateTime;
        this.priceCheckPrice = priceCheckPrice.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public String getProductId() {
        return productId;
    }

    public LocalDate getPriceCheckDateTime() {
        return priceCheckDateTime;
    }

    public BigDecimal getPriceCheckPrice() {
        return priceCheckPrice;
    }

    public String toString() {
        return "productID: " + productId + "\n" +
               "priceCheckDateTime: " + priceCheckDateTime + "\n" +
               "priceCheckPrice: " + priceCheckPrice;
    }
}
