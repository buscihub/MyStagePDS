module mystage {
    requires transitive java.sql;
    requires transitive javafx.controls;
    requires transitive javafx.graphics;
    requires javafx.fxml;
    requires jakarta.mail;
    requires jdk.httpserver;
    requires java.desktop;

    exports pkgEntity;
    exports pkgBoundary;
    exports pkgTextmessage;
    exports pkgUtility;
    exports pkgServer;
    exports pkgMain;
    exports pkgControl;
    
    opens pkgControl to javafx.fxml;
}
