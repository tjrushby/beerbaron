import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class ProductListCell extends ListCell<Product> {
    @FXML private HBox hbox;
    @FXML private Label labelProductName;
    @FXML private Label labelProductAvgPrice;

    private FXMLLoader fxmlLoader;

    @Override
    protected void updateItem(Product product, boolean empty) {
        super.updateItem(product, empty);

        if(empty || product == null) {
            // this cell contains no data from the ObservableList so we'll make sure it stays blank
            // in order to avoid any graphical artifacts
            setText(null);
            setGraphic(null);
        } else {
            if(fxmlLoader == null) {
                // there is no FXMLLoader set for this cell so we need to instantiate one using the
                // correct .fxml file for this type of ListCell
                fxmlLoader = new FXMLLoader(getClass().getResource("/ProductListCell.fxml"));
                fxmlLoader.setController(this);

                try {
                    // actually load the layout hierarchy from the .fxml file we specified above
                    fxmlLoader.load();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }

            // set the text of the Label objects to the data we want to represent
            labelProductName.setText(product.getProductName());
            labelProductAvgPrice.setText(product.getProductAvgPrice().toString());

            // draw the HBox containing our Label objects in this cell
            setGraphic(hbox);
        }
    }
}
