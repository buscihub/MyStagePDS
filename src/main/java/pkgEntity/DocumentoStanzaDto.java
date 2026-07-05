package pkgEntity;

public class DocumentoStanzaDto extends DocumentoEntity {
    private boolean scaricabile;

    public DocumentoStanzaDto(int idDocumento, String codiceFiscaleArtista, boolean visibile, String percorso, boolean scaricabile) {
        super(idDocumento, codiceFiscaleArtista, visibile, percorso);
        this.scaricabile = scaricabile;
    }

    public boolean isScaricabile() {
        return scaricabile;
    }

    public void setScaricabile(boolean scaricabile) {
        this.scaricabile = scaricabile;
    }
}
