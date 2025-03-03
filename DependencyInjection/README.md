# Dependency Injection Pattern

## Übersicht

Das Dependency Injection (DI) Pattern ist ein Entwurfsmuster, das die Abhängigkeiten eines Objekts von außen bereitstellt, anstatt dass das Objekt diese selbst erstellt. Dies führt zu loser Kopplung, besserer Testbarkeit und erhöhter Flexibilität in Softwareanwendungen. Dieses Pattern ist besonders wichtig in verteilten Systemen, wo Komponenten unabhängig voneinander entwickelt, getestet und bereitgestellt werden müssen.

## Anwendungsfälle in Verteilten Systemen

- **Microservices**: Verwaltung von Abhängigkeiten zwischen unabhängigen Services
- **Remote-Dienst-Integration**: Bereitstellung von Client-Stubs für Remote-Services
- **Konfigurations-Management**: Injizieren von Umgebungsspezifischen Konfigurationen
- **Ressourcen-Management**: Zentrale Verwaltung von Ressourcen wie Datenbankverbindungen
- **Service Discovery**: Dynamische Bereitstellung von Service-Endpunkten
- **Lastverteilung**: Austauschbare Implementierungen für verschiedene Lastszenarien

## Typen der Dependency Injection

### 1. Constructor Injection

Bei der Constructor Injection werden Abhängigkeiten über den Konstruktor einer Klasse injiziert. Dies ist die bevorzugte Methode für obligatorische Abhängigkeiten und fördert die Immutabilität.

```java
public class ServiceClient {
    private final RemoteService service;
    
    // Die Abhängigkeit wird über den Konstruktor injiziert
    public ServiceClient(RemoteService service) {
        this.service = service;
    }
    
    public void performOperation() {
        service.execute();
    }
}
```

### 2. Setter Injection

Bei der Setter Injection werden Abhängigkeiten über Setter-Methoden injiziert. Dies ist nützlich für optionale Abhängigkeiten oder wenn Abhängigkeiten nach der Objekterstellung geändert werden müssen.

```java
public class ConfigurableClient {
    private RemoteService service;
    
    // Die Abhängigkeit wird über einen Setter injiziert
    public void setService(RemoteService service) {
        this.service = service;
    }
    
    public void performOperation() {
        if (service != null) {
            service.execute();
        }
    }
}
```

### 3. Interface Injection

Bei der Interface Injection wird eine Schnittstelle definiert, die von Klassen implementiert wird, die eine bestimmte Abhängigkeit akzeptieren. Diese Methode ist weniger gebräuchlich als die beiden anderen.

```java
public interface ServiceInjectable {
    void injectService(RemoteService service);
}

public class ServiceConsumer implements ServiceInjectable {
    private RemoteService service;
    
    @Override
    public void injectService(RemoteService service) {
        this.service = service;
    }
    
    public void performOperation() {
        service.execute();
    }
}
```

## DI-Container und Frameworks

In der Praxis werden Dependency Injection oft durch spezialisierte Frameworks oder Container unterstützt:

### 1. Spring Framework

Spring ist eines der bekanntesten Java-Frameworks für Dependency Injection und bietet verschiedene Konfigurationsmethoden:

```java
@Service
public class RemoteServiceImpl implements RemoteService {
    @Override
    public void execute() {
        // Implementierung
    }
}

@Component
public class ServiceClientImpl {
    private final RemoteService service;
    
    // Spring injiziert die Abhängigkeit automatisch
    @Autowired
    public ServiceClientImpl(RemoteService service) {
        this.service = service;
    }
}
```

### 2. Google Guice

Guice ist ein leichtgewichtiges DI-Framework von Google:

```java
public class DependencyModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(RemoteService.class).to(RemoteServiceImpl.class);
    }
}

public class Application {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new DependencyModule());
        ServiceClient client = injector.getInstance(ServiceClient.class);
        client.performOperation();
    }
}
```

### 3. Java CDI (Contexts and Dependency Injection)

CDI ist der Standard für Dependency Injection in Java EE:

```java
@ApplicationScoped
public class RemoteServiceImpl implements RemoteService {
    @Override
    public void execute() {
        // Implementierung
    }
}

@RequestScoped
public class ServiceClientImpl {
    @Inject
    private RemoteService service;
    
    public void performOperation() {
        service.execute();
    }
}
```

## Vor- und Nachteile

### Vorteile

- **Lose Kopplung**: Klassen sind unabhängiger von konkreten Implementierungen
- **Erhöhte Testbarkeit**: Abhängigkeiten können leicht durch Mocks ersetzt werden
- **Bessere Modularität**: Komponenten können unabhängig entwickelt und ausgetauscht werden
- **Flexibilität**: Implementierungen können zur Laufzeit ausgetauscht werden
- **Wiederverwendbarkeit**: Komponenten können in verschiedenen Kontexten eingesetzt werden

### Nachteile

- **Erhöhte Komplexität**: DI-Container können die Architektur komplizierter machen
- **Lernkurve**: DI-Frameworks erfordern ein gewisses Verständnis und Einarbeitung
- **Performance-Overhead**: DI-Container können einen geringen Overhead verursachen
- **Debugging-Herausforderungen**: Die indirekte Instanziierung kann das Debugging erschweren

## DI in Verteilten Systemen

In verteilten Systemen bietet Dependency Injection besondere Vorteile:

1. **Service Discovery**: DI kann mit Service-Discovery-Mechanismen integriert werden, um dynamisch Dienste zu lokalisieren.
2. **Client-Side Load Balancing**: Verschiedene Load-Balancing-Strategien können injiziert werden.
3. **Circuit Breaking**: Circuit-Breaker-Implementierungen können transparent integriert werden.
4. **Failover-Strategien**: Alternative Service-Implementierungen können bei Ausfällen verwendet werden.
5. **Verteilte Konfiguration**: Konfigurationsparameter können zentral verwaltet und injiziert werden.

## Implementierungen in diesem Projekt

Dieses Projekt enthält verschiedene Implementierungen des Dependency Injection Patterns:

1. **Manuelle DI**: Grundlegende Implementierungen ohne Frameworks
2. **Spring-basierte DI**: Beispiele mit dem Spring Framework
3. **Guice-basierte DI**: Beispiele mit Google Guice
4. **Service Locator**: Ein verwandtes Pattern zum Vergleich
5. **Verteilte DI**: Anwendung von DI in einem verteilten Kontext mit Remote-Diensten

Jede Implementierung demonstriert die spezifischen Merkmale und Anwendungsfälle des jeweiligen DI-Ansatzes im Kontext verteilter Systeme.