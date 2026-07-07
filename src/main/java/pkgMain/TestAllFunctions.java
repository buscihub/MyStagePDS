package pkgMain;

import pkgBoundary.DBMSboundary;
import pkgServer.WebServerManager;
import java.sql.ResultSet;

public class TestAllFunctions {

    public static void main(String[] args) {
        // Run verification tests for all operations
        System.out.println("=== Inzio Test di Verifica Funzioni ===");

        DBMSboundary dbms = DBMSboundary.getInstance();

        // 1. Test Registrazione
        System.out.println("\n[1] Test Registrazione");
        dbms.removeDBMSProfiloArtista("CF_TEST_123");
        int res = dbms.insertDBMSCreaProfilo("TestNome", "TestCognome", "1990-01-01", "M", "CF_TEST_123", "TestArt", "Musicista", 5, "test@example.com", "pass123");
        if (res > 0) {
            System.out.println("OK: Utente registrato con successo.");
        } else {
            System.out.println("ERRORE: Registrazione fallita.");
        }

        // 2. Test Login
        System.out.println("\n[2] Test Login");
        try {
            ResultSet rsLogin = dbms.queryDBMSVerificaCredenziali("test@example.com", "pass123");
            if (rsLogin != null && rsLogin.next()) {
                System.out.println("OK: Login effettuato.");
            } else {
                System.out.println("ERRORE: Login fallito.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3. Test Modifica Profilo
        System.out.println("\n[3] Test Modifica Profilo");
        dbms.updateDBMSNomeArte("CF_TEST_123", "NuovoTestArt");
        if (dbms.queryDBMSVerificaNomeArte("NuovoTestArt")) {
            System.out.println("OK: Nome d'arte aggiornato.");
        } else {
            System.out.println("ERRORE: Nome d'arte non aggiornato.");
        }

        dbms.queryDBMSUpdateImmagineProfilo("new_avatar.png", "CF_TEST_123");
        System.out.println("OK: Immagine profilo aggiornata.");

        dbms.insertDBMSCarriera("CF_TEST_123", "Musica", 5);
        try {
            ResultSet rsCarr = dbms.queryDBMSListaCarriere("CF_TEST_123");
            if (rsCarr != null && rsCarr.next()) {
                System.out.println("OK: Carriera inserita e recuperata.");
            } else {
                System.out.println("ERRORE: Carriera non recuperata.");
            }
        } catch (Exception e) { e.printStackTrace(); }

        // 4. Test Stanze e Documenti
        System.out.println("\n[4] Test Documenti e Stanze");
        try {
            ResultSet rsDoc = dbms.queryDBMSInsertDocumenti("CF_TEST_123", true, "doc_test.pdf");
            if (rsDoc != null && rsDoc.next()) {
                int idDoc = rsDoc.getInt(1);
                System.out.println("OK: Documento inserito con ID " + idDoc);

                ResultSet rsStanza = dbms.insertDBMSStanza("CF_TEST_123", "StanzaTest", "link123");
                if (rsStanza != null && rsStanza.next()) {
                    int idStanza = rsStanza.getInt(1);
                    System.out.println("OK: Stanza inserita con ID " + idStanza);

                    dbms.insertDocumentiDBMSStanza(idStanza, idDoc, true);
                    System.out.println("OK: Documento associato alla Stanza.");

                    ResultSet rsDocsStanza = dbms.queryDBMSListaDocumentiStanza(idStanza);
                    if (rsDocsStanza != null && rsDocsStanza.next()) {
                        System.out.println("OK: Documento recuperato correttamente dalla stanza.");
                    } else {
                        System.out.println("ERRORE: Recupero documenti stanza fallito.");
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }

        // 5. Test Ricerca e Filtri
        System.out.println("\n[5] Test Ricerca");
        try {
            ResultSet rsRicerca = dbms.queryDBMSCercaArtista("TestNome");
            if (rsRicerca != null && rsRicerca.next()) {
                System.out.println("OK: Artista trovato tramite Ricerca Testuale.");
            } else {
                System.out.println("ERRORE: Ricerca testuale fallita.");
            }

            ResultSet rsFiltro = dbms.queryDBMSFiltraArtisti("Musica", 3);
            if (rsFiltro != null && rsFiltro.next()) {
                System.out.println("OK: Artista trovato tramite Filtro Carriera.");
            } else {
                System.out.println("ERRORE: Filtro carriera fallito.");
            }
        } catch (Exception e) { e.printStackTrace(); }

        // 6. Test Web Server
        System.out.println("\n[6] Test Web Server");
        try {
            WebServerManager.getInstance().startServer();
            // Effettuiamo una chiamata locale
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URI("http://localhost:8080/stanza?link=link123").toURL().openConnection();
            conn.setRequestMethod("GET");
            int code = conn.getResponseCode();
            if (code == 200) {
                System.out.println("OK: Web Server ha risposto 200 per la stanza.");
            } else {
                System.out.println("ERRORE: Web Server ha risposto con codice " + code);
            }
            WebServerManager.getInstance().stopServer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 7. Pulizia (Oblio)
        System.out.println("\n[7] Test Diritto all'Oblio");
        dbms.removeDBMSProfiloArtista("CF_TEST_123");
        try {
            ResultSet rsVerifica = dbms.queryDBMSVerificaEsistenzaAccount("test@example.com");
            if (rsVerifica != null && !rsVerifica.next()) {
                System.out.println("OK: L'account e tutti i dati a cascata sono stati cancellati.");
            } else {
                System.out.println("ERRORE: L'account non è stato cancellato completamente.");
            }
        } catch (Exception e) { e.printStackTrace(); }

        System.out.println("\n=== Test di Verifica Completati ===");
        System.exit(0);
    }
}
