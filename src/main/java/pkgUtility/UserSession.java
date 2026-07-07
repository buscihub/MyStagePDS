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
        this.stanzaSelezionata = null;
    }

    private Integer stanzaSelezionata;

    public Integer getStanzaSelezionata() {
        return stanzaSelezionata;
    }

    public void setStanzaSelezionata(Integer stanzaSelezionata) {
        this.stanzaSelezionata = stanzaSelezionata;
    }

    private String emailInVerifica;
    private String azioneVerifica;

    public String getEmailInVerifica() { return emailInVerifica; }
    public void setEmailInVerifica(String email) { this.emailInVerifica = email; }

    public String getAzioneVerifica() { return azioneVerifica; }
    public void setAzioneVerifica(String azione) { this.azioneVerifica = azione; }
    
    private java.util.Map<String, Object> sessionCache = new java.util.HashMap<>();
    
    public void saveToCache(String key, Object data) {
        sessionCache.put(key, data);
    }
    
    public Object retrieveFromCache(String key) {
        return sessionCache.get(key);
    }
    
    public void clearCache(String key) {
        sessionCache.remove(key);
    }
}
