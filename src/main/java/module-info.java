module org.example.votingsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.example.votingsystem to javafx.fxml;
    exports org.example.votingsystem;
}