package pkgEntity;

public class StanzaEntity {
    private int idStanza;
    private String linkStanza;
    private String nomeStanza;
    private String codiceFiscaleArtista;

    public StanzaEntity(int idStanza, String codiceFiscaleArtista, String nomeStanza, String linkStanza) {
        this.idStanza = idStanza;
        this.codiceFiscaleArtista = codiceFiscaleArtista;
        this.nomeStanza = nomeStanza;
        this.linkStanza = linkStanza;
    }

    public int getIdStanza() {
        return idStanza;
    }

    public void setIdStanza(int idStanza) {
        this.idStanza = idStanza;
    }

    public String getLinkStanza() {
        return linkStanza;
    }

    public void setLinkStanza(String linkStanza) {
        this.linkStanza = linkStanza;
    }

    public String getNomeStanza() {
        return nomeStanza;
    }

    public void setNomeStanza(String nomeStanza) {
        this.nomeStanza = nomeStanza;
    }

    public String getCodiceFiscaleArtista() {
        return codiceFiscaleArtista;
    }

    public void setCodiceFiscaleArtista(String codiceFiscaleArtista) {
        this.codiceFiscaleArtista = codiceFiscaleArtista;
    }
}
