package pkgEntity;

public class VisualizzazioneDto {
    private String nomeVisualizzatore;
    private String cognomeVisualizzatore;
    private String emailVisualizzatore;
    private String dataVisualizzazione;

    public VisualizzazioneDto(String nome, String cognome, String email, String data) {
        this.nomeVisualizzatore = nome;
        this.cognomeVisualizzatore = cognome;
        this.emailVisualizzatore = email;
        this.dataVisualizzazione = data;
    }

    public String getNomeVisualizzatore() {
        return nomeVisualizzatore;
    }

    public String getCognomeVisualizzatore() {
        return cognomeVisualizzatore;
    }

    public String getEmailVisualizzatore() {
        return emailVisualizzatore;
    }

    public String getDataVisualizzazione() {
        return dataVisualizzazione;
    }
}
