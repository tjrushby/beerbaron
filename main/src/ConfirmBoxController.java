import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ConfirmBoxController implements Initializable {
    @FXML private Label labelMessage;
    @FXML private Button buttonYes;
    @FXML private Button buttonNo;

    private boolean confirmed;

    private Stage confirmBoxStage;
    private String message;

    public ConfirmBoxController(Stage confirmBoxStage, String message) {
        this.confirmBoxStage = confirmBoxStage;
        this.message = message;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // set the Label text to the passed in message
        labelMessage.setText(message);

        // set confirmed to true and close the stage
        buttonYes.setOnAction(e -> {
            confirmed = true;
            confirmBoxStage.close();
        });

        // close the stage, confirmed is false by default
        buttonNo.setOnAction(e -> {
            confirmBoxStage.close();
        });
    }

    public boolean getConfirmed() {
        return confirmed;
    }
}
