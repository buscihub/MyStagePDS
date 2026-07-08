package pkgBoundary;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class ServerBoundary {
    private static ServerBoundary instance;
    private final HttpClient httpClient;
    private final Gson gson;
    private final String SERVER_URL = "http://localhost:8080/api/dbms";

    private ServerBoundary() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public static ServerBoundary getInstance() {
        if (instance == null) { instance = new ServerBoundary(); }
        return instance;
    }

    @SuppressWarnings("unchecked")
    private <T> T makeRequest(String method, List<String> paramTypes, List<Object> params, Class<T> returnType) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("method", method);
            payload.put("paramTypes", paramTypes);
            payload.put("params", params);
            String json = gson.toJson(payload);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SERVER_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                if (returnType == ResultDto.class) {
                    List<Map<String, Object>> data = gson.fromJson(response.body(), new TypeToken<List<Map<String, Object>>>(){}.getType());
                    return (T) new ResultDto(data);
                }
                return gson.fromJson(response.body(), returnType);
            } else {
                System.err.println("Error from server: " + response.statusCode());
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public ResultDto queryDBMSVerificaCredenziali(String email, String password) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("String");
        paramTypes.add("String");
        params.add(email);
        params.add(password);
        return makeRequest("queryDBMSVerificaCredenziali", paramTypes, params, ResultDto.class);
    }

    public int insertDBMScodice(String email, String codice) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("String");
        paramTypes.add("String");
        params.add(email);
        params.add(codice);
        Integer res = makeRequest("insertDBMScodice", paramTypes, params, Integer.class);
        return res != null ? res : 0;
    }

    public ResultDto queryDBMSVerificaRegistrazione(String codiceFiscale, String email) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("String");
        paramTypes.add("String");
        params.add(codiceFiscale);
        params.add(email);
        return makeRequest("queryDBMSVerificaRegistrazione", paramTypes, params, ResultDto.class);
    }

    public ResultDto queryDBMSVerificaCodice(String email, String codice) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("String");
        paramTypes.add("String");
        params.add(email);
        params.add(codice);
        return makeRequest("queryDBMSVerificaCodice", paramTypes, params, ResultDto.class);
    }

    public int insertDBMSCreaProfilo(String nome, String cognome, String dataDiNascita, String sesso, String codiceFiscale, String nomeDarte, String carriera, int anniCarriera, String email, String password) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("String");
        paramTypes.add("String");
        paramTypes.add("String");
        paramTypes.add("String");
        paramTypes.add("String");
        paramTypes.add("String");
        paramTypes.add("String");
        paramTypes.add("int");
        paramTypes.add("String");
        paramTypes.add("String");
        params.add(nome);
        params.add(cognome);
        params.add(dataDiNascita);
        params.add(sesso);
        params.add(codiceFiscale);
        params.add(nomeDarte);
        params.add(carriera);
        params.add(anniCarriera);
        params.add(email);
        params.add(password);
        Integer res = makeRequest("insertDBMSCreaProfilo", paramTypes, params, Integer.class);
        return res != null ? res : 0;
    }

    public ResultDto queryDBMSVerificaEsistenzaAccount(String email) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("String");
        params.add(email);
        return makeRequest("queryDBMSVerificaEsistenzaAccount", paramTypes, params, ResultDto.class);
    }

    public ResultDto queryDBMSVerificaEsistenzaAccountByCF(String codiceFiscale) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("String");
        params.add(codiceFiscale);
        return makeRequest("queryDBMSVerificaEsistenzaAccountByCF", paramTypes, params, ResultDto.class);
    }

    public ResultDto queryDBMSVerificaEmail(String email) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("String");
        params.add(email);
        return makeRequest("queryDBMSVerificaEmail", paramTypes, params, ResultDto.class);
    }

    public ResultDto queryDBMSRecuperaPassword(String email) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("String");
        params.add(email);
        return makeRequest("queryDBMSRecuperaPassword", paramTypes, params, ResultDto.class);
    }

    public ResultDto queryDBMSCercaArtista(String keyword) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("String");
        params.add(keyword);
        return makeRequest("queryDBMSCercaArtista", paramTypes, params, ResultDto.class);
    }

    public ResultDto queryDBMSFiltraArtisti(String carriera, int anniDiCarriera) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("String");
        paramTypes.add("int");
        params.add(carriera);
        params.add(anniDiCarriera);
        return makeRequest("queryDBMSFiltraArtisti", paramTypes, params, ResultDto.class);
    }

    public ResultDto queryDBMSProfiloArtista(String codiceFiscale) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("String");
        params.add(codiceFiscale);
        return makeRequest("queryDBMSProfiloArtista", paramTypes, params, ResultDto.class);
    }

    public int removeDBMSProfiloArtista(String codiceFiscale) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("String");
        params.add(codiceFiscale);
        Integer res = makeRequest("removeDBMSProfiloArtista", paramTypes, params, Integer.class);
        return res != null ? res : 0;
    }

    public int updateDBMSPassword(String codiceFiscale, String nuovaPassword) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("String");
        paramTypes.add("String");
        params.add(codiceFiscale);
        params.add(nuovaPassword);
        Integer res = makeRequest("updateDBMSPassword", paramTypes, params, Integer.class);
        return res != null ? res : 0;
    }

    public boolean queryDBMSVerificaPassword(String codiceFiscale, String password) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("String");
        paramTypes.add("String");
        params.add(codiceFiscale);
        params.add(password);
        Boolean res = makeRequest("queryDBMSVerificaPassword", paramTypes, params, Boolean.class);
        return res != null ? res : false;
    }

    public boolean queryDBMSVerificaNomeArte(String nomeDarte) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("String");
        params.add(nomeDarte);
        Boolean res = makeRequest("queryDBMSVerificaNomeArte", paramTypes, params, Boolean.class);
        return res != null ? res : false;
    }

    public int updateDBMSNomeArte(String codiceFiscale, String nuovoNomeArte) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("String");
        paramTypes.add("String");
        params.add(codiceFiscale);
        params.add(nuovoNomeArte);
        Integer res = makeRequest("updateDBMSNomeArte", paramTypes, params, Integer.class);
        return res != null ? res : 0;
    }

    public int queryDBMSUpdateImmagineProfilo(String urlImmagine, String codiceFiscale) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("String");
        paramTypes.add("String");
        params.add(urlImmagine);
        params.add(codiceFiscale);
        Integer res = makeRequest("queryDBMSUpdateImmagineProfilo", paramTypes, params, Integer.class);
        return res != null ? res : 0;
    }

    public int updateDBMSDefaultImmagineProfilo(String codiceFiscale) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("String");
        params.add(codiceFiscale);
        Integer res = makeRequest("updateDBMSDefaultImmagineProfilo", paramTypes, params, Integer.class);
        return res != null ? res : 0;
    }

    public int insertDBMSCarriera(String codiceFiscale, String tipologia, int anni) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("String");
        paramTypes.add("String");
        paramTypes.add("int");
        params.add(codiceFiscale);
        params.add(tipologia);
        params.add(anni);
        Integer res = makeRequest("insertDBMSCarriera", paramTypes, params, Integer.class);
        return res != null ? res : 0;
    }

    public ResultDto queryDBMSListaCarriere(String codiceFiscale) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("String");
        params.add(codiceFiscale);
        return makeRequest("queryDBMSListaCarriere", paramTypes, params, ResultDto.class);
    }

    public int removeDBMSCarriereSelezionate(int idCarriera) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("int");
        params.add(idCarriera);
        Integer res = makeRequest("removeDBMSCarriereSelezionate", paramTypes, params, Integer.class);
        return res != null ? res : 0;
    }

    public ResultDto queryDBMSInsertDocumenti(String codiceFiscale, boolean visibile, String percorso) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("String");
        paramTypes.add("boolean");
        paramTypes.add("String");
        params.add(codiceFiscale);
        params.add(visibile);
        params.add(percorso);
        return makeRequest("queryDBMSInsertDocumenti", paramTypes, params, ResultDto.class);
    }

    public ResultDto queryDBMSListaDocumenti(String codiceFiscale) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("String");
        params.add(codiceFiscale);
        return makeRequest("queryDBMSListaDocumenti", paramTypes, params, ResultDto.class);
    }

    public ResultDto queryDBMSListaDocumentiVisibili(String codiceFiscale) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("String");
        params.add(codiceFiscale);
        return makeRequest("queryDBMSListaDocumentiVisibili", paramTypes, params, ResultDto.class);
    }

    public int queryDBMSRemoveDocumenti(int idDocumento) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("int");
        params.add(idDocumento);
        Integer res = makeRequest("queryDBMSRemoveDocumenti", paramTypes, params, Integer.class);
        return res != null ? res : 0;
    }

    public int queryDBMSUpdateStatoDocumenti(int idDocumento, boolean visibile) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("int");
        paramTypes.add("boolean");
        params.add(idDocumento);
        params.add(visibile);
        Integer res = makeRequest("queryDBMSUpdateStatoDocumenti", paramTypes, params, Integer.class);
        return res != null ? res : 0;
    }

    public boolean queryDBMSVerificaNomeStanza(String codiceFiscale, String nomeStanza) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("String");
        paramTypes.add("String");
        params.add(codiceFiscale);
        params.add(nomeStanza);
        Boolean res = makeRequest("queryDBMSVerificaNomeStanza", paramTypes, params, Boolean.class);
        return res != null ? res : false;
    }

    public ResultDto insertDBMSStanza(String codiceFiscale, String nomeStanza, String link) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("String");
        paramTypes.add("String");
        paramTypes.add("String");
        params.add(codiceFiscale);
        params.add(nomeStanza);
        params.add(link);
        return makeRequest("insertDBMSStanza", paramTypes, params, ResultDto.class);
    }

    public ResultDto queryDBMSStanzaByLink(String link) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("String");
        params.add(link);
        return makeRequest("queryDBMSStanzaByLink", paramTypes, params, ResultDto.class);
    }

    public ResultDto queryDBMSStanzaById(int idStanza) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("int");
        params.add(idStanza);
        return makeRequest("queryDBMSStanzaById", paramTypes, params, ResultDto.class);
    }

    public void insertDocumentiDBMSStanza(int idStanza, int idDocumento, boolean scaricabile) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("int");
        paramTypes.add("int");
        paramTypes.add("boolean");
        params.add(idStanza);
        params.add(idDocumento);
        params.add(scaricabile);
        makeRequest("insertDocumentiDBMSStanza", paramTypes, params, Void.class);
    }

    public ResultDto queryDBMSLinkStanza(int idStanza) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("int");
        params.add(idStanza);
        return makeRequest("queryDBMSLinkStanza", paramTypes, params, ResultDto.class);
    }

    public int updateDBMSStanze(int idStanza, String nuovoNome) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("int");
        paramTypes.add("String");
        params.add(idStanza);
        params.add(nuovoNome);
        Integer res = makeRequest("updateDBMSStanze", paramTypes, params, Integer.class);
        return res != null ? res : 0;
    }

    public int updateDBMSNomeStanza(int idStanza, String nuovoNome) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("int");
        paramTypes.add("String");
        params.add(idStanza);
        params.add(nuovoNome);
        Integer res = makeRequest("updateDBMSNomeStanza", paramTypes, params, Integer.class);
        return res != null ? res : 0;
    }

    public int deleteDBMSStanza(int idStanza) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("int");
        params.add(idStanza);
        Integer res = makeRequest("deleteDBMSStanza", paramTypes, params, Integer.class);
        return res != null ? res : 0;
    }

    public ResultDto queryDocumentiNonInStanza(int idStanza, String codiceFiscale) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("int");
        paramTypes.add("String");
        params.add(idStanza);
        params.add(codiceFiscale);
        return makeRequest("queryDocumentiNonInStanza", paramTypes, params, ResultDto.class);
    }

    public ResultDto queryDBMSListaStanze(String codiceFiscale) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("String");
        params.add(codiceFiscale);
        return makeRequest("queryDBMSListaStanze", paramTypes, params, ResultDto.class);
    }

    public ResultDto queryDBMSListaDocumentiStanza(int idStanza) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("int");
        params.add(idStanza);
        return makeRequest("queryDBMSListaDocumentiStanza", paramTypes, params, ResultDto.class);
    }

    public int queryDBMSRemoveDocumentiStanza(int idStanza, int idDocumento) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("int");
        paramTypes.add("int");
        params.add(idStanza);
        params.add(idDocumento);
        Integer res = makeRequest("queryDBMSRemoveDocumentiStanza", paramTypes, params, Integer.class);
        return res != null ? res : 0;
    }

    public int queryDBMSUpdateScaricabiliENonScaricabiliDocumentiStanza(int idStanza, int idDocumento, boolean scaricabile) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("int");
        paramTypes.add("int");
        paramTypes.add("boolean");
        params.add(idStanza);
        params.add(idDocumento);
        params.add(scaricabile);
        Integer res = makeRequest("queryDBMSUpdateScaricabiliENonScaricabiliDocumentiStanza", paramTypes, params, Integer.class);
        return res != null ? res : 0;
    }

    public ResultDto queryDBMSListaVisualizzatori(String linkStanza) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("String");
        params.add(linkStanza);
        return makeRequest("queryDBMSListaVisualizzatori", paramTypes, params, ResultDto.class);
    }

    public ResultDto insertDBMSVisualizzatore(String nome, String cognome, String email) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("String");
        paramTypes.add("String");
        paramTypes.add("String");
        params.add(nome);
        params.add(cognome);
        params.add(email);
        return makeRequest("insertDBMSVisualizzatore", paramTypes, params, ResultDto.class);
    }

    public void insertDBMSVisualizzazione(int idStanza, int idVisualizzatore) {
        List<Object> params = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("int");
        paramTypes.add("int");
        params.add(idStanza);
        params.add(idVisualizzatore);
        makeRequest("insertDBMSVisualizzazione", paramTypes, params, Void.class);
    }

}