# Anwendungsbeispiele des Dependency Injection Patterns in verteilten Systemen

Dieses Dokument stellt reale Anwendungsfälle des Dependency Injection Patterns in verteilten Systemen vor und analysiert deren Implementierungsdetails.

## Übersicht der Anwendungsfälle

```mermaid
mindmap
  root((Dependency Injection))
    Microservices-Architektur
      Service-Konfiguration
      Service-Discovery-Integration
      Circuit-Breaker-Integration
      Protokoll-Abstraktion
    Cloud-native Anwendungen
      Umgebungsspezifische Konfiguration
      Multi-Cloud-Abstraktion
      Containerisierung
      Skalierbarkeit
    Verteilte Ressourcenverwaltung
      Connection-Pooling
      Transaktionsmanagement
      Distributed Caching
      Load Balancing
    Enterprise Integrationen
      Legacy-System-Adaptierung
      API-Gateway-Konfiguration
      Event-Driven-Architektur
      Messaging-Integration
```

## Detaillierte Anwendungsfälle

### 1. Microservices mit Dynamischer Service-Discovery

```mermaid
flowchart TD
    A[Client] -->|1. Fordert Service an| B[API Gateway]
    B -->|2. Service-Lookup| C[Service Registry]
    C -->|3. Liefert verfügbare Endpoints| B
    
    B -->|4. Request an Service| D[Service Instanz]
    D -->|5. Antwort| B
    B -->|6. Antwort an Client| A
    
    subgraph "Service Registry"
        C
        E[Service Registrar]
        F[Health Checker]
    end
    
    subgraph "Microservice mit DI"
        D
        G[Controller]
        H[Business Logic]
        I[Data Access]
        J[External Service Client]
        
        G --> H
        H --> I
        H --> J
    end
    
    K[Service Configuration] -.-> J
    L[Circuit Breaker] -.-> J
    M[Load Balancer] -.-> J
```

#### Sequenzdiagramm für Microservice mit Dependency Injection

```mermaid
sequenceDiagram
    participant Client
    participant Gateway as API Gateway
    participant Registry as Service Registry
    participant Service as Microservice
    participant External as External Service
    
    Client->>Gateway: Request
    Gateway->>Registry: Lookup Service
    Registry-->>Gateway: Service Endpoints
    
    Gateway->>Service: Forward Request
    
    Note over Service: DI Container injiziert Abhängigkeiten
    
    Service->>External: Call External Service
    External-->>Service: Response
    Service-->>Gateway: Response
    Gateway-->>Client: Response
```

### 2. Cloud-native Anwendungen mit Umgebungsspezifischer Konfiguration

```mermaid
flowchart LR
    A[Anwendung] --> B[Spring Boot App]
    
    subgraph "ConfigServer"
        C[Spring Cloud Config]
        D[Git Repository]
        E[Vault]
        C --> D
        C --> E
    end
    
    B --> F[DI Container]
    
    F --> G[Dev Profile]
    F --> H[Test Profile]
    F --> I[Prod Profile]
    
    G --> J[Dev Beans]
    H --> K[Test Beans]
    I --> L[Prod Beans]
    
    B --> C
```

#### Aktivitätsdiagramm für Umgebungsspezifische DI-Konfiguration

```mermaid
stateDiagram-v2
    [*] --> Start
    
    Start --> CheckEnvironment
    
    CheckEnvironment --> LoadDevConfig: Dev Environment
    CheckEnvironment --> LoadTestConfig: Test Environment
    CheckEnvironment --> LoadProdConfig: Prod Environment
    
    LoadDevConfig --> ConfigureDevBeans
    LoadTestConfig --> ConfigureTestBeans
    LoadProdConfig --> ConfigureProdBeans
    
    ConfigureDevBeans --> InjectDependencies
    ConfigureTestBeans --> InjectDependencies
    ConfigureProdBeans --> InjectDependencies
    
    InjectDependencies --> ApplicationReady
    
    ApplicationReady --> [*]
```

### 3. API-Gateway mit Pluggable Routing-Strategien

```mermaid
flowchart TD
    A[Client] -->|Request| B[API Gateway]
    
    subgraph "API Gateway mit DI"
        B --> C{Router}
        C -->|Route 1| D[Service A]
        C -->|Route 2| E[Service B]
        C -->|Route 3| F[Service C]
        
        G[RoutingStrategyProvider] -->|injiziert| C
        H[SecurityProvider] -->|injiziert| B
        I[RateLimiterProvider] -->|injiziert| B
        J[CircuitBreakerProvider] -->|injiziert| B
    end
    
    K[Configuration] --> G
    K --> H
    K --> I
    K --> J
```

