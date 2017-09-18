package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import models.Product;

import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

public class SearchListCellController implements Initializable {
    @FXML private Hyperlink linkWeb;
    @FXML private Label productName;
    @FXML private Label productPrice;

    private Product product;

    public SearchListCellController(Product product) {
        this.product = product;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // set the Label text to display the product name and price
        productName.setText(product.getProductName());
        productPrice.setText("$" + product.getProductCurrentPrice().toString());

        // set the Hyperlink to go to the product's web page
        linkWeb.setOnAction(e -> {
            try{
                Desktop.getDesktop().browse(new URI("https://www.danmurphys.com.au/product/" + product.getProductId()));
            } catch(Exception ex) {
                ex.printStackTrace();
            }

            // set visited to false so that the appearance of linkWeb remains the same
            linkWeb.setVisited(false);
        });
    }
}
