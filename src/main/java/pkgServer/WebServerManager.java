package pkgServer;

import com.sun.net.httpserver.HttpServer;
import pkgServer.routes.ServerRouter;

import java.io.IOException;
import java.net.InetSocketAddress;

public class WebServerManager {
    private static WebServerManager instance;
    private HttpServer server;

    private WebServerManager() {}

    public static WebServerManager getInstance() {
        if (instance == null) {
            instance = new WebServerManager();
        }
        return instance;
    }

    public void startServer() {
        try {
            server = HttpServer.create(new InetSocketAddress(8080), 0);
            
            // Inizializza le rotte
            ServerRouter.initializeRoutes(server);
            
            server.setExecutor(null);
            server.start();
            System.out.println("Server started on port 8080");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        if (server != null) {
            server.stop(0);
            System.out.println("Server stopped");
        }
    }
}
