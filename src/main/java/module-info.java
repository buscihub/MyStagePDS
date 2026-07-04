module shareroomafam {
    requires transitive java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.mail;
    requires jdk.httpserver;

    exports pkgEntity;
    exports pkgBoundary;
    exports textmessage;
    exports pkgUtility;
    exports pkgServer;
}
