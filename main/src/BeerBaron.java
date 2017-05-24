import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.time.LocalDate;

public class BeerBaron extends Application {
    private static final String PROGRAM_TITLE = "Beer Baron";
    private static final String PROGRAM_VERSION = "0.3";

    @Override
    public void init() throws Exception {
         DatabaseHelper dbHelper = new DatabaseHelper();
         dbHelper.setUpDatabase();

         // update the prices on launch if they haven't been updated today
         if(dbHelper.getLatestPriceCheckDate().isBefore(LocalDate.now())) {
             dbHelper.addPriceChecks();
         }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("BeerBaron.fxml"));
        primaryStage.setTitle(PROGRAM_TITLE + " " + PROGRAM_VERSION);
        primaryStage.setScene(new Scene(root, 1024, 768));
        primaryStage.show();
    }
}
