# Implementierungsleitfaden für das Singleton-Pattern

Dieser Leitfaden beschreibt den Implementierungsprozess des Singleton-Patterns in verteilten Systemen anhand praktischer Beispiele aus diesem Projekt.

## Inhaltsverzeichnis

1. [Wann sollte das Singleton-Pattern verwendet werden?](#wann-sollte-das-singleton-pattern-verwendet-werden)
2. [Schrittweise Implementierung](#schrittweise-implementierung)
3. [Implementierungsbeispiele](#implementierungsbeispiele)
4. [Best Practices](#best-practices)
5. [Häufige Fehler](#häufige-fehler)
6. [Performanceüberlegungen](#performanceüberlegungen)

## Wann sollte das Singleton-Pattern verwendet werden?

Das Singleton-Pattern ist in folgenden Situationen besonders nützlich:

```mermaid
graph TD
    A[Entscheidungsbaum: Singleton-Pattern verwenden?] --> B{Ist eine einzige Instanz ausreichend?}
    B -->|Ja| C{Globaler Zugriffspunkt notwendig?}
    B -->|Nein| D[Andere Patterns erwägen]
    
    C -->|Ja| E{Lazy Initialization erwünscht?}
    C -->|Nein| F[Dependency Injection erwägen]
    
    E -->|Ja| G[Singleton-Pattern verwenden]
    E -->|Nein| H[Statische Klasse oder Eager Singleton erwägen]
    
    G --> I{Multithread-Umgebung?}
    
    I -->|Ja| J[Thread-sichere Implementierung wählen]
    I -->|Nein| K[Einfache Implementierung ausreichend]
```

## Schrittweise Implementierung

### 1. Basic Singleton (nicht thread-sicher)

```mermaid
graph LR
    A[Private statischen Klassenvariable definieren] --> B[Privaten Konstruktor erstellen]
    B --> C[Öffentliche statische getInstance-Methode implementieren]
    C --> D[Instanzzugriffsmethoden hinzufügen]
```

### 2. Thread-sichere Implementierung (Double-Checked Locking)

```mermaid
graph TD
    A[Private volatile statische Variable definieren] --> B[Privaten Konstruktor erstellen]
    B --> C[getInstance-Methode mit Double-Checked Locking]
    C --> D[Erste Prüfung ohne Synchronisation]
    D --> E[Synchronisationsblock hinzufügen]
    E --> F[Zweite Prüfung innerhalb des Blocks]
    F --> G[Instanzzugriffsmethoden hinzufügen]
```

## Implementierungsbeispiele

### Beispiel 1: Basic Singleton (nicht thread-sicher)

Dieses Diagramm zeigt den Kontrollfluss beim Zugriff auf ein einfaches Singleton:

```mermaid
graph LR
    A[Client] --> B[BasicSingleton.getInstance()]
    B --> C{Instanz existiert?}
    C -->|Nein| D[Neue Instanz erzeugen]
    C -->|Ja| E[Existierende Instanz verwenden]
    D --> F[Instanz zurückgeben]
    E --> F
    F --> G[Client verwendet Singleton]
```

```java
public class BasicSingleton {
    // Private statische Variable, die die einzige Instanz speichert
    private static BasicSingleton instance;
    
    // Privater Konstruktor verhindert Instanziierung von außen
    private BasicSingleton() {
        // Initialisierungscode
    }
    
    // Öffentliche statische Methode für Zugriff auf Instanz
    public static BasicSingleton getInstance() {
        if (instance == null) {
            instance = new BasicSingleton();
        }
        return instance;
    }
    
    // Businessmethoden
    public void doSomething() {
        // Implementierung
    }
}
```

### Beispiel 2: Thread-sicheres Singleton (Double-Checked Locking)

```mermaid
sequenceDiagram
    participant Thread1
    participant Thread2
    participant Singleton as ThreadSafeSingleton
    
    Thread1->>+Singleton: getInstance()
    Thread2->>+Singleton: getInstance()
    
    Note over Singleton: Erste Prüfung (nicht synchronisiert)
    
    par Thread1 erhält Zugriff
        Singleton-->>Thread1: instance == null, betreten sync-Block
        Note over Thread1,Singleton: Thread 1 hat Lock
        Thread1->>Singleton: prüfe nochmals (instance == null)
        Thread1->>Singleton: erzeuge neue Instanz
        Singleton-->>-Thread1: return instance
    and Thread2 wartet
        Note over Thread2,Singleton: Thread 2 wartet auf Lock
        Note over Thread2,Singleton: Thread 2 erhält Lock
        Thread2->>Singleton: prüfe nochmals (instance != null)
        Singleton-->>-Thread2: return existing instance
    end
```

```java
public class ThreadSafeSingleton {
    // Volatile stellt sicher, dass Änderungen sofort für alle Threads sichtbar sind
    private static volatile ThreadSafeSingleton instance;
    
    // Privater Konstruktor
    private ThreadSafeSingleton() {
        // Initialisierungscode
    }
    
    // Thread-sichere getInstance-Methode mit Double-Checked Locking
    public static ThreadSafeSingleton getInstance() {
        // Erste Prüfung (nicht synchronisiert)
        if (instance == null) {
            // Synchronisieren nur bei Bedarf
            synchronized (ThreadSafeSingleton.class) {
                // Zweite Prüfung (synchronisiert)
                if (instance == null) {
                    instance = new ThreadSafeSingleton();
                }
            }
        }
        return instance;
    }
}
```

### Beispiel 3: Enum-basiertes Singleton

```mermaid
graph LR
    A[Client] --> B[EnumSingleton.INSTANCE]
    B --> C[JVM garantiert Thread-Sicherheit]
    C --> D[Client verwendet Enum-Singleton]
```

```java
public enum EnumSingleton {
    INSTANCE;
    
    // Private Felder
    private Map<String, String> configuration;
    
    // Konstruktor (automatisch privat in Enums)
    EnumSingleton() {
        configuration = new HashMap<>();
        // Initialisierungscode
    }
    
    // Öffentliche Methoden
    public String getConfigValue(String key) {
        return configuration.get(key);
    }
    
    public void setConfigValue(String key, String value) {
        configuration.put(key, value);
    }
}
```

## Best Practices

```mermaid
graph TD
    A[Best Practices für Singleton-Pattern] --> B[Privater Konstruktor]
    A --> C[Thread-Sicherheit beachten]
    A --> D[Serialisierungsprobleme berücksichtigen]
    A --> E[Testbarkeit verbessern]
    A --> F[Lazy Loading bei Bedarf]
    A --> G[Enum für einfache Implementierung]
    
    B --> B1[Verhindert versehentliche Instanziierung]
    C --> C1[Double-Checked Locking oder Enum verwenden]
    D --> D1[readResolve-Methode implementieren]
    E --> E1[Dependency Injection ermöglichen]
    F --> F1[Ressourcenverbrauch bei Programmstart reduzieren]
    G --> G1[Einfachste thread- und serialisierungssichere Lösung]
```

## Häufige Fehler

Die folgenden Fehler sollten bei der Implementierung des Singleton-Patterns vermieden werden:

```mermaid
graph TD
    A[Häufige Fehler] --> B[Ignorieren der Thread-Sicherheit]
    A --> C[Mehrere Instanzen durch Reflection]
    A --> D[Mehrere Instanzen durch Serialisierung]
    A --> E[Übermäßige Nutzung von Singletons]
    A --> F[Nicht berücksichtigte ClassLoader-Probleme]
    
    B --> B1[Race Conditions führen zu mehreren Instanzen]
    C --> C1[Reflection kann private Konstruktoren umgehen]
    D --> D1[Serialisierung erzeugt neue Instanzen]
    E --> E1[Enge Kopplung und schwer testbarer Code]
    F --> F1[Verschiedene ClassLoader erzeugen separate Instanzen]
```

## Performanceüberlegungen

```mermaid
graph LR
    A[Performance-Optimierung] --> B[Initialisierungszeitpunkt]
    A --> C[Synchronisationsoverhead]
    A --> D[Memory Footprint]
    
    B --> B1[Lazy vs. Eager Initialization]
    C --> C1[Double-Checked Locking vs. Holder-Klasse]
    D --> D1[Ressourcenverbrauch minimieren]
```

## Varianten des Singleton-Patterns

```mermaid
graph TD
    A[Singleton-Varianten] --> B[Basic Singleton]
    A --> C[Thread-Safe Singleton]
    A --> D[Bill Pugh Singleton]
    A --> E[Enum Singleton]
    A --> F[Eager Singleton]
    
    B --> B1[Einfachste Implementierung, nicht thread-sicher]
    C --> C1[Double-Checked Locking für Thread-Sicherheit]
    D --> D1[Holder-Klasse für lazy thread-safe Implementierung]
    E --> E1[Enum-basiert, thread- und serialisierungssicher]
    F --> F1[Statische Initialisierung, thread-sicher]
```

### Bill Pugh Singleton (Static Inner Helper)

```java
public class BillPughSingleton {
    private BillPughSingleton() {}
    
    // Innere statische Helper-Klasse
    private static class SingletonHelper {
        private static final BillPughSingleton INSTANCE = new BillPughSingleton();
    }
    
    public static BillPughSingleton getInstance() {
        return SingletonHelper.INSTANCE;
    }
}
```

## Herausforderungen in verteilten Systemen

```mermaid
graph LR
    A[Herausforderungen] --> B[Multiple JVMs]
    A --> C[Clustered Environment]
    A --> D[Serialization Issues]
    A --> E[Cache Coherency]
    
    B --> B1[Separate JVMs haben separate Singletons]
    C --> C1[Erfordert Cluster-weites Singleton]
    D --> D1[Serialisierung kann mehrere Instanzen erzeugen]
    E --> E1[Konsistenz zwischen Knoten sicherstellen]
```

## Lösungen für verteilte Umgebungen

```mermaid
graph TD
    A[Lösungen für verteilte Systeme] --> B[Distributed Registry]
    A --> C[Database Singleton]
    A --> D[Clustered Cache]
    A --> E[External Configuration]
    
    B --> B1[Zentrales Registry für Singleton-Verwaltung]
    C --> C1[Singleton-Status in Datenbank speichern]
    D --> D1[Distributed Cache für gemeinsame Instanz]
    E --> E1[Auslagern in externe Konfigurationssysteme]
```