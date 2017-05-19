import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class BeerPreloaderController implements Initializable {
    @FXML private VBox vBox;
    @FXML private ProgressBar progressBar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // add a StyleClass to the VBox
        vBox.getStyleClass().add("vbox");

        // set progressBar to 'slide' infinitely
        progressBar.setProgress(-1.0f);
    }
}
