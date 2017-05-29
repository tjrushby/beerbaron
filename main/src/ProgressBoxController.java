import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class ProgressBoxController implements Initializable {
    @FXML private Label labelMessage;

    private String message;

    public ProgressBoxController(String message) {
        this.message = message;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // set labelMessage text
        labelMessage.setText(message);
    }
}
