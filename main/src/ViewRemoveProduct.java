import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class ViewRemoveProduct {
    private static final String FORM_TITLE = "Remove a Product";

    private ArrayList<Product> products;

    private boolean removedProduct;

    public ViewRemoveProduct(ArrayList<Product> products) {
        this.products = products;
    }

    public boolean display() {
        Stage formStage = new Stage();
        formStage.setResizable(false);
        formStage.setTitle(FORM_TITLE);

        // set modality so that the user can only interact with this window
        formStage.initModality(Modality.APPLICATION_MODAL);

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("ViewRemoveProduct.fxml"));

            // setControllerFactory so that we can pass in the ArrayList of Product objects to the controller instead of
            // accessing the database again for data we already have
            loader.setControllerFactory(controllerClass -> {
                if(controllerClass == ViewRemoveProductController.class) {
                    return new ViewRemoveProductController(products);
                } else {
                    try {
                        return controllerClass.newInstance();
                    } catch(Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            formStage.setScene(new Scene(loader.load(), 320, 240));

            // when the 'Remove Product' window is closed we want to check and see if a product(s) was removed
            // so that we can tell BeerBaronController to refresh the ListView or not via the return type
            formStage.setOnCloseRequest(e -> {
                ViewRemoveProductController controller = loader.getController();
                removedProduct = controller.getRemovedProduct();
            });

            // wait until the window is closed before running more code
            formStage.showAndWait();
        } catch(IOException e) {
            e.printStackTrace();
        }

        if(removedProduct) {
            // at least one product was removed, return true so we update the ListView
            return true;
        } else {
            // no products were removed, return false so we don't update the ListView
            return false;
        }
    }
}
