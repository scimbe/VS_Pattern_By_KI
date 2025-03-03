package de.becke.vs.pattern.proxy.caching;

import java.util.Arrays;
import java.util.Objects;

/**
 * Schlüsselklasse für den Cache des CachingProxy.
 * 
 * Diese Klasse repräsentiert eindeutige Schlüssel für verschiedene Arten von
 * Anfragen an den RemoteService und implementiert entsprechende equals- und
 * hashCode-Methoden für die korrekte Funktionsweise des Caches.
 */
public class CacheKey {
    
    // Konstante für den Anfrage-Typ
    private static final int SIMPLE_REQUEST = 1;
    private static final int PARAMETERIZED_REQUEST = 2;
    private static final int COMPLEX_REQUEST = 3;
    
    private final int requestType;
    private final Integer id;
    private final String parameter;
    private final String data;
    private final String[] options;
    
    /**
     * Privater Konstruktor für einfache Anfragen.
     */
    private CacheKey() {
        this.requestType = SIMPLE_REQUEST;
        this.id = null;
        this.parameter = null;
        this.data = null;
        this.options = null;
    }
    
    /**
     * Privater Konstruktor für parametrisierte Anfragen.
     * 
     * @param parameter Der Anfrageparameter
     */
    private CacheKey(String parameter) {
        this.requestType = PARAMETERIZED_REQUEST;
        this.id = null;
        this.parameter = parameter;
        this.data = null;
        this.options = null;
    }
    
    /**
     * Privater Konstruktor für komplexe Anfragen.
     * 
     * @param id Die Anfrage-ID
     * @param data Die Anfragedaten
     * @param options Die Anfrageoptionen
     */
    private CacheKey(int id, String data, String[] options) {
        this.requestType = COMPLEX_REQUEST;
        this.id = id;
        this.parameter = null;
        this.data = data;
        this.options = options;
    }
    
    /**
     * Erstellt einen Cache-Schlüssel für eine einfache Anfrage.
     * 
     * @return Der erstellte Cache-Schlüssel
     */
    public static CacheKey forSimpleRequest() {
        return new CacheKey();
    }
    
    /**
     * Erstellt einen Cache-Schlüssel für eine parametrisierte Anfrage.
     * 
     * @param parameter Der Anfrageparameter
     * @return Der erstellte Cache-Schlüssel
     */
    public static CacheKey forParameterizedRequest(String parameter) {
        return new CacheKey(parameter);
    }
    
    /**
     * Erstellt einen Cache-Schlüssel für eine komplexe Anfrage.
     * 
     * @param id Die Anfrage-ID
     * @param data Die Anfragedaten
     * @param options Die Anfrageoptionen
     * @return Der erstellte Cache-Schlüssel
     */
    public static CacheKey forComplexRequest(int id, String data, String[] options) {
        return new CacheKey(id, data, options);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        CacheKey cacheKey = (CacheKey) o;
        
        if (requestType != cacheKey.requestType) return false;
        
        // Vergleich basierend auf dem Anfrage-Typ
        switch (requestType) {
            case SIMPLE_REQUEST:
                return true;
            case PARAMETERIZED_REQUEST:
                return Objects.equals(parameter, cacheKey.parameter);
            case COMPLEX_REQUEST:
                return Objects.equals(id, cacheKey.id) &&
                        Objects.equals(data, cacheKey.data) &&
                        Arrays.equals(options, cacheKey.options);
            default:
                return false;
        }
    }
    
    @Override
    public int hashCode() {
        int result = requestType;
        
        // Hash basierend auf dem Anfrage-Typ
        switch (requestType) {
            case SIMPLE_REQUEST:
                break;
            case PARAMETERIZED_REQUEST:
                result = 31 * result + (parameter != null ? parameter.hashCode() : 0);
                break;
            case COMPLEX_REQUEST:
                result = 31 * result + (id != null ? id.hashCode() : 0);
                result = 31 * result + (data != null ? data.hashCode() : 0);
                result = 31 * result + (options != null ? Arrays.hashCode(options) : 0);
                break;
        }
        
        return result;
    }
    
    @Override
    public String toString() {
        switch (requestType) {
            case SIMPLE_REQUEST:
                return "CacheKey{SIMPLE_REQUEST}";
            case PARAMETERIZED_REQUEST:
                return "CacheKey{PARAMETERIZED_REQUEST, parameter='" + parameter + "'}";
            case COMPLEX_REQUEST:
                return "CacheKey{COMPLEX_REQUEST, id=" + id + ", data='" + data + "', options=" + 
                        (options != null ? Arrays.toString(options) : "null") + "}";
            default:
                return "CacheKey{UNKNOWN}";
        }
    }
}