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

public class loginSceneController {
    public TextField login_srcode_tf;
    public PasswordField login_password_tf;

    @FXML
    protected void onLoginSubmitButton(ActionEvent event) throws IOException
    {
        // Declare Alert object for errors
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setHeaderText(null);

        String sr_code = login_srcode_tf.getText();
        String password = login_password_tf.getText();
        String user_full_name = "";
        String password_hash = "";
        String user_role = "";

        // Insert data into the database
        String url = "jdbc:postgresql://localhost:5433/lost_and_found_system";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "pass1234");

        try(Connection conn = DriverManager.getConnection(url, props)) {
            // check if sr-code is already present
            String check_user_query = "SELECT user_fullname, user_role, user_password_hash FROM user_info WHERE sr_code = ?";
            try (PreparedStatement check_user_pstmt = conn.prepareStatement(check_user_query))
            {
                check_user_pstmt.setString(1, sr_code);

                // execute query
                try (ResultSet rs = check_user_pstmt.executeQuery())
                {
                    if (rs.next()) {
                        password_hash = rs.getString("user_password_hash");
                        user_full_name = rs.getString("user_fullname");
                        user_role = rs.getString("user_role");
                    }
                    else {
                        alert.setAlertType(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setContentText("Invalid credentials");
                        alert.show();
                        return;
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
        }
        catch (SQLException e)
        {
            System.out.println("Error: " + e.getMessage());
        }

        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), password_hash);

        if (result.verified && user_role.equals("ordinary"))
        {
            alert.setAlertType(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setContentText("Login Successfully.");
            alert.show();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("userDashboardScene.fxml"));
            Parent root = loader.load();

            UserDashboardSceneController user_dashboard = loader.getController();
            user_dashboard.user_full_name_lbl.setText(user_full_name);
            user_dashboard.user_srcode_lbl.setText(sr_code);

            // Get the stage information from the button's source node
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } else if (result.verified && user_role.equals("superuser"))
        {
            alert.setAlertType(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setContentText("Welcome to Administrator Dashboard");
            alert.show();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("adminScene.fxml"));
            Parent root = loader.load();

            // Get the stage information from the button's source node
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
        else {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Invalid Credentials.");
            alert.show();
        }
    }
}
