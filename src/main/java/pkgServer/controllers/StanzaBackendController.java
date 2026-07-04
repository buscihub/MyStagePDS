package pkgServer.controllers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import pkgServer.services.StanzaService;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class StanzaBackendController implements HttpHandler {
    
    private StanzaService stanzaService;

    public StanzaBackendController() {
        this.stanzaService = new StanzaService();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String response = "";
        int statusCode = 200;

        try {
            if ("GET".equalsIgnoreCase(method)) {
                // Esempio: /stanza?link=xyz123
                Map<String, String> queryParams = queryToMap(exchange.getRequestURI().getQuery());
                String link = queryParams.get("link");

                if (link != null && !link.isEmpty()) {
                    response = stanzaService.getHtmlForStanza(link);
                } else {
                    statusCode = 400;
                    response = "Bad Request: link parameter missing";
                }
            } else {
                statusCode = 405; // Method Not Allowed
                response = "Method Not Allowed";
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusCode = 500;
            response = "Internal Server Error";
        }

        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        if (query != null) {
            for (String param : query.split("&")) {
                String[] entry = param.split("=");
                if (entry.length > 1) {
                    result.put(entry[0], entry[1]);
                } else {
                    result.put(entry[0], "");
                }
            }
        }
        return result;
    }
}
