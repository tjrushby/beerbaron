import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

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

            // styling for buttonWeb
            buttonWeb.getStyleClass().addAll("button-icon", "button-web");

            // open the product's url in the desktop browser
            buttonWeb.setOnAction(e -> {
                try{
                    Desktop.getDesktop().browse(new URI("https://www.danmurphys.com.au/product/" + product.getProductId()));
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            });

            // styling for buttonGraph
            buttonGraph.getStyleClass().addAll("button-icon", "button-graph");

            // change the scene to view the graph of PriceCheck data for this product
            buttonGraph.setOnAction(e -> {
                Scene thisScene = this.getScene();
                Stage thisStage = (Stage) thisScene.getWindow();

                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("ViewPriceCheckGraph.fxml"));

                    // setControllerFactory so that when loader.load() is called it uses our constructor that allows
                    // us to pass in the product the graph is for, along with the current stage and scene so we can
                    // come back without accessing the database again for the list of products
                    loader.setControllerFactory(controllerClass -> {
                        if(controllerClass == ViewPriceCheckGraphController.class) {
                            return new ViewPriceCheckGraphController(product, thisStage, thisScene);
                        } else {
                            try {
                                return controllerClass.newInstance();
                            } catch(Exception ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    });

                    // update the Stage with the new Scene to display ViewPriceCheckGraph.fxml
                    thisStage.setScene(new Scene(loader.load(), thisScene.getWidth(), thisScene.getHeight()));
                } catch(IOException ex) {
                    ex.printStackTrace();
                }
            });

            // draw the HBox containing our FXML objects in this cell
            setGraphic(hbox);
        }
    }
}
