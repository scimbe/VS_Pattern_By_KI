package de.becke.vs.pattern.dependencyinjection.spring;

import de.becke.vs.pattern.dependencyinjection.common.FaultTolerantService;
import de.becke.vs.pattern.dependencyinjection.common.RemoteService;
import de.becke.vs.pattern.dependencyinjection.common.RemoteServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Spring-Konfigurationsklasse, die Beans für die Dependency Injection definiert.
 */
@Configuration
public class SpringConfig {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringConfig.class);
    
    /**
     * Erstellt einen primären RemoteService-Bean.
     * 
     * @return Der primäre RemoteService
     */
    @Bean
    @Primary
    public RemoteService primaryService() {
        LOGGER.info("Spring erstellt primären RemoteService");
        return new RemoteServiceImpl("SpringPrimaryService");
    }
    
    /**
     * Erstellt einen sekundären RemoteService-Bean.
     * 
     * @return Der sekundäre RemoteService
     */
    @Bean(name = "secondaryService")
    public RemoteService backupService() {
        LOGGER.info("Spring erstellt sekundären RemoteService");
        return new RemoteServiceImpl("SpringSecondaryService", 100);
    }
    
    /**
     * Erstellt einen fehlertoleranten RemoteService-Bean.
     * 
     * @param primary Der primäre Service (wird automatisch injiziert)
     * @param secondary Der sekundäre Service (wird über Namen injiziert)
     * @return Der fehlertolerante RemoteService
     */
    @Bean
    public RemoteService faultTolerantService(RemoteService primaryService, 
                                              @org.springframework.beans.factory.annotation.Qualifier("secondaryService") 
                                              RemoteService secondaryService) {
        LOGGER.info("Spring erstellt fehlertoleranten RemoteService");
        return new FaultTolerantService(primaryService, secondaryService, 3);
    }
    
    /**
     * Erstellt einen konfigurierbaren Service für Testzwecke.
     * 
     * @return Ein simulierter Service mit hoher Latenz
     */
    @Bean(name = "slowService")
    public RemoteService highLatencyService() {
        LOGGER.info("Spring erstellt Service mit hoher Latenz");
        return new RemoteServiceImpl("SlowSpringService", 500);
    }
    
    /**
     * Erstellt einen simulierten fehlerhaften Service für Testzwecke.
     * 
     * @return Ein Service, der gelegentlich Fehler wirft
     */
    @Bean(name = "unreliableService")
    public RemoteService failingService() {
        LOGGER.info("Spring erstellt unzuverlässigen Service");
        
        return new RemoteService() {
            private final RemoteService delegate = new RemoteServiceImpl("UnreliableSpringService");
            
            @Override
            public String executeOperation() {
                if (Math.random() < 0.5) {
                    throw new RuntimeException("Simulierter Fehler im UnreliableSpringService");
                }
                return delegate.executeOperation();
            }
            
            @Override
            public String executeOperation(String parameter) {
                if (Math.random() < 0.5) {
                    throw new RuntimeException("Simulierter Fehler im UnreliableSpringService");
                }
                return delegate.executeOperation(parameter);
            }
            
            @Override
            public String getServiceName() {
                return "UnreliableSpringService";
            }
        };
    }
}