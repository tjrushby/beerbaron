import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ProgressBox {
    private Stage progressBoxStage;

    // displays the the progress box
    public void display(String title, String message) {
        progressBoxStage = new Stage();
        progressBoxStage.setResizable(false);
        progressBoxStage.setTitle(title);

        // set modality so that the user can't interact with the underlying stage
        progressBoxStage.initModality(Modality.APPLICATION_MODAL);

        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("ProgressBox.fxml"));

            // setControllerFactory so that ProgressBoxController is instantiated with message
            fxmlLoader.setControllerFactory(controllerClass -> {
                if(controllerClass == ProgressBoxController.class) {
                    return new ProgressBoxController(message);
                } else {
                    try {
                        return controllerClass.newInstance();
                    } catch(Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            progressBoxStage.setScene(new Scene(fxmlLoader.load(), 320, 100));
            progressBoxStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dismiss() {
        progressBoxStage.hide();
    }
}
