package de.becke.vs.pattern.interceptor.pipeline;

import de.becke.vs.pattern.interceptor.core.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demonstrationsklasse für verteilte Szenarien mit dem Pipeline-Interceptor Pattern.
 * 
 * Diese Klasse demonstriert fortgeschrittene Pipeline-Szenarien in verteilten Systemen
 * mit Fokus auf Nachrichtenverteilung, Routing und Tracing zwischen mehreren Diensten.
 */
public class DistributedPipelineDemo {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DistributedPipelineDemo.class);
    
    /**
     * Führt die Demo aus.
     */
    public static void runDemo() {
        LOGGER.info("\n=== Verteilte Pipeline-Interceptor Demo ===");
        
        // Initialisiere die Dienste
        ServiceRegistry registry = ServiceRegistry.createDemoServices();
        
        // Führe die Demo-Szenarien aus
        demoAuthRequest(registry);
        demoDataTransformationRequest(registry);
        demoPersistenceRequest(registry);
        demoChainedOperation(registry);
        
        LOGGER.info("\n=== Verteilte Pipeline-Demo abgeschlossen ===");
    }
    
    /**
     * Demonstriert einen Authentifizierungsrequest.
     */
    private static void demoAuthRequest(ServiceRegistry registry) {
        LOGGER.info("\nDemo: Authentifizierungsanfrage");
        
        Message authRequest = new Message("auth", "Authenticate user");
        authRequest.addHeader("Authorization", "Bearer valid-token-12345");
        
        Context authContext = new Context();
        boolean authResult = registry.getGatewayDispatcher().process(authRequest, authContext);
        
        LOGGER.info("Authentifizierungsanfrage Ergebnis: {}", authResult);
        LOGGER.info("Authentifiziert: {}", authContext.getAttribute("auth.authenticated"));
        LOGGER.info("Benutzername: {}", authContext.getAttribute("auth.username"));
    }
    
    /**
     * Demonstriert einen Datentransformationsrequest.
     */
    private static void demoDataTransformationRequest(ServiceRegistry registry) {
        LOGGER.info("\nDemo: Datenanfrage mit Transformation");
        
        Message dataRequest = new Message("data", "raw data for processing");
        dataRequest.addHeader("Authorization", "Bearer valid-token-12345");
        
        Context dataContext = new Context();
        boolean dataResult = registry.getGatewayDispatcher().process(dataRequest, dataContext);
        
        LOGGER.info("Datenanfrage Ergebnis: {}", dataResult);
        LOGGER.info("Trace-ID: {}", dataContext.getAttribute("tracing.traceId"));
    }
    
    /**
     * Demonstriert einen Persistenzrequest.
     */
    private static void demoPersistenceRequest(ServiceRegistry registry) {
        LOGGER.info("\nDemo: Persistenzanfrage");
        
        Message persistRequest = new Message("persistence", "data to be stored");
        persistRequest.addHeader("Authorization", "Bearer valid-token-12345");
        
        Context persistContext = new Context();
        boolean persistResult = registry.getGatewayDispatcher().process(persistRequest, persistContext);
        
        LOGGER.info("Persistenzanfrage Ergebnis: {}", persistResult);
        LOGGER.info("Gespeichert: {}", persistContext.getAttribute("persistence.stored"));
        LOGGER.info("Entity-ID: {}", persistContext.getAttribute("persistence.entityId"));
        LOGGER.info("Ergebnis: {}", persistContext.getResult());
    }
    
    /**
     * Demonstriert eine verkettete Operation über mehrere Dienste.
     */
    private static void demoChainedOperation(ServiceRegistry registry) {
        LOGGER.info("\nDemo: Verkettete Operation");
        
        // Simuliere eine komplexe Operation über mehrere Dienste
        Message initialRequest = new Message("auth", "initial request");
        initialRequest.addHeader("Authorization", "Bearer valid-token-12345");
        
        Context complexContext = new Context();
        
        // Schritt 1: Authentifizierung
        LOGGER.info("Schritt 1: Authentifizierung");
        boolean step1Result = registry.getGatewayDispatcher().process(initialRequest, complexContext);
        
        if (step1Result && (Boolean) complexContext.getAttribute("auth.authenticated")) {
            // Schritt 2: Transformation
            LOGGER.info("Schritt 2: Transformation");
            String username = (String) complexContext.getAttribute("auth.username");
            
            Message transformRequest = new Message("data", "data for user " + username);
            transformRequest.addHeader("Authorization", "Bearer valid-token-12345");
            transformRequest.addHeader("X-Authenticated-User", username);
            
            boolean step2Result = registry.getGatewayDispatcher().process(transformRequest, complexContext);
            
            if (step2Result) {
                // Schritt 3: Persistenz
                LOGGER.info("Schritt 3: Persistenz");
                
                Object transformedData = complexContext.getResult();
                
                Message persistRequest = new Message("persistence", transformedData);
                persistRequest.addHeader("Authorization", "Bearer valid-token-12345");
                persistRequest.addHeader("X-Authenticated-User", username);
                
                boolean step3Result = registry.getGatewayDispatcher().process(persistRequest, complexContext);
                
                LOGGER.info("Verkettete Operation abgeschlossen mit Ergebnis: {}", step3Result);
                LOGGER.info("Finales Ergebnis: {}", complexContext.getResult());
            }
        }
    }
}