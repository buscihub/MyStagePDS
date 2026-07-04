package pkgEntity;

public class CarrieraEntity {
    private String tipo;
    private int anniDiCarriera;
    private int idCarriera;
    private String codiceFiscaleArtista;

    public CarrieraEntity(int idCarriera, String codiceFiscaleArtista, String tipo, int anniDiCarriera) {
        this.idCarriera = idCarriera;
        this.codiceFiscaleArtista = codiceFiscaleArtista;
        this.tipo = tipo;
        this.anniDiCarriera = anniDiCarriera;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getAnniDiCarriera() {
        return anniDiCarriera;
    }

    public void setAnniDiCarriera(int anniDiCarriera) {
        this.anniDiCarriera = anniDiCarriera;
    }

    public int getIdCarriera() {
        return idCarriera;
    }

    public void setIdCarriera(int idCarriera) {
        this.idCarriera = idCarriera;
    }

    public String getCodiceFiscaleArtista() {
        return codiceFiscaleArtista;
    }

    public void setCodiceFiscaleArtista(String codiceFiscaleArtista) {
        this.codiceFiscaleArtista = codiceFiscaleArtista;
    }
}
