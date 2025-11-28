package org.example.lostandfound;

import at.favre.lib.crypto.bcrypt.BCrypt;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class registerSceneController
{
    public TextField sr_code_tf;
    public TextField fullname_tf;
    public TextField email_tf;
    public TextField contact_number_tf;
    public PasswordField password_tf;
    public PasswordField confirm_password_tf;

    @FXML
    protected void onRegisterSubmitButton(ActionEvent event) throws IOException
    {
        // Declare Alert object for errors
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setHeaderText(null);

        // get user input
        String sr_code = sr_code_tf.getText();
        String fullname = fullname_tf.getText();
        String email = email_tf.getText();
        String contact_number = contact_number_tf.getText();
        String password = password_tf.getText();
        String confirm_password = confirm_password_tf.getText();

        if (sr_code.isBlank() || fullname.isBlank()
                || email.isBlank() || contact_number.isBlank()
                || password.isEmpty() || confirm_password.isBlank())
        {
            System.out.println("Fields is empty.");
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Missing value");
            alert.show();
            return;
        }

        // check length
        if (sr_code.length() != 8) // ex. srcode -> 21-61678 and contact number -> (09669711929)
        {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Invalid SR-code.");
            alert.show();
            return;
        }

        if (contact_number.length() != 11)
        {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Invalid contact number.");
            alert.show();
            return;
        }
        // check for verifications
        String bcryptHashPassword;
        if (!confirm_password.equals(password))
        {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Password don't match.");
            alert.show();
            return;
        } else {
            // Hashing the password
            bcryptHashPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        }

        //-----------------------------------------------------------------------------------


        // Insert data into the database
        String url = "jdbc:postgresql://localhost:5433/lost_and_found_system";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "pass1234");

        try(Connection conn = DriverManager.getConnection(url, props)){

            // check if sr-code is already present
            String check_user_query = "SELECT sr_code FROM user_info WHERE sr_code = ?";
            try (PreparedStatement check_user_pstmt = conn.prepareStatement(check_user_query))
            {
                check_user_pstmt.setString(1, sr_code);

                // execute query
                try (ResultSet rs = check_user_pstmt.executeQuery())
                {
                    if (rs.next()) {
                        alert.setAlertType(Alert.AlertType.WARNING);
                        alert.setTitle("Warning");
                        alert.setContentText("User already exist.");
                        alert.show();
                        return;
                    }
                    else {
                        String add_user_query = "INSERT INTO user_info(sr_code, user_fullname, user_email, user_contact_number, user_password_hash, user_role) " +
                                "VALUES(?, ?, ?, ?, ?, ?);";
                        try (PreparedStatement add_user_stmt = conn.prepareStatement(add_user_query))
                        {
                            add_user_stmt.setString(1, sr_code);
                            add_user_stmt.setString(2, fullname);
                            add_user_stmt.setString(3, email);
                            add_user_stmt.setString(4, contact_number);
                            add_user_stmt.setString(5, bcryptHashPassword);
                            add_user_stmt.setString(6, "ordinary");

                            add_user_stmt.executeUpdate();
                            alert.setAlertType(Alert.AlertType.INFORMATION);
                            alert.setTitle("Information");
                            alert.setContentText("Successfully Registered!");
                            alert.show();
                        } catch (SQLException e)
                        {
                            System.out.println("Error: " + e.getMessage());
                        }
                    }
                } catch (SQLException e)
                {
                    System.out.println("Error: " + e.getMessage());
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

        System.out.println("Register!!");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("userDashboardScene.fxml"));
        Parent root = loader.load();

        UserDashboardSceneController user_dashboard = loader.getController();
        user_dashboard.user_full_name_lbl.setText(fullname);
        user_dashboard.user_srcode_lbl.setText(sr_code);

        // Get the stage information from the button's source node
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
