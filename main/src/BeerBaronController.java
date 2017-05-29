import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class BeerBaronController implements Initializable {
    @FXML private MenuBar menuBar;
    @FXML private MenuItem menuItemAddNewProduct;
    @FXML private MenuItem menuItemRemoveProduct;
    @FXML private MenuItem menuItemCheckPrices;
    @FXML private MenuItem menuItemExit;

    @FXML private TextField tfSearchProducts;

    @FXML private ListView<Product> listView;

    @FXML private Label labelLastUpdated;

    private ArrayList<Product> products;
    private ArrayList<Product> searchResults;

    private DatabaseHelper dbHelper;
    private LocalDate latestPriceCheckDate;

    private Stage stage;

    public BeerBaronController(Stage stage) {
        this.stage = stage;

        dbHelper = new DatabaseHelper();
        products = dbHelper.getAllProducts();
        searchResults = new ArrayList<>();
        latestPriceCheckDate = dbHelper.getLatestPriceCheckDate();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // setOnCloseRequest for stage to use ConfirmBox
        stage.setOnCloseRequest(e -> {
            if(!new ConfirmBox().display("Exit?", "Are you sure you want to exit?")) {
                // consume the close request
                e.consume();
            }
        });

        // set the on action methods for the menu items
        menuItemAddNewProduct.setOnAction(e -> {
            if(new ViewAddNewProduct().display()) {
                // if this returned true then at least one product was added, so refresh the ListView
                updateListView();
            }
        });

        menuItemRemoveProduct.setOnAction(e -> {
            if (new ViewRemoveProduct(products).display()) {
                // if this returned true than at least one product was removed, so refresh the ListView
                updateListView();
            }
        });

        menuItemCheckPrices.setOnAction(e -> {
            if (new ConfirmBox().display(
                    menuItemCheckPrices.getText() + "?",
                    "Are you sure? The prices were last checked on " + latestPriceCheckDate)) {
                ProgressBox progBox = new ProgressBox();

                // display a progress box whilst a price check is performed
                progBox.display("Fetching Prices");

                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        // perform a price check
                        dbHelper.addPriceChecks();
                        return null;
                    }

                    @Override
                    protected void succeeded() {
                        // hide the progress box now that call() is finished
                        progBox.dismiss();

                        // update listView with the new prices from the price check
                        updateListView();
                    }
                };

                new Thread(task).start();
            }
        });

        menuItemExit.setOnAction(e -> {
            if (new ConfirmBox().display(menuItemExit.getText() + "?", "Are you sure you want to exit?")) {
                Platform.exit();
            }
        });

        // add a listener for change in content on tfSearchProducts
        tfSearchProducts.textProperty().addListener(((observable, oldValue, newValue) -> {
            // clear any previous search results
            searchResults.clear();

            if(newValue == "") {
                // if we haven't searched for anything set the ListView to use the original products list
                listView.setItems(FXCollections.observableArrayList(products));
            } else {
                // linear search as it is only ever going to be a small list, ~50 elements at most
                // could change to binary search if we wanted an exact match for the search key instead of a contains
                // as the list is already sorted from the sql query used in DatabaseHelper
                for (Product product : products) {
                    if (product.getProductName().toLowerCase().contains(newValue.toLowerCase())) {
                        // product name matches the search text, add it to the results list
                        searchResults.add(product);
                    }
                }

                // set the ListView to use the search results
                listView.setItems(FXCollections.observableArrayList(searchResults));
            }
        }));

        // tell the CellFactory to use ProductListCell instead of the default
        listView.setCellFactory(productListView -> new ProductListCell());

        // populate listView with the list of products
        listView.setItems(FXCollections.observableArrayList(products));

        // set Label text to display the date of the last price check
        labelLastUpdated.setText("Prices last updated " + latestPriceCheckDate);
    }

    // updates the list of products from the database and binds it to the ListView
    private void updateListView() {
        products = dbHelper.getAllProducts();
        listView.setItems(FXCollections.observableArrayList(products));
    }
}
