import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ViewRemoveProductController implements Initializable {
    @FXML private Button buttonDelete;
    @FXML private ComboBox<Product> comboBox;
    @FXML private Label labelMessage;

    private boolean removedProduct;

    private ArrayList<Product> products;

    public ViewRemoveProductController(ArrayList<Product> products) {
        this.products = products;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // populate comboBox with the same products from the ListView in BeerBaronController
        comboBox.setItems(FXCollections.observableArrayList(products));

        // disable the button until an item is selected in comboBox
        buttonDelete.setDisable(true);
        comboBox.setOnAction(e -> buttonDelete.setDisable(false));

        // set onAction for buttonDelete
        buttonDelete.setOnAction(e -> {
            // get the selected Product
            Product product = comboBox.getSelectionModel().getSelectedItem();

            if (new ConfirmBox().display(
                    "Remove Product?",
                    "Are you sure you want to remove " + product.getProductName() + "?\nThis action cannot be undone.")) {
                ProgressBox progBox = new ProgressBox();
                progBox.display("Removing Product", "Removing " + product.getProductName() + " from database...");

                // run database operations on another Thread
                Task<Boolean> removeTask = new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        if(new DatabaseHelper().removeProductById(product.getProductId())) {
                            // the Product was removed from the database, update removedProduct to reflect this so that
                            // BeerBaronController knows to refresh the ListView once this stage is closed
                            removedProduct = true;
                            return true;
                        } else {
                            // there was an error removing the Product from the database
                            return false;
                        }
                    }
                };

                removeTask.setOnSucceeded(t -> {
                    // hide the ProgressBox now the Task has succeeded
                    progBox.dismiss();

                    if(removeTask.getValue()) {
                        // clear the selection from comboBox
                        comboBox.getSelectionModel().clearSelection();

                        // remove the product from products so it can't be selected again
                        products.remove(product);
                        comboBox.setItems(FXCollections.observableArrayList(products));

                        // display a message to the user informing them that the product has been deleted
                        labelMessage.setText("Removed " + product.getProductName() + " from the database.");
                        labelMessage.getStyleClass().clear();
                        labelMessage.getStyleClass().add("label-success");
                        labelMessage.setVisible(true);
                    } else {
                        // there was an error removing the selected product from the database, display a message to the user
                        labelMessage.setText("There was an error removing " + product.getProductName() + " from the database. Try again.");
                        labelMessage.getStyleClass().clear();
                        labelMessage.getStyleClass().add("label-error");
                        labelMessage.setVisible(true);
                    }
                });

                // run the Task
                new Thread(removeTask).start();
            }
        });
    }

    public boolean getRemovedProduct() {
        return removedProduct;
    }
}