#### Komponentendiagramm für ein API-Gateway-System

```mermaid
flowchart TD
    A[Gateway Controller] --> B[Request Processor]
    
    B --> C[Authentication Module]
    B --> D[Authorization Module]
    B --> E[Rate Limiting]
    B --> F[Request Routing]
    B --> G[Response Transformation]
    
    subgraph "Dependency Injection Container"
        H[Auth Provider]
        I[Route Strategy]
        J[Rate Limit Config]
        K[Transformation Rules]
    end
    
    H -->|injiziert| C
    H -->|injiziert| D
    I -->|injiziert| F
    J -->|injiziert| E
    K -->|injiziert| G
```

### 4. Event-Driven-Architektur mit austauschbaren Message Brokern

```mermaid
flowchart TD
    A[Producer Service] --> B[Event Publisher]
    
    subgraph "Message Broker Abstraction"
        B --> C{Message Broker Factory}
        C -->|Kafka Config| D[Kafka Broker]
        C -->|RabbitMQ Config| E[RabbitMQ Broker]
        C -->|ActiveMQ Config| F[ActiveMQ Broker]
    end
    
    D --> G[Kafka Cluster]
    E --> H[RabbitMQ Cluster]
    F --> I[ActiveMQ Cluster]
    
    J[Consumer Service 1] --> K[Event Subscriber 1]
    L[Consumer Service 2] --> M[Event Subscriber 2]
    
    G --> K
    G --> M
    H --> K
    H --> M
    I --> K
    I --> M
```

#### Aktivitätsdiagramm eines Event-Prozesses mit DI

```mermaid
stateDiagram-v2
    [*] --> EventTrigger
    
    EventTrigger --> CreateEvent
    CreateEvent --> SerializeEvent
    
    SerializeEvent --> GetMessageBroker
    GetMessageBroker --> KafkaPublish: Kafka konfiguriert
    GetMessageBroker --> RabbitPublish: RabbitMQ konfiguriert
    GetMessageBroker --> ActiveMQPublish: ActiveMQ konfiguriert
    
    KafkaPublish --> WaitForAck
    RabbitPublish --> WaitForAck
    ActiveMQPublish --> WaitForAck
    
    WaitForAck --> HandleSuccess: Erfolgreich
    WaitForAck --> HandleError: Fehler
    
    HandleSuccess --> [*]
    HandleError --> RetryLogic
    RetryLogic --> SerializeEvent: Retry
    RetryLogic --> [*]: Max Retries
```

## Design-Entscheidungen bei Dependency Injection

```mermaid
flowchart TD
    A[DI Design-Entscheidungen] --> B{Framework oder manuell?}
    
    B -->|Framework| C[Spring/Guice/Dagger/etc.]
    B -->|Manuell| D[Eigene DI-Implementierung]
    
    C --> E{Konfigurationstyp?}
    D --> F{Injektionstyp?}
    
    E -->|XML| G[XML-basierte Konfiguration]
    E -->|Annotation| H[Annotation-basierte DI]
    E -->|Java-Code| I[Java-basierte Konfiguration]
    
    F -->|Konstruktor| J[Konstruktor-Injektion]
    F -->|Setter| K[Setter-Injektion]
    F -->|Field| L[Field-Injektion]
    
    G --> M[Explizite Konfiguration, IDE-Support]
    H --> N[Weniger Boilerplate, enge Code-Kopplung]
    I --> O[Typsicherheit, IDE-Support, Runtime-Flexibilität]
    
    J --> P[Unveränderliche Objekte, klare Abhängigkeiten]
    K --> Q[Optionale Abhängigkeiten, Zirkelbezüge möglich]
    L --> R[Einfache Syntax, aber versteckte Abhängigkeiten]
```

## Implementierungsbeispiele

### Beispiel 1: Spring Cloud für Microservices

