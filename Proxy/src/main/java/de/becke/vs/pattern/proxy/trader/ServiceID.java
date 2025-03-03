package de.becke.vs.pattern.proxy.trader;

import java.util.Objects;

/**
 * Stellt eine eindeutige Kennung für einen Dienst im Trader-Proxy dar.
 * 
 * Diese Klasse kombiniert Diensttyp und Anbieter, um einen Dienst eindeutig zu identifizieren.
 */
public class ServiceID {
    
    private final String serviceType;
    private final String provider;
    
    /**
     * Erstellt eine neue Dienst-ID.
     * 
     * @param serviceType Der Typ des Dienstes
     * @param provider Der Anbieter des Dienstes
     */
    public ServiceID(String serviceType, String provider) {
        this.serviceType = serviceType;
        this.provider = provider;
    }
    
    /**
     * Gibt den Typ des Dienstes zurück.
     * 
     * @return Der Diensttyp
     */
    public String getServiceType() {
        return serviceType;
    }
    
    /**
     * Gibt den Anbieter des Dienstes zurück.
     * 
     * @return Der Dienstanbieter
     */
    public String getProvider() {
        return provider;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        ServiceID serviceID = (ServiceID) o;
        return Objects.equals(serviceType, serviceID.serviceType) &&
                Objects.equals(provider, serviceID.provider);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(serviceType, provider);
    }
    
    @Override
    public String toString() {
        return serviceType + ":" + provider;
    }
}