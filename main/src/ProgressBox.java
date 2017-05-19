import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ProgressBox {
    private Stage progressBoxStage;

    // displays the the progress box
    public void display(String title) {
        progressBoxStage = new Stage();
        progressBoxStage.setResizable(false);
        progressBoxStage.setTitle(title);

        // set modality so that the user can't interact with the underlying stage
        progressBoxStage.initModality(Modality.APPLICATION_MODAL);

        try {
            Parent root = new FXMLLoader(getClass().getResource("ProgressBox.fxml")).load();
            progressBoxStage.setScene(new Scene(root, 320, 100));
            progressBoxStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dismiss() {
        progressBoxStage.hide();
    }
}
