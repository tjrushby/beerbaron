import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.jsoup.nodes.Document;

import java.net.URL;
import java.util.ResourceBundle;

public class ViewAddNewProductController implements Initializable {
    @FXML private Button buttonSearchProduct;
    @FXML private Button buttonAddProduct;

    @FXML private GridPane gridPane;

    @FXML private Label labelMessage;
    @FXML private Label labelProductId;
    @FXML private Label labelProductName;
    @FXML private Label labelProductCurPrice;

    @FXML private TextField tfProductId;

    private boolean addedProduct;

    private ProductParser parser;
    private ProgressBox progBox;

    private String productId;
    private String productName;
    private String productCurPrice;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        parser = new ProductParser();
        progBox = new ProgressBox();

        // we haven't added a new product because the window just opened
        addedProduct = false;

        // set on action methods for the buttons
        buttonSearchProduct.setOnAction(e -> buttonSearchProductClicked());
        buttonAddProduct.setOnAction(e -> buttonAddProductClicked());

        // disable buttonSearchProduct if tfProductId is empty
        buttonSearchProduct.setDisable(true);

        tfProductId.textProperty().addListener(((observable, oldValue, newValue) -> {
            if(observable.getValue().equals("")) {
                buttonSearchProduct.setDisable(true);
            } else {
                buttonSearchProduct.setDisable(false);
            }
        }));
    }

    // adds a new row to the Product table and PriceCheck table for the parsed product
    private void buttonAddProductClicked() {
        DatabaseHelper dbHelper = new DatabaseHelper();

        // hide the product details
        gridPane.setVisible(false);

        // display a ProgressBox to the user whilst database operations are performed
        progBox.display("Adding Product", "Adding " + productName + " to database...");

        // run database operations on another Thread
        Task<Boolean> addTask = new Task<Boolean>() {
           @Override
           protected Boolean call() throws Exception {
                // add the product to the Product table and add a row to the PriceCheck table for this product
                if (dbHelper.addProduct(productId, productName) &&
                    dbHelper.addPriceCheck(productId, productCurPrice)) {

                    addedProduct = true;
                    return true;
                } else {
                    // there was an error adding the product to the database
                    return false;
                }
           }
        };

        addTask.setOnSucceeded(e -> {
            // hide the ProgressBox now the Task has succeeded
            progBox.dismiss();

            if (addTask.getValue()) {
                // display a success message to the user
                labelMessage.setText("Successfully added " + productName);
                labelMessage.getStyleClass().clear();
                labelMessage.getStyleClass().add("label-success");

                // clear tfProductId so the user doesn't need to if they want to add another product
                tfProductId.clear();
            } else {
                // display an error message to the user
                labelMessage.setText("There was an error adding " + productName);
                labelMessage.getStyleClass().clear();
                labelMessage.getStyleClass().add("label-error");

                // disable buttonAddProduct so the user can't repeatedly try and add the product
                buttonAddProduct.setDisable(true);
            }
        });

        // run the Task
        new Thread(addTask).start();
    }

    // searches for the entered product id and parses data for it if found
    private void buttonSearchProductClicked() {
        productId = tfProductId.getText();

        // display a ProgressBox to the user whilst parsing operations are performed
        progBox.display("Searching Products", "Searching for " + productId + "...");

        // run parsing operations on another Thread
        Task<Boolean> searchTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                Document doc = parser.parseProductPage(productId);
                productName = parser.parseProductName(doc);

                if(!productName.equals("")) {
                    // found a product for productId, parse the price
                    productCurPrice = parser.parseProductPrice(doc);
                    return true;
                } else {
                    // there is no product for productId
                    return false;
                }
            }
        };

        searchTask.setOnSucceeded(e -> {
            // hide the ProgressBox now the Task has succeeded
            progBox.dismiss();

            if(searchTask.getValue()) {
                // hide the error label in case it was displayed previously
                labelMessage.getStyleClass().clear();
                labelMessage.getStyleClass().add("label-hidden");

                // display the product data to the user
                labelProductId.setText(productId);
                labelProductName.setText(productName);
                labelProductCurPrice.setText(productCurPrice);

                // display the GridPane where we will show parsed product data
                gridPane.setVisible(true);

                // enable buttonAddProduct so the user can add this product to the database if they wish
                buttonAddProduct.setDisable(false);
            } else {
                // there is no product with the given id, hide the GridPane in case it is displaying any product data
                gridPane.setVisible(false);

                // disable buttonAddProduct because there is no product to add to the database
                buttonAddProduct.setDisable(true);

                // display an error message to the user
                labelMessage.setText("Error. No Product found for id " + productId);
                labelMessage.getStyleClass().clear();
                labelMessage.getStyleClass().add("label-error");
            }
        });

        // run the Task
        new Thread(searchTask).start();
    }

    public boolean getAddedProduct() {
        return addedProduct;
    }
}
