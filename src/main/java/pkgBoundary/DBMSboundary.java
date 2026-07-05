package pkgBoundary;

import java.sql.*;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class DBMSboundary {
    private static DBMSboundary instance;
    private static final String URL = "jdbc:sqlite:database.db";
    private Connection connection;

    private DBMSboundary() {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(URL);
            initializeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeDatabase() {
        try {
            Statement stmt = this.connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='ARTISTA'");
            if (!rs.next()) {
                System.out.println("Tabelle non trovate, inizializzazione del database...");
                executeSqlScript("/sql/schema.sql");
                executeSqlScript("/sql/populate.sql");
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeSqlScript(String path) {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                System.out.println("Script non trovato: " + path);
                return;
            }
            try (Scanner scanner = new Scanner(is, StandardCharsets.UTF_8.name())) {
                scanner.useDelimiter(";");
                Statement stmt = this.connection.createStatement();
                while (scanner.hasNext()) {
                    String query = scanner.next().trim();
                    if (!query.isEmpty()) {
                        stmt.execute(query);
                    }
                }
                stmt.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DBMSboundary getInstance() {
        if (instance == null) {
            instance = new DBMSboundary();
        }
        return instance;
    }

    private Connection getConnection() {
        try {
            if (this.connection == null || this.connection.isClosed()) {
                this.connection = DriverManager.getConnection(URL);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this.connection;
    }

    public void closeConnection() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- Autenticazione ---
    public ResultSet queryDBMSVerificaCredenziali(String email, String password) {
        try {
            String query = "SELECT * FROM ARTISTA WHERE email = ? AND password = ?";
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2, password);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int insertDBMScodice(String email, String codice) {
        try {
            String query = "UPDATE ARTISTA SET codiceVerifica = ? WHERE email = ?";
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setString(1, codice);
            ps.setString(2, email);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public ResultSet queryDBMSVerificaRegistrazione(String codiceFiscale, String email) {
        try {
            String query = "SELECT * FROM ARTISTA WHERE codiceFiscale = ? OR email = ?";
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setString(1, codiceFiscale);
            ps.setString(2, email);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet queryDBMSVerificaCodice(String email, String codice) {
        try {
            String query = "SELECT * FROM ARTISTA WHERE email = ? AND codiceVerifica = ?";
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2, codice);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int insertDBMSCreaProfilo(String nome, String cognome, String dataDiNascita, String sesso, String codiceFiscale, String nomeDarte, String email, String password) {
        try {
            String query = "INSERT INTO ARTISTA (codiceFiscale, nome, cognome, dataDiNascita, sesso, nomeDarte, email, password, urlImmagineProfilo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setString(1, codiceFiscale);
            ps.setString(2, nome);
            ps.setString(3, cognome);
            ps.setString(4, dataDiNascita);
            ps.setString(5, sesso);
            ps.setString(6, nomeDarte);
            ps.setString(7, email);
            ps.setString(8, password);
            ps.setString(9, "default.png");
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public ResultSet queryDBMSVerificaEsistenzaAccount(String email) {
        return queryDBMSVerificaEmail(email);
    }

    public ResultSet queryDBMSVerificaEmail(String email) {
        try {
            String query = "SELECT * FROM ARTISTA WHERE email = ?";
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setString(1, email);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet queryDBMSRecuperaPassword(String email) {
        try {
            String query = "SELECT password FROM ARTISTA WHERE email = ?";
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setString(1, email);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // --- Ricerca ---
    public ResultSet queryDBMSCercaArtista(String keyword) {
        try {
            String query = "SELECT * FROM ARTISTA WHERE nome LIKE ? OR cognome LIKE ? OR nomeDarte LIKE ?";
            PreparedStatement ps = getConnection().prepareStatement(query);
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet queryDBMSFiltraArtisti(String carriera, int anniDiCarriera) {
        try {
            String query = "SELECT a.* FROM ARTISTA a JOIN CARRIERA c ON a.codiceFiscale = c.codiceFiscaleArtist WHERE c.tipologia = ? AND c.anni >= ?";
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setString(1, carriera);
            ps.setInt(2, anniDiCarriera);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet queryDBMSProfiloArtista(String codiceFiscale) {
        try {
            String query = "SELECT * FROM ARTISTA WHERE codiceFiscale = ?";
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setString(1, codiceFiscale);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // --- Gestione Profilo ---
    public int removeDBMSProfiloArtista(String codiceFiscale) {
        try {
            String query = "DELETE FROM ARTISTA WHERE codiceFiscale = ?";
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setString(1, codiceFiscale);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int updateDBMSPassword(String codiceFiscale, String nuovaPassword) {
        try {
            String query = "UPDATE ARTISTA SET password = ? WHERE codiceFiscale = ?";
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setString(1, nuovaPassword);
            ps.setString(2, codiceFiscale);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public boolean queryDBMSVerificaPassword(String codiceFiscale, String password) {
        try {
            String query = "SELECT * FROM ARTISTA WHERE codiceFiscale = ? AND password = ?";
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setString(1, codiceFiscale);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean queryDBMSVerificaNomeArte(String nomeDarte) {
        try {
            String query = "SELECT * FROM ARTISTA WHERE nomeDarte = ?";
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setString(1, nomeDarte);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    public int updateDBMSNomeArte(String codiceFiscale, String nuovoNomeArte) {
        try {
            String query = "UPDATE ARTISTA SET nomeDarte = ? WHERE codiceFiscale = ?";
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setString(1, nuovoNomeArte);
            ps.setString(2, codiceFiscale);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int queryDBMSUpdateImmagineProfilo(String urlImmagine, String codiceFiscale) {
        try {
            String query = "UPDATE ARTISTA SET urlImmagineProfilo = ? WHERE codiceFiscale = ?";
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setString(1, urlImmagine);
            ps.setString(2, codiceFiscale);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int updateDBMSDefaultImmagineProfilo(String codiceFiscale) {
        return queryDBMSUpdateImmagineProfilo("default.png", codiceFiscale);
    }

    // --- Carriera ---
    public int insertDBMSCarriera(String codiceFiscale, String tipologia, int anni) {
        try {
            String query = "INSERT INTO CARRIERA (codiceFiscaleArtist, tipologia, anni) VALUES (?, ?, ?)";
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setString(1, codiceFiscale);
            ps.setString(2, tipologia);
            ps.setInt(3, anni);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public ResultSet queryDBMSListaCarriere(String codiceFiscale) {
        try {
            String query = "SELECT * FROM CARRIERA WHERE codiceFiscaleArtist = ?";
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setString(1, codiceFiscale);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int removeDBMSCarriereSelezionate(int idCarriera) {
        try {
            String query = "DELETE FROM CARRIERA WHERE idCarriera = ?";
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setInt(1, idCarriera);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // --- Documenti ---
    public ResultSet queryDBMSInsertDocumenti(String codiceFiscale, boolean visibile, String percorso) {
        try {
            String query = "INSERT INTO DOCUMENTO (codiceFiscaleArtist, visibile, percorso) VALUES (?, ?, ?)";
            PreparedStatement ps = getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, codiceFiscale);
            ps.setBoolean(2, visibile);
            ps.setString(3, percorso);
            ps.executeUpdate();
            return ps.getGeneratedKeys();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet queryDBMSListaDocumenti(String codiceFiscale) {
        try {
            String query = "SELECT * FROM DOCUMENTO WHERE codiceFiscaleArtist = ?";
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setString(1, codiceFiscale);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int queryDBMSRemoveDocumenti(int idDocumento) {
        try {
            String query = "DELETE FROM DOCUMENTO WHERE idDocumento = ?";
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setInt(1, idDocumento);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int queryDBMSUpdateStatoDocumenti(int idDocumento, boolean visibile) {
        try {
            String query = "UPDATE DOCUMENTO SET visibile = ? WHERE idDocumento = ?";
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setBoolean(1, visibile);
            ps.setInt(2, idDocumento);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // --- Stanze ---
    public boolean queryDBMSVerificaNomeStanza(String codiceFiscale, String nomeStanza) {
        try {
            String query = "SELECT * FROM STANZA WHERE codiceFiscaleArtist = ? AND nomeStanza = ?";
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setString(1, codiceFiscale);
            ps.setString(2, nomeStanza);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    public ResultSet insertDBMSStanza(String codiceFiscale, String nomeStanza, String link) {
        try {
            String query = "INSERT INTO STANZA (codiceFiscaleArtist, nomeStanza, link) VALUES (?, ?, ?)";
            PreparedStatement ps = getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, codiceFiscale);
            ps.setString(2, nomeStanza);
            ps.setString(3, link);
            ps.executeUpdate();
            return ps.getGeneratedKeys();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void insertDocumentiDBMSStanza(int idStanza, int idDocumento, boolean scaricabile) {
        try {
            String query = "INSERT INTO CONTIENE (idStanza, idDocumento, scaricabile) VALUES (?, ?, ?)";
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setInt(1, idStanza);
            ps.setInt(2, idDocumento);
            ps.setBoolean(3, scaricabile);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet queryDBMSLinkStanza(int idStanza) {
        try {
            String query = "SELECT link FROM STANZA WHERE idStanza = ?";
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setInt(1, idStanza);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int updateDBMSStanze(int idStanza, String nuovoNome) {
        return updateDBMSNomeStanza(idStanza, nuovoNome);
    }

    public int updateDBMSNomeStanza(int idStanza, String nuovoNome) {
        try {
            String query = "UPDATE STANZA SET nomeStanza = ? WHERE idStanza = ?";
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setString(1, nuovoNome);
            ps.setInt(2, idStanza);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int deleteDBMSStanza(int idStanza) {
        try {
            // First delete constraints in CONTIENE and VISUALIZZAZIONE
            String query1 = "DELETE FROM CONTIENE WHERE idStanza = ?";
            PreparedStatement ps1 = getConnection().prepareStatement(query1);
            ps1.setInt(1, idStanza);
            ps1.executeUpdate();

            String query2 = "DELETE FROM VISUALIZZAZIONE WHERE idStanza = ?";
            PreparedStatement ps2 = getConnection().prepareStatement(query2);
            ps2.setInt(1, idStanza);
            ps2.executeUpdate();

            String query3 = "DELETE FROM STANZA WHERE idStanza = ?";
            PreparedStatement ps3 = getConnection().prepareStatement(query3);
            ps3.setInt(1, idStanza);
            return ps3.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public ResultSet queryDocumentiNonInStanza(int idStanza, String codiceFiscale) {
        try {
            String query = "SELECT * FROM DOCUMENTO WHERE codiceFiscaleArtist = ? AND idDocumento NOT IN (SELECT idDocumento FROM CONTIENE WHERE idStanza = ?)";
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setString(1, codiceFiscale);
            ps.setInt(2, idStanza);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet queryDBMSListaStanze(String codiceFiscale) {
        try {
            String query = "SELECT * FROM STANZA WHERE codiceFiscaleArtist = ?";
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setString(1, codiceFiscale);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet queryDBMSListaDocumentiStanza(int idStanza) {
        try {
            String query = "SELECT d.*, c.scaricabile FROM DOCUMENTO d JOIN CONTIENE c ON d.idDocumento = c.idDocumento WHERE c.idStanza = ?";
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setInt(1, idStanza);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int queryDBMSRemoveDocumentiStanza(int idStanza, int idDocumento) {
        try {
            String query = "DELETE FROM CONTIENE WHERE idStanza = ? AND idDocumento = ?";
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setInt(1, idStanza);
            ps.setInt(2, idDocumento);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int queryDBMSUpdateScaricabiliENonScaricabiliDocumentiStanza(int idStanza, int idDocumento, boolean scaricabile) {
        try {
            String query = "UPDATE CONTIENE SET scaricabile = ? WHERE idStanza = ? AND idDocumento = ?";
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setBoolean(1, scaricabile);
            ps.setInt(2, idStanza);
            ps.setInt(3, idDocumento);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // --- Monitoraggio ---
    public ResultSet queryDBMSListaVisualizzatori(String linkStanza) {
        try {
            String query = "SELECT v.*, vz.dataVisualizzazione FROM VISUALIZZATORE v JOIN VISUALIZZAZIONE vz ON v.idVisualizzatore = vz.idVisualizzatore JOIN STANZA s ON vz.idStanza = s.idStanza WHERE s.link = ?";
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setString(1, linkStanza);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet insertDBMSVisualizzatore(String nome, String cognome, String email) {
        try {
            String query = "INSERT INTO VISUALIZZATORE (nomeVisualizzatore, cognomeVisualizzatore, emailVisualizzatore) VALUES (?, ?, ?)";
            PreparedStatement ps = getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, nome);
            ps.setString(2, cognome);
            ps.setString(3, email);
            ps.executeUpdate();
            return ps.getGeneratedKeys();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void insertDBMSVisualizzazione(int idStanza, int idVisualizzatore) {
        try {
            String query = "INSERT INTO VISUALIZZAZIONE (idVisualizzatore, idStanza, dataVisualizzazione) VALUES (?, ?, DATETIME('now'))";
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setInt(1, idVisualizzatore);
            ps.setInt(2, idStanza);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
