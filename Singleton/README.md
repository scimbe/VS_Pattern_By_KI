# Singleton Pattern

## Übersicht

Das Singleton-Pattern ist ein Entwurfsmuster, das sicherstellt, dass eine Klasse nur eine Instanz besitzt und einen globalen Zugriffspunkt auf diese Instanz bietet. Dieses Pattern wird häufig in verteilten Systemen verwendet, um gemeinsam genutzte Ressourcen zu verwalten oder um sicherzustellen, dass bestimmte Komponenten nur einmal instanziiert werden.

## Anwendungsfälle in Verteilten Systemen

- **Konfigurationsmanagement**: Zentrale Verwaltung von Konfigurationseinstellungen
- **Verbindungspools**: Verwaltung von Datenbankverbindungen oder anderen ressourcenintensiven Verbindungen
- **Cache-Manager**: Zentralisiertes Caching von häufig abgerufenen Daten
- **Logger**: Zentrale Protokollierungskomponente

## Implementierung

Die Implementierung umfasst:

1. Einen privaten Konstruktor, um die Instanziierung von außen zu verhindern
2. Eine statische Variable, die die einzige Instanz der Klasse enthält
3. Eine statische Methode, die den Zugriff auf die einzige Instanz ermöglicht

### Grundimplementierung

```java
public class Singleton {
    // Private statische Variable für die einzige Instanz
    private static Singleton instance;
    
    // Private Konstruktor verhindert Instanziierung von außen
    private Singleton() {
        // Initialisierungscode
    }
    
    // Öffentliche statische Methode für den Zugriff auf die Instanz
    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
    
    // Weitere Methoden der Klasse
}
```

### Thread-sichere Implementierung

```java
public class ThreadSafeSingleton {
    // Volatile Keyword stellt sicher, dass Änderungen sofort für alle Threads sichtbar sind
    private static volatile ThreadSafeSingleton instance;
    
    private ThreadSafeSingleton() {
        // Initialisierungscode
    }
    
    // Double-checked locking für Threadsicherheit und Performance
    public static ThreadSafeSingleton getInstance() {
        if (instance == null) {
            synchronized (ThreadSafeSingleton.class) {
                if (instance == null) {
                    instance = new ThreadSafeSingleton();
                }
            }
        }
        return instance;
    }
}
```

### Enum-basierte Implementierung (Java-spezifisch)

```java
public enum EnumSingleton {
    INSTANCE;
    
    // Attribute und Methoden
    private Connection connection;
    
    EnumSingleton() {
        // Initialisierungscode
    }
    
    public Connection getConnection() {
        return connection;
    }
}
```

## Vor- und Nachteile

### Vorteile

- Garantiert eine einzige Instanz der Klasse
- Bietet einen globalen Zugriffspunkt
- Initialisiert die Instanz erst bei Bedarf (lazy loading)
- Reduziert den Speicherverbrauch (im Vergleich zu mehreren Instanzen)

### Nachteile

- Kann das Testen erschweren
- Kann zu versteckten Abhängigkeiten führen
- Die globale Zustandsverwaltung kann problematisch sein
- Benötigt besondere Vorkehrungen für Thread-Sicherheit

## Hinweise für verteilte Systeme

In verteilten Umgebungen ist zu beachten, dass das Singleton-Pattern pro JVM-Instanz gilt. Wenn mehrere Server oder Prozesse beteiligt sind, garantiert das Pattern nicht die Einzigartigkeit über Prozessgrenzen hinweg. Für solche Szenarien sollten zusätzliche Mechanismen wie verteilte Sperren oder Konsensus-Algorithmen verwendet werden.

## Maven-Projektstruktur

Das Maven-Projekt enthält implementierte Beispiele für verschiedene Singleton-Varianten und demonstriert deren Anwendung in verteilten Szenarien wie Konfigurationsmanagement und Verbindungspooling.