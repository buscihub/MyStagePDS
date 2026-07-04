package pkgServer.routes;

import com.sun.net.httpserver.HttpServer;
import pkgServer.controllers.StanzaBackendController;
import pkgServer.controllers.FileBackendController;

public class ServerRouter {
    public static void initializeRoutes(HttpServer server) {
        // Rotta per accedere alle informazioni e visualizzazione della stanza
        server.createContext("/stanza", new StanzaBackendController());
        
        // Rotta per la gestione e download dei file/documenti
        server.createContext("/files", new FileBackendController());
    }
}
