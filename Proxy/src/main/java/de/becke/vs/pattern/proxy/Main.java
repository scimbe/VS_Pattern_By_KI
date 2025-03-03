package de.becke.vs.pattern.proxy;

import de.becke.vs.pattern.proxy.broker.BrokerProxy;
import de.becke.vs.pattern.proxy.broker.ClientInfo;
import de.becke.vs.pattern.proxy.broker.ServiceProvider;
import de.becke.vs.pattern.proxy.caching.CacheKey;
import de.becke.vs.pattern.proxy.caching.CachingProxy;
import de.becke.vs.pattern.proxy.common.RealRemoteService;
import de.becke.vs.pattern.proxy.common.RemoteService;
import de.becke.vs.pattern.proxy.common.ServiceException;
import de.becke.vs.pattern.proxy.forward.AccessController;
import de.becke.vs.pattern.proxy.forward.ContentFilter;
import de.becke.vs.pattern.proxy.forward.ForwardProxy;
import de.becke.vs.pattern.proxy.loadbalancing.LoadBalancingProxy;
import de.becke.vs.pattern.proxy.reverse.ReverseProxy;
import de.becke.vs.pattern.proxy.trader.QualityOfService;
import de.becke.vs.pattern.proxy.trader.ServiceID;
import de.becke.vs.pattern.proxy.trader.TraderProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Hauptklasse zur Demonstration der verschiedenen Proxy-Pattern-Implementierungen.
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        LOGGER.info("Starte Demonstration des Proxy-Patterns");

        // Demonstration der verschiedenen Proxy-Arten
        demonstrateForwardProxy();
        demonstrateCachingProxy();
        demonstrateLoadBalancingProxy();
        demonstrateReverseProxy();
        demonstrateBrokerProxy();
        demonstrateTraderProxy();

        LOGGER.info("Demonstration abgeschlossen");
    }

    /**
     * Demonstriert die Verwendung eines Forward-Proxys.
     */
    private static void demonstrateForwardProxy() {
        LOGGER.info("\n--- Forward-Proxy Demonstration ---");

        // Erstelle den realen Dienst
        RealRemoteService realService = new RealRemoteService("TargetService");

        // Erstelle einen einfachen AccessController
        AccessController accessController = new AccessController() {
            @Override
            public boolean checkAccess() {
                return true;
            }

            @Override
            public boolean checkAccess(String parameter) {
                // Erlaube nur Parameter ohne "restricted"
                return parameter == null || !parameter.contains("restricted");
            }

            @Override
            public boolean checkComplexAccess(int id, String data, String[] options) {
                // Erlaube nur IDs größer als 0
                return id > 0;
            }
        };

        // Erstelle einen einfachen ContentFilter
        ContentFilter contentFilter = new ContentFilter() {
            @Override
            public String filterContent(String content) {
                // Ersetze alle Vorkommen von "vertraulich" durch "***"
                return content != null ? content.replaceAll("vertraulich", "***") : null;
            }

            @Override
            public boolean isContentAllowed(String content) {
                // Erlaube nur Inhalte ohne "gefährlich"
                return content == null || !content.contains("gefährlich");
            }
        };

        // Erstelle den Forward-Proxy
        ForwardProxy forwardProxy = new ForwardProxy(realService, accessController, contentFilter, true);

        try {
            // Führe erlaubte Anfragen aus
            String result1 = forwardProxy.request("normaler Parameter");
            LOGGER.info("Ergebnis der erlaubten Anfrage: {}", result1);

            String result2 = forwardProxy.request("Dies ist ein vertraulich Parameter");
            LOGGER.info("Ergebnis mit gefiltertem Inhalt: {}", result2);

            // Führe komplexe Anfrage aus
            String result3 = forwardProxy.complexRequest(1, "Daten", new String[]{"Option1", "Option2"});
            LOGGER.info("Ergebnis der komplexen Anfrage: {}", result3);

            // Versuche, eine abgelehnte Anfrage auszuführen
            try {
                forwardProxy.request("restricted Parameter");
            } catch (ServiceException e) {
                LOGGER.info("Erwartete Ausnahme für abgelehnte Anfrage: {}", e.getMessage());
            }

            // Zeige Statistiken
            LOGGER.info(forwardProxy.getStatistics());

        } catch (ServiceException e) {
            LOGGER.error("Fehler bei der Forward-Proxy-Demonstration: {}", e.getMessage());
        }
    }

    /**
     * Demonstriert die Verwendung eines Caching-Proxys.
     */
    private static void demonstrateCachingProxy() {
        LOGGER.info("\n--- Caching-Proxy Demonstration ---");

        // Erstelle den realen Dienst
        RealRemoteService realService = new RealRemoteService("CachedService", true, 500);

        // Erstelle den Caching-Proxy mit kurzer TTL für die Demonstration
        CachingProxy cachingProxy = new CachingProxy(realService, 100, 30, TimeUnit.SECONDS);

        try {
            // Erste Anfrage (Cache-Miss)
            LOGGER.info("Führe erste Anfrage aus (Cache-Miss)");
            long startTime = System.currentTimeMillis();
            String result1 = cachingProxy.request("Daten");
            long duration1 = System.currentTimeMillis() - startTime;
            LOGGER.info("Erste Anfrage dauerte {}ms: {}", duration1, result1);

            // Zweite identische Anfrage (Cache-Hit)
            LOGGER.info("Führe zweite identische Anfrage aus (Cache-Hit)");
            startTime = System.currentTimeMillis();
            String result2 = cachingProxy.request("Daten");
            long duration2 = System.currentTimeMillis() - startTime;
            LOGGER.info("Zweite Anfrage dauerte {}ms: {}", duration2, result2);

            // Andere Anfrage (Cache-Miss)
            LOGGER.info("Führe andere Anfrage aus (Cache-Miss)");
            startTime = System.currentTimeMillis();
            String result3 = cachingProxy.request("Andere Daten");
            long duration3 = System.currentTimeMillis() - startTime;
            LOGGER.info("Andere Anfrage dauerte {}ms: {}", duration3, result3);

            // Lösche einen Cache-Eintrag
            LOGGER.info("Lösche Cache-Eintrag für 'Daten'");
            cachingProxy.invalidateCacheEntry(CacheKey.forParameterizedRequest("Daten"));

            // Erneute Anfrage nach dem Löschen (Cache-Miss)
            LOGGER.info("Führe erneute Anfrage nach dem Löschen aus (Cache-Miss)");
            startTime = System.currentTimeMillis();
            String result4 = cachingProxy.request("Daten");
            long duration4 = System.currentTimeMillis() - startTime;
            LOGGER.info("Erneute Anfrage dauerte {}ms: {}", duration4, result4);

            // Zeige Cache-Statistiken
            LOGGER.info(cachingProxy.getCacheStatistics());

        } catch (ServiceException e) {
            LOGGER.error("Fehler bei der Caching-Proxy-Demonstration: {}", e.getMessage());
        }
    }

    /**
     * Demonstriert die Verwendung eines Load-Balancing-Proxys.
     */
    private static void demonstrateLoadBalancingProxy() {
        LOGGER.info("\n--- Load-Balancing-Proxy Demonstration ---");

        // Erstelle mehrere Backend-Dienste
        List<RemoteService> backendServices = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            backendServices.add(new RealRemoteService("Backend-" + i, false, 100));
        }

        // Erstelle den Load-Balancing-Proxy mit Round-Robin-Strategie
        LoadBalancingProxy loadBalancer = new LoadBalancingProxy(
                backendServices, LoadBalancingProxy.Strategy.ROUND_ROBIN);

        try {
            // Führe mehrere Anfragen aus, um das Round-Robin zu demonstrieren
            for (int i = 1; i <= 5; i++) {
                LOGGER.info("Anfrage {}", i);
                String result = loadBalancer.request("Daten");
                LOGGER.info("Ergebnis: {}", result);
            }

            // Füge einen neuen Backend-Dienst hinzu
            RemoteService newBackend = new RealRemoteService("Backend-4", false, 100);
            loadBalancer.addBackendService(newBackend);
            LOGGER.info("Neuer Backend-Dienst hinzugefügt");

            // Führe weitere Anfragen aus
            for (int i = 6; i <= 8; i++) {
                LOGGER.info("Anfrage {}", i);
                String result = loadBalancer.request("Daten");
                LOGGER.info("Ergebnis: {}", result);
            }

            // Entferne einen Backend-Dienst
            loadBalancer.removeBackendService(backendServices.get(0));
            LOGGER.info("Backend-Dienst entfernt");

            // Führe eine letzte Anfrage aus
            LOGGER.info("Letzte Anfrage");
            String result = loadBalancer.request("Daten");
            LOGGER.info("Ergebnis: {}", result);

        } catch (ServiceException e) {
            LOGGER.error("Fehler bei der Load-Balancing-Proxy-Demonstration: {}", e.getMessage());
        }
    }

    /**
     * Demonstriert die Verwendung eines Reverse-Proxys.
     */
    private static void demonstrateReverseProxy() {
        LOGGER.info("\n--- Reverse-Proxy Demonstration ---");

        // Erstelle den Reverse-Proxy mit SSL-Terminierung und Anfragenkompression
        ReverseProxy reverseProxy = new ReverseProxy(true, true, true);

        // Erstelle Backend-Dienste für verschiedene Pfade
        RemoteService userService = new RealRemoteService("UserService", false, 50);
        RemoteService productService = new RealRemoteService("ProductService", false, 75);
        RemoteService orderService = new RealRemoteService("OrderService", false, 100);

        // Registriere die Dienste beim Reverse-Proxy
        reverseProxy.registerService("/api/users", userService);
        reverseProxy.registerService("/api/products", productService);
        reverseProxy.registerService("/api/orders", orderService);

        // Setze einen Standarddienst
        reverseProxy.setDefaultService(new RealRemoteService("DefaultService", false, 25));

        try {
            // Führe Anfragen an verschiedene Pfade aus
            LOGGER.info("Anfrage an /api/users");
            String result1 = reverseProxy.request("/api/users");
            LOGGER.info("Ergebnis: {}", result1);

            LOGGER.info("Anfrage an /api/products");
            String result2 = reverseProxy.request("/api/products");
            LOGGER.info("Ergebnis: {}", result2);

            LOGGER.info("Anfrage an /api/orders");
            String result3 = reverseProxy.request("/api/orders");
            LOGGER.info("Ergebnis: {}", result3);

            // Anfrage an einen nicht registrierten Pfad (sollte an den Standarddienst gehen)
            LOGGER.info("Anfrage an nicht registrierten Pfad");
            String result4 = reverseProxy.request("/api/unknown");
            LOGGER.info("Ergebnis: {}", result4);

            // Komplexe Anfrage
            LOGGER.info("Komplexe Anfrage an /api/users");
            String result5 = reverseProxy.complexRequest(1, "/api/users", new String[]{"Option1", "Option2"});
            LOGGER.info("Ergebnis: {}", result5);

            // Zeige Statistiken
            LOGGER.info(reverseProxy.getStatistics());

        } catch (ServiceException e) {
            LOGGER.error("Fehler bei der Reverse-Proxy-Demonstration: {}", e.getMessage());
        }
    }

    /**
     * Demonstriert die Verwendung eines Broker-Proxys.
     */
    private static void demonstrateBrokerProxy() {
        LOGGER.info("\n--- Broker-Proxy Demonstration ---");

        // Erstelle den Broker-Proxy
        BrokerProxy broker = new BrokerProxy();

        // Erstelle und registriere Clients
        ClientInfo client1 = new ClientInfo("Client1");
        client1.addPermission("auth");
        client1.addPermission("data");

        ClientInfo client2 = new ClientInfo("Client2");
        client2.addPermission("data");

        String client1Id = broker.registerClient(client1);
        String client2Id = broker.registerClient(client2);

        // Erstelle und registriere Dienstanbieter
        ServiceProvider authProvider = createServiceProvider("AuthProvider", 
                Arrays.asList("login", "logout"));
        ServiceProvider dataProvider = createServiceProvider("DataProvider", 
                Arrays.asList("read", "write"));

        broker.registerServiceProvider("auth", authProvider);
        broker.registerServiceProvider("data", dataProvider);

        try {
            // Client1 verwendet Auth-Dienste
            LOGGER.info("Client1 verwendet Auth-Dienste");
            String result1 = broker.invokeService(client1Id, "auth", "login", "credentials");
            LOGGER.info("Ergebnis: {}", result1);

            // Client1 verwendet Data-Dienste
            LOGGER.info("Client1 verwendet Data-Dienste");
            String result2 = broker.invokeService(client1Id, "data", "read", "query");
            LOGGER.info("Ergebnis: {}", result2);

            // Client2 versucht, Auth-Dienste zu verwenden (sollte fehlschlagen)
            try {
                LOGGER.info("Client2 versucht, Auth-Dienste zu verwenden");
                broker.invokeService(client2Id, "auth", "login", "credentials");
            } catch (ServiceException e) {
                LOGGER.info("Erwartete Ausnahme: {}", e.getMessage());
            }

            // Client2 verwendet Data-Dienste
            LOGGER.info("Client2 verwendet Data-Dienste");
            String result3 = broker.invokeService(client2Id, "data", "write", "data");
            LOGGER.info("Ergebnis: {}", result3);

            // Versuch, einen nicht vorhandenen Dienst zu verwenden
            try {
                LOGGER.info("Versuch, einen nicht vorhandenen Dienst zu verwenden");
                broker.invokeService(client1Id, "data", "update", "data");
            } catch (ServiceException e) {
                LOGGER.info("Erwartete Ausnahme: {}", e.getMessage());
            }

        } catch (ServiceException e) {
            LOGGER.error("Fehler bei der Broker-Proxy-Demonstration: {}", e.getMessage());
        }
    }

    /**
     * Erstellt einen einfachen ServiceProvider mit den angegebenen Diensten.
     */
    private static ServiceProvider createServiceProvider(String providerId, List<String> serviceNames) {
        Map<String, RemoteService> services = new HashMap<>();
        for (String serviceName : serviceNames) {
            services.put(serviceName, new RealRemoteService(providerId + "-" + serviceName, false, 50));
        }

        return new ServiceProvider() {
            @Override
            public String getProviderId() {
                return providerId;
            }

            @Override
            public Set<String> getAvailableServices() {
                return services.keySet();
            }

            @Override
            public boolean providesService(String serviceName) {
                return services.containsKey(serviceName);
            }

            @Override
            public RemoteService getService(String serviceName) {
                if (!providesService(serviceName)) {
                    throw new IllegalArgumentException("Dienst nicht verfügbar: " + serviceName);
                }
                return services.get(serviceName);
            }
        };
    }

    /**
     * Demonstriert die Verwendung eines Trader-Proxys.
     */
    private static void demonstrateTraderProxy() {
        LOGGER.info("\n--- Trader-Proxy Demonstration ---");

        // Erstelle den Trader-Proxy
        TraderProxy trader = new TraderProxy();

        // Erstelle und registriere Dienste mit verschiedenen Eigenschaften
        Map<String, Object> fastLowQualityProps = new HashMap<>();
        fastLowQualityProps.put("type", "calculator");
        fastLowQualityProps.put("category", "math");
        fastLowQualityProps.put("provider", "FastMath");
        fastLowQualityProps.put("performance", 9.0);
        fastLowQualityProps.put("reliability", 0.7);
        fastLowQualityProps.put("availability", 0.9);

        Map<String, Object> slowHighQualityProps = new HashMap<>();
        slowHighQualityProps.put("type", "calculator");
        slowHighQualityProps.put("category", "math");
        slowHighQualityProps.put("provider", "PreciseMath");
        slowHighQualityProps.put("performance", 6.0);
        slowHighQualityProps.put("reliability", 0.95);
        slowHighQualityProps.put("availability", 0.99);

        Map<String, Object> balancedProps = new HashMap<>();
        balancedProps.put("type", "calculator");
        balancedProps.put("category", "math");
        balancedProps.put("provider", "BalancedMath");
        balancedProps.put("performance", 7.5);
        balancedProps.put("reliability", 0.85);
        balancedProps.put("availability", 0.95);

        // Registriere die Dienste
        RealRemoteService fastService = new RealRemoteService("FastMath", false, 50);
        RealRemoteService slowService = new RealRemoteService("PreciseMath", false, 200);
        RealRemoteService balancedService = new RealRemoteService("BalancedMath", false, 100);

        ServiceID fastId = trader.registerService(fastService, fastLowQualityProps);
        ServiceID slowId = trader.registerService(slowService, slowHighQualityProps);
        ServiceID balancedId = trader.registerService(balancedService, balancedProps);

        // Suche nach Diensten
        LOGGER.info("Suche nach Taschenrechnerdiensten");
        Map<String, Object> requiredProps = new HashMap<>();
        requiredProps.put("category", "math");

        List<ServiceID> foundServices = trader.findServices("calculator", requiredProps);
        LOGGER.info("Gefundene Dienste: {}", foundServices);

        // Wähle den besten Dienst basierend auf Leistung
        LOGGER.info("Wähle den besten Dienst basierend auf Leistung");
        List<QualityOfService> performanceQoS = Arrays.asList(QualityOfService.PERFORMANCE);
        ServiceID bestPerformanceService = trader.selectBestService("calculator", requiredProps, performanceQoS);
        LOGGER.info("Bester Dienst für Leistung: {}", bestPerformanceService);

        // Wähle den besten Dienst basierend auf Zuverlässigkeit
        LOGGER.info("Wähle den besten Dienst basierend auf Zuverlässigkeit");
        List<QualityOfService> reliabilityQoS = Arrays.asList(QualityOfService.RELIABILITY);
        ServiceID bestReliabilityService = trader.selectBestService("calculator", requiredProps, reliabilityQoS);
        LOGGER.info("Bester Dienst für Zuverlässigkeit: {}", bestReliabilityService);

        // Wähle den besten Dienst basierend auf mehreren Kriterien
        LOGGER.info("Wähle den besten Dienst basierend auf mehreren Kriterien");
        List<QualityOfService> mixedQoS = Arrays.asList(
                QualityOfService.RELIABILITY, 
                QualityOfService.PERFORMANCE, 
                QualityOfService.AVAILABILITY);
        ServiceID bestOverallService = trader.selectBestService("calculator", requiredProps, mixedQoS);
        LOGGER.info("Bester Gesamtdienst: {}", bestOverallService);

        try {
            // Führe Anfragen an den ausgewählten Dienst aus
            LOGGER.info("Führe Anfrage an den ausgewählten Dienst aus");
            String result = trader.invokeService(bestOverallService, "calculate");
            LOGGER.info("Ergebnis: {}", result);

            // Bewerte die Dienste
            LOGGER.info("Bewerte Dienste");
            trader.rateService(fastId, "User1", 3.5, "Schnell, aber nicht sehr genau");
            trader.rateService(slowId, "User1", 4.5, "Sehr genau, aber langsam");
            trader.rateService(balancedId, "User1", 4.0, "Guter Kompromiss");

            trader.rateService(fastId, "User2", 4.0, "Gut für einfache Berechnungen");
            trader.rateService(slowId, "User2", 3.0, "Zu langsam für meine Bedürfnisse");
            trader.rateService(balancedId, "User2", 4.5, "Perfekt für meine Anforderungen");

            // Wähle den besten Dienst basierend auf Benutzerbewertungen
            LOGGER.info("Wähle den besten Dienst basierend auf Benutzerbewertungen");
            List<QualityOfService> ratingQoS = Arrays.asList(QualityOfService.USER_RATING);
            ServiceID bestRatedService = trader.selectBestService("calculator", requiredProps, ratingQoS);
            LOGGER.info("Bester bewerteter Dienst: {}", bestRatedService);

        } catch (ServiceException e) {
            LOGGER.error("Fehler bei der Trader-Proxy-Demonstration: {}", e.getMessage());
        }
    }
}