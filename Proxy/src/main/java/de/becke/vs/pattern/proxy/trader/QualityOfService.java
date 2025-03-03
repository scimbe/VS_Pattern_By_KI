package de.becke.vs.pattern.proxy.trader;

/**
 * Aufzählung der unterstützten Qualitätsattribute für die Dienstauswahl.
 * 
 * Diese Enum definiert verschiedene Qualitätsattribute, die bei der Auswahl
 * des besten Dienstes durch den Trader berücksichtigt werden können.
 */
public enum QualityOfService {
    
    /**
     * Die Verfügbarkeit des Dienstes, typischerweise als Prozentsatz der Zeit,
     * in der der Dienst erreichbar ist.
     */
    AVAILABILITY,
    
    /**
     * Die Leistung des Dienstes, typischerweise gemessen in Antwortzeit
     * oder Durchsatz.
     */
    PERFORMANCE,
    
    /**
     * Die Zuverlässigkeit des Dienstes, typischerweise gemessen an der
     * Fehlerrate oder der mittleren Zeit zwischen Ausfällen.
     */
    RELIABILITY,
    
    /**
     * Die von Benutzern abgegebenen Bewertungen für den Dienst.
     */
    USER_RATING,
    
    /**
     * Die Sicherheit des Dienstes, z.B. Verschlüsselung oder Authentifizierung.
     */
    SECURITY,
    
    /**
     * Die Kosten für die Nutzung des Dienstes.
     */
    COST,
    
    /**
     * Die geografische Nähe des Dienstes zum Client.
     */
    PROXIMITY
}