package de.becke.vs.pattern.proxy.broker;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Enthält Informationen über einen Client, der beim Broker registriert ist.
 * 
 * Diese Klasse speichert die Client-ID und die Berechtigungen, die dem Client für
 * verschiedene Dienstkategorien gewährt wurden.
 */
public class ClientInfo {
    
    private final String clientId;
    private final String clientName;
    private final Set<String> permissions;
    
    /**
     * Erstellt eine neue ClientInfo-Instanz mit automatisch generierter ID.
     * 
     * @param clientName Der Name des Clients
     */
    public ClientInfo(String clientName) {
        this(UUID.randomUUID().toString(), clientName, new HashSet<>());
    }
    
    /**
     * Erstellt eine neue ClientInfo-Instanz mit spezifischer ID.
     * 
     * @param clientId Die ID des Clients
     * @param clientName Der Name des Clients
     * @param permissions Die Berechtigungen des Clients
     */
    public ClientInfo(String clientId, String clientName, Set<String> permissions) {
        this.clientId = clientId;
        this.clientName = clientName;
        this.permissions = new HashSet<>(permissions);
    }
    
    /**
     * Gibt die ID des Clients zurück.
     * 
     * @return Die Client-ID
     */
    public String getClientId() {
        return clientId;
    }
    
    /**
     * Gibt den Namen des Clients zurück.
     * 
     * @return Der Client-Name
     */
    public String getClientName() {
        return clientName;
    }
    
    /**
     * Gibt die Berechtigungen des Clients zurück.
     * 
     * @return Die Berechtigungen des Clients (als unveränderbare Menge)
     */
    public Set<String> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }
    
    /**
     * Fügt eine Berechtigung für eine Dienstkategorie hinzu.
     * 
     * @param category Die Dienstkategorie
     * @return true, wenn die Berechtigung hinzugefügt wurde, sonst false
     */
    public boolean addPermission(String category) {
        return permissions.add(category);
    }
    
    /**
     * Entfernt eine Berechtigung für eine Dienstkategorie.
     * 
     * @param category Die Dienstkategorie
     * @return true, wenn die Berechtigung entfernt wurde, sonst false
     */
    public boolean removePermission(String category) {
        return permissions.remove(category);
    }
    
    /**
     * Überprüft, ob der Client Zugriff auf eine bestimmte Dienstkategorie hat.
     * 
     * @param category Die zu überprüfende Dienstkategorie
     * @return true, wenn der Client Zugriff hat, sonst false
     */
    public boolean hasPermission(String category) {
        return permissions.contains(category);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        ClientInfo that = (ClientInfo) o;
        return Objects.equals(clientId, that.clientId);
    }
    
    @Override
    public int hashCode() {
        return clientId != null ? clientId.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return "ClientInfo{" +
                "clientId='" + clientId + '\'' +
                ", clientName='" + clientName + '\'' +
                ", permissions=" + permissions +
                '}';
    }
}