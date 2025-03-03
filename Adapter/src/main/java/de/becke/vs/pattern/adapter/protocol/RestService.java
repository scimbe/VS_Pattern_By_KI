package de.becke.vs.pattern.adapter.protocol;

/**
 * Repräsentiert einen REST-Service mit Standard HTTP-Methoden.
 * Diese Schnittstelle definiert die vom Client erwartete Zielschnittstelle.
 */
public interface RestService {
    
    /**
     * Führt eine GET-Anfrage für eine bestimmte Ressource aus.
     * 
     * @param resourceId Die ID der anzufordernden Ressource
     * @return Eine REST-Antwort mit den Ressourcendaten
     */
    RestResponse get(String resourceId);
    
    /**
     * Führt eine POST-Anfrage zum Erstellen einer neuen Ressource aus.
     * 
     * @param data Die Daten für die neue Ressource
     * @return Eine REST-Antwort mit der erstellten Ressource
     */
    RestResponse post(String data);
    
    /**
     * Führt eine PUT-Anfrage zum Aktualisieren einer bestehenden Ressource aus.
     * 
     * @param resourceId Die ID der zu aktualisierenden Ressource
     * @param data Die aktualisierten Daten
     * @return Eine REST-Antwort mit der aktualisierten Ressource
     */
    RestResponse put(String resourceId, String data);
    
    /**
     * Führt eine DELETE-Anfrage zum Löschen einer Ressource aus.
     * 
     * @param resourceId Die ID der zu löschenden Ressource
     * @return Eine REST-Antwort mit dem Löschstatus
     */
    RestResponse delete(String resourceId);
}