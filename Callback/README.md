# Callback Pattern

## Übersicht

Das Callback-Pattern ist ein Entwurfsmuster, bei dem ein Stück ausführbarer Code (der "Callback") als Argument an eine andere Funktion übergeben wird. Diese Funktion kann den übergebenen Code zu einem späteren Zeitpunkt ausführen, wenn ein bestimmtes Ereignis eintritt. Callbacks sind besonders nützlich für asynchrone Operationen in verteilten Systemen, bei denen die Ausführungszeit unbestimmt sein kann.

## Anwendungsfälle in Verteilten Systemen

- **Asynchrone Kommunikation**: Abwicklung von Antworten auf asynchrone Anfragen
- **Ereignisbehandlung**: Reaktion auf Systemereignisse oder Statusänderungen
- **Fertigstellungsbenachrichtigungen**: Benachrichtigung über den Abschluss lang andauernder Operationen
- **Ressourcenmanagement**: Freigabe von Ressourcen, wenn sie nicht mehr benötigt werden
- **Fehlerbehandlung**: Umgang mit Fehlern, die während asynchroner Operationen auftreten

## Callback-Arten

### 1. Synchrone Callbacks

Synchrone Callbacks werden innerhalb des Kontrollflusses der aufrufenden Funktion ausgeführt, bevor diese zurückkehrt.

**Beispiel**:
```java
public void processItems(List<Item> items, ItemProcessor processor) {
    for (Item item : items) {
        processor.process(item); // Synchroner Callback
    }
}
```

### 2. Asynchrone Callbacks

Asynchrone Callbacks werden zu einem späteren Zeitpunkt, normalerweise in einem separaten Thread oder nach Abschluss einer Operation, ausgeführt.

**Beispiel**:
```java
public void fetchDataAsync(String url, DataCallback callback) {
    executorService.submit(() -> {
        try {
            Data result = fetchData(url);
            callback.onSuccess(result); // Asynchroner Callback
        } catch (Exception e) {
            callback.onError(e); // Fehlerbehandlung
        }
    });
}
```

### 3. Lokale Callbacks

Lokale Callbacks werden innerhalb derselben JVM ausgeführt und können direkt auf gemeinsame Objekte zugreifen.

### 4. Remote Callbacks

Remote Callbacks werden über ein Netzwerk an einen entfernten Client zurückgesendet, oft über einen festgelegten Endpunkt oder eine Verbindung.

## Implementierungsvarianten im verteilten Kontext

### 1. Polling-basierte Callbacks

Der Client überprüft regelmäßig den Status einer Operation.

**Vor- und Nachteile**:
- ✅ Einfach zu implementieren
- ✅ Funktioniert über zustandslose Protokolle wie HTTP
- ❌ Ineffizient bei langen Wartezeiten
- ❌ Erzeugt unnötigen Netzwerkverkehr

### 2. Callback-URLs (Webhooks)

Der Client gibt eine URL an, an die der Server eine Benachrichtigung sendet, wenn die Operation abgeschlossen ist.

**Vor- und Nachteile**:
- ✅ Effizient für lang andauernde Operationen
- ✅ Keine ständige Verbindung erforderlich
- ❌ Erfordert, dass der Client öffentlich erreichbar ist
- ❌ Sicherheitsbedenken bei öffentlichen Endpunkten

### 3. Push-basierte Callbacks

Der Server sendet aktiv Benachrichtigungen an den Client, wenn Ereignisse eintreten.

**Vor- und Nachteile**:
- ✅ Echtzeit-Benachrichtigungen
- ✅ Effizient für ereignisreiche Systeme
- ❌ Erfordert eine persistente Verbindung oder einen Messagingdienst
- ❌ Komplexere Implementierung

### 4. Message Queue-basierte Callbacks

Verwendet eine Nachrichtenwarteschlange zur asynchronen Kommunikation zwischen Client und Server.

**Vor- und Nachteile**:
- ✅ Hohe Zuverlässigkeit und Skalierbarkeit
- ✅ Entkopplung von Produzenten und Konsumenten
- ❌ Zusätzliche Infrastruktur erforderlich
- ❌ Mögliche Verzögerungen bei der Verarbeitung

## Herausforderungen und Lösungen im verteilten Kontext

### 1. Timeouts und Verbindungsabbrüche

**Problem**: Callback kann aufgrund von Netzwerkproblemen nicht geliefert werden.

**Lösungen**:
- Wiederholungsmechanismen mit exponentieller Backoff-Strategie
- Ablaufzeiten (TTL) für Callback-Registrierungen
- Alternative Kommunikationspfade

### 2. Fehlerbehandlung

**Problem**: Fehler während der asynchronen Verarbeitung müssen an den ursprünglichen Anforderer weitergeleitet werden.

**Lösungen**:
- Separate Callback-Methoden für Erfolgs- und Fehlerfälle
- Standardisierte Fehlerberichterstattung
- Circuit Breaker für wiederholte Fehler

### 3. Zustandsverwaltung

**Problem**: Aufrechterhaltung des Kontexts zwischen der ursprünglichen Anfrage und dem Callback.

**Lösungen**:
- Korrelations-IDs zur Nachverfolgung von Anfragen
- Zustandsspeicherung in einer verteilten Datenbank
- Zustandslose Designs, die alle erforderlichen Informationen in der Callback-Anfrage enthalten

### 4. Garantierte Ausführung

**Problem**: Sicherstellen, dass Callbacks auch bei Systemausfällen ausgeführt werden.

**Lösungen**:
- Persistente Warteschlangen
- Transaktionale Ausführung
- Idempotente Callbacks

## Implementierungen in diesem Projekt

Dieses Projekt enthält verschiedene Implementierungen des Callback-Patterns:

1. **Einfaches Callback**: Grundlegende Implementierung synchroner und asynchroner Callbacks
2. **Polling-Callback**: Implementierung eines Polling-Mechanismus für Ergebnisse
3. **Webhook-Callback**: Verwendung von HTTP-Callbacks für asynchrone Benachrichtigungen
4. **Message-basierte Callbacks**: Verwendung einer Nachrichtenwarteschlange für zuverlässige Callbacks
5. **Retry-Callback**: Callbacks mit automatischen Wiederholungsversuchen bei Fehlern

Jede Implementierung demonstriert die spezifischen Merkmale und Anwendungsfälle der jeweiligen Callback-Art im Kontext verteilter Systeme.