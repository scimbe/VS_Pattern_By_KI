package de.becke.vs.pattern.proxy.caching;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import de.becke.vs.pattern.proxy.common.RemoteService;
import de.becke.vs.pattern.proxy.common.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Ein Caching-Proxy für RemoteServices, der Anfrageergebnisse zwischenspeichert.
 * 
 * Diese Implementierung speichert die Ergebnisse von Service-Anfragen zwischen,
 * um wiederholte Anfragen effizienter zu bedienen und die Last auf dem Zieldienst
 * zu reduzieren.
 */
public class CachingProxy implements RemoteService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CachingProxy.class);
    
    private final RemoteService targetService;
    private final Cache<CacheKey, String> cache;
    
    // Cache-Statistik
    private final AtomicInteger cacheHits = new AtomicInteger(0);
    private final AtomicInteger cacheMisses = new AtomicInteger(0);
    
    /**
     * Erstellt einen Caching-Proxy mit Standardkonfiguration.
     * 
     * @param targetService Der Zieldienst, an den Anfragen bei Cache-Misses weitergeleitet werden
     */
    public CachingProxy(RemoteService targetService) {
        this(targetService, 1000, 10, TimeUnit.MINUTES);
    }
    
    /**
     * Erstellt einen Caching-Proxy mit angepasster Konfiguration.
     * 
     * @param targetService Der Zieldienst, an den Anfragen bei Cache-Misses weitergeleitet werden
     * @param maximumSize Die maximale Anzahl von Einträgen im Cache
     * @param expireAfter Die Ablaufzeit der Cache-Einträge
     * @param timeUnit Die Zeiteinheit für die Ablaufzeit
     */
    public CachingProxy(RemoteService targetService, long maximumSize, long expireAfter, TimeUnit timeUnit) {
        this.targetService = targetService;
        
        // Konfiguriere den Cache
        this.cache = Caffeine.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterWrite(expireAfter, timeUnit)
                .recordStats()
                .build();
        
        LOGGER.info("CachingProxy initialisiert mit maximaler Größe {}, Ablaufzeit {} {}", 
                maximumSize, expireAfter, timeUnit);
    }
    
    @Override
    public String request() throws ServiceException {
        LOGGER.info("CachingProxy: Einfache Anfrage empfangen");
        
        // Erstelle einen Cache-Schlüssel für die Anfrage
        CacheKey cacheKey = CacheKey.forSimpleRequest();
        
        // Versuche, das Ergebnis aus dem Cache zu holen
        String cachedResult = cache.getIfPresent(cacheKey);
        
        if (cachedResult != null) {
            // Cache-Treffer
            cacheHits.incrementAndGet();
            LOGGER.info("CachingProxy: Cache-Treffer für einfache Anfrage");
            return cachedResult;
        }
        
        // Cache-Miss: Leite die Anfrage an den Zieldienst weiter
        cacheMisses.incrementAndGet();
        LOGGER.info("CachingProxy: Cache-Miss für einfache Anfrage, leite weiter an Zieldienst");
        
        String result = targetService.request();
        
        // Speichere das Ergebnis im Cache
        cache.put(cacheKey, result);
        LOGGER.info("CachingProxy: Ergebnis im Cache gespeichert");
        
        return result;
    }
    
    @Override
    public String request(String parameter) throws ServiceException {
        LOGGER.info("CachingProxy: Anfrage mit Parameter '{}' empfangen", parameter);
        
        // Erstelle einen Cache-Schlüssel für die Anfrage
        CacheKey cacheKey = CacheKey.forParameterizedRequest(parameter);
        
        // Versuche, das Ergebnis aus dem Cache zu holen
        String cachedResult = cache.getIfPresent(cacheKey);
        
        if (cachedResult != null) {
            // Cache-Treffer
            cacheHits.incrementAndGet();
            LOGGER.info("CachingProxy: Cache-Treffer für Anfrage mit Parameter '{}'", parameter);
            return cachedResult;
        }
        
        // Cache-Miss: Leite die Anfrage an den Zieldienst weiter
        cacheMisses.incrementAndGet();
        LOGGER.info("CachingProxy: Cache-Miss für Anfrage mit Parameter '{}', leite weiter an Zieldienst", parameter);
        
        String result = targetService.request(parameter);
        
        // Speichere das Ergebnis im Cache
        cache.put(cacheKey, result);
        LOGGER.info("CachingProxy: Ergebnis für Parameter '{}' im Cache gespeichert", parameter);
        
        return result;
    }
    
    @Override
    public String complexRequest(int id, String data, String[] options) throws ServiceException {
        LOGGER.info("CachingProxy: Komplexe Anfrage empfangen (ID: {}, Daten: {}, Optionen: {})",
                id, data, Arrays.toString(options));
        
        // Erstelle einen Cache-Schlüssel für die komplexe Anfrage
        CacheKey cacheKey = CacheKey.forComplexRequest(id, data, options);
        
        // Versuche, das Ergebnis aus dem Cache zu holen
        String cachedResult = cache.getIfPresent(cacheKey);
        
        if (cachedResult != null) {
            // Cache-Treffer
            cacheHits.incrementAndGet();
            LOGGER.info("CachingProxy: Cache-Treffer für komplexe Anfrage (ID: {})", id);
            return cachedResult;
        }
        
        // Cache-Miss: Leite die Anfrage an den Zieldienst weiter
        cacheMisses.incrementAndGet();
        LOGGER.info("CachingProxy: Cache-Miss für komplexe Anfrage (ID: {}), leite weiter an Zieldienst", id);
        
        String result = targetService.complexRequest(id, data, options);
        
        // Speichere das Ergebnis im Cache
        cache.put(cacheKey, result);
        LOGGER.info("CachingProxy: Ergebnis für komplexe Anfrage (ID: {}) im Cache gespeichert", id);
        
        return result;
    }
    
    /**
     * Leert den Cache.
     */
    public void clearCache() {
        LOGGER.info("CachingProxy: Cache wird geleert");
        cache.invalidateAll();
    }
    
    /**
     * Entfernt einen bestimmten Eintrag aus dem Cache.
     * 
     * @param cacheKey Der zu entfernende Cache-Schlüssel
     */
    public void invalidateCacheEntry(CacheKey cacheKey) {
        LOGGER.info("CachingProxy: Cache-Eintrag wird entfernt: {}", cacheKey);
        cache.invalidate(cacheKey);
    }
    
    /**
     * Gibt Cache-Statistiken zurück.
     * 
     * @return Ein String mit Cache-Statistiken
     */
    public String getCacheStatistics() {
        double hitRate = (double) cacheHits.get() / (cacheHits.get() + cacheMisses.get());
        
        return String.format(
                "Cache-Statistik:\n" +
                "  Treffer: %d\n" +
                "  Fehlschläge: %d\n" +
                "  Trefferrate: %.2f%%\n" +
                "  Cache-Größe: %d",
                cacheHits.get(),
                cacheMisses.get(),
                hitRate * 100,
                cache.estimatedSize()
        );
    }
}