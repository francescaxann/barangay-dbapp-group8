import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        AnchorPane root = FXMLLoader.load(getClass().getResource("view/resident_view.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Barangay Residents CRUD");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
