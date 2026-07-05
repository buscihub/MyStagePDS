package pkgUtility;

public class UserSession {
    private static UserSession instance;
    private String utenteLoggato;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public String getUtenteLoggato() {
        return utenteLoggato;
    }

    public void setUtenteLoggato(String cf) {
        this.utenteLoggato = cf;
    }

    public void logout() {
        this.utenteLoggato = null;
    }
}
