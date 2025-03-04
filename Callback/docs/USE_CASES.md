# Anwendungsbeispiele des Callback-Patterns in verteilten Systemen

Dieses Dokument stellt reale Anwendungsfälle des Callback-Patterns in verteilten Systemen vor und analysiert deren Implementierungsdetails.

## Übersicht der Anwendungsfälle

```mermaid
mindmap
  root((Callback-Pattern))
    Asynchrone Kommunikation
      REST API mit Callbacks
      WebHooks
      Event Notification
      Message Queue Callbacks
    Lang andauernde Prozesse
      Batch-Verarbeitung
      Datenbereinigung
      Berichterstellung
      Datenimport/-export
    Fehlerbehandlung
      Retry-Mechanismen
      Circuit Breaker
      Fallback-Strategien
      Fehlerprotokollierung
    Fortschrittsüberwachung
      Progress Tracking
      Statusupdates
      Stufenweise Verarbeitung
      Logging-Events
```

## Detaillierte Anwendungsfälle

### 1. Asynchrone API-Integrationen mit Webhooks

```mermaid
graph TD
    A[Client] -->|1. API-Anfrage mit callback_url| B[API-Server]
    B -->|2. Akzeptiert Anfrage| A
    B -->|3. Startet asynchrone Verarbeitung| C[Verarbeitungsdienst]
    C -->|4. Verarbeitet Daten| C
    C -->|5. Ergebnisse| B
    B -->|6. Webhook-Callback| D[Client-Webhook-Endpunkt]
    
    subgraph "Client-System"
        A
        D
    end
    
    subgraph "Server-System"
        B
        C
    end
```

#### Webhook-Integration: Sequenzdiagramm für Zahlungsverarbeitung

```mermaid
sequenceDiagram
    participant Client
    participant PaymentAPI as Zahlungs-API
    participant Processing as Verarbeitungsdienst
    participant Webhook as Client-Webhook
    
    Client->>+PaymentAPI: POST /payments {amount: 100, callback_url: "https://client.com/webhook"}
    PaymentAPI-->>-Client: HTTP 202 {payment_id: "12345", status: "pending"}
    
    PaymentAPI->>+Processing: Zahlungsanfrage-Event
    Processing->>Processing: Verarbeite Zahlung
    Processing->>Processing: Autorisiere bei Zahlungsanbieter
    
    alt Erfolgreiche Verarbeitung
        Processing-->>-PaymentAPI: Erfolg: Zahlung autorisiert
        PaymentAPI->>+Webhook: POST /webhook {payment_id: "12345", status: "completed"}
        Webhook-->>-PaymentAPI: HTTP 200 OK
    else Fehlgeschlagene Verarbeitung
        Processing-->>-PaymentAPI: Fehler: Unzureichendes Guthaben
        PaymentAPI->>+Webhook: POST /webhook {payment_id: "12345", status: "failed", reason: "insufficient_funds"}
        Webhook-->>-PaymentAPI: HTTP 200 OK
    end
```

### 2. Message-Queue-basierte Callbacks für Verarbeitung großer Datenmengen

```mermaid
graph LR
    A[Datenimport-Client] --> B[Message Queue]
    
    subgraph "Verarbeitungscluster"
        B --> C[Worker 1]
        B --> D[Worker 2]
        B --> E[Worker n]
    end
    
    C --> F[Ergebnis-Queue]
    D --> F
    E --> F
    
    F --> G[Callback-Processor]
    G --> H[Ergebnis-Speicher]
    G --> I[Client-Benachrichtigung]
    I --> A
```

#### Aktivitätsdiagramm für Message-Queue Callbacks

```mermaid
stateDiagram-v2
    [*] --> EmpfangeAufgabe
    
    EmpfangeAufgabe --> VerarbeitungsQueueErstellen
    VerarbeitungsQueueErstellen --> AufgabenVerteilen
    
    AufgabenVerteilen --> Worker1
    AufgabenVerteilen --> Worker2
    AufgabenVerteilen --> WorkerN
    
    Worker1 --> ErgebnisseSammeln
    Worker2 --> ErgebnisseSammeln
    WorkerN --> ErgebnisseSammeln
    
    ErgebnisseSammeln --> ÜberprüfeFehler
    
    ÜberprüfeFehler --> ErgebnisseZusammenfassen: Keine Fehler
    ÜberprüfeFehler --> FehlerBehandlung: Fehler gefunden
    
    FehlerBehandlung --> CallbackMitFehler
    ErgebnisseZusammenfassen --> CallbackMitErfolg
    
    CallbackMitFehler --> [*]
    CallbackMitErfolg --> [*]
```

