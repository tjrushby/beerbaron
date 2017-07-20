package views;

import controllers.ViewAddNewProductController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Product;

import java.io.IOException;
import java.util.List;

public class ViewAddNewProduct {
    private static final String FORM_ADD_NEW_PRODUCT_TITLE = "Add New Product";

    private boolean addedProduct;

    // displays the View and returns a boolean indicating whether a new product(s) was added or not
    public boolean display(List<Product> existingProducts) {
        Stage formStage = new Stage();
        formStage.setResizable(false);
        formStage.setTitle(FORM_ADD_NEW_PRODUCT_TITLE);

        // set modality so that the user can only interact with this window
        formStage.initModality(Modality.APPLICATION_MODAL);

        // use a custom constructor for the Controller Class
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../fxml/ViewAddNewProduct.fxml"));

            fxmlLoader.setControllerFactory(controllerClass -> {
                if(controllerClass == ViewAddNewProductController.class) {
                    return new ViewAddNewProductController(existingProducts);
                } else {
                    try {
                        return controllerClass.newInstance();
                    } catch(Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            Parent root = fxmlLoader.load();
            formStage.setScene(new Scene(root, 640, 480));

            // check if a product was added to the database when the window is closed
            formStage.setOnCloseRequest(e -> {
                ViewAddNewProductController controller = fxmlLoader.getController();
                addedProduct = controller.getAddedProduct();
            });

            // wait until the window is closed before running more code
            formStage.showAndWait();
        } catch(IOException e) {
            e.printStackTrace();
        }

        // at least one product was added, return true to update the ListView in views.BeerBaron
// no products were added, return false to not update the ListView in views.BeerBaron
        return addedProduct;
    }
}
