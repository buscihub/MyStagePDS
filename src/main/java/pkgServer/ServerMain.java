package pkgServer;

public class ServerMain {
    public static void main(String[] args) {
        System.out.println("Avvio del Server Backend MyStage (Standalone)...");
        
        // Avvia il server HTTP sulla porta 8080
        WebServerManager.getInstance().startServer();
        
        System.out.println("Server Backend avviato con successo e in ascolto.");
    }
}
