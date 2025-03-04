package de.becke.vs.pattern.interceptor.pipeline;

import de.becke.vs.pattern.interceptor.core.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registry für Services in der verteilten Architektur.
 * 
 * Diese Klasse verwaltet die verschiedenen Dienste (als Dispatcher) 
 * und deren Konfiguration in der Demo-Architektur.
 */
public class ServiceRegistry {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistry.class);
    
    private final PipelineDispatcher gatewayDispatcher;
    private final PipelineDispatcher authDispatcher;
    private final PipelineDispatcher transformDispatcher;
    private final PipelineDispatcher persistenceDispatcher;
    
    /**
     * Erstellt eine neue ServiceRegistry mit den angegebenen Dispatchern.
     */
    public ServiceRegistry(
            PipelineDispatcher gatewayDispatcher,
            PipelineDispatcher authDispatcher,
            PipelineDispatcher transformDispatcher,
            PipelineDispatcher persistenceDispatcher) {
        this.gatewayDispatcher = gatewayDispatcher;
        this.authDispatcher = authDispatcher;
        this.transformDispatcher = transformDispatcher;
        this.persistenceDispatcher = persistenceDispatcher;
    }
    
    /**
     * Erstellt und konfiguriert eine ServiceRegistry für die Demo.
     * 
     * @return Die konfigurierte ServiceRegistry
     */
    public static ServiceRegistry createDemoServices() {
        // Gateway-Dienst - Eintrittspunkt ins System
        PipelineDispatcher gatewayDispatcher = new PipelineDispatcher();
        gatewayDispatcher.registerInterceptor(new LoggingPipelineInterceptor("Gateway"));
        gatewayDispatcher.registerInterceptor(new DistributedTracingInterceptor());
        
        // Authentifizierungsdienst
        PipelineDispatcher authDispatcher = new PipelineDispatcher();
        authDispatcher.registerInterceptor(new LoggingPipelineInterceptor("Auth"));
        authDispatcher.registerInterceptor(new DistributedTracingInterceptor());
        authDispatcher.registerInterceptor(new AuthInterceptor());
        
        // Transformationsdienst
        PipelineDispatcher transformDispatcher = new PipelineDispatcher();
        transformDispatcher.registerInterceptor(new LoggingPipelineInterceptor("Transform"));
        transformDispatcher.registerInterceptor(new DistributedTracingInterceptor());
        transformDispatcher.registerInterceptor(new TransformInterceptor());
        
        // Persistenzdienst
        PipelineDispatcher persistenceDispatcher = new PipelineDispatcher();
        persistenceDispatcher.registerInterceptor(new LoggingPipelineInterceptor("Persistence"));
        persistenceDispatcher.registerInterceptor(new DistributedTracingInterceptor());
        persistenceDispatcher.registerInterceptor(new PersistenceInterceptor());
        
        // Routing-Interceptor für das Gateway
        RoutingInterceptor routingInterceptor = new RoutingInterceptor();
        
        // Routen definieren
        routingInterceptor.addRoute("auth", 
                msg -> msg instanceof Message && ((Message) msg).getType().equals("auth"),
                authDispatcher);
        
        routingInterceptor.addRoute("data", 
                msg -> msg instanceof Message && ((Message) msg).getType().equals("data"),
                transformDispatcher);
        
        routingInterceptor.addRoute("persistence", 
                msg -> msg instanceof Message && ((Message) msg).getType().equals("persistence"),
                persistenceDispatcher);
        
        // Routing-Interceptor zum Gateway hinzufügen
        gatewayDispatcher.registerInterceptor(routingInterceptor);
        
        return new ServiceRegistry(gatewayDispatcher, authDispatcher, transformDispatcher, persistenceDispatcher);
    }
    
    public PipelineDispatcher getGatewayDispatcher() {
        return gatewayDispatcher;
    }
    
    public PipelineDispatcher getAuthDispatcher() {
        return authDispatcher;
    }
    
    public PipelineDispatcher getTransformDispatcher() {
        return transformDispatcher;
    }
    
    public PipelineDispatcher getPersistenceDispatcher() {
        return persistenceDispatcher;
    }
}