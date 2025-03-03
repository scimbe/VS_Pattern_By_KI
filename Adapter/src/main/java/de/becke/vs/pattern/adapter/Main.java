package de.becke.vs.pattern.adapter;

import de.becke.vs.pattern.adapter.api.ApiGatewayDemo;
import de.becke.vs.pattern.adapter.format.FormatAdapterDemo;
import de.becke.vs.pattern.adapter.legacy.LegacySystemDemo;
import de.becke.vs.pattern.adapter.messaging.MessagingAdapterDemo;
import de.becke.vs.pattern.adapter.protocol.ProtocolAdapterDemo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hauptklasse zur Demonstration der verschiedenen Adapter-Pattern-Implementierungen.
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        LOGGER.info("Starte Demonstration des Adapter-Patterns in verteilten Systemen");

        // Demonstration der verschiedenen Adapter-Typen
        demonstrateProtocolAdapter();
        demonstrateFormatAdapter();
        demonstrateLegacySystemAdapter();
        demonstrateMessagingAdapter();
        demonstrateApiGatewayAdapter();

        LOGGER.info("Demonstration abgeschlossen");
    }

    /**
     * Demonstriert die Verwendung von Protokolladaptern.
     */
    private static void demonstrateProtocolAdapter() {
        LOGGER.info("\n--- Protokolladapter Demonstration ---");
        ProtocolAdapterDemo demo = new ProtocolAdapterDemo();
        demo.runDemo();
    }

    /**
     * Demonstriert die Verwendung von Formatadaptern.
     */
    private static void demonstrateFormatAdapter() {
        LOGGER.info("\n--- Formatadapter Demonstration ---");
        FormatAdapterDemo demo = new FormatAdapterDemo();
        demo.runDemo();
    }

    /**
     * Demonstriert die Integration eines Legacy-Systems.
     */
    private static void demonstrateLegacySystemAdapter() {
        LOGGER.info("\n--- Legacy-System-Adapter Demonstration ---");
        LegacySystemDemo demo = new LegacySystemDemo();
        demo.runDemo();
    }

    /**
     * Demonstriert die Verwendung von Messaging-Adaptern.
     */
    private static void demonstrateMessagingAdapter() {
        LOGGER.info("\n--- Messaging-Adapter Demonstration ---");
        MessagingAdapterDemo demo = new MessagingAdapterDemo();
        demo.runDemo();
    }

    /**
     * Demonstriert die Verwendung eines API-Gateways als Adapter.
     */
    private static void demonstrateApiGatewayAdapter() {
        LOGGER.info("\n--- API-Gateway-Adapter Demonstration ---");
        ApiGatewayDemo demo = new ApiGatewayDemo();
        demo.runDemo();
    }
}