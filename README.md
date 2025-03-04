# VS Pattern (Supported by Claude)

## Überblick

Dieses Repository enthält Beispielimplementierungen verschiedener Entwurfsmuster (Design Patterns), die in der Vorlesung Verteilte Systeme (VS) von Prof. Dr. Martin Becke verwendet werden. Jedes Pattern wurde als eigenständiges Maven-Projekt implementiert, um die praktische Anwendung in verteilten Systemen zu demonstrieren.     
Zum Test der Integrationsmöglichkeiten von Claude in die Lehre wurde das SE durch Claude begleitet. http://claude.ai.

## Struktur

Das Repository ist wie folgt strukturiert:

- Jeder Ordner repräsentiert ein spezifisches Entwurfsmuster
- Innerhalb jedes Ordners finden Sie:
  - Eine README.md mit einer detaillierten Erklärung des Patterns
  - Ein vollständiges Maven-Projekt mit Quellcode
  - Beispiele für die Anwendung des Patterns in verteilten Systemen
  - Unit-Tests zur Demonstration der Funktionalität

## Enthaltene Patterns

Die folgenden Entwurfsmuster sind oder werden in diesem Repository implementiert:

1. **Singleton Pattern** - Stellt sicher, dass eine Klasse nur eine Instanz besitzt
2. **Observer Pattern** - Implementiert einen Subscription-Mechanismus für Ereignisbenachrichtigungen
3. **Factory Method Pattern** - Definiert eine Schnittstelle zur Objekterstellung
4. **Proxy Pattern** - Stellt einen Stellvertreter für ein anderes Objekt dar
5. **Command Pattern** - Kapselt Anfragen als Objekte
6. **Strategy Pattern** - Definiert eine Familie austauschbarer Algorithmen

## Verwendung

Jedes Pattern-Projekt kann unabhängig kompiliert und ausgeführt werden:

```bash
cd [Pattern-Ordner]
mvn clean install
mvn exec:java -Dexec.mainClass="de.becke.vs.pattern.Main"
```

## Lernziele

Diese Beispiele sollen:

1. Das Verständnis grundlegender Entwurfsmuster fördern
2. Die Anwendung von Patterns in verteilten Systemen veranschaulichen
3. Als Referenz für Studenten der VS-Vorlesung dienen
4. Praktische Implementierungsbeispiele mit Best Practices bieten

## Mitwirkung

Beiträge zu diesem Repository sind willkommen. Wenn Sie ein neues Pattern hinzufügen oder ein bestehendes verbessern möchten, erstellen Sie bitte einen Pull Request.

## Lizenz

Dieses Projekt ist unter der MIT-Lizenz lizenziert - siehe die LICENSE-Datei für Details.
