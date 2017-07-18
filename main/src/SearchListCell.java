import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;

import java.io.IOException;

public class SearchListCell extends ListCell<Product> {
    private FXMLLoader fxmlLoader;

    @Override
    protected void updateItem(Product product, boolean empty) {
        super.updateItem(product, empty);

        if(empty || product == null) {
            // this cell contains nothing so don't render anything in it
            setText(null);
            setGraphic(null);
        } else {
            fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("SearchListCell.fxml"));

            // setControllerFactory so we can instantiate SearchListCellController with product
            fxmlLoader.setControllerFactory(controllerClass -> {
                if(controllerClass == SearchListCellController.class) {
                    return new SearchListCellController(product);
                } else {
                    try {
                        return controllerClass.newInstance();
                    } catch(Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            // render the FXML layout in this ListCell
            try {
                setGraphic(fxmlLoader.load());
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
