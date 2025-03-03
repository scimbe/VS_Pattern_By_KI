package de.becke.vs.pattern.interceptor.logging;

import de.becke.vs.pattern.interceptor.core.Context;
import de.becke.vs.pattern.interceptor.core.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ein Interceptor, der den Ausführungsverlauf protokolliert.
 * 
 * Diese Klasse implementiert einen einfachen Logging-Interceptor,
 * der den Start, das Ende und eventuelle Fehler einer Operation protokolliert.
 */
public class LoggingInterceptor implements Interceptor {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingInterceptor.class);
    
    private final String name;
    private final LogLevel level;
    
    /**
     * Die unterstützten Log-Level.
     */
    public enum LogLevel {
        DEBUG, INFO, WARN, ERROR
    }
    
    /**
     * Erstellt einen neuen LoggingInterceptor mit INFO-Level.
     */
    public LoggingInterceptor() {
        this("DefaultLogger", LogLevel.INFO);
    }
    
    /**
     * Erstellt einen neuen LoggingInterceptor mit benutzerdefiniertem Namen und INFO-Level.
     * 
     * @param name Der Name des Loggers, der in den Log-Einträgen erscheint
     */
    public LoggingInterceptor(String name) {
        this(name, LogLevel.INFO);
    }
    
    /**
     * Erstellt einen neuen LoggingInterceptor mit benutzerdefiniertem Namen und Level.
     * 
     * @param name Der Name des Loggers, der in den Log-Einträgen erscheint
     * @param level Das Log-Level
     */
    public LoggingInterceptor(String name, LogLevel level) {
        this.name = name;
        this.level = level;
    }
    
    @Override
    public boolean preProcess(Context context) {
        log("Operation gestartet [ID: {}] mit Eingabe: {}", 
                context.getExecutionId(), 
                context.getInput());
        return true;
    }
    
    @Override
    public void postProcess(Context context) {
        if (context.isSuccessful()) {
            log("Operation erfolgreich abgeschlossen [ID: {}] in {}ms mit Ergebnis: {}", 
                    context.getExecutionId(), 
                    context.getDuration(), 
                    context.getResult());
        } else {
            log("Operation fehlgeschlagen [ID: {}] in {}ms", 
                    context.getExecutionId(), 
                    context.getDuration());
        }
    }
    
    @Override
    public boolean handleException(Context context, Exception exception) {
        log("Fehler bei Operation [ID: {}]: {}", 
                context.getExecutionId(), 
                exception.getMessage());
        
        if (level == LogLevel.DEBUG || level == LogLevel.INFO) {
            // Bei DEBUG oder INFO schreiben wir den Stack-Trace
            LOGGER.debug("Stack-Trace:", exception);
        }
        
        // Wir behandeln den Fehler nicht selbst, sondern protokollieren ihn nur
        return false;
    }
    
    /**
     * Protokolliert eine Nachricht mit dem konfigurierten Log-Level.
     * 
     * @param format Das Format der Nachricht
     * @param args Die Argumente für das Format
     */
    private void log(String format, Object... args) {
        String message = "[" + name + "] " + format;
        
        switch (level) {
            case DEBUG:
                LOGGER.debug(message, args);
                break;
            case INFO:
                LOGGER.info(message, args);
                break;
            case WARN:
                LOGGER.warn(message, args);
                break;
            case ERROR:
                LOGGER.error(message, args);
                break;
        }
    }
}