### 3. Polling-basierte Callbacks für öffentliche Cloud-APIs

```mermaid
graph TD
    A[Client] -->|1. Starte Dataflow-Job| B[Cloud API]
    B -->|2. Erstelle Job| C[Verarbeitungsdienst]
    B -->|3. Liefere Job-ID| A
    
    A -->|4. Polling: GET /jobs/{job_id}| B
    B -->|5. Status: RUNNING| A
    
    C -->|6. Verarbeitet Daten| C
    C -->|7. Aktualisiert Status| B
    
    A -->|8. Polling: GET /jobs/{job_id}| B
    B -->|9. Status: COMPLETED, Ergebnisse| A
    
    subgraph "Client-System"
        A
    end
    
    subgraph "Cloud-Plattform"
        B
        C
    end
```

#### Komponentendiagramm für ein Polling-System

```mermaid
graph TD
    A[Client] -->|1. Initiiert Aufgabe| B[API-Gateway]
    
    subgraph "Backend-Dienste"
        B --> C[Aufgabenmanager]
        C --> D[Aufgabenspeicher]
        C --> E[Verarbeitungsengine]
        E --> F[Worker-Pool]
        F --> D
    end
    
    A -->|2. Polling-Anfrage| B
    B --> C
    C --> D
    D -->|3. Status/Ergebnis| C
    C -->|4. Status/Ergebnis| B
    B -->|5. Status/Ergebnis| A
```

### 4. Retry-Callbacks für zuverlässige Systemintegration

```mermaid
graph TD
    A[Client] --> B[Retry-Manager]
    B --> C{Ausführen}
    
    C -->|1. Versuch| D[Remote Service]
    D -->|Fehler| E[Backoff-Strategie]
    E --> F{Max Retries?}
    
    F -->|Nein| C
    F -->|Ja| G[Fehler-Callback]
    
    D -->|Erfolg| H[Erfolgs-Callback]
    
    G --> A
    H --> A
```

#### Aktivitätsdiagramm eines Retry-Callbacks

```mermaid
stateDiagram-v2
    [*] --> InitialRequest
    
    InitialRequest --> CheckResponse
    
    CheckResponse --> Success: OK (2xx)
    CheckResponse --> Retriable: Fehler (5xx)
    CheckResponse --> NonRetriable: Fehler (4xx)
    
    Retriable --> CalculateBackoff
    CalculateBackoff --> CheckRetryCount
    
    CheckRetryCount --> Wait: Versuche < Max
    CheckRetryCount --> FinalFailure: Versuche >= Max
    
    Wait --> Retry
    Retry --> CheckResponse
    
    Success --> SuccessCallback
    NonRetriable --> ErrorCallback
    FinalFailure --> ErrorCallback
    
    SuccessCallback --> [*]
    ErrorCallback --> [*]
```

## Design-Entscheidungen beim Callback-Einsatz

```mermaid
graph TD
    A[Callback-Design-Entscheidungen] --> B{Ausführungskontext}
    
    B -->|Synchron| C[Direkter Aufruf]
    B -->|Asynchron| D[Threadpool-Ausführung]
    
    C --> E{Granularität}
    D --> F{Fehlerbehandlung}
    
    E -->|Grobkörnig| G[Wenige, umfangreiche Callbacks]
    E -->|Feinkörnig| H[Viele, spezifische Callbacks]
    
    F -->|Einfach| I[Error-Callback übergeben]
    F -->|Fortgeschritten| J[Retry mit exponential Backoff]
    
    G --> K[Weniger Overhead, schwieriger zu parallelisieren]
    H --> L[Bessere Parallelisierung, höherer Overhead]
    
    I --> M[Fehlerinformationen an Client]
    I --> N[Logging für Diagnose]
    
    J --> O[Circuit Breaker]
    J --> P[Fallback-Strategien]
```

## Evolutionspfad für Callback-Pattern

```mermaid
graph LR
    A[Einfache Callbacks] --> B{Problem: Verschachtelung}
    B -->|Lösung 1| C[Promise/Future]
    B -->|Lösung 2| D[Funktionale Ketten]
    
    C --> E{Problem: Fehlerbehandlung}
    D --> E
    
    E -->|Lösung 1| F[Try/Either Monaden]
    E -->|Lösung 2| G[Reactive Streams]
    
    F --> H{Problem: Skalierbarkeit}
    G --> H
    
    H -->|Lösung 1| I[Verteilte Systeme]
    I --> J[Event-getriebene Microservices]
```

