package de.becke.vs.pattern.interceptor.http;

import de.becke.vs.pattern.interceptor.core.Context;
import de.becke.vs.pattern.interceptor.core.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Ein Interceptor für die Inhaltsaushandlung bei HTTP-Anfragen.
 * 
 * Diese Klasse analysiert die HTTP-Header einer Anfrage und bestimmt
 * das gewünschte Format für die Antwort.
 */
public class ContentNegotiationInterceptor implements Interceptor {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ContentNegotiationInterceptor.class);
    
    // Unterstützte Inhaltstypen
    private static final String[] SUPPORTED_TYPES = {"application/json", "application/xml", "text/plain"};
    
    @Override
    public boolean preProcess(Context context) {
        LOGGER.debug("ContentNegotiation Interceptor: preProcess für Operation [ID: {}]", 
                context.getExecutionId());
        
        // Extrahiere den Accept-Header
        Map<String, String> headers = context.getAttributeAs("http.headers", Map.class);
        if (headers == null || !headers.containsKey("Accept")) {
            // Default auf application/json, wenn kein Accept-Header vorhanden
            context.setAttribute("http.acceptType", "application/json");
            LOGGER.debug("Kein Accept-Header gefunden, verwende Standard: application/json");
            return true;
        }
        
        // Parse Accept-Header
        String acceptHeader = headers.get("Accept");
        String bestType = negotiateContentType(acceptHeader);
        
        // Setze den ausgehandelten Inhaltstyp im Kontext
        context.setAttribute("http.acceptType", bestType);
        LOGGER.debug("Ausgehandelter Inhaltstyp: {}", bestType);
        
        return true;
    }
    
    @Override
    public void postProcess(Context context) {
        LOGGER.debug("ContentNegotiation Interceptor: postProcess für Operation [ID: {}]", 
                context.getExecutionId());
        
        // Hole den ausgehandelten Inhaltstyp
        String acceptType = context.getAttributeAs("http.acceptType", String.class);
        if (acceptType == null) {
            // Sollte nicht passieren, aber für den Fall, dass etwas schiefgeht
            acceptType = "application/json";
        }
        
        // Hole oder erstelle die Response-Header
        Map<String, String> responseHeaders = context.getAttributeAs("http.responseHeaders", Map.class);
        if (responseHeaders == null) {
            responseHeaders = new HashMap<>();
            context.setAttribute("http.responseHeaders", responseHeaders);
        }
        
        // Setze den Content-Type-Header
        responseHeaders.put("Content-Type", acceptType + "; charset=UTF-8");
        
        // Hier könnte man das Ergebnis je nach Inhaltstyp formatieren
        // Zum Beispiel könnte man ein Objekt in JSON oder XML umwandeln
        // Für diese Demonstration behalten wir das Ergebnis aber wie es ist
    }
    
    @Override
    public boolean handleException(Context context, Exception exception) {
        // Wir behandeln keine Exceptions
        return false;
    }
    
    /**
     * Verhandelt den besten Inhaltstyp basierend auf dem Accept-Header.
     * 
     * @param acceptHeader Der Accept-Header
     * @return Der beste unterstützte Inhaltstyp
     */
    private String negotiateContentType(String acceptHeader) {
        // Wenn der Accept-Header "*/*" ist, verwenden wir application/json
        if (acceptHeader == null || acceptHeader.trim().isEmpty() || acceptHeader.equals("*/*")) {
            return "application/json";
        }
        
        // Parse den Accept-Header
        String[] acceptedTypes = acceptHeader.split(",");
        Map<String, Float> typeWeights = new HashMap<>();
        
        for (String type : acceptedTypes) {
            type = type.trim();
            float weight = 1.0f;
            
            // Extrahiere die Qualität (q-Wert), falls vorhanden
            if (type.contains(";")) {
                String[] parts = type.split(";");
                type = parts[0].trim();
                
                for (int i = 1; i < parts.length; i++) {
                    String param = parts[i].trim();
                    if (param.startsWith("q=")) {
                        try {
                            weight = Float.parseFloat(param.substring(2));
                        } catch (NumberFormatException e) {
                            // Ignoriere ungültige q-Werte
                        }
                    }
                }
            }
            
            typeWeights.put(type, weight);
        }
        
        // Finde den besten unterstützten Typ
        String bestType = "application/json"; // Standardwert
        float bestWeight = 0;
        
        for (String supportedType : SUPPORTED_TYPES) {
            if (typeWeights.containsKey(supportedType)) {
                float weight = typeWeights.get(supportedType);
                if (weight > bestWeight) {
                    bestWeight = weight;
                    bestType = supportedType;
                }
            } else if (typeWeights.containsKey("*/*")) {
                float weight = typeWeights.get("*/*");
                if (weight > bestWeight) {
                    bestWeight = weight;
                    bestType = supportedType;
                }
            }
        }
        
        return bestType;
    }
}