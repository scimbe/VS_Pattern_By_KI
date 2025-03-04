package de.becke.vs.pattern.interceptor.pipeline;

import de.becke.vs.pattern.interceptor.core.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Ein Pipeline-Interceptor für Routing-Entscheidungen in verteilten Systemen.
 * 
 * Diese Klasse implementiert einen Routing-Mechanismus, der Nachrichten
 * basierend auf ihrem Inhalt oder Typ an verschiedene Ziele weiterleitet.
 */
public class RoutingInterceptor implements PipelineInterceptor {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RoutingInterceptor.class);
    
    private final Map<String, Predicate<Object>> routes = new HashMap<>();
    private final Map<String, PipelineDispatcher> dispatchers = new HashMap<>();
    
    /**
     * Fügt eine Route hinzu.
     * 
     * @param routeName Der Name der Route
     * @param condition Die Bedingung, unter der die Route gewählt wird
     * @param dispatcher Der Dispatcher, der die Nachrichten für diese Route verarbeitet
     */
    public void addRoute(String routeName, Predicate<Object> condition, PipelineDispatcher dispatcher) {
        routes.put(routeName, condition);
        dispatchers.put(routeName, dispatcher);
        LOGGER.info("Route '{}' hinzugefügt", routeName);
    }
    
    @Override
    public boolean intercept(Object message, Context context, InterceptorChain chain) {
        LOGGER.debug("Prüfe Routing für Nachricht: {}", message);
        
        // Suche nach einer Route, die auf die Nachricht passt
        for (Map.Entry<String, Predicate<Object>> route : routes.entrySet()) {
            String routeName = route.getKey();
            Predicate<Object> condition = route.getValue();
            
            if (condition.test(message)) {
                LOGGER.info("Nachricht wird an Route '{}' weitergeleitet", routeName);
                context.setAttribute("routing.selectedRoute", routeName);
                
                // Hole den passenden Dispatcher
                PipelineDispatcher dispatcher = dispatchers.get(routeName);
                if (dispatcher != null) {
                    // Erstelle einen neuen Kontext für die Weiterleitung
                    Context routingContext = new Context();
                    
                    // Übertrage Attribute aus dem ursprünglichen Kontext
                    context.getAttributes().forEach(routingContext::setAttribute);
                    
                    // Verarbeite die Nachricht im ausgewählten Dispatcher
                    boolean routingResult = dispatcher.process(message, routingContext);
                    
                    // Setze das Ergebnis der Weiterleitung
                    context.setResult(routingContext.getResult());
                    context.setAttribute("routing.result", routingResult);
                    
                    // Übertrage alle Attribute aus dem Routing-Kontext zurück
                    routingContext.getAttributes().forEach(context::setAttribute);
                    
                    return routingResult;
                } else {
                    LOGGER.error("Kein Dispatcher für Route '{}' gefunden", routeName);
                    context.setAttribute("routing.error", "Kein Dispatcher gefunden");
                    return false;
                }
            }
        }
        
        // Keine passende Route gefunden, verarbeite die Nachricht in der aktuellen Kette weiter
        LOGGER.debug("Keine passende Route gefunden, fahre mit der Pipeline fort");
        context.setAttribute("routing.selectedRoute", "default");
        return chain.proceed(message, context);
    }
    
    @Override
    public String getName() {
        return "RoutingInterceptor";
    }
}