## Praktische Umsetzungsbeispiele

### Beispiel 1: Implementierung eines Webhook-Callbacks in Java

```java
// Server-Seite
public class WebhookService {
    
    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();
    
    public void processOrderAsync(Order order, String callbackUrl) {
        CompletableFuture.runAsync(() -> {
            try {
                // Lang andauernde Verarbeitung simulieren
                Thread.sleep(3000);
                
                // Ergebnis vorbereiten
                OrderResult result = new OrderResult(order.getId(), "COMPLETED");
                String json = new ObjectMapper().writeValueAsString(result);
                
                // Webhook aufrufen
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(callbackUrl))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();
                
                HttpResponse<String> response = httpClient.send(
                        request, HttpResponse.BodyHandlers.ofString());
                
                // Antwort loggen
                System.out.println("Webhook Antwort: " + response.statusCode() + 
                        " - " + response.body());
                
            } catch (Exception e) {
                // Fehlerbehandlung und Wiederholungslogik
                System.err.println("Webhook-Fehler: " + e.getMessage());
            }
        });
    }
}

// Client-Seite
@RestController
public class WebhookController {
    
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody OrderResult orderResult) {
        // Ergebnis verarbeiten
        System.out.println("Webhook empfangen: Order " + orderResult.getOrderId() + 
                " ist jetzt " + orderResult.getStatus());
        
        // Erfolgreiche Verarbeitung bestätigen
        return ResponseEntity.ok("Webhook empfangen");
    }
}
```

### Beispiel 2: Retry-Callback mit Resilience4j

```java
public class RetryCallbackExample {
    
    private final RetryRegistry retryRegistry;
    
    public RetryCallbackExample() {
        // Retry-Konfiguration erstellen
        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofSeconds(1))
                .retryExceptions(IOException.class, TimeoutException.class)
                .ignoreExceptions(IllegalArgumentException.class)
                .build();
        
        // Retry-Registry erstellen
        retryRegistry = RetryRegistry.of(retryConfig);
    }
    
    public void executeWithRetry(Runnable operation, Consumer<Throwable> errorCallback) {
        Retry retry = retryRegistry.retry("operation");
        
        Runnable retryableOperation = Retry.decorateRunnable(retry, operation);
        
        try {
            retryableOperation.run();
        } catch (Exception e) {
            errorCallback.accept(e);
        }
    }
}
```

### Beispiel 3: Polling-Callback mit CompletableFuture

```java
public class PollingCallbackExample {
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Map<String, JobStatus> jobStatuses = new ConcurrentHashMap<>();
    
    public CompletableFuture<String> startJobWithPolling(String jobId, long timeoutMs) {
        CompletableFuture<String> resultFuture = new CompletableFuture<>();
        
        // Starte den Job asynchron
        jobStatuses.put(jobId, JobStatus.RUNNING);
        
        // Simuliere Jobverarbeitung
        scheduler.schedule(() -> {
            jobStatuses.put(jobId, JobStatus.COMPLETED);
        }, 5, TimeUnit.SECONDS);
        
        // Polling starten
        AtomicInteger attempts = new AtomicInteger(0);
        AtomicLong delay = new AtomicLong(100);
        
        Runnable pollTask = new Runnable() {
            @Override
            public void run() {
                if (resultFuture.isDone()) {
                    return;
                }
                
                JobStatus status = jobStatuses.get(jobId);
                System.out.println("Polling Versuch " + attempts.incrementAndGet() + 
                        ": Status = " + status);
                
                if (status == JobStatus.COMPLETED) {
                    resultFuture.complete("Job " + jobId + " erfolgreich abgeschlossen");
                } else if (status == JobStatus.FAILED) {
                    resultFuture.completeExceptionally(
                            new RuntimeException("Job " + jobId + " fehlgeschlagen"));
                } else if (System.currentTimeMillis() > timeoutMs) {
                    resultFuture.completeExceptionally(
                            new TimeoutException("Timeout beim Warten auf Job " + jobId));
                } else {
                    // Exponential Backoff für das nächste Polling
                    long nextDelay = Math.min(delay.get() * 2, 1000);
                    delay.set(nextDelay);
                    
                    // Nächster Versuch
                    scheduler.schedule(this, nextDelay, TimeUnit.MILLISECONDS);
                }
            }
        };
        
        // Erstes Polling starten
        scheduler.schedule(pollTask, 100, TimeUnit.MILLISECONDS);
        
        return resultFuture;
    }
    
    private enum JobStatus {
        PENDING, RUNNING, COMPLETED, FAILED
    }
}
```
