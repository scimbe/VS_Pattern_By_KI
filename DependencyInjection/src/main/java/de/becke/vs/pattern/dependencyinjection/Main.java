package de.becke.vs.pattern.dependencyinjection;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.becke.vs.pattern.dependencyinjection.common.RemoteService;
import de.becke.vs.pattern.dependencyinjection.common.RemoteServiceImpl;
import de.becke.vs.pattern.dependencyinjection.guice.GuiceDIClient;
import de.becke.vs.pattern.dependencyinjection.guice.GuiceModule;
import de.becke.vs.pattern.dependencyinjection.manual.ManualDIClient;
import de.becke.vs.pattern.dependencyinjection.manual.ServiceFactory;
import de.becke.vs.pattern.dependencyinjection.servicelocator.ServiceLocator;
import de.becke.vs.pattern.dependencyinjection.spring.SpringDIClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Hauptklasse zur Demonstration verschiedener Ansätze der Dependency Injection.
 */
public class Main {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) {
        LOGGER.info("Starte Demonstration des Dependency Injection Patterns");
        
        // Demonstriere manuelle Dependency Injection
        demonstrateManualDI();
        
        // Demonstriere Service Locator Pattern (als Alternative zu DI)
        demonstrateServiceLocator();
        
        // Demonstriere Dependency Injection mit Spring
        demonstrateSpringDI();
        
        // Demonstriere Dependency Injection mit Guice
        demonstrateGuiceDI();
        
        LOGGER.info("Demonstration abgeschlossen");
    }
    
    /**
     * Demonstriert manuelle Dependency Injection ohne Framework.
     */
    private static void demonstrateManualDI() {
        LOGGER.info("\n=== Manuelle Dependency Injection Demonstration ===");
        
        // Erstelle eine Factory
        ServiceFactory factory = new ServiceFactory();
        
        // Erstelle Services über die Factory
        RemoteService primaryService = factory.createSimpleService("PrimaryManualService");
        RemoteService secondaryService = factory.createServiceWithLatency("SecondaryManualService", 150);
        RemoteService faultTolerantService = factory.createFaultTolerantService(
                "PrimaryFTService", "FallbackService", 3);
        RemoteService failingService = factory.createFailingService("UnreliableService", 0.7);
        
        // Erstelle den Client mit Konstruktor-Injektion
        ManualDIClient client = new ManualDIClient(primaryService);
        
        // Setze zusätzliche Abhängigkeit mit Setter-Injektion
        client.setSecondaryService(secondaryService);
        
        // Verwende den Client
        LOGGER.info("Verwende Client mit primärem Service: {}", client.getPrimaryServiceName());
        LOGGER.info("Sekundärer Service: {}", client.getSecondaryServiceName());
        
        // Führe Operationen aus
        String result1 = client.executeOperation("TestParam");
        LOGGER.info("Ergebnis primäre Operation: {}", result1);
        
        String result2 = client.executeSecondaryOperation("TestParam");
        LOGGER.info("Ergebnis sekundäre Operation: {}", result2);
        
        // Demonstriere fehlertolerante und fehlerhafte Services
        try {
            LOGGER.info("Teste fehlertoleranten Service");
            String resultFT = faultTolerantService.executeOperation("TestParam");
            LOGGER.info("Ergebnis fehlertolerante Operation: {}", resultFT);
        } catch (Exception e) {
            LOGGER.error("Fehler beim fehlertoleranten Service: {}", e.getMessage());
        }
        
        try {
            LOGGER.info("Teste fehlerhaften Service (mit 70% Fehlerrate)");
            String resultFail = failingService.executeOperation("TestParam");
            LOGGER.info("Ergebnis fehlerhafte Operation: {}", resultFail);
        } catch (Exception e) {
            LOGGER.error("Erwarteter Fehler beim fehlerhaften Service: {}", e.getMessage());
        }
    }
    
    /**
     * Demonstriert das Service Locator Pattern als Alternative zu Dependency Injection.
     */
    private static void demonstrateServiceLocator() {
        LOGGER.info("\n=== Service Locator Pattern Demonstration ===");
        
        // Initialisiere den Service Locator
        ServiceLocator locator = ServiceLocator.getInstance();
        
        // Registriere Services beim Locator
        locator.registerService("defaultService", new RemoteServiceImpl("DefaultLocatorService"));
        locator.registerService("slowService", new RemoteServiceImpl("SlowLocatorService", 300));
        
        // Rufe Services vom Locator ab
        RemoteService service1 = locator.getService("defaultService");
        RemoteService service2 = locator.getService("slowService");
        
        // Verwende die Services
        LOGGER.info("Verwende Service vom Locator: {}", service1.getServiceName());
        String result1 = service1.executeOperation("LocatorParam");
        LOGGER.info("Ergebnis: {}", result1);
        
        LOGGER.info("Verwende langsamen Service vom Locator: {}", service2.getServiceName());
        String result2 = service2.executeOperation("LocatorParam");
        LOGGER.info("Ergebnis: {}", result2);
        
        // Versuche, einen nicht registrierten Service abzurufen
        try {
            RemoteService unknownService = locator.getService("unknownService");
            LOGGER.info("Service gefunden: {}", unknownService);
        } catch (Exception e) {
            LOGGER.error("Erwarteter Fehler beim Abrufen eines unbekannten Services: {}", e.getMessage());
        }
    }
    
    /**
     * Demonstriert Dependency Injection mit dem Spring Framework.
     */
    private static void demonstrateSpringDI() {
        LOGGER.info("\n=== Spring Dependency Injection Demonstration ===");
        
        // Erstelle einen Spring-Kontext mit Konfiguration
        try (AnnotationConfigApplicationContext context = 
                new AnnotationConfigApplicationContext(de.becke.vs.pattern.dependencyinjection.spring.SpringConfig.class)) {
            
            LOGGER.info("Spring-Kontext initialisiert");
            
            // Hole den Client aus dem Spring-Kontext
            SpringDIClient client = context.getBean(SpringDIClient.class);
            
            // Zeige alle injizierten Services an
            LOGGER.info(client.listAllServices());
            
            // Führe Operationen mit den injizierten Services aus
            client.executeServices("SpringParam");
        }
    }
    
    /**
     * Demonstriert Dependency Injection mit Google Guice.
     */
    private static void demonstrateGuiceDI() {
        LOGGER.info("\n=== Guice Dependency Injection Demonstration ===");
        
        // Erstelle einen Guice-Injector mit Modulkonfiguration
        Injector injector = Guice.createInjector(new GuiceModule());
        
        LOGGER.info("Guice-Injector initialisiert");
        
        // Hole den Client aus dem Guice-Injector
        GuiceDIClient client = injector.getInstance(GuiceDIClient.class);
        
        // Zeige alle injizierten Services an
        LOGGER.info(client.listAllServices());
        
        // Führe Operationen mit den injizierten Services aus
        client.executeServices("GuiceParam");
    }
}