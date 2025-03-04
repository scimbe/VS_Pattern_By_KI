package de.becke.vs.pattern.interceptor.pipeline;

import de.becke.vs.pattern.interceptor.core.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Demonstrationsklasse für die Pipeline-Variante des Interceptor-Patterns.
 * 
 * Diese Klasse zeigt die Verwendung der Pipeline-Interceptoren in verschiedenen
 * Szenarien, die für verteilte Systeme relevant sind.
 */
public class PipelineDemo {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PipelineDemo.class);
    
    /**
     * Führt die Demo der Pipeline-Interceptoren aus.
     */
    public static void runDemo() {
        LOGGER.info("\n=== Pipeline-Interceptor Pattern Demo ===");
        
        // Grundlegende Pipeline-Demo
        basicPipelineDemo();
        
        // Nachrichtentransformations-Demo
        messageTransformationDemo();
        
        // Nachrichtenvalidierungs-Demo
        messageValidationDemo();
        
        // Verteiltes Tracing-Demo
        distributedTracingDemo();
        
        LOGGER.info("\n=== Pipeline-Interceptor Demo abgeschlossen ===");
    }
    
    /**
     * Demonstriert die grundlegende Verwendung der Pipeline-Interceptoren.
     */
    private static void basicPipelineDemo() {
        LOGGER.info("\n--- Grundlegende Pipeline-Demo ---");
        
        // Erstelle einen Pipeline-Dispatcher
        PipelineDispatcher dispatcher = new PipelineDispatcher();
        
        // Registriere Interceptoren
        dispatcher.registerInterceptor(new LoggingPipelineInterceptor("FirstLogger"));
        dispatcher.registerInterceptor(new LoggingPipelineInterceptor("SecondLogger"));
        
        // Erstelle einen Kontext und eine Testnachricht
        Context context = new Context();
        String message = "Testnachricht für Pipeline";
        
        // Verarbeite die Nachricht durch die Pipeline
        boolean result = dispatcher.process(message, context);
        
        LOGGER.info("Pipeline-Verarbeitung abgeschlossen mit Ergebnis: {}", result);
    }
    
    /**
     * Demonstriert die Transformation von Nachrichten in einer Pipeline.
     */
    private static void messageTransformationDemo() {
        LOGGER.info("\n--- Nachrichtentransformations-Demo ---");
        
        // Erstelle einen Pipeline-Dispatcher
        PipelineDispatcher dispatcher = new PipelineDispatcher();
        
        // Registriere Interceptoren
        dispatcher.registerInterceptor(new LoggingPipelineInterceptor("TransformLogger"));
        
        // Transformations-Interceptor: Wandelt String in Großbuchstaben um
        dispatcher.registerInterceptor(new TransformationPipelineInterceptor(
                message -> ((String) message).toUpperCase(),
                "toUpperCase"
        ));
        
        // Transformations-Interceptor: Fügt ein Präfix hinzu
        dispatcher.registerInterceptor(new TransformationPipelineInterceptor(
                message -> "Verarbeitet: " + message,
                "addPrefix"
        ));
        
        // Erstelle einen Kontext und eine Testnachricht
        Context context = new Context();
        String message = "Beispielnachricht";
        
        // Verarbeite die Nachricht durch die Pipeline
        boolean result = dispatcher.process(message, context);
        
        // Hole das transformierte Ergebnis
        Object transformedMessage = context.getAttribute("transformation.result.addPrefix");
        
        LOGGER.info("Pipeline-Transformation abgeschlossen: {} -> {}", message, transformedMessage);
    }
    
    /**
     * Demonstriert die Validierung von Nachrichten in einer Pipeline.
     */
    private static void messageValidationDemo() {
        LOGGER.info("\n--- Nachrichtenvalidierungs-Demo ---");
        
        // Erstelle einen Pipeline-Dispatcher
        PipelineDispatcher dispatcher = new PipelineDispatcher();
        
        // Registriere Interceptoren
        dispatcher.registerInterceptor(new LoggingPipelineInterceptor("ValidationLogger"));
        
        // Validierungs-Interceptor: Prüft, ob die Nachricht eine Map ist
        dispatcher.registerInterceptor(new ValidationPipelineInterceptor(
                message -> message instanceof Map,
                "Nachricht muss eine Map sein"
        ));
        
        // Validierungs-Interceptor: Prüft, ob die Map bestimmte Schlüssel enthält
        dispatcher.registerInterceptor(new ValidationPipelineInterceptor(
                message -> {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> map = (Map<String, Object>) message;
                    return map.containsKey("id") && map.containsKey("content");
                },
                "Map muss 'id' und 'content' Schlüssel enthalten"
        ));
        
        // Erstelle Nachrichten für die Tests
        Map<String, Object> validMessage = new HashMap<>();
        validMessage.put("id", "12345");
        validMessage.put("content", "Gültige Nachricht");
        validMessage.put("timestamp", System.currentTimeMillis());
        
        Map<String, Object> invalidMessage = new HashMap<>();
        invalidMessage.put("id", "67890");
        // 'content' fehlt
        
        String nonMapMessage = "Dies ist keine Map";
        
        // Teste mit gültiger Nachricht
        Context validContext = new Context();
        boolean validResult = dispatcher.process(validMessage, validContext);
        LOGGER.info("Validierung mit gültiger Nachricht: {}", validResult);
        
        // Teste mit ungültiger Map (fehlender Schlüssel)
        Context invalidContext = new Context();
        boolean invalidResult = dispatcher.process(invalidMessage, invalidContext);
        LOGGER.info("Validierung mit ungültiger Map: {}", invalidResult);
        if (!invalidResult) {
            LOGGER.info("Validierungsfehler: {}", invalidContext.getAttribute("validation.error"));
        }
        
        // Teste mit ungültigem Typ
        Context nonMapContext = new Context();
        boolean nonMapResult = dispatcher.process(nonMapMessage, nonMapContext);
        LOGGER.info("Validierung mit ungültigem Typ: {}", nonMapResult);
        if (!nonMapResult) {
            LOGGER.info("Validierungsfehler: {}", nonMapContext.getAttribute("validation.error"));
        }
    }
    
    /**
     * Demonstriert verteiltes Tracing mit Pipeline-Interceptoren.
     */
    private static void distributedTracingDemo() {
        LOGGER.info("\n--- Verteiltes Tracing-Demo ---");
        
        // Erstelle einen Pipeline-Dispatcher
        PipelineDispatcher dispatcher = new PipelineDispatcher();
        
        // Registriere Interceptoren
        dispatcher.registerInterceptor(new DistributedTracingInterceptor());
        dispatcher.registerInterceptor(new LoggingPipelineInterceptor("Service1"));
        
        // Simuliere einen zweiten Service mit eigener Pipeline
        PipelineDispatcher service2Dispatcher = new PipelineDispatcher();
        service2Dispatcher.registerInterceptor(new DistributedTracingInterceptor());
        service2Dispatcher.registerInterceptor(new LoggingPipelineInterceptor("Service2"));
        
        // Kontext erstellen
        Context context = new Context();
        
        // Erste Nachricht verarbeiten (Service 1)
        LOGGER.info("Starte Verarbeitung in Service 1");
        dispatcher.process("Service1-Anfrage", context);
        
        // Trace-IDs abrufen
        String traceId = context.getAttributeAs("tracing.traceId", String.class);
        String spanId = context.getAttributeAs("tracing.spanId", String.class);
        
        LOGGER.info("Übergabe an Service 2 mit traceId={}, parentSpanId={}", traceId, spanId);
        
        // Zweite Nachricht verarbeiten (Service 2) - mit den gleichen Tracing-Infos
        Context service2Context = new Context();
        service2Context.setAttribute("tracing.traceId", traceId);
        service2Context.setAttribute("tracing.parentSpanId", spanId);
        
        LOGGER.info("Starte Verarbeitung in Service 2");
        service2Dispatcher.process("Service2-Anfrage", service2Context);
        
        // Trace-IDs von Service 2 abrufen
        String service2SpanId = service2Context.getAttributeAs("tracing.spanId", String.class);
        
        LOGGER.info("Verarbeitung abgeschlossen mit traceId={}, service1SpanId={}, service2SpanId={}", 
                traceId, spanId, service2SpanId);
        
        // Simuliere einen dritten Service mit eigener Pipeline
        PipelineDispatcher service3Dispatcher = new PipelineDispatcher();
        service3Dispatcher.registerInterceptor(new DistributedTracingInterceptor());
        service3Dispatcher.registerInterceptor(new LoggingPipelineInterceptor("Service3"));
        
        // Dritte Nachricht verarbeiten (Service 3) - mit den gleichen Trace-Infos
        Context service3Context = new Context();
        service3Context.setAttribute("tracing.traceId", traceId);
        service3Context.setAttribute("tracing.parentSpanId", service2SpanId);
        
        LOGGER.info("Starte Verarbeitung in Service 3");
        service3Dispatcher.process("Service3-Anfrage", service3Context);
        
        // Trace-IDs von Service 3 abrufen
        String service3SpanId = service3Context.getAttributeAs("tracing.spanId", String.class);
        
        LOGGER.info("Gesamte Trace-Kette: traceId={}, service1={}, service2={}, service3={}", 
                traceId, spanId, service2SpanId, service3SpanId);
    }
}