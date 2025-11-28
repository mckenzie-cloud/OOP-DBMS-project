package org.example.lostandfound;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Properties;

public class adminSceneController {

    public VBox pendingPost_vbox;

    private String target_user_fullname;
    private String target_user_email;
    private String target_user_contact_number;

    ArrayList<ItemInfo> infos = new ArrayList<ItemInfo>();

    @FXML
    protected void viewPendingReportsButton()
    {
        if (!pendingPost_vbox.getChildren().isEmpty())
        {
            return;
        }
        // Insert data into the database
        String url = "jdbc:postgresql://localhost:5433/lost_and_found_system";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "pass1234");

        // DB connection
        try (Connection conn = DriverManager.getConnection(url, props)) {
            // check if sr-code is already present
            String get_status_query = "SELECT item_id, description, location_of, date_of, image_attach, report_type, user_srcode FROM item_post WHERE status = ?";
            try (PreparedStatement get_status_pstmt = conn.prepareStatement(get_status_query))
            {
                get_status_pstmt.setString(1, "pending");

                // execute query
                try (ResultSet rs = get_status_pstmt.executeQuery())
                {
                    while (rs.next())
                    {
                        ItemInfo info = new ItemInfo(rs.getString("item_id"), rs.getString("description"), rs.getString("location_of"),
                                rs.getObject("date_of", LocalDate.class), rs.getString("image_attach"),
                                rs.getString("report_type"), rs.getString("user_srcode"));
                        infos.add(info);
                    }
                    rs.close();
                    get_status_pstmt.close();
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


        for (int i=0; i<infos.size(); i++)
        {
            String btn_name = "view report";
            LocalDate report_date = infos.get(i).date_of;

            int report_date_year = report_date.getYear();
            int report_date_month = report_date.getMonthValue();
            int report_date_day = report_date.getDayOfMonth();
            pendingPost_vbox.getChildren().add(new Label(String.format("Date reported: %d/%d/%d", report_date_month, report_date_day, report_date_year)));
            Button btn = new Button(btn_name);
            btn.setId(String.valueOf(i));
            btn.setOnAction(e -> {

                Button clicked_btn = (Button) e.getSource();
                int btn_id = Integer.parseInt(clicked_btn.getId());
                ItemInfo info = infos.get(btn_id);


                // ------------------------get user full name ------------------------------------
                try (Connection conn = DriverManager.getConnection(url, props))
                {
                    String get_user_query = "SELECT user_fullname, user_email, user_contact_number FROM user_info WHERE sr_code = ?";
                    try (PreparedStatement get_user_pstmt = conn.prepareStatement(get_user_query))
                    {
                        get_user_pstmt.setString(1, info.target_user_srcode);

                        // execute query
                        try (ResultSet rs = get_user_pstmt.executeQuery())
                        {
                            if (rs.next())
                            {
                                this.target_user_fullname = rs.getString("user_fullname");
                                this.target_user_email = rs.getString("user_email");
                                this.target_user_contact_number = rs.getString("user_contact_number");
                            }
                            rs.close();
                            get_user_pstmt.close();
                        } catch (SQLException exc)
                        {
                            System.out.println("Error: " + exc.getMessage());
                        }
                    } catch (SQLException exc)
                    {
                        System.out.println("Error: " + exc.getMessage());
                    }
                } catch (SQLException exc)
                {
                    System.out.println("Error: " + exc.getMessage());
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource("viewReportScene.fxml"));
                Parent root = null;

                try {
                    root = loader.load();

                    viewReportSceneController viewReportCont = loader.getController();
                    viewReportCont.item_report_id = info.item_id;
                    viewReportCont.targetDescription_TA.setText(info.description);
                    viewReportCont.targetLoc_TA.setText(info.location_of);
                    viewReportCont.target_item_reportType_txt.setText(info.report_type + " item");
                    viewReportCont.targetDate_dt.setValue(info.date_of);
                    viewReportCont.target_user_fullname_txt.setText(this.target_user_fullname);
                    viewReportCont.target_user_srcode_txt.setText(info.target_user_srcode);
                    viewReportCont.target_user_email_txt.setText(this.target_user_email);
                    viewReportCont.target_user_contact_txt.setText(this.target_user_contact_number);

                    String imageUrl = "http://localhost:9000/bsu-system/" + info.image_attach;
                    System.out.println(imageUrl);

                    Image image = new Image(imageUrl);
                    viewReportCont.targetImage_IMV.setImage(image);
                    viewReportCont.targetImage_IMV.setFitWidth(300);
                    viewReportCont.targetImage_IMV.setFitHeight(200);
                    viewReportCont.targetImage_IMV.setPreserveRatio(true);

                    // Get the stage information from the button's source node
                    Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            pendingPost_vbox.getChildren().add(btn);
        }
    }

    @FXML
    protected void onLogoutButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("loginScene.fxml"));
        Parent root = loader.load();

        // Get the stage information from the button's source node
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
