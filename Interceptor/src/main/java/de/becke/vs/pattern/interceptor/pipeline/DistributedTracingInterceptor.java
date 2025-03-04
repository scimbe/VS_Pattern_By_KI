package de.becke.vs.pattern.interceptor.pipeline;

import de.becke.vs.pattern.interceptor.core.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Ein Pipeline-Interceptor für verteiltes Tracing.
 * 
 * Diese Klasse fügt Tracing-Informationen zu Nachrichten hinzu, um den
 * Verlauf von Anfragen in einem verteilten System nachvollziehbar zu machen.
 */
public class DistributedTracingInterceptor implements PipelineInterceptor {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DistributedTracingInterceptor.class);
    
    @Override
    public boolean intercept(Object message, Context context, InterceptorChain chain) {
        // Erstelle oder erhalte eine Trace-ID
        String traceId = context.getAttributeAs("tracing.traceId", String.class);
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
            context.setAttribute("tracing.traceId", traceId);
            LOGGER.info("Neue Trace-ID erstellt: {}", traceId);
        }
        
        // Erstelle eine neue Span-ID für diesen Verarbeitungsschritt
        String spanId = UUID.randomUUID().toString();
        context.setAttribute("tracing.spanId", spanId);
        
        // Speichere den vorherigen Span als Parent
        String parentSpanId = context.getAttributeAs("tracing.currentSpanId", String.class);
        if (parentSpanId != null) {
            context.setAttribute("tracing.parentSpanId", parentSpanId);
        }
        
        // Setze die aktuelle Span-ID
        context.setAttribute("tracing.currentSpanId", spanId);
        
        // Protokolliere Start des Spans
        LOGGER.info("Tracing - Start: [traceId={}, spanId={}]", traceId, spanId);
        
        // Startzeit speichern
        long startTime = System.currentTimeMillis();
        context.setAttribute("tracing.startTime", startTime);
        
        // Rufe den nächsten Interceptor in der Kette auf
        boolean result = chain.proceed(message, context);
        
        // Berechne Dauer
        long duration = System.currentTimeMillis() - startTime;
        context.setAttribute("tracing.duration", duration);
        
        // Protokolliere Ende des Spans
        LOGGER.info("Tracing - Ende: [traceId={}, spanId={}, duration={}ms, result={}]", 
                traceId, spanId, duration, result);
        
        return result;
    }
}