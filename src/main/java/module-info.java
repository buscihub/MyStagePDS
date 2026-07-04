module shareroomafam {
    requires transitive java.sql;
    requires transitive javafx.controls;
    requires transitive javafx.graphics;
    requires javafx.fxml;
    requires jakarta.mail;
    requires jdk.httpserver;

    exports pkgEntity;
    exports pkgBoundary;
    exports textmessage;
    exports pkgUtility;
    exports pkgServer;
}
