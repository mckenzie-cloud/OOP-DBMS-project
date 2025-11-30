package org.example.lostandfound;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Properties;

public class adminSceneController {

    public ScrollPane pendingReports_scrollPane;
    public ScrollPane unresolvedReports_ScrollPane;
    public TextField resolved_reported_item_post_txtField;

    private String target_user_fullname;
    private String target_user_email;
    private String target_user_contact_number;

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

    @FXML
    protected void onViewPendingReportsButton()
    {
        // Insert data into the database
        String url = "jdbc:postgresql://localhost:5433/lost_and_found_system";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "pass1234");

        ArrayList<ItemInfo> infos = new ArrayList<ItemInfo>();

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
                                rs.getObject("date_of", LocalDate.class), null, rs.getString("image_attach"),
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

        VBox pendingPost_vbox = new VBox();
        for (int i=0; i<infos.size(); i++)
        {
            VBox sub_vbox = new VBox();

            String btn_name = "view report";
            LocalDate report_date = infos.get(i).date_of;

            int report_date_year = report_date.getYear();
            int report_date_month = report_date.getMonthValue();
            int report_date_day = report_date.getDayOfMonth();

            Label date_lbl = new Label(String.format("Date reported: %d/%d/%d", report_date_month, report_date_day, report_date_year));

            Button btn = new Button(btn_name);
            btn.setId(String.valueOf(i));

            TextArea description_Txtarea = new TextArea();
            description_Txtarea.setText(infos.get(i).description);
            description_Txtarea.setPrefWidth(270);
            description_Txtarea.setPrefHeight(100);
            description_Txtarea.setWrapText(true);

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

                FXMLLoader loader = new FXMLLoader(getClass().getResource("viewReportAdminScene.fxml"));
                Parent root = null;

                try {
                    root = loader.load();

                    viewReportAdminSceneController viewReportCont = loader.getController();
                    viewReportCont.item_report_id = info.item_id;
                    viewReportCont.reported_item_post_id_txtField.setText(info.item_id);
                    viewReportCont.date_status_lbl.setText("Date Reported:");
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

                    viewReportCont.id_ResolvedReportButton.setDisable(true);

                    // Get the stage information from the button's source node
                    Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            sub_vbox.getChildren().addAll(date_lbl, description_Txtarea, btn);
            sub_vbox.setSpacing(3);
            sub_vbox.setAlignment(Pos.CENTER);
            pendingPost_vbox.getChildren().add(sub_vbox);
        }
        pendingPost_vbox.setSpacing(10);
        pendingPost_vbox.setPadding(new Insets(0, 5, 0, 5));
        pendingReports_scrollPane.setContent(pendingPost_vbox);
        pendingReports_scrollPane.setPannable(true);
    }

    @FXML
    public void onCheckUnresolvedReportsButton(ActionEvent event) {

        System.out.println("clciasicaisi");

        // Insert data into the database
        String url = "jdbc:postgresql://localhost:5433/lost_and_found_system";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "pass1234");

        ArrayList<ItemInfo> infos = new ArrayList<ItemInfo>();

        // DB connection
        try (Connection conn = DriverManager.getConnection(url, props)) {
            // check if sr-code is already present
            String get_status_query = "SELECT item_id, description, location_of, date_of, date_posted, image_attach, report_type, user_srcode FROM item_post WHERE status = ?";
            try (PreparedStatement get_status_pstmt = conn.prepareStatement(get_status_query))
            {
                get_status_pstmt.setString(1, "approved");

                // execute query
                try (ResultSet rs = get_status_pstmt.executeQuery())
                {
                    while (rs.next())
                    {
                        ItemInfo info = new ItemInfo(rs.getString("item_id"), rs.getString("description"), rs.getString("location_of"),
                                rs.getObject("date_of", LocalDate.class), rs.getObject("date_posted", LocalDate.class), rs.getString("image_attach"),
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

        VBox unresolvedPost_vbox = new VBox();
        for (int i=0; i<infos.size(); i++)
        {
            LocalDate date_posted = infos.get(i).date_posted;   // timeframe will be 1-week
            LocalDate timeframe = date_posted.plusWeeks(1);
            // LocalDate date_now = LocalDate.now();
            LocalDate specific_date = LocalDate.of(2025, 12, 7); // for debugging

            if (specific_date.isEqual(timeframe) || specific_date.isAfter(timeframe))
            {
                VBox sub_vbox = new VBox();

                String btn_name = "view report";

                int report_date_posted_year = date_posted.getYear();
                int report_date_posted_month = date_posted.getMonthValue();
                int report_date_posted_day = date_posted.getDayOfMonth();

                Label date_lbl = new Label(String.format("Date posted: %d/%d/%d", report_date_posted_year, report_date_posted_month, report_date_posted_day));

                Button btn = new Button(btn_name);
                btn.setId(String.valueOf(i));

                TextArea description_Txtarea = new TextArea();
                description_Txtarea.setText(infos.get(i).description);
                description_Txtarea.setPrefWidth(270);
                description_Txtarea.setPrefHeight(100);
                description_Txtarea.setWrapText(true);

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

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("viewReportAdminScene.fxml"));
                    Parent root = null;

                    try {
                        root = loader.load();

                        viewReportAdminSceneController viewReportCont = loader.getController();
                        viewReportCont.item_report_id = info.item_id;
                        viewReportCont.date_status_lbl.setText("Date Posted:");
                        viewReportCont.reported_item_post_id_txtField.setText(info.item_id);
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

                        // disable approve/reject button
                        viewReportCont.id_RejectReportButton.setDisable(true);
                        viewReportCont.id_ApproveReportButton.setDisable(true);

                        // Get the stage information from the button's source node
                        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.show();

                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                sub_vbox.getChildren().addAll(date_lbl, description_Txtarea, btn);
                sub_vbox.setSpacing(3);
                sub_vbox.setAlignment(Pos.CENTER);
                unresolvedPost_vbox.getChildren().add(sub_vbox);
            }
        }
        unresolvedPost_vbox.setSpacing(10);
        unresolvedPost_vbox.setPadding(new Insets(0, 5, 0, 5));
        unresolvedReports_ScrollPane.setContent(unresolvedPost_vbox);
        unresolvedReports_ScrollPane.setPannable(true);
    }

    public void onMarkResolvedReportedItemPost(ActionEvent event) throws IOException {

        String item_report_id = resolved_reported_item_post_txtField.getText();
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
                get_srcode_ptsmt.setString(1, item_report_id);
                try (ResultSet rs = get_srcode_ptsmt.executeQuery())
                {
                    if (rs.next())
                    {
                        user_srcode_from_item = rs.getString("user_srcode");
                    } else {
                        alert.setAlertType(Alert.AlertType.WARNING);
                        alert.setTitle("Warning");
                        alert.setContentText("Reported Item Post not found.");
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

            //-------------------------- UPDATE USER INFO----------------------------------------------

            String update_user_info_notification = "UPDATE user_info SET user_notification = ? WHERE sr_code = ?";
            try (PreparedStatement update_notification_pstmt = conn.prepareStatement(update_user_info_notification))
            {
                String message = "Your reported item post is resolved. Reported Item Post will be removed in the database.";
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
                delete_item_post_pstmt.setString(1, item_report_id);
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
