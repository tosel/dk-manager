package de.villigst.dk;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class FXMain extends Application {

    public static Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        try {
            URL url = new URL("file:src/de/villigst/dk/view/MainView.fxml");
            Parent root = FXMLLoader.load(url);
            //StackPane root = new StackPane();
            //root.getChildren().add(label);
            Scene scene = new Scene(root, 1000, 600);
            primaryStage.setTitle("DK-Manager");
            primaryStage.setScene(scene);
            primaryStage.show();
        }catch(IOException ex) {
            ex.printStackTrace();
        }
    }
}