```java
@Configuration
@EnableDiscoveryClient
public class MicroserviceConfig {
    
    @Bean
    @ConditionalOnProperty(name = "service.mode", havingValue = "local")
    public RemoteServiceClient localServiceClient() {
        return new LocalServiceClient();
    }
    
    @Bean
    @ConditionalOnProperty(name = "service.mode", havingValue = "remote", matchIfMissing = true)
    public RemoteServiceClient remoteServiceClient(
            @Value("${service.timeout:1000}") int timeout,
            @Value("${service.retry.maxAttempts:3}") int maxRetries) {
        
        return new RemoteServiceClientImpl(timeout, maxRetries);
    }
    
    @Bean
    public CircuitBreaker circuitBreaker() {
        return CircuitBreaker.builder()
                .failureThreshold(3)
                .resetTimeout(Duration.ofSeconds(30))
                .build();
    }
    
    @Bean
    public LoadBalancerClient loadBalancerClient(DiscoveryClient discoveryClient) {
        return new RoundRobinLoadBalancer(discoveryClient);
    }
}
```

### Beispiel 2: Guice für austauschbare Implementierungen

```java
public class MessagingModule extends AbstractModule {
    
    private final Config config;
    
    public MessagingModule(Config config) {
        this.config = config;
    }
    
    @Override
    protected void configure() {
        // Bind allgemeine Schnittstellen an konkrete Implementierungen
        bind(MessagePublisher.class).to(getPublisherImplementation());
        bind(MessageConsumer.class).to(getConsumerImplementation());
        
        // Named Bindings für spezifische Verwendungszwecke
        bind(MessagePublisher.class)
            .annotatedWith(Names.named("audit"))
            .to(AuditMessagePublisher.class);
        
        // Provider-Methode für komplexe Erstellung
        bind(MessageSerializer.class)
            .toProvider(SerializerProvider.class)
            .in(Singleton.class);
    }
    
    private Class<? extends MessagePublisher> getPublisherImplementation() {
        String type = config.getString("messaging.type", "kafka");
        switch (type) {
            case "kafka":
                return KafkaMessagePublisher.class;
            case "rabbitmq":
                return RabbitMQMessagePublisher.class;
            case "activemq":
                return ActiveMQMessagePublisher.class;
            default:
                throw new IllegalArgumentException("Unsupported messaging type: " + type);
        }
    }
    
    private Class<? extends MessageConsumer> getConsumerImplementation() {
        String type = config.getString("messaging.type", "kafka");
        switch (type) {
            case "kafka":
                return KafkaMessageConsumer.class;
            case "rabbitmq":
                return RabbitMQMessageConsumer.class;
            case "activemq":
                return ActiveMQMessageConsumer.class;
            default:
                throw new IllegalArgumentException("Unsupported messaging type: " + type);
        }
    }
    
    @Provides
    @Singleton
    public ConnectionFactory provideConnectionFactory() {
        String type = config.getString("messaging.type", "kafka");
        String host = config.getString("messaging.host", "localhost");
        int port = config.getInt("messaging.port", getDefaultPort(type));
        
        return ConnectionFactoryBuilder.newBuilder()
                .type(type)
                .host(host)
                .port(port)
                .credentials(
                    config.getString("messaging.username", ""),
                    config.getString("messaging.password", ""))
                .build();
    }
    
    private int getDefaultPort(String type) {
        switch (type) {
            case "kafka": return 9092;
            case "rabbitmq": return 5672;
            case "activemq": return 61616;
            default: return 0;
        }
    }
}
```

### Beispiel 3: Spring Boot mit Profilen für Multi-Cloud-Deployment

```java
@Configuration
public class CloudConfig {
    
    @Bean
    @Profile("aws")
    public StorageClient awsStorageClient(
            @Value("${aws.access-key}") String accessKey,
            @Value("${aws.secret-key}") String secretKey,
            @Value("${aws.region}") String region) {
        
        return new S3StorageClient(accessKey, secretKey, region);
    }
    
    @Bean
    @Profile("azure")
    public StorageClient azureStorageClient(
            @Value("${azure.account-name}") String accountName,
            @Value("${azure.account-key}") String accountKey) {
        
        return new AzureBlobStorageClient(accountName, accountKey);
    }
    
    @Bean
    @Profile("gcp")
    public StorageClient gcpStorageClient(
            @Value("${gcp.project-id}") String projectId,
            @Value("${gcp.credentials-file}") String credentialsFile) {
        
        return new GcpStorageClient(projectId, credentialsFile);
    }
    
    @Bean
    @Profile("local")
    public StorageClient localStorageClient(
            @Value("${local.storage-path}") String storagePath) {
        
        return new FileSystemStorageClient(storagePath);
    }
    
    @Bean
    public StorageService storageService(StorageClient storageClient,
                                        @Value("${storage.bucket-name}") String bucketName) {
        return new StorageServiceImpl(storageClient, bucketName);
    }
}
```