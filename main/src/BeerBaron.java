import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BeerBaron extends Application {
    private static final String PROGRAM_TITLE = "Beer Baron";
    private static final String PROGRAM_VERSION = "0.3";

    public static void main(String[] args) {
        SQLiteInitializer sqLiteInitializer = new SQLiteInitializer();
        sqLiteInitializer.checkForExistingDatabase();

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("BeerBaron.fxml"));
        primaryStage.setTitle(PROGRAM_TITLE + " " + PROGRAM_VERSION);
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }
}
