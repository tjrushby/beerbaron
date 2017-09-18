package views;

import controllers.BeerBaronController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sqlite.DatabaseHelper;

import java.time.LocalDate;

public class BeerBaron extends Application {
    private static final String PROGRAM_TITLE = "Beer Baron";
    private static final String PROGRAM_VERSION = "1.0";

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
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxml/BeerBaron.fxml"));

        // setControllerFactory so we can instantiate the controllers.BeerBaronController with primaryStage
        fxmlLoader.setControllerFactory(controllerClass -> {
            if(controllerClass == BeerBaronController.class) {
                return new BeerBaronController(primaryStage);
            } else {
                try {
                    return controllerClass.newInstance();
                } catch(Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        primaryStage.setTitle(PROGRAM_TITLE + " " + PROGRAM_VERSION);
        primaryStage.setScene(new Scene(fxmlLoader.load(), 1024, 768));
        primaryStage.show();
    }
}
