package org.example.lostandfound;

import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDate;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

public class reportLostItemSceneController {

    public TextArea description_textArea;
    public TextArea location_textArea;
    public Label imagePath_lbl;
    public DatePicker dateLost_datePicker;
    private String imageAbsolutePath = null;

    public String target_user_fullname = null;
    public String target_user_srcode = null;

    private static final String ALPHA_NUMERIC_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random random = new Random();

    @FXML
    protected void onLostSceneReturnButton(ActionEvent event) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("userDashboardScene.fxml"));
        Parent root = loader.load();

        UserDashboardSceneController user_dashboard = loader.getController();
        user_dashboard.user_full_name_lbl.setText(target_user_fullname);
        user_dashboard.user_srcode_lbl.setText(target_user_srcode);

        // Get the stage information from the button's source node
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void onAttachImageButton(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Attach Image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null)
        {
             this.imageAbsolutePath = selectedFile.getAbsolutePath();
             imagePath_lbl.setText(this.imageAbsolutePath);
        }
    }

    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(ALPHA_NUMERIC_CHARS.length());
            sb.append(ALPHA_NUMERIC_CHARS.charAt(randomIndex));
        }
        return sb.toString();
    }

    private String getImageIdName() throws IOException, NoSuchAlgorithmException, InvalidKeyException
    {
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint("http://127.0.0.1:9000/")
                        .credentials("minioadmin", "minioadmin")
                        .build();

        try {
            String bucket_name = "bsu-system";
            String image_name = this.target_user_srcode + generateRandomString(20) + ".jpg";

            boolean found = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucket_name).build()
            );

            if (found)
            {
                try {
                    minioClient.uploadObject(
                            UploadObjectArgs.builder().bucket(bucket_name)
                                    .object(image_name)
                                    .filename(this.imageAbsolutePath)
                                    .build()
                    );
                    System.out.println("Image successfully uploaded to local file storage.");
                    return image_name;
                } catch (Exception e)
                {
                    System.out.println(e.getMessage());
                }
            } else {
                System.out.println("No '" + bucket_name + "' found in the file storage.");
            }

        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private void InsertPostIntoDb(String description, String location, LocalDate date_lost, String img_id_name)
    {
        // Declare Alert object for errors
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setHeaderText(null);

        // Insert data into the database
        String url = "jdbc:postgresql://localhost:5433/lost_and_found_system";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "pass1234");

        try(Connection conn = DriverManager.getConnection(url, props)){

            String post_item_key = UUID.randomUUID().toString().replace("-", "");

            // check if sr-code is already present
            String add_post_item_query = "INSERT INTO item_post(item_id, description, location_of, date_of, date_posted, status, report_type, image_attach, user_srcode) " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?);";


            try (PreparedStatement add_user_stmt = conn.prepareStatement(add_post_item_query))
            {
                add_user_stmt.setString(1, post_item_key);
                add_user_stmt.setString(2, description);
                add_user_stmt.setString(3, location);
                add_user_stmt.setObject(4, date_lost);
                add_user_stmt.setObject(5, null);
                add_user_stmt.setString(6, "pending");
                add_user_stmt.setString(7, "lost");
                add_user_stmt.setString(8, img_id_name);
                add_user_stmt.setString(9, this.target_user_srcode);


                add_user_stmt.executeUpdate();
                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setContentText("Your post is successfully created. Your post will appear automatically in the 'View item page' once the admin approve your post");
                alert.show();
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

    public void onReportSubmitButton(ActionEvent event) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        // Declare Alert object for errors
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setHeaderText(null);

        String description = description_textArea.getText();
        String location = location_textArea.getText();
        LocalDate date_of = dateLost_datePicker.getValue();

        if (description.isBlank() || location.isBlank() ||
                this.imageAbsolutePath == null || date_of == null)
        {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Missing values.");
            alert.show();
        }
        else {
            String img_id_name =  getImageIdName();
            if (img_id_name != null)
            {
                InsertPostIntoDb(description, location, date_of, img_id_name);
            }
        }

    }
}
