package controllers;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import models.Product;
import network.SearchParser;
import sqlite.DatabaseHelper;
import views.ProgressBox;
import views.SearchListCell;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ViewAddNewProductController implements Initializable {
    @FXML private Button buttonSearch;
    @FXML private Button buttonAdd;
    @FXML private Label labelMessage;
    @FXML private ListView<Product> listView;
    @FXML private TextField tfSearch;

    private boolean addedProduct;

    private List<Product> existingProducts;
    private ProgressBox progBox;

    public ViewAddNewProductController(List<Product> existingProducts) {
        this.existingProducts = existingProducts;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        progBox = new ProgressBox();

        // bind the disableProperty for the Button objects to their respective conditions
        buttonAdd.disableProperty().bind(Bindings.isEmpty(listView.getSelectionModel().getSelectedItems()));
        buttonSearch.disableProperty().bind(Bindings.isEmpty(tfSearch.textProperty()));

        // fire buttonSearch using the ENTER key in tfSearch
        tfSearch.setOnKeyPressed(keyInput -> {
            if(keyInput.getCode().equals(KeyCode.ENTER)) {
                buttonSearch.fire();
            }
        });

        // set on action methods for the buttons
        buttonSearch.setOnAction(e -> searchProducts());
        buttonAdd.setOnAction(e -> addProducts());

        // set the CellFactory for listView
        listView.setCellFactory(searchListView -> new SearchListCell());

        // allow the user to select multiple products in listView
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    // adds a new row to the Product table and the PriceCheck table for the parsed product
    private void addProducts() {
        DatabaseHelper dbHelper = new DatabaseHelper();
        List<Product> addedProductsList = new ArrayList<>();

        // set up a Task to run database operations
        Task<Boolean> addTask = new Task<Boolean>() {
           @Override
           protected Boolean call() throws Exception {
               boolean addedProductToDatabase, addedPriceCheck, error = false;

               // add the product to the Product table and add a row to the PriceCheck table for this product
               for(Product p : listView.getSelectionModel().getSelectedItems()) {
                   addedProductToDatabase = dbHelper.addProduct(p.getProductId(), p.getProductName());
                   addedPriceCheck = dbHelper.addPriceCheck(p.getProductId(), p.getProductCurrentPrice().toString());

                   if(addedProductToDatabase && addedPriceCheck) {
                       if (addedProduct != true) {
                           // added a product to the database
                           addedProduct = true;
                       }

                       // add p to addedProductsList so it can be removed from listView once addTask has completed
                       addedProductsList.add(p);
                   } else {
                       // there was an error adding this product to the database
                       error = true;
                   }
               }

               // successfully added all selected product(s) to the database, return true
// there was an error adding one or more of the selected product(s) to the database, return false
               return !error;
           }
        };

        addTask.setOnSucceeded(e -> {
            // hide the views.ProgressBox now the Task has succeeded
            progBox.dismiss();

            if (addTask.getValue()) {
                // display a success message to the user
                labelMessage.getStyleClass().clear();
                labelMessage.getStyleClass().add("label-success");
                labelMessage.setText("Successfully added product(s) to the database.");

                // clear tfSearch so the user doesn't need to if they want to search for another term
                tfSearch.clear();

                // remove the added product(s) from listView, clear selections
                listView.getItems().removeAll(addedProductsList);
                listView.getSelectionModel().clearSelection();
            } else {
                // display an error message to the user
                labelMessage.getStyleClass().clear();
                labelMessage.getStyleClass().add("label-error");
                labelMessage.setText("There was an error adding one or more products to the database.");
            }
        });

        // display a views.ProgressBox to the user whilst database operations are performed
        progBox.display("Adding Product", "Adding product(s) to the database...");

        // run database operations on another Thread
        new Thread(addTask).start();
    }

    // searches for the text in tfSearch and parses data for it if found
    private void searchProducts() {
        String searchTerm = tfSearch.getText();

        // set up a Task to perform parsing operations
        Task<List<Product>> searchTask = new Task<List<Product>>() {
            @Override
            protected List<Product> call() throws Exception {
                // search for the contents of tfSearch
                return new SearchParser().parseSearchPage(searchTerm);
            }
        };

        searchTask.setOnSucceeded(e -> {
            tfSearch.selectAll();

            List<Product> results = searchTask.getValue();
            List<Product> resultsToRemove = new ArrayList<>();

            for(Product p : results) {
                if(existingProducts.contains(p)) {
                    resultsToRemove.add(p);
                }
            }

            results.removeAll(resultsToRemove);

            if(!results.isEmpty()) {
                // found results that are not in the local database, bind the listView to the new List<Product>
                listView.setItems(FXCollections.observableArrayList(results));

                // scroll to the top of listView
                listView.scrollTo(0);

                labelMessage.setText("Results found for '" + tfSearch.getText() + "'. " +
                                     "\nProducts already in the database are not displayed");
                labelMessage.getStyleClass().clear();
                labelMessage.getStyleClass().add("label-success");
            } else {
                // no results were found that are not in the local database, clear listView in case it has previous
                // results displayed
                listView.getItems().clear();

                // display a message to the user
                labelMessage.setText("No results found for '" + tfSearch.getText() + "' that are not already in the database.");
                labelMessage.getStyleClass().clear();
                labelMessage.getStyleClass().add("label-error");
            }

            // hide progBox
            progBox.dismiss();
        });

        // display a views.ProgressBox to the user whilst parsing operations are performed
        progBox.display("Searching Products", "Searching for " + searchTerm + "...");

        // run parsing operations on another Thread
        new Thread(searchTask).start();
    }

    public boolean getAddedProduct() {
        return addedProduct;
    }
}
