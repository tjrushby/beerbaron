import javafx.collections.FXCollections;
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
            // get the id for the selected product and remove it from the database
            Product product = comboBox.getSelectionModel().getSelectedItem();

            DatabaseHelper dbHelper = new DatabaseHelper();
            if(dbHelper.removeProductById(product.getProductId())) {
                // the product was removed from the database, update removedProduct to reflect this so that
                // BeerBaronController knows to refresh the ListView once this stage is closed
                removedProduct = true;

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
    }

    public boolean getRemovedProduct() {
        return removedProduct;
    }
}
