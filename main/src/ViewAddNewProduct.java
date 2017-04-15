import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewAddNewProduct {
    private static final String FORM_ADD_NEW_PRODUCT_TITLE = "Add New Product";

    private boolean addedProduct;

    private FXMLLoader fxmlLoader;

    // displays the View and returns a boolean indicating whether a new product(s) was added or not
    public boolean display() {
        Stage formStage = new Stage();
        formStage.setResizable(false);
        formStage.setTitle(FORM_ADD_NEW_PRODUCT_TITLE);

        // set modality so that the user can only interact with this window
        formStage.initModality(Modality.APPLICATION_MODAL);

        try {
            fxmlLoader = new FXMLLoader(getClass().getResource("ViewAddNewProduct.fxml"));
            Parent root = fxmlLoader.load();
            formStage.setScene(new Scene(root, 320, 240));

            // when the 'Add New Product' window is closed we want to check and see if a new product(s) was added
            // so that we can tell BeerBaronController to refresh the ListView or not via the return type
            formStage.setOnCloseRequest(e -> {
                ViewAddNewProductController controller = fxmlLoader.getController();
                addedProduct = controller.getAddedProduct();
            });

            // wait until the window is closed before running more code
            formStage.showAndWait();
        } catch(IOException e) {
            e.printStackTrace();
        }

        if(addedProduct) {
            // at least one product was added, return true so we update the ListView
            return true;
        } else {
            // no products were added, return false so we don't update the ListView for no reason
            return false;
        }
    }
}
