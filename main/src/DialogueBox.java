import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class DialogueBox {
    private FXMLLoader fxmlLoader;

    // displays the the progress box
    public void display(String title, String message) {
        Stage dialogueBoxStage = new Stage();
        dialogueBoxStage.setResizable(false);
        dialogueBoxStage.setTitle(title);

        // set modality so that the user can't interact with the underlying stage
        dialogueBoxStage.initModality(Modality.APPLICATION_MODAL);

        try {
            fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("DialogueBox.fxml"));

            // setControllerFactory so that DialogueBoxController is instantiated with dialogueBoxStage and message
            fxmlLoader.setControllerFactory(controllerClass -> {
                if(controllerClass == DialogueBoxController.class) {
                    return new DialogueBoxController(dialogueBoxStage, message);
                } else {
                    try {
                        return controllerClass.newInstance();
                    } catch(Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            dialogueBoxStage.setScene(new Scene(fxmlLoader.load(), 320, 120));

            // wait until the window is closed before running more code
            dialogueBoxStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
