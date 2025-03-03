package de.becke.vs.pattern.interceptor;

import de.becke.vs.pattern.interceptor.caching.CachingInterceptor;
import de.becke.vs.pattern.interceptor.core.Context;
import de.becke.vs.pattern.interceptor.core.Dispatcher;
import de.becke.vs.pattern.interceptor.http.AuthenticationInterceptor;
import de.becke.vs.pattern.interceptor.logging.LoggingInterceptor;
import de.becke.vs.pattern.interceptor.performance.PerformanceInterceptor;
import de.becke.vs.pattern.interceptor.security.SecurityInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Hauptklasse zur Demonstration des Interceptor-Patterns.
 */
public class Main {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) {
        LOGGER.info("Starte Demonstration des Interceptor-Patterns");
        
        // Einfache Demonstration
        demonstrateBasicInterceptors();
        
        // Demonstration mit HTTP-Anfrage
        demonstrateHttpInterceptors();
        
        // Demonstration mit Caching
        demonstrateCachingInterceptors();
        
        // Fehlerbehandlung demonstrieren
        demonstrateErrorHandling();
        
        LOGGER.info("Demonstration abgeschlossen");
    }
    
    /**
     * Demonstriert die grundlegende Verwendung von Interceptoren.
     */
    private static void demonstrateBasicInterceptors() {
        LOGGER.info("\n--- Grundlegende Interceptor-Pattern-Demonstration ---");
        
        // Erstelle einen Dispatcher
        Dispatcher dispatcher = new Dispatcher();
        
        // Registriere Interceptoren
        dispatcher.registerInterceptor(new LoggingInterceptor("DemoLogger"));
        dispatcher.registerInterceptor(new PerformanceInterceptor(50, 100));
        
        // Erstelle einen Kontext mit Eingabedaten
        Context context = new Context("Testdaten");
        
        // Führe eine Operation aus
        dispatcher.dispatch(context, ctx -> {
            LOGGER.info("Führe Hauptoperation aus mit Eingabe: {}", ctx.getInput());
            
            // Simuliere eine Verarbeitung
            try {
                Thread.sleep(75); // Diese Dauer sollte eine Warnung auslösen
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            return "Verarbeitetes Ergebnis: " + ctx.getInput();
        });
        
        // Zeige das Ergebnis
        LOGGER.info("Operation abgeschlossen mit Ergebnis: {}", context.getResult());
    }
    
    /**
     * Demonstriert die Verwendung von HTTP-Interceptoren.
     */
    private static void demonstrateHttpInterceptors() {
        LOGGER.info("\n--- HTTP-Interceptor-Demonstration ---");
        
        // Erstelle einen Dispatcher
        Dispatcher dispatcher = new Dispatcher();
        
        // Registriere Interceptoren
        dispatcher.registerInterceptor(new LoggingInterceptor("HTTPLogger"));
        
        // Erstelle einen Authentifizierungs-Interceptor
        AuthenticationInterceptor authInterceptor = new AuthenticationInterceptor();
        authInterceptor.addUser("admin", "password123");
        dispatcher.registerInterceptor(authInterceptor);
        
        // Erstelle einen Sicherheits-Interceptor mit Rollenanforderungen
        SecurityInterceptor securityInterceptor = new SecurityInterceptor(true);
        securityInterceptor.addRequiredRole("ADMIN");
        dispatcher.registerInterceptor(securityInterceptor);
        
        // Erstelle einen Kontext mit HTTP-Anfrage
        Context context = new Context("http://example.com/api/data");
        
        // Füge HTTP-Headers hinzu
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic " + Base64Encode("admin:password123"));
        context.setAttribute("http.headers", headers);
        
        // Füge Rollen des Benutzers hinzu (normalerweise nach erfolgreicher Authentifizierung)
        Set<String> roles = new HashSet<>();
        roles.add("ADMIN");
        context.setAttribute("security.roles", roles);
        
        // Führe eine Operation aus
        boolean success = dispatcher.dispatch(context, ctx -> {
            LOGGER.info("Verarbeite HTTP-Anfrage: {}", ctx.getInput());
            
            // Simuliere eine HTTP-Anfrage
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            return "HTTP-Antwort für: " + ctx.getInput();
        });
        
        // Zeige das Ergebnis
        if (success) {
            LOGGER.info("HTTP-Anfrage erfolgreich verarbeitet: {}", context.getResult());
        } else {
            LOGGER.info("HTTP-Anfrage fehlgeschlagen: {}", 
                    context.getAttribute("security.error"));
        }
    }
    
    /**
     * Demonstriert die Verwendung von Caching-Interceptoren.
     */
    private static void demonstrateCachingInterceptors() {
        LOGGER.info("\n--- Caching-Interceptor-Demonstration ---");
        
        // Erstelle einen Dispatcher
        Dispatcher dispatcher = new Dispatcher();
        
        // Registriere Interceptoren
        dispatcher.registerInterceptor(new LoggingInterceptor("CacheLogger"));
        dispatcher.registerInterceptor(new PerformanceInterceptor());
        
        // Erstelle einen Caching-Interceptor mit kurzer TTL für die Demonstration
        CachingInterceptor cachingInterceptor = new CachingInterceptor(10000); // 10 Sekunden
        dispatcher.registerInterceptor(cachingInterceptor);
        
        // Erstelle einen Kontext mit Eingabedaten
        Context context1 = new Context("CacheDaten");
        context1.setAttribute("caching.cacheable", true);
        
        // Führe die erste Operation aus (Cache-Miss)
        LOGGER.info("Erste Anfrage (Cache-Miss):");
        dispatcher.dispatch(context1, ctx -> {
            LOGGER.info("Verarbeite Anfrage: {}", ctx.getInput());
            
            // Simuliere eine zeitaufwendige Berechnung
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            return "Berechnetes Ergebnis für: " + ctx.getInput();
        });
        
        // Zeige das Ergebnis der ersten Anfrage
        LOGGER.info("Erste Anfrage abgeschlossen mit Ergebnis: {}", context1.getResult());
        
        // Führe eine zweite identische Operation aus (Cache-Hit)
        Context context2 = new Context("CacheDaten");
        context2.setAttribute("caching.cacheable", true);
        
        LOGGER.info("\nZweite identische Anfrage (Cache-Hit):");
        dispatcher.dispatch(context2, ctx -> {
            LOGGER.info("Verarbeite Anfrage: {}", ctx.getInput());
            
            // Diese sollte nicht ausgeführt werden, da das Ergebnis aus dem Cache kommt
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            return "Berechnetes Ergebnis für: " + ctx.getInput();
        });
        
        // Zeige das Ergebnis der zweiten Anfrage
        LOGGER.info("Zweite Anfrage abgeschlossen mit Ergebnis: {}", context2.getResult());
        
        // Führe eine dritte Anfrage mit anderen Daten aus (Cache-Miss)
        Context context3 = new Context("AndereDaten");
        context3.setAttribute("caching.cacheable", true);
        
        LOGGER.info("\nDritte Anfrage mit anderen Daten (Cache-Miss):");
        dispatcher.dispatch(context3, ctx -> {
            LOGGER.info("Verarbeite Anfrage: {}", ctx.getInput());
            
            // Simuliere eine zeitaufwendige Berechnung
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            return "Berechnetes Ergebnis für: " + ctx.getInput();
        });
        
        // Zeige das Ergebnis der dritten Anfrage
        LOGGER.info("Dritte Anfrage abgeschlossen mit Ergebnis: {}", context3.getResult());
    }
    
    /**
     * Demonstriert die Fehlerbehandlung mit Interceptoren.
     */
    private static void demonstrateErrorHandling() {
        LOGGER.info("\n--- Fehlerbehandlungs-Demonstration ---");
        
        // Erstelle einen Dispatcher
        Dispatcher dispatcher = new Dispatcher();
        
        // Registriere Interceptoren
        dispatcher.registerInterceptor(new LoggingInterceptor("ErrorLogger"));
        
        // Registriere einen benutzerdefinierten Fehlerbehandlungs-Interceptor
        dispatcher.registerInterceptor(new Interceptor() {
            @Override
            public boolean preProcess(Context context) {
                return true;
            }
            
            @Override
            public void postProcess(Context context) {
                // Nichts zu tun
            }
            
            @Override
            public boolean handleException(Context context, Exception exception) {
                LOGGER.info("Fehlerbehandlung für Exception: {} - {}", 
                        exception.getClass().getSimpleName(), exception.getMessage());
                
                // Setze ein Standardergebnis im Fehlerfall
                context.setResult("Standardergebnis im Fehlerfall");
                context.setAttribute("error.handled", true);
                context.setAttribute("error.message", exception.getMessage());
                
                // Wir geben true zurück, um anzuzeigen, dass wir den Fehler behandelt haben
                return true;
            }
        });
        
        // Erstelle einen Kontext mit Eingabedaten
        Context context = new Context("FehlerhafteDaten");
        
        // Führe eine Operation aus, die eine Exception wirft
        dispatcher.dispatch(context, ctx -> {
            LOGGER.info("Führe fehlerhafte Operation aus");
            throw new RuntimeException("Simulierter Fehler");
        });
        
        // Zeige das Ergebnis
        LOGGER.info("Operation abgeschlossen mit Ergebnis: {}", context.getResult());
        LOGGER.info("Fehler wurde behandelt: {}", context.getAttribute("error.handled"));
        LOGGER.info("Fehlermeldung: {}", context.getAttribute("error.message"));
    }
    
    /**
     * Kodiert einen String in Base64.
     * 
     * @param input Der zu kodierende String
     * @return Der kodierte String
     */
    private static String Base64Encode(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes());
    }
}