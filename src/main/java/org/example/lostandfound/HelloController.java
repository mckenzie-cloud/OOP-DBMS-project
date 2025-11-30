package org.example.lostandfound;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class HelloController {
    public ScrollPane target_ScrPane;
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

    public void onGenerateContentBtn(ActionEvent event) {
        VBox root = new VBox();
        for (int i=0; i<10; i++)
        {
            VBox newVBox = new VBox();
            TextArea txArea = new TextArea();
            txArea.setText("Ako siaskjasjkansjanskaskxnaxa");
            newVBox.getChildren().addAll(txArea, new Button("View report"));
            newVBox.setAlignment(Pos.CENTER);
            root.getChildren().add(newVBox);
            root.setSpacing(10);
        }
        target_ScrPane.setContent(root);
        target_ScrPane.setPannable(true);
    }
}