import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

import java.net.URL;
import java.util.ResourceBundle;

public class BeerBaronController implements Initializable {
    @FXML private MenuBar menuBar;
    @FXML private MenuItem menuItemCheckPrices;

    @FXML private ListView<Product> listView;

    private ObservableList<Product> products;
    private DatabaseHelper dbHelper;

    public BeerBaronController() {
        dbHelper = new DatabaseHelper();
        products = dbHelper.getAllProducts();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // retrieve a list of Product objects using data from the Products table and
        // populate the ListView with it
        listView.setItems(products);

        // tell the CellFactory to use ProductListCell instead of the default
        listView.setCellFactory(productListView -> new ProductListCell());

        // set the on action methods for the menu items
        menuItemCheckPrices.setOnAction(e -> {
            // perform a price check
            dbHelper.addPriceChecks();

            // update listView with the new prices from the price check
            listView.setItems(dbHelper.getAllProducts());
        });
    }
}
