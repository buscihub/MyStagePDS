package pkgEntity;

import java.time.LocalDateTime;

public class ArtistaEntity {
    private String URLImmagineProfilo;
    private String email;
    private String password;
    private String nome;
    private String cognome;
    private String sesso;
    private String codiceFiscale;
    private String nomeDarte;
    private LocalDateTime dataDiNascita;

    public ArtistaEntity(String codiceFiscale, String nome, String cognome, LocalDateTime dataDiNascita, String sesso, String nomeDarte, String email, String password, String URLImmagineProfilo) {
        this.codiceFiscale = codiceFiscale;
        this.nome = nome;
        this.cognome = cognome;
        this.dataDiNascita = dataDiNascita;
        this.sesso = sesso;
        this.nomeDarte = nomeDarte;
        this.email = email;
        this.password = password;
        this.URLImmagineProfilo = URLImmagineProfilo;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public LocalDateTime getDataDiNascita() {
        return dataDiNascita;
    }

    public void setDataDiNascita(LocalDateTime dataDiNascita) {
        this.dataDiNascita = dataDiNascita;
    }

    public String getSesso() {
        return sesso;
    }

    public void setSesso(String sesso) {
        this.sesso = sesso;
    }

    public String getNomeDarte() {
        return nomeDarte;
    }

    public void setNomeDarte(String nomeDarte) {
        this.nomeDarte = nomeDarte;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getURLImmagineProfilo() {
        return URLImmagineProfilo;
    }

    public void setURLImmagineProfilo(String URLImmagineProfilo) {
        this.URLImmagineProfilo = URLImmagineProfilo;
    }

    public void setDefaultImmagineProfilo() {
        this.URLImmagineProfilo = "default.png";
    }
}
