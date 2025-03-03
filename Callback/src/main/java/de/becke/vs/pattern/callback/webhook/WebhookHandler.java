package de.becke.vs.pattern.callback.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Ein einfacher HTTP-Server zum Empfangen von Webhook-Callbacks.
 */
public class WebhookHandler extends AbstractHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WebhookHandler.class);
    
    // Der Jetty-Server
    private final Server server;
    
    // JSON-Mapper für die Deserialisierung von Objekten
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Liste der empfangenen Callbacks
    private final List<Map<String, Object>> receivedCallbacks = new CopyOnWriteArrayList<>();
    
    // Listener für empfangene Callbacks
    private final List<Consumer<Map<String, Object>>> listeners = new ArrayList<>();
    
    /**
     * Erstellt einen neuen WebhookHandler, der auf dem angegebenen Port lauscht.
     * 
     * @param port Der zu verwendende Port.
     * @throws Exception Wenn der Server nicht gestartet werden kann.
     */
    public WebhookHandler(int port) throws Exception {
        LOGGER.info("Starte Webhook-Handler auf Port {}", port);
        server = new Server(port);
        server.setHandler(this);
        server.start();
        LOGGER.info("Webhook-Handler gestartet auf Port {}", port);
    }
    
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, 
                      HttpServletResponse response) throws IOException {
        
        LOGGER.info("Webhook-Anfrage empfangen: {}", target);
        
        // Nur POST-Anfragen akzeptieren
        if (!request.getMethod().equals("POST")) {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            baseRequest.setHandled(true);
            return;
        }
        
        // JSON-Inhalt lesen
        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
        }
        
        String json = jsonBuilder.toString();
        LOGGER.info("Empfangene Daten: {}", json);
        
        try {
            // Daten als Map deserialisieren
            @SuppressWarnings("unchecked")
            Map<String, Object> callbackData = objectMapper.readValue(json, Map.class);
            
            // Callback zu empfangenen Callbacks hinzufügen
            receivedCallbacks.add(callbackData);
            
            // Benachrichtige alle Listener
            for (Consumer<Map<String, Object>> listener : listeners) {
                try {
                    listener.accept(callbackData);
                } catch (Exception e) {
                    LOGGER.error("Fehler beim Benachrichtigen eines Listeners: {}", 
                            e.getMessage(), e);
                }
            }
            
            // Antworte mit 200 OK
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("{\"status\":\"success\"}");
            
        } catch (Exception e) {
            LOGGER.error("Fehler beim Verarbeiten der Webhook-Daten: {}", e.getMessage(), e);
            
            // Antworte mit 400 Bad Request
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("{\"status\":\"error\",\"message\":\"" + 
                    e.getMessage() + "\"}");
        }
        
        baseRequest.setHandled(true);
    }
    
    /**
     * Fügt einen Listener hinzu, der bei jedem empfangenen Callback aufgerufen wird.
     * 
     * @param listener Der hinzuzufügende Listener.
     */
    public void addCallbackListener(Consumer<Map<String, Object>> listener) {
        listeners.add(listener);
    }
    
    /**
     * Gibt alle bisher empfangenen Callbacks zurück.
     * 
     * @return Eine Liste der empfangenen Callbacks.
     */
    public List<Map<String, Object>> getReceivedCallbacks() {
        return new ArrayList<>(receivedCallbacks);
    }
    
    /**
     * Gibt den letzten empfangenen Callback zurück oder null, wenn noch kein Callback empfangen wurde.
     * 
     * @return Der letzte empfangene Callback oder null.
     */
    public Map<String, Object> getLastCallback() {
        return receivedCallbacks.isEmpty() ? null : receivedCallbacks.get(receivedCallbacks.size() - 1);
    }
    
    /**
     * Löscht alle bisher empfangenen Callbacks.
     */
    public void clearCallbacks() {
        receivedCallbacks.clear();
    }
    
    /**
     * Stoppt den Server und gibt alle Ressourcen frei.
     * 
     * @throws Exception Wenn beim Stoppen des Servers ein Fehler auftritt.
     */
    public void stop() throws Exception {
        LOGGER.info("Stoppe Webhook-Handler");
        server.stop();
    }
}