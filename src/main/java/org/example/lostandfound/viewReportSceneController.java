package org.example.lostandfound;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class viewReportSceneController {
    public ImageView targetImage_IMV;
    public TextArea targetDescription_TA;
    public TextArea targetLoc_TA;
    public Text target_user_fullname_txt;
    public Text target_user_srcode_txt;
    public DatePicker targetDate_dt;
    public Text target_item_reportType_txt;
    public Text target_user_email_txt;
    public Text target_user_contact_txt;

    public String item_report_id;


    @FXML
    protected void onReturnAdmin_button(ActionEvent event) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("adminScene.fxml"));
        Parent root = loader.load();
        // Get the stage information from the button's source node
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void onRejectReportButton(ActionEvent event) {
        System.out.println("Report: " + item_report_id + " is rejected.");
    }

    public void onApproveReportButton(ActionEvent event) {
        System.out.println("Report: " + item_report_id + " is approved.");
    }
}
