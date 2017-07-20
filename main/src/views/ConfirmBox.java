package views;

import controllers.ConfirmBoxController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ConfirmBox {
    private FXMLLoader fxmlLoader;

    // displays the the progress box
    public boolean display(String title, String message) {
        Stage confirmBoxStage = new Stage();
        confirmBoxStage.setResizable(false);
        confirmBoxStage.setTitle(title);

        // set modality so that the user can't interact with the underlying stage
        confirmBoxStage.initModality(Modality.APPLICATION_MODAL);

        try {
            fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("../fxml/ConfirmBox.fxml"));

            // setControllerFactory so that controllers.ConfirmBoxController is instantiated with confirmBoxStage and message
            fxmlLoader.setControllerFactory(controllerClass -> {
                if(controllerClass == ConfirmBoxController.class) {
                    return new ConfirmBoxController(confirmBoxStage, message);
                } else {
                    try {
                        return controllerClass.newInstance();
                    } catch(Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            confirmBoxStage.setScene(new Scene(fxmlLoader.load(), 320, 120));

            // wait until the window is closed before running more code
            confirmBoxStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // return the value of confirmed from controllers.ConfirmBoxController
        return ((ConfirmBoxController) fxmlLoader.getController()).getConfirmed();
    }
}
