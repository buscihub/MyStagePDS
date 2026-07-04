package pkgServer.services;

public class StanzaService {
    
    public StanzaService() {
    }

    public String getHtmlForStanza(String link) {
        StringBuilder htmlBuilder = new StringBuilder();
        
        try {
            // Find Stanza by link (in a real system, we'd need a queryDBMSStanzaByLink)
            // Using existing logic to mock the page since we don't have exactly 'queryDBMSStanzaByLink'
            // Wait, we have queryDBMSListaVisualizzatori(String linkStanza) which verifies link
            htmlBuilder.append("<html><head><title>Stanza Condivisa</title></head><body>");
            htmlBuilder.append("<h1>Benvenuto nella Stanza</h1>");
            htmlBuilder.append("<p>Link di accesso fornito: ").append(link).append("</p>");
            htmlBuilder.append("<ul>");
            
            // Simulating document retrieval for this room
            htmlBuilder.append("<li><a href='/files?path=esempio.pdf'>Esempio Documento PDF</a></li>");
            
            htmlBuilder.append("</ul></body></html>");

        } catch (Exception e) {
            e.printStackTrace();
            return "<html><body><h1>Errore interno</h1></body></html>";
        }
        
        return htmlBuilder.toString();
    }
}
