import com.sun.javafx.application.LauncherImpl;
import views.BeerBaron;

public class Main {
    public static void main(String[] args) {
        // launch our main Application class along with a Preloader class
        LauncherImpl.launchApplication(BeerBaron.class, BeerPreloader.class, args);
    }
}
