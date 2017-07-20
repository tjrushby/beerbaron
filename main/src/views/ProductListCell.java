package views;

import controllers.ProductListCellController;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import models.Product;

import java.io.IOException;

public class ProductListCell extends ListCell<Product> {
    private ListView listView;
    private FXMLLoader fxmlLoader;

    public ProductListCell(ListView listView) {
        this.listView = listView;
    }

    @Override
    protected void updateItem(Product product, boolean empty) {
        super.updateItem(product, empty);

        if (empty || product == null) {
            // this cell contains nothing so don't render anything in it
            setText(null);
            setGraphic(null);
        } else {
            fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("../fxml/ProductListCell.fxml"));

            // setControllerFactory so we can instantiate controllers.SearchListCellController with product
            fxmlLoader.setControllerFactory(controllerClass -> {
                if (controllerClass == ProductListCellController.class) {
                    return new ProductListCellController(this.getScene(), this, product, listView);
                } else {
                    try {
                        return controllerClass.newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            // render the FXML layout in this ListCell
            try {
                setGraphic(fxmlLoader.load());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
