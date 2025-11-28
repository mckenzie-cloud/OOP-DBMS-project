package org.example.lostandfound;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected  void onLoginButtonClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("loginScene.fxml"));
        Parent root = loader.load();
        // Get the stage information from the button's source node
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected  void onRegisterButtonClick(ActionEvent event) throws IOException {
        System.out.println("Register btn click");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("registerScene.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}