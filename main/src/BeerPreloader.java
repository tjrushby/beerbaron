import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BeerPreloader extends Preloader {
    private Stage plStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = new FXMLLoader().load((getClass().getResource("fxml/BeerPreloader.fxml")));

        plStage = primaryStage;
        plStage.setScene(new Scene(root, 480, 360));
        plStage.show();
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        if(info.getType() == StateChangeNotification.Type.BEFORE_START) {
            // main application thread is ready so hide plStage
            plStage.hide();
        }
    }
}
