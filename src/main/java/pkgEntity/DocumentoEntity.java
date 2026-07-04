package pkgEntity;

public class DocumentoEntity {
    private int idDocumento;
    private boolean visibile;
    private String percorso;
    private String codiceFiscaleArtista;

    public DocumentoEntity(int idDocumento, String codiceFiscaleArtista, boolean visibile, String percorso) {
        this.idDocumento = idDocumento;
        this.codiceFiscaleArtista = codiceFiscaleArtista;
        this.visibile = visibile;
        this.percorso = percorso;
    }

    public int getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(int idDocumento) {
        this.idDocumento = idDocumento;
    }

    public boolean isVisibile() {
        return visibile;
    }

    public void setVisibile(boolean visibile) {
        this.visibile = visibile;
    }

    public String getPercorso() {
        return percorso;
    }

    public void setPercorso(String percorso) {
        this.percorso = percorso;
    }

    public String getCodiceFiscaleArtista() {
        return codiceFiscaleArtista;
    }

    public void setCodiceFiscaleArtista(String codiceFiscaleArtista) {
        this.codiceFiscaleArtista = codiceFiscaleArtista;
    }
}
