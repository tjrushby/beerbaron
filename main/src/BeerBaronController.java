import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class BeerBaronController implements Initializable {
    @FXML
    private ListView<Product> listView;

    private ObservableList<Product> products;

    public BeerBaronController() {
        SQLiteInitializer sqLiteInitializer = new SQLiteInitializer();
        products = sqLiteInitializer.getAllProducts();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // retrieve a list of Product objects using data from the Products table and
        // populate the ListView with it
        listView.setItems(products);

        // tell the CellFactory to use ProductListCell instead of the default
        listView.setCellFactory(productListView -> new ProductListCell());
    }
}
