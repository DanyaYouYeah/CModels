package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("mainForm.fxml"));
        primaryStage.setTitle("CModels - FEFU");
        root.getStylesheets().addAll(getClass().getResource("style.css").toExternalForm());
        primaryStage.setScene(new Scene(root, 1300, 520));
        primaryStage.setResizable(false);
        primaryStage.getIcons().addAll(new Image("sample/images/icon.png"));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
