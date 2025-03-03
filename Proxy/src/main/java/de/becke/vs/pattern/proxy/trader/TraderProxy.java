package de.becke.vs.pattern.proxy.trader;

import de.becke.vs.pattern.proxy.common.RemoteService;
import de.becke.vs.pattern.proxy.common.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Ein Trader-Proxy, der dynamische Dienstsuche und -auswahl basierend auf Qualitätsattributen ermöglicht.
 * 
 * Diese Implementierung erweitert das Broker-Konzept um die Fähigkeit, Dienste dynamisch 
 * aufzufinden und auszuwählen, basierend auf Qualitätsattributen und Clientanforderungen.
 */
public class TraderProxy implements RemoteService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TraderProxy.class);
    
    // Die registrierten Dienste mit ihren Eigenschaften
    private final Map<ServiceID, ServiceInfo> services = new HashMap<>();
    
    // Die bekannten Dienstkategorien
    private final Set<String> categories = new HashSet<>();
    
    // Die Bewertungen der Dienste durch Clients
    private final Map<ServiceID, List<ServiceRating>> ratings = new HashMap<>();
    
    /**
     * Erstellt einen neuen Trader-Proxy.
     */
    public TraderProxy() {
        LOGGER.info("TraderProxy initialisiert");
    }
    
    /**
     * Registriert einen neuen Dienst beim Trader.
     * 
     * @param service Der zu registrierende Dienst
     * @param properties Die Eigenschaften des Dienstes
     * @return Die generierte Dienst-ID
     */
    public ServiceID registerService(RemoteService service, Map<String, Object> properties) {
        String serviceType = (String) properties.getOrDefault("type", "unknown");
        String category = (String) properties.getOrDefault("category", "general");
        String provider = (String) properties.getOrDefault("provider", "anonymous");
        
        ServiceID serviceId = new ServiceID(serviceType, provider);
        ServiceInfo serviceInfo = new ServiceInfo(service, properties);
        
        services.put(serviceId, serviceInfo);
        categories.add(category);
        
        LOGGER.info("Dienst registriert: {} in Kategorie {}", serviceId, category);
        return serviceId;
    }
    
    /**
     * Entfernt einen Dienst aus dem Trader.
     * 
     * @param serviceId Die ID des zu entfernenden Dienstes
     * @return true, wenn der Dienst erfolgreich entfernt wurde, sonst false
     */
    public boolean unregisterService(ServiceID serviceId) {
        ServiceInfo removed = services.remove(serviceId);
        if (removed != null) {
            LOGGER.info("Dienst entfernt: {}", serviceId);
            
            // Entferne auch die Bewertungen des Dienstes
            ratings.remove(serviceId);
            
            return true;
        }
        
        LOGGER.warn("Dienst nicht gefunden für die Entfernung: {}", serviceId);
        return false;
    }
    
    /**
     * Sucht nach Diensten, die bestimmten Kriterien entsprechen.
     * 
     * @param serviceType Der Typ des Dienstes
     * @param requiredProperties Die erforderlichen Eigenschaften
     * @return Eine Liste von Dienst-IDs, die den Kriterien entsprechen
     */
    public List<ServiceID> findServices(String serviceType, Map<String, Object> requiredProperties) {
        LOGGER.info("Suche nach Diensten vom Typ {} mit Eigenschaften {}", serviceType, requiredProperties);
        
        List<ServiceID> matchingServices = new ArrayList<>();
        
        for (Map.Entry<ServiceID, ServiceInfo> entry : services.entrySet()) {
            ServiceID serviceId = entry.getKey();
            ServiceInfo serviceInfo = entry.getValue();
            
            // Überprüfe, ob der Diensttyp übereinstimmt
            if (serviceType != null && !serviceId.getServiceType().equals(serviceType)) {
                continue;
            }
            
            // Überprüfe, ob alle erforderlichen Eigenschaften vorhanden sind
            boolean matches = true;
            for (Map.Entry<String, Object> prop : requiredProperties.entrySet()) {
                String key = prop.getKey();
                Object value = prop.getValue();
                
                if (!serviceInfo.getProperties().containsKey(key) || 
                        !serviceInfo.getProperties().get(key).equals(value)) {
                    matches = false;
                    break;
                }
            }
            
            if (matches) {
                matchingServices.add(serviceId);
            }
        }
        
        LOGGER.info("Gefunden: {} passende Dienste", matchingServices.size());
        return matchingServices;
    }
    
    /**
     * Wählt den besten Dienst basierend auf Qualitätsattributen aus.
     * 
     * @param serviceType Der Typ des Dienstes
     * @param requiredProperties Die erforderlichen Eigenschaften
     * @param preferredQoS Die bevorzugten Qualitätsattribute, sortiert nach Priorität
     * @return Die ID des besten Dienstes oder null, wenn kein passender Dienst gefunden wurde
     */
    public ServiceID selectBestService(String serviceType, Map<String, Object> requiredProperties, 
            List<QualityOfService> preferredQoS) {
        
        LOGGER.info("Wähle besten Dienst vom Typ {} mit QoS-Präferenzen {}", serviceType, preferredQoS);
        
        // Finde alle passenden Dienste
        List<ServiceID> candidates = findServices(serviceType, requiredProperties);
        
        if (candidates.isEmpty()) {
            LOGGER.warn("Keine passenden Dienste gefunden");
            return null;
        }
        
        // Wenn keine QoS-Präferenzen angegeben sind, wähle den ersten Kandidaten
        if (preferredQoS == null || preferredQoS.isEmpty()) {
            LOGGER.info("Keine QoS-Präferenzen angegeben, wähle ersten Kandidaten: {}", candidates.get(0));
            return candidates.get(0);
        }
        
        // Bewerte jeden Kandidaten basierend auf den QoS-Präferenzen
        final Map<ServiceID, Double> scores = new HashMap<>();
        
        for (ServiceID serviceId : candidates) {
            ServiceInfo info = services.get(serviceId);
            double score = calculateServiceScore(serviceId, info, preferredQoS);
            scores.put(serviceId, score);
        }
        
        // Sortiere die Kandidaten nach ihren Scores
        candidates.sort(Comparator.comparing(scores::get).reversed());
        
        // Wähle den besten Kandidaten
        ServiceID bestService = candidates.get(0);
        LOGGER.info("Bester Dienst ausgewählt: {} mit Score {}", bestService, scores.get(bestService));
        
        return bestService;
    }
    
    /**
     * Berechnet einen Score für einen Dienst basierend auf QoS-Präferenzen.
     * 
     * @param serviceId Die Dienst-ID
     * @param info Die Dienstinformationen
     * @param preferredQoS Die bevorzugten Qualitätsattribute
     * @return Der berechnete Score
     */
    private double calculateServiceScore(ServiceID serviceId, ServiceInfo info, List<QualityOfService> preferredQoS) {
        double score = 0.0;
        Map<String, Object> properties = info.getProperties();
        
        // Gewichte für verschiedene QoS-Typen
        double availabilityWeight = 0.3;
        double performanceWeight = 0.3;
        double reliabilityWeight = 0.2;
        double ratingWeight = 0.2;
        
        for (QualityOfService qos : preferredQoS) {
            switch (qos) {
                case AVAILABILITY:
                    // Verfügbarkeit: 0.0 (nicht verfügbar) bis 1.0 (immer verfügbar)
                    if (properties.containsKey("availability")) {
                        double availability = getDoubleProperty(properties, "availability", 0.0);
                        score += availability * availabilityWeight;
                    }
                    break;
                
                case PERFORMANCE:
                    // Performance: höhere Werte sind besser
                    if (properties.containsKey("performance")) {
                        double performance = getDoubleProperty(properties, "performance", 0.0);
                        // Normalisiere Performance auf einen Wert zwischen 0 und 1
                        double normalizedPerformance = Math.min(performance / 10.0, 1.0);
                        score += normalizedPerformance * performanceWeight;
                    }
                    break;
                
                case RELIABILITY:
                    // Zuverlässigkeit: 0.0 (unzuverlässig) bis 1.0 (sehr zuverlässig)
                    if (properties.containsKey("reliability")) {
                        double reliability = getDoubleProperty(properties, "reliability", 0.0);
                        score += reliability * reliabilityWeight;
                    }
                    break;
                
                case USER_RATING:
                    // Benutzerbewertung: Durchschnitt der Bewertungen (0.0 bis 5.0)
                    double avgRating = getAverageRating(serviceId);
                    // Normalisiere Bewertung auf einen Wert zwischen 0 und 1
                    double normalizedRating = avgRating / 5.0;
                    score += normalizedRating * ratingWeight;
                    break;
                
                default:
                    // Ignoriere unbekannte QoS-Typen
                    break;
            }
        }
        
        return score;
    }
    
    /**
     * Bewertet einen Dienst basierend auf der Erfahrung eines Clients.
     * 
     * @param serviceId Die Dienst-ID
     * @param clientId Die Client-ID
     * @param rating Die Bewertung (0.0 bis 5.0)
     * @param comment Ein optionaler Kommentar
     */
    public void rateService(ServiceID serviceId, String clientId, double rating, String comment) {
        LOGGER.info("Client {} bewertet Dienst {} mit {}: {}", clientId, serviceId, rating, comment);
        
        ServiceRating serviceRating = new ServiceRating(clientId, rating, comment);
        
        ratings.computeIfAbsent(serviceId, k -> new ArrayList<>()).add(serviceRating);
    }
    
    /**
     * Gibt die durchschnittliche Bewertung eines Dienstes zurück.
     * 
     * @param serviceId Die Dienst-ID
     * @return Die durchschnittliche Bewertung oder 0.0, wenn keine Bewertungen vorhanden sind
     */
    public double getAverageRating(ServiceID serviceId) {
        List<ServiceRating> serviceRatings = ratings.get(serviceId);
        
        if (serviceRatings == null || serviceRatings.isEmpty()) {
            return 0.0;
        }
        
        double sum = 0.0;
        for (ServiceRating rating : serviceRatings) {
            sum += rating.getRating();
        }
        
        return sum / serviceRatings.size();
    }
    
    /**
     * Führt einen Dienst mit der angegebenen ID aus.
     * 
     * @param serviceId Die Dienst-ID
     * @param parameter Der Parameter für die Anfrage
     * @return Das Ergebnis der Anfrage
     * @throws ServiceException Wenn der Dienst nicht gefunden wird oder ein Fehler auftritt
     */
    public String invokeService(ServiceID serviceId, String parameter) throws ServiceException {
        LOGGER.info("Führe Dienst aus: {} mit Parameter: {}", serviceId, parameter);
        
        ServiceInfo serviceInfo = services.get(serviceId);
        if (serviceInfo == null) {
            LOGGER.error("Dienst nicht gefunden: {}", serviceId);
            throw new ServiceException("Dienst nicht gefunden: " + serviceId,
                    ServiceException.ErrorType.RESOURCE_NOT_FOUND);
        }
        
        try {
            RemoteService service = serviceInfo.getService();
            return service.request(parameter);
        } catch (ServiceException e) {
            LOGGER.error("Fehler bei der Ausführung des Dienstes {}: {}", serviceId, e.getMessage());
            throw e;
        }
    }
    
    /**
     * Hilfsmethode zum Abrufen einer Double-Eigenschaft.
     * 
     * @param properties Die Eigenschaften-Map
     * @param key Der Schlüssel
     * @param defaultValue Der Standardwert, wenn die Eigenschaft nicht gefunden wird
     * @return Der Wert als Double
     */
    private double getDoubleProperty(Map<String, Object> properties, String key, double defaultValue) {
        Object value = properties.get(key);
        
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                LOGGER.warn("Konnte Eigenschaft {} nicht als Double parsen: {}", key, value);
                return defaultValue;
            }
        }
        
        return defaultValue;
    }
    
    // RemoteService-Methoden, die für die Trader-Proxy-Funktionalität verwendet werden
    
    @Override
    public String request() throws ServiceException {
        LOGGER.info("TraderProxy: Einfache Anfrage empfangen");
        throw new ServiceException("Einfache Anfragen werden nicht unterstützt, bitte verwenden Sie komplexe Anfragen",
                ServiceException.ErrorType.INVALID_REQUEST);
    }
    
    @Override
    public String request(String parameter) throws ServiceException {
        LOGGER.info("TraderProxy: Anfrage mit Parameter empfangen: '{}'", parameter);
        throw new ServiceException("Parametrisierte Anfragen werden nicht unterstützt, bitte verwenden Sie komplexe Anfragen",
                ServiceException.ErrorType.INVALID_REQUEST);
    }
    
    @Override
    public String complexRequest(int id, String data, String[] options) throws ServiceException {
        LOGGER.info("TraderProxy: Komplexe Anfrage empfangen (ID: {})", id);
        
        if (options.length < 1) {
            throw new ServiceException("Ungültige Anzahl von Optionen", 
                    ServiceException.ErrorType.INVALID_REQUEST);
        }
        
        String operation = options[0];
        
        switch (operation) {
            case "find":
                // Format: [0]=find, [1]=serviceType, [2..n]=propertyKey:propertyValue
                if (options.length < 2) {
                    throw new ServiceException("Ungültiges Format für Suchanfrage", 
                            ServiceException.ErrorType.INVALID_REQUEST);
                }
                
                String serviceType = options[1];
                Map<String, Object> requiredProperties = new HashMap<>();
                
                // Parse zusätzliche Eigenschaften
                for (int i = 2; i < options.length; i++) {
                    String property = options[i];
                    String[] parts = property.split(":", 2);
                    if (parts.length == 2) {
                        requiredProperties.put(parts[0], parts[1]);
                    }
                }
                
                List<ServiceID> foundServices = findServices(serviceType, requiredProperties);
                return formatServiceList(foundServices);
                
            case "select":
                // Format: [0]=select, [1]=serviceType, [2]=qos1,qos2,..., [3..n]=propertyKey:propertyValue
                if (options.length < 3) {
                    throw new ServiceException("Ungültiges Format für Auswahlabfrage", 
                            ServiceException.ErrorType.INVALID_REQUEST);
                }
                
                serviceType = options[1];
                String[] qosStrings = options[2].split(",");
                List<QualityOfService> qosList = new ArrayList<>();
                
                for (String qosString : qosStrings) {
                    try {
                        QualityOfService qos = QualityOfService.valueOf(qosString.toUpperCase());
                        qosList.add(qos);
                    } catch (IllegalArgumentException e) {
                        LOGGER.warn("Unbekanntes QoS-Attribut: {}", qosString);
                    }
                }
                
                requiredProperties = new HashMap<>();
                
                // Parse zusätzliche Eigenschaften
                for (int i = 3; i < options.length; i++) {
                    String property = options[i];
                    String[] parts = property.split(":", 2);
                    if (parts.length == 2) {
                        requiredProperties.put(parts[0], parts[1]);
                    }
                }
                
                ServiceID bestService = selectBestService(serviceType, requiredProperties, qosList);
                return bestService != null ? bestService.toString() : "Kein passender Dienst gefunden";
                
            case "invoke":
                // Format: [0]=invoke, [1]=serviceType, [2]=provider
                if (options.length < 3) {
                    throw new ServiceException("Ungültiges Format für Dienstaufrufanfrage", 
                            ServiceException.ErrorType.INVALID_REQUEST);
                }
                
                serviceType = options[1];
                String provider = options[2];
                
                ServiceID serviceId = new ServiceID(serviceType, provider);
                return invokeService(serviceId, data);
                
            case "rate":
                // Format: [0]=rate, [1]=serviceType, [2]=provider, [3]=clientId, [4]=rating
                if (options.length < 5) {
                    throw new ServiceException("Ungültiges Format für Bewertungsanfrage", 
                            ServiceException.ErrorType.INVALID_REQUEST);
                }
                
                serviceType = options[1];
                provider = options[2];
                String clientId = options[3];
                double rating;
                
                try {
                    rating = Double.parseDouble(options[4]);
                } catch (NumberFormatException e) {
                    throw new ServiceException("Ungültiges Format für Bewertung: " + options[4], 
                            ServiceException.ErrorType.INVALID_REQUEST);
                }
                
                serviceId = new ServiceID(serviceType, provider);
                String comment = data != null ? data : "";
                
                rateService(serviceId, clientId, rating, comment);
                return "Dienst erfolgreich bewertet";
                
            default:
                throw new ServiceException("Unbekannte Operation: " + operation, 
                        ServiceException.ErrorType.INVALID_REQUEST);
        }
    }
    
    /**
     * Formatiert eine Liste von Dienst-IDs als String.
     * 
     * @param serviceIds Die Liste der Dienst-IDs
     * @return Ein formatierter String mit den Dienst-IDs
     */
    private String formatServiceList(List<ServiceID> serviceIds) {
        if (serviceIds.isEmpty()) {
            return "Keine Dienste gefunden";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Gefundene Dienste (").append(serviceIds.size()).append("):\n");
        
        for (int i = 0; i < serviceIds.size(); i++) {
            ServiceID serviceId = serviceIds.get(i);
            ServiceInfo info = services.get(serviceId);
            
            sb.append(i + 1).append(". ").append(serviceId);
            
            if (info != null) {
                Object category = info.getProperties().get("category");
                if (category != null) {
                    sb.append(" (").append(category).append(")");
                }
            }
            
            sb.append("\n");
        }
        
        return sb.toString();
    }
}