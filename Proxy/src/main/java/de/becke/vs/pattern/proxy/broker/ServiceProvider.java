package de.becke.vs.pattern.proxy.broker;

import de.becke.vs.pattern.proxy.common.RemoteService;

import java.util.Set;

/**
 * Schnittstelle für einen Dienstanbieter im Broker-Proxy-Muster.
 * 
 * Ein ServiceProvider stellt eine Sammlung von Diensten bereit und wird beim
 * Broker registriert, um Anfragen von Clients zu bearbeiten.
 */
public interface ServiceProvider {
    
    /**
     * Gibt die eindeutige ID des Dienstanbieters zurück.
     * 
     * @return Die Provider-ID
     */
    String getProviderId();
    
    /**
     * Gibt eine Liste der verfügbaren Dienste zurück.
     * 
     * @return Die Namen der verfügbaren Dienste
     */
    Set<String> getAvailableServices();
    
    /**
     * Überprüft, ob der Anbieter einen bestimmten Dienst anbietet.
     * 
     * @param serviceName Der Name des Dienstes
     * @return true, wenn der Dienst angeboten wird, sonst false
     */
    boolean providesService(String serviceName);
    
    /**
     * Gibt den RemoteService für einen bestimmten Dienst zurück.
     * 
     * @param serviceName Der Name des Dienstes
     * @return Der RemoteService für den angegebenen Dienst
     * @throws IllegalArgumentException Wenn der Dienst nicht verfügbar ist
     */
    RemoteService getService(String serviceName);
}