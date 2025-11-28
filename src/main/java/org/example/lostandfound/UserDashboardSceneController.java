package org.example.lostandfound;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class UserDashboardSceneController {

    public Label user_full_name_lbl;
    public Label user_srcode_lbl;

    @FXML
    protected void onReportLostItemButton(ActionEvent event) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("reportLostItemScene.fxml"));
        Parent root = loader.load();

        reportLostItemSceneController repLostCont = loader.getController();
        repLostCont.target_user_fullname = user_full_name_lbl.getText();
        repLostCont.target_user_srcode = user_srcode_lbl.getText();

        // Get the stage information from the button's source node
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void onReportFoundItemButton(ActionEvent event) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("reportFoundItemScene.fxml"));
        Parent root = loader.load();

        reportFoundItemSceneController repFoundCont = loader.getController();
        repFoundCont.target_user_fullname = user_full_name_lbl.getText();
        repFoundCont.target_user_srcode = user_srcode_lbl.getText();

        // Get the stage information from the button's source node
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void onViewItemButton(ActionEvent event) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("viewListOfReportedItemsScene.fxml"));
        Parent root = loader.load();
        // Get the stage information from the button's source node
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void onLogoutSubmitButton(ActionEvent event) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("loginScene.fxml"));
        Parent root = loader.load();
        // Get the stage information from the button's source node
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
