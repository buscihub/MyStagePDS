package pkgBoundary;

public class EnteSPIDboundary {
    private static EnteSPIDboundary instance;

    private EnteSPIDboundary() {
        // Private constructor for Singleton
    }

    public static EnteSPIDboundary getInstance() {
        if (instance == null) {
            instance = new EnteSPIDboundary();
        }
        return instance;
    }

    public boolean queryVerificaEsistenzaAccountSPID(String ente, String email, String password) {
        // Mock implementation for testing purposes
        // In a real scenario, this would communicate with the actual SPID provider
        if (ente != null && email != null && password != null) {
            // Simulated fallback logic
            return email.endsWith("@spid.it") && password.equals("password123");
        }
        return false;
    }
}
