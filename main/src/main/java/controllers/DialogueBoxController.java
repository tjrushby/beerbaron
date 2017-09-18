package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class DialogueBoxController implements Initializable {
    @FXML private Button buttonOK;
    @FXML private Label labelMessage;

    private Stage dialogueBoxStage;
    private String message;

    public DialogueBoxController(Stage dialogueBoxStage, String message) {
        this.dialogueBoxStage = dialogueBoxStage;
        this.message = message;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // set the Label text to the passed in message
        labelMessage.setText(message);

        // close the stage on Button press
        buttonOK.setOnAction(e -> dialogueBoxStage.close());
    }
}
