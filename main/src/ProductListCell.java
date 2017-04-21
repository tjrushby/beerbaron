import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;

public class ProductListCell extends ListCell<Product> {
    @FXML private HBox hbox;
    @FXML private Button buttonWeb;
    @FXML private Button buttonGraph;
    @FXML private Label labelProductName;
    @FXML private Label labelProductCurrentPrice;
    @FXML private Label labelProductAvgPrice;

    private FXMLLoader fxmlLoader;

    @Override
    protected void updateItem(Product product, boolean empty) {
        super.updateItem(product, empty);

        if(empty || product == null) {
            // this cell contains no data from the ObservableList so we'll make sure it stays blank
            // in order to avoid any graphical artifacts
            setText(null);
            setGraphic(null);
        } else {
            if(fxmlLoader == null) {
                // there is no FXMLLoader set for this cell so we need to instantiate one using the
                // correct .fxml file for this type of ListCell
                fxmlLoader = new FXMLLoader(getClass().getResource("/ProductListCell.fxml"));
                fxmlLoader.setController(this);

                try {
                    // load the layout hierarchy from the .fxml file we specified above
                    fxmlLoader.load();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }

            // add a css style class to the hbox containing everything
            hbox.getStyleClass().add("hbox");

            // set the text of the Label objects to the data we want to represent
            labelProductName.setText(product.getProductName());

            BigDecimal productCurrentPrice = product.getProductCurrentPrice();
            BigDecimal productAvgPrice = product.getProductAvgPrice();

            labelProductCurrentPrice.setText("$" + productCurrentPrice);
            labelProductAvgPrice.setText("$" + productAvgPrice);

            // compare the current price with the average price so we know what colour to make the text
            int higherOrLower = productCurrentPrice.compareTo(productAvgPrice);

            if(higherOrLower < 0) {
                // the current price is below the average price, set the appropriate css class
                // for labelProductCurrentPrice
                labelProductCurrentPrice.getStyleClass().add("label-below-average");
            } else if(higherOrLower > 0) {
                // the current price is above the average price, set the appropriate css class
                // for labelProductCurrentPrice
                labelProductCurrentPrice.getStyleClass().add("label-above-average");
            }

            // set up our buttons
            Image iconWeb = new Image(getClass().getResourceAsStream("res/icon-web.png"));

            buttonWeb.setGraphic(new ImageView(iconWeb));
            buttonWeb.getStyleClass().add("res/icon-button");

            buttonWeb.setOnAction(e -> {
                try{
                    Desktop.getDesktop().browse(new URI("https://www.danmurphys.com.au/product/" + product.getProductId()));
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            });

            Image iconGraph = new Image(getClass().getResourceAsStream("res/icon-graph.png"));

            buttonGraph.setGraphic(new ImageView(iconGraph));
            buttonGraph.getStyleClass().add("icon-button");

            // draw the HBox containing our Label objects in this cell
            setGraphic(hbox);
        }
    }
}
