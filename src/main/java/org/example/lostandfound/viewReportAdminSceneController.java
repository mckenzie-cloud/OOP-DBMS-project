package org.example.lostandfound;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.Properties;

public class viewReportAdminSceneController {
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
    public Button id_RejectReportButton;
    public Button id_ApproveReportButton;
    public Button id_ResolvedReportButton;
    public Label date_status_lbl;
    public TextField reported_item_post_id_txtField;


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

    public void onRejectReportButton(ActionEvent event) throws IOException {
        System.out.println("Report: " + item_report_id + " is rejected.");        // Declare Alert object for errors
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setHeaderText(null);

        //---------------- Db connection parameters  ------------------------
        String url = "jdbc:postgresql://localhost:5433/lost_and_found_system";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "pass1234");

        // DB connection
        try (Connection conn = DriverManager.getConnection(url, props))
        {
            //---------------- Retrieve User SR-CODE from this item_post ------------------------
            String get_srcode_query = "SELECT user_srcode FROM item_post WHERE item_id = ?";
            String user_srcode_from_item = null;

            try (PreparedStatement get_srcode_ptsmt = conn.prepareStatement(get_srcode_query))
            {
                get_srcode_ptsmt.setString(1, this.item_report_id);
                try (ResultSet rs = get_srcode_ptsmt.executeQuery())
                {
                    if (rs.next())
                    {
                        user_srcode_from_item = rs.getString("user_srcode");
                    }
                } catch (SQLException e)
                {
                    System.out.println("Error: " + e.getMessage());
                }
            }
            catch (SQLException e)
            {
                System.out.println("Error: " + e.getMessage());
            }

            //-------------------------- UPDATE USER INFO----------------------------------------------

            String update_user_info_notification = "UPDATE user_info SET user_notification = ? WHERE sr_code = ?";
            try (PreparedStatement update_notification_pstmt = conn.prepareStatement(update_user_info_notification))
            {
                String message = "The admin declined your report. Please ensure that your post is legitimate.";
                update_notification_pstmt.setString(1, message);
                update_notification_pstmt.setString(2, user_srcode_from_item);
                update_notification_pstmt.executeUpdate();
            }
            catch (SQLException e)
            {
                System.out.println("Error: " + e.getMessage());
            }

            //-------------------------- DELETE this Item Post ----------------------------------------------

            // Deleting the post in the record
            String delete_item_post_query = "DELETE FROM item_post WHERE item_id = ?";
            try (PreparedStatement delete_item_post_pstmt = conn.prepareStatement(delete_item_post_query))
            {
                delete_item_post_pstmt.setString(1, this.item_report_id);
                delete_item_post_pstmt.executeUpdate();
                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setContentText("The Item report post is successfully removed from the database records.");
                alert.show();
            }
            catch (SQLException e)
            {
                System.out.println("Error: " + e.getMessage());
            }
        }
        catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // return to admin dashboard
        FXMLLoader loader = new FXMLLoader(getClass().getResource("adminScene.fxml"));
        Parent root = loader.load();

        // Get the stage information from the button's source node
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void onApproveReportButton(ActionEvent event) throws SQLException, IOException {
        System.out.println("Report: " + item_report_id + " is approved.");
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setHeaderText(null);

        //---------------- Db connection parameters  ------------------------
        String url = "jdbc:postgresql://localhost:5433/lost_and_found_system";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "pass1234");


