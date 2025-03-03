package de.becke.vs.pattern.interceptor.core;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Testklasse für das Interceptor-Pattern.
 */
public class InterceptorTest {
    
    private Dispatcher dispatcher;
    private TestInterceptor testInterceptor;
    
    @Before
    public void setup() {
        dispatcher = new Dispatcher();
        testInterceptor = new TestInterceptor();
        dispatcher.registerInterceptor(testInterceptor);
    }
    
    @Test
    public void testBasicInterceptorFlow() {
        // Erstelle einen Kontext mit Testdaten
        Context context = new Context("TestData");
        
        // Führe eine Operation mit dem Dispatcher aus
        boolean result = dispatcher.dispatch(context, ctx -> {
            return "Processed: " + ctx.getInput();
        });
        
        // Prüfe, ob die Operation erfolgreich war
        assertTrue("Die Operation sollte erfolgreich sein", result);
        
        // Prüfe, ob preProcess und postProcess aufgerufen wurden
        assertTrue("preProcess sollte aufgerufen worden sein", testInterceptor.wasPreProcessCalled());
        assertTrue("postProcess sollte aufgerufen worden sein", testInterceptor.wasPostProcessCalled());
        assertFalse("handleException sollte nicht aufgerufen worden sein", 
                testInterceptor.wasHandleExceptionCalled());
        
        // Prüfe das Ergebnis
        assertEquals("Processed: TestData", context.getResult());
    }
    
    @Test
    public void testErrorHandling() {
        // Erstelle einen Kontext mit Testdaten
        Context context = new Context("TestData");
        
        // Konfiguriere den Interceptor, um Fehler zu behandeln
        testInterceptor.setHandleExceptions(true);
        
        // Führe eine Operation aus, die eine Exception wirft
        boolean result = dispatcher.dispatch(context, ctx -> {
            throw new RuntimeException("Test Exception");
        });
        
        // Prüfe, ob die Operation trotz Exception als erfolgreich markiert wurde
        // da der Interceptor den Fehler behandelt hat
        assertTrue("Die Operation sollte als erfolgreich markiert sein, da der Fehler behandelt wurde", result);
        
        // Prüfe, ob die richtigen Methoden aufgerufen wurden
        assertTrue("preProcess sollte aufgerufen worden sein", testInterceptor.wasPreProcessCalled());
        assertFalse("postProcess sollte nicht aufgerufen worden sein", testInterceptor.wasPostProcessCalled());
        assertTrue("handleException sollte aufgerufen worden sein", testInterceptor.wasHandleExceptionCalled());
        
        // Prüfe, ob im Kontext das Standardergebnis gesetzt wurde
        assertEquals("Default result after exception", context.getResult());
    }
    
    @Test
    public void testPreProcessCancel() {
        // Erstelle einen Kontext mit Testdaten
        Context context = new Context("TestData");
        
        // Konfiguriere den Interceptor, preProcess abzubrechen
        testInterceptor.setCancelInPreProcess(true);
        
        // Führe eine Operation mit dem Dispatcher aus
        boolean result = dispatcher.dispatch(context, ctx -> {
            fail("Diese Operation sollte nicht ausgeführt werden");
            return null;
        });
        
        // Prüfe, ob die Operation als nicht erfolgreich markiert wurde
        assertFalse("Die Operation sollte als nicht erfolgreich markiert sein", result);
        
        // Prüfe, ob nur preProcess aufgerufen wurde
        assertTrue("preProcess sollte aufgerufen worden sein", testInterceptor.wasPreProcessCalled());
        assertFalse("postProcess sollte nicht aufgerufen worden sein", testInterceptor.wasPostProcessCalled());
        assertFalse("handleException sollte nicht aufgerufen worden sein", 
                testInterceptor.wasHandleExceptionCalled());
        
        // Prüfe, ob das Ergebnis nicht gesetzt wurde
        assertNull("Das Ergebnis sollte nicht gesetzt sein", context.getResult());
    }
    
    @Test
    public void testMultipleInterceptors() {
        // Erstelle einen zweiten Interceptor
        TestInterceptor secondInterceptor = new TestInterceptor();
        dispatcher.registerInterceptor(secondInterceptor);
        
        // Erstelle einen Kontext mit Testdaten
        Context context = new Context("TestData");
        
        // Führe eine Operation mit dem Dispatcher aus
        boolean result = dispatcher.dispatch(context, ctx -> {
            return "Processed: " + ctx.getInput();
        });
        
        // Prüfe, ob die Operation erfolgreich war
        assertTrue("Die Operation sollte erfolgreich sein", result);
        
        // Prüfe, ob beide Interceptoren aufgerufen wurden
        assertTrue("preProcess des ersten Interceptors sollte aufgerufen worden sein", 
                testInterceptor.wasPreProcessCalled());
        assertTrue("postProcess des ersten Interceptors sollte aufgerufen worden sein", 
                testInterceptor.wasPostProcessCalled());
        
        assertTrue("preProcess des zweiten Interceptors sollte aufgerufen worden sein", 
                secondInterceptor.wasPreProcessCalled());
        assertTrue("postProcess des zweiten Interceptors sollte aufgerufen worden sein", 
                secondInterceptor.wasPostProcessCalled());
    }
    
    /**
     * Eine Implementierung des Interceptor-Interfaces für Testzwecke.
     */
    private static class TestInterceptor implements Interceptor {
        
        private boolean preProcessCalled = false;
        private boolean postProcessCalled = false;
        private boolean handleExceptionCalled = false;
        private boolean cancelInPreProcess = false;
        private boolean handleExceptions = false;
        
        @Override
        public boolean preProcess(Context context) {
            preProcessCalled = true;
            return !cancelInPreProcess;
        }
        
        @Override
        public void postProcess(Context context) {
            postProcessCalled = true;
        }
        
        @Override
        public boolean handleException(Context context, Exception exception) {
            handleExceptionCalled = true;
            
            if (handleExceptions) {
                // Setze ein Standardergebnis im Fehlerfall
                context.setResult("Default result after exception");
                return true;
            }
            
            return false;
        }
        
        public boolean wasPreProcessCalled() {
            return preProcessCalled;
        }
        
        public boolean wasPostProcessCalled() {
            return postProcessCalled;
        }
        
        public boolean wasHandleExceptionCalled() {
            return handleExceptionCalled;
        }
        
        public void setCancelInPreProcess(boolean cancelInPreProcess) {
            this.cancelInPreProcess = cancelInPreProcess;
        }
        
        public void setHandleExceptions(boolean handleExceptions) {
            this.handleExceptions = handleExceptions;
        }
    }
}