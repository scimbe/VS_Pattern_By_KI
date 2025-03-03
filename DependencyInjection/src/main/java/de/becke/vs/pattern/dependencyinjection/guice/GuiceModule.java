package de.becke.vs.pattern.dependencyinjection.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import de.becke.vs.pattern.dependencyinjection.common.FaultTolerantService;
import de.becke.vs.pattern.dependencyinjection.common.RemoteService;
import de.becke.vs.pattern.dependencyinjection.common.RemoteServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Guice-Modul zur Konfiguration der Dependency Injection.
 */
public class GuiceModule extends AbstractModule {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(GuiceModule.class);
    
    @Override
    protected void configure() {
        LOGGER.info("Konfiguriere Guice-Module für Dependency Injection");
        
        // Standard-Binding für RemoteService
        bind(RemoteService.class)
                .to(RemoteServiceImpl.class);
        
        // Benannte Bindings für verschiedene Service-Implementierungen
        bind(RemoteService.class)
                .annotatedWith(Names.named("primaryService"))
                .toInstance(new RemoteServiceImpl("GuicePrimaryService"));
        
        bind(RemoteService.class)
                .annotatedWith(Names.named("secondaryService"))
                .toInstance(new RemoteServiceImpl("GuiceSecondaryService", 100));
        
        // Binding für eine spezielle Implementierung
        bind(RemoteService.class)
                .annotatedWith(Names.named("slowService"))
                .toInstance(new RemoteServiceImpl("SlowGuiceService", 500));
    }
    
    /**
     * Provider-Methode für einen fehlertoleranten Service.
     * 
     * @param primary Der primäre Service (injiziert mit Namen)
     * @param secondary Der sekundäre Service (injiziert mit Namen)
     * @return Ein neuer fehlertoleranter Service
     */
    @Provides
    @com.google.inject.name.Named("faultTolerantService")
    public RemoteService provideFaultTolerantService(
            @com.google.inject.name.Named("primaryService") RemoteService primary,
            @com.google.inject.name.Named("secondaryService") RemoteService secondary) {
        
        LOGGER.info("Guice erstellt fehlertoleranten Service");
        return new FaultTolerantService(primary, secondary, 3);
    }
    
    /**
     * Provider-Methode für einen unzuverlässigen Service.
     * 
     * @return Ein Service, der gelegentlich Fehler wirft
     */
    @Provides
    @com.google.inject.name.Named("unreliableService")
    public RemoteService provideUnreliableService() {
        LOGGER.info("Guice erstellt unzuverlässigen Service");
        
        return new RemoteService() {
            private final RemoteService delegate = new RemoteServiceImpl("UnreliableGuiceService");
            
            @Override
            public String executeOperation() {
                if (Math.random() < 0.5) {
                    throw new RuntimeException("Simulierter Fehler im UnreliableGuiceService");
                }
                return delegate.executeOperation();
            }
            
            @Override
            public String executeOperation(String parameter) {
                if (Math.random() < 0.5) {
                    throw new RuntimeException("Simulierter Fehler im UnreliableGuiceService");
                }
                return delegate.executeOperation(parameter);
            }
            
            @Override
            public String getServiceName() {
                return "UnreliableGuiceService";
            }
        };
    }
}