        // DB connection
        try (Connection conn = DriverManager.getConnection(url, props))
        {
            //---------------- Retrieve User SR-CODE from this item_post -----------------------

            String get_srcode_query = "SELECT user_srcode FROM item_post WHERE item_id = ?";
            String user_srcode_from_item = null;

            try (PreparedStatement get_srcode_ptsmt = conn.prepareStatement(get_srcode_query))
            {
                get_srcode_ptsmt.setString(1, this.item_report_id);
                try (ResultSet rs = get_srcode_ptsmt.executeQuery())
                {
                    if (rs.next())
                    {
                        user_srcode_from_item = rs.getString("user_srcode");
                        System.out.println(user_srcode_from_item);
                    }
                } catch (SQLException e)
                {
                    System.out.println("Error: " + e.getMessage());
                }
            }
            catch (SQLException e)
            {
                System.out.println("Error: " + e.getMessage());
            }

            //-------------------------- UPDATE Item Post ------------------------------------------------

            String update_status_query = "UPDATE item_post SET status = ?, date_posted = ? WHERE item_id = ?";
            try (PreparedStatement update_status_pstmt = conn.prepareStatement(update_status_query))
            {
                update_status_pstmt.setString(1, "approved");
                update_status_pstmt.setObject(2, LocalDate.now());
                update_status_pstmt.setString(3, this.item_report_id);
                update_status_pstmt.executeUpdate();
            }
            catch (SQLException e)
            {
                System.out.println("Error: " + e.getMessage());
            }

            //-------------------------- UPDATE USER INFO------------------------------------------------

            String update_user_info_notification = "UPDATE user_info SET user_notification = ? WHERE sr_code = ?";
            try (PreparedStatement update_notification_pstmt = conn.prepareStatement(update_user_info_notification))
            {
                String message = "The admin approved your report. You post is now added to View report post feed.";
                update_notification_pstmt.setString(1, message);
                update_notification_pstmt.setString(2, user_srcode_from_item);
                update_notification_pstmt.executeUpdate();
            }
            catch (SQLException e)
            {
                System.out.println("Error: " + e.getMessage());
            }
        }
        catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        alert.setAlertType(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText("You approved the reported item post.");
        alert.show();

        // return to admin dashboard
        FXMLLoader loader = new FXMLLoader(getClass().getResource("adminScene.fxml"));
        Parent root = loader.load();

        // Get the stage information from the button's source node
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void onResolvedReportButton(ActionEvent event) throws IOException {

        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setHeaderText(null);

        //---------------- Db connection parameters  ------------------------
        String url = "jdbc:postgresql://localhost:5433/lost_and_found_system";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "pass1234");

        // DB connection
        try (Connection conn = DriverManager.getConnection(url, props))
        {
            //---------------- Retrieve User SR-CODE from this item_post ------------------------

            String get_srcode_query = "SELECT user_srcode FROM item_post WHERE item_id = ?";
            String user_srcode_from_item = null;

            try (PreparedStatement get_srcode_ptsmt = conn.prepareStatement(get_srcode_query))
            {
                get_srcode_ptsmt.setString(1, this.item_report_id);
                try (ResultSet rs = get_srcode_ptsmt.executeQuery())
                {
                    if (rs.next())
                    {
                        user_srcode_from_item = rs.getString("user_srcode");
                    }
                } catch (SQLException e)
                {
                    System.out.println("Error: " + e.getMessage());
                }
            }
            catch (SQLException e)
            {
                System.out.println("Error: " + e.getMessage());
            }

            //-------------------------- UPDATE USER INFO----------------------------------------------

            String update_user_info_notification = "UPDATE user_info SET user_notification = ? WHERE sr_code = ?";
            try (PreparedStatement update_notification_pstmt = conn.prepareStatement(update_user_info_notification))
            {
                String message = "Your report will be forwarded to the Office of Student Affairs and Services or to the Supreme Student Council.";
                update_notification_pstmt.setString(1, message);
                update_notification_pstmt.setString(2, user_srcode_from_item);
                update_notification_pstmt.executeUpdate();
            }
            catch (SQLException e)
            {
                System.out.println("Error: " + e.getMessage());
            }

            //-------------------------- DELETE this Item Post ----------------------------------------------

            // Deleting the post in the record
            String delete_item_post_query = "DELETE FROM item_post WHERE item_id = ?";
            try (PreparedStatement delete_item_post_pstmt = conn.prepareStatement(delete_item_post_query))
            {
                delete_item_post_pstmt.setString(1, this.item_report_id);
                delete_item_post_pstmt.executeUpdate();
                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setContentText("The Item report post is successfully removed from the database records.");
                alert.show();
            }
            catch (SQLException e)
            {
                System.out.println("Error: " + e.getMessage());
            }
        }
        catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // return to adminDashboard
        FXMLLoader loader = new FXMLLoader(getClass().getResource("adminScene.fxml"));
        Parent root = loader.load();

        // Get the stage information from the button's source node
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
