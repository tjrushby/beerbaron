package controllers;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import models.Product;
import sqlite.DatabaseHelper;
import views.ConfirmBox;
import views.DialogueBox;
import views.ProductListCell;
import views.ProgressBox;

import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

public class ProductListCellController implements Initializable {
    @FXML private Hyperlink linkWeb;
    @FXML private Hyperlink linkGraph;
    @FXML private Hyperlink linkRemove;
    @FXML private Label labelProductName;
    @FXML private Label labelProductCurrentPrice;
    @FXML private Label labelProductAvgPrice;

    private ProductListCell listCell;
    private ListView listView;
    private Product product;
    private ProgressBox progBox;
    private Scene currentScene;

    public ProductListCellController(Scene currentScene, ProductListCell listCell, Product product, ListView listView) {
        this.currentScene = currentScene;
        this.listCell = listCell;
        this.product = product;
        this.listView = listView;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        progBox = new ProgressBox();

        // set the Label text to display the product name and price
        labelProductName.setText(product.getProductName());

        BigDecimal productCurrentPrice = product.getProductCurrentPrice();
        BigDecimal productAvgPrice = product.getProductAvgPrice();

        labelProductCurrentPrice.setText("$" + productCurrentPrice);
        labelProductAvgPrice.setText("$" + productAvgPrice);

        // compare the current price with the average price so we know what colour to make the text
        int higherOrLower = productCurrentPrice.compareTo(productAvgPrice);

        // remove any previous style classes added to the list cell to prevent the wrong style being applied
        labelProductCurrentPrice.getStyleClass().removeAll("label-below-average", "label-above-average");

        if(higherOrLower < 0) {
            // the current price is below the average price, set css class
            labelProductCurrentPrice.getStyleClass().add("label-below-average");
        } else if(higherOrLower > 0) {
            // the current price is above the average price, set css class
            labelProductCurrentPrice.getStyleClass().add("label-above-average");
        }

        // open the product's url in the desktop browser
        linkWeb.setOnAction(e -> {
            try{
                Desktop.getDesktop().browse(new URI("https://www.danmurphys.com.au/product/" + product.getProductId()));
            } catch(Exception ex) {
                ex.printStackTrace();
            }

            // set visited to false so that the appearance of linkWeb remains the same
            linkWeb.setVisited(false);
        });

        // changes the scene to view the graph of PriceCheck data for this product
        linkGraph.setOnAction(e -> {
            Stage thisStage = (Stage) currentScene.getWindow();

            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/ViewPriceCheckGraph.fxml"));

                // setControllerFactory so that we can instantiate controllers.ViewPriceCheckGraphController with what it needs
                loader.setControllerFactory(controllerClass -> {
                    if(controllerClass == ViewPriceCheckGraphController.class) {
                        return new ViewPriceCheckGraphController(product, thisStage, currentScene);
                    } else {
                        try {
                            return controllerClass.newInstance();
                        } catch(Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                });

                // update the Stage with the new Scene to display ViewPriceCheckGraph.fxml
                thisStage.setScene(new Scene(loader.load(), currentScene.getWidth(), currentScene.getHeight()));
            } catch(IOException ex) {
                ex.printStackTrace();
            }

            // set visited to false so that the appearance of linkGraph remains the same
            linkGraph.setVisited(false);
        });

        // removes this product from the database and then from listView if successful
        linkRemove.setOnAction(e -> {
            if (new ConfirmBox().display(
                    "Remove Product?",
                    "Are you sure you want to remove " + product.getProductName() + "?\nThis action cannot be undone.")) {

                // set up a Task to run database operations
                Task<Boolean> removeTask = new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        // the Product was successfully removed from the database
// there was an error removing the Product from the database
                        return new DatabaseHelper().removeProductById(product.getProductId());
                    }
                };

                removeTask.setOnSucceeded(t -> {
                    // hide the views.ProgressBox now the Task has finished running
                    progBox.dismiss();

                    if (removeTask.getValue()) {
                        // remove the product from listView because it was successfully removed from the
                        // database and clear the selection in listView
                        listView.getItems().remove(listCell.getIndex());
                        listView.getSelectionModel().clearSelection();
                    } else {
                        new DialogueBox().display(
                                "Error",
                                "There was an error removing " + product.getProductName() + " from the database"
                        );
                    }
                });

                // display views.ProgressBox to the user whilst database operations are performed
                progBox.display("Removing Product", "Removing " + product.getProductName() + " from database...");

                // run database operations on another Thread
                new Thread(removeTask).start();
            }

            // set visited to false so that the appearance of linkRemove remains the same
            linkRemove.setVisited(false);
        });
    }
}
