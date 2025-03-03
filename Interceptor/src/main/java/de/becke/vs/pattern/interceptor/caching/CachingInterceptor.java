package de.becke.vs.pattern.interceptor.caching;

import de.becke.vs.pattern.interceptor.core.Context;
import de.becke.vs.pattern.interceptor.core.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Ein Interceptor, der Ergebnisse von Operationen zwischenspeichert.
 * 
 * Diese Klasse implementiert einen einfachen In-Memory-Cache für Operationsergebnisse,
 * um wiederholte Ausführungen derselben Operation zu optimieren.
 */
public class CachingInterceptor implements Interceptor {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CachingInterceptor.class);
    
    // Cache für die Ergebnisse
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    
    // Standardablaufzeit in Millisekunden (5 Minuten)
    private static final long DEFAULT_TTL = 5 * 60 * 1000;
    
    private final long timeToLiveMs;
    
    /**
     * Erstellt einen neuen CachingInterceptor mit Standardablaufzeit.
     */
    public CachingInterceptor() {
        this(DEFAULT_TTL);
    }
    
    /**
     * Erstellt einen neuen CachingInterceptor mit benutzerdefinierter Ablaufzeit.
     * 
     * @param timeToLiveMs Die Ablaufzeit in Millisekunden
     */
    public CachingInterceptor(long timeToLiveMs) {
        this.timeToLiveMs = timeToLiveMs;
    }
    
    @Override
    public boolean preProcess(Context context) {
        // Prüfe, ob die Operation gecached werden soll
        Boolean cacheable = context.getAttributeAs("caching.cacheable", Boolean.class);
        if (cacheable == null || !cacheable) {
            return true; // Wenn nicht cacheable, normal fortfahren
        }
        
        // Generiere einen Cache-Schlüssel basierend auf dem Input
        String cacheKey = generateCacheKey(context);
        context.setAttribute("caching.key", cacheKey);
        
        // Suche im Cache
        CacheEntry entry = cache.get(cacheKey);
        if (entry != null && !entry.isExpired()) {
            // Cache-Treffer
            LOGGER.debug("Cache-Treffer für Operation [ID: {}] mit Schlüssel: {}", 
                    context.getExecutionId(), cacheKey);
            
            // Setze das Ergebnis aus dem Cache
            context.setResult(entry.getValue());
            context.setAttribute("caching.hit", true);
            
            // Springe zum Post-Processing
            return false;
        }
        
        // Cache-Miss
        LOGGER.debug("Cache-Miss für Operation [ID: {}] mit Schlüssel: {}", 
                context.getExecutionId(), cacheKey);
        context.setAttribute("caching.hit", false);
        return true;
    }
    
    @Override
    public void postProcess(Context context) {
        // Prüfe, ob die Operation gecached werden soll
        Boolean cacheable = context.getAttributeAs("caching.cacheable", Boolean.class);
        if (cacheable == null || !cacheable) {
            return; // Wenn nicht cacheable, nichts zu tun
        }
        
        // Prüfe, ob es ein Cache-Treffer war
        Boolean cacheHit = context.getAttributeAs("caching.hit", Boolean.class);
        if (cacheHit != null && cacheHit) {
            return; // Wenn es ein Cache-Treffer war, nichts zu tun
        }
        
        // Hole den Cache-Schlüssel
        String cacheKey = context.getAttributeAs("caching.key", String.class);
        if (cacheKey == null) {
            cacheKey = generateCacheKey(context);
        }
        
        // Speichere das Ergebnis im Cache
        if (context.isSuccessful() && context.getResult() != null) {
            LOGGER.debug("Speichere Ergebnis im Cache für Operation [ID: {}] mit Schlüssel: {}", 
                    context.getExecutionId(), cacheKey);
            cache.put(cacheKey, new CacheEntry(context.getResult(), timeToLiveMs));
        }
    }
    
    @Override
    public boolean handleException(Context context, Exception exception) {
        // Wir behandeln keine Exceptions
        return false;
    }
    
    /**
     * Generiert einen Cache-Schlüssel basierend auf dem Kontext.
     * 
     * @param context Der Kontext
     * @return Der generierte Cache-Schlüssel
     */
    private String generateCacheKey(Context context) {
        Object input = context.getInput();
        return input != null ? input.hashCode() + "" : "null";
    }
    
    /**
     * Entfernt einen Eintrag aus dem Cache.
     * 
     * @param cacheKey Der Schlüssel des zu entfernenden Eintrags
     * @return true, wenn ein Eintrag entfernt wurde, sonst false
     */
    public boolean invalidate(String cacheKey) {
        return cache.remove(cacheKey) != null;
    }
    
    /**
     * Leert den gesamten Cache.
     */
    public void invalidateAll() {
        LOGGER.info("Cache wird geleert");
        cache.clear();
    }
    
    /**
     * Entfernt abgelaufene Einträge aus dem Cache.
     */
    public void cleanUp() {
        LOGGER.debug("Entferne abgelaufene Cache-Einträge");
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
    
    /**
     * Eine Klasse, die einen Cache-Eintrag mit Ablaufzeit darstellt.
     */
    private static class CacheEntry {
        private final Object value;
        private final long expiryTime;
        
        /**
         * Erstellt einen neuen Cache-Eintrag.
         * 
         * @param value Der zu speichernde Wert
         * @param timeToLiveMs Die Lebensdauer in Millisekunden
         */
        public CacheEntry(Object value, long timeToLiveMs) {
            this.value = value;
            this.expiryTime = System.currentTimeMillis() + timeToLiveMs;
        }
        
        /**
         * Gibt den gespeicherten Wert zurück.
         * 
         * @return Der Wert
         */
        public Object getValue() {
            return value;
        }
        
        /**
         * Prüft, ob der Eintrag abgelaufen ist.
         * 
         * @return true, wenn der Eintrag abgelaufen ist, sonst false
         */
        public boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }
}