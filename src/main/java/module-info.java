module org.example.lostandfound {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires bcrypt;
    requires minio;
    requires org.apache.commons.io;

    opens org.example.lostandfound to javafx.fxml;
    exports org.example.lostandfound;
}