from manim import *
import numpy as np

class AdapterPatternAnimation(Scene):
    def construct(self):
        self.create_title()
        self.explain_basics()
        self.show_protocol_adapter()
        self.show_legacy_adapter()
        self.show_format_adapter()
        self.show_conclusion()
    
    def create_title(self):
        # Erstelle Titel für den Lehrfilm
        title = Text("Das Adapter-Pattern", font_size=72)
        subtitle = Text("Ein strukturelles Entwurfsmuster für verteilte Systeme", 
                     font_size=36).next_to(title, DOWN, buff=0.5)
        
        self.play(Write(title), run_time=1.5)
        self.play(FadeIn(subtitle), run_time=1)
        self.wait(2)
        
        # Definition
        definition = Text(
            "Das Adapter-Pattern ermöglicht die Zusammenarbeit von Klassen\n" +
            "mit inkompatiblen Schnittstellen, indem es eine Klasse in\n" +
            "eine andere Schnittstelle übersetzt.",
            font_size=28
        ).next_to(subtitle, DOWN, buff=0.5)
        
        self.play(Write(definition))
        self.wait(2)
        self.play(FadeOut(title), FadeOut(subtitle), FadeOut(definition))
    
    def explain_basics(self):
        # Grundstruktur des Adapter-Patterns zeigen
        title = Text("Grundkonzept des Adapter-Patterns", font_size=48)
        self.play(Write(title))
        self.play(title.animate.to_edge(UP))
        
        # Klassen erstellen
        client = self.create_class("Client", BLUE, LEFT+UP)
        target = self.create_class("Target Interface", GREEN, RIGHT+UP, client, 2)
        adapter = self.create_class("Adapter", YELLOW, DOWN+RIGHT, target, 2)
        adaptee = self.create_class("Adaptee", RED, RIGHT, adapter, 2)
        
        # Pfeile zwischen Klassen
        arrows = VGroup()
        arrow1, arrow1_text = self.create_arrow(client, target, "verwendet", UP)
        arrow2, arrow2_text = self.create_arrow(target, adapter, "implementiert", LEFT)
        arrow3, arrow3_text = self.create_arrow(adapter, adaptee, "adaptiert", UP)
        
        arrows.add(arrow1, arrow1_text, arrow2, arrow2_text, arrow3, arrow3_text)
        
        self.play(
            Create(client), Create(target), Create(adapter), Create(adaptee),
            Create(arrows)
        )
        
        self.wait(2)
        
        explanation = Text(
            "Das Adapter-Pattern konvertiert Schnittstellen, um\n" +
            "inkompatible Klassen zusammenarbeiten zu lassen.",
            font_size=24
        ).to_edge(DOWN, buff=0.5)
        
        self.play(Write(explanation))
        self.wait(3)
        
        self.play(
            FadeOut(client), FadeOut(target), FadeOut(adapter), FadeOut(adaptee),
            FadeOut(arrows), FadeOut(explanation), FadeOut(title)
        )
    
    def show_protocol_adapter(self):
        # Protokolladapter: SOAP zu REST
        title = Text("Protokolladapter: SOAP zu REST", font_size=48)
        self.play(Write(title))
        self.play(title.animate.to_edge(UP))
        
        # Sequenzdiagramm-Komponenten
        client = self.create_class("Client", BLUE, LEFT+UP*2, width=1.5, height=0.8)
        adapter = self.create_class("SoapToRestAdapter", YELLOW, RIGHT, client, 2, width=1.5, height=0.8)
        soap = self.create_class("SoapService", RED, RIGHT, adapter, 2, width=1.5, height=0.8)
        
        # Lebenslinien
        client_line, adapter_line, soap_line = self.create_lifelines([client, adapter, soap])
        
        # Sequenzablauf darstellen
        message_flow = VGroup()
        
        # REST GET Anfrage
        get_arrow, get_text = self.create_sequence_arrow(
            client_line, adapter_line, 0.5, "get(\"123\")", BLUE
        )
        
        # Adapter-Aktivierung
        adapter_activation = Rectangle(height=4, width=0.3, color=YELLOW).set_fill(YELLOW, opacity=0.3)
        adapter_activation.move_to(adapter_line.get_start() + DOWN*2.5)
        adapter_activation.align_to(adapter_line, RIGHT)
        
        # SOAP Request
        soap_arrow, soap_text = self.create_sequence_arrow(
            adapter_line, soap_line, 2, "executeRequest(soapRequest)", YELLOW
        )
        
        # SOAP Response
        soap_response_arrow, soap_response_text = self.create_sequence_arrow(
            soap_line, adapter_line, 3, "soapResponse", RED, reverse=True
        )
        
        # REST Response
        rest_response_arrow, rest_response_text = self.create_sequence_arrow(
            adapter_line, client_line, 4, "restResponse", YELLOW, reverse=True
        )
        
        message_flow.add(
            get_arrow, get_text, adapter_activation,
            soap_arrow, soap_text, 
            soap_response_arrow, soap_response_text,
            rest_response_arrow, rest_response_text
        )
        
        # Code-Beispiel
        code_box = Rectangle(height=3, width=5, color=WHITE).set_fill(BLACK, opacity=0.8)
        code_box.to_edge(DOWN, buff=0.5)
        
        code_text = """
        public RestResponse get(String resourceId) {
            // Erstelle SOAP-Anfrage
            SoapRequest soapRequest = new SoapRequest("GetResource", resourceId, null);
            
            // Rufe SOAP-Service auf
            SoapResponse response = soapService.executeRequest(soapRequest);
            
            // Konvertiere zu REST-Antwort
            return convertToRestResponse(response);
        }
        """
        
        code = Code(
            code=code_text,
            language="java",
            font_size=16,
            line_spacing=0.5,
            background_stroke_width=0
        ).move_to(code_box)
        
        self.play(
            Create(client), Create(adapter), Create(soap),
            Create(client_line), Create(adapter_line), Create(soap_line)
        )
        self.play(Create(message_flow), run_time=3)
        self.play(Create(code_box), Write(code))
        
        self.wait(3)
        self.play(
            FadeOut(client), FadeOut(adapter), FadeOut(soap),
            FadeOut(client_line), FadeOut(adapter_line), FadeOut(soap_line),
            FadeOut(message_flow), FadeOut(code_box), FadeOut(code), FadeOut(title)
        )
    
    def show_legacy_adapter(self):
        # Legacy-System-Adapter
        title = Text("Legacy-System-Adapter", font_size=48)
        self.play(Write(title))
        self.play(title.animate.to_edge(UP))
        
        # Klassenstruktur für Legacy-Adapter
        modern_ui = self.create_class("UserManagementSystem", BLUE, UP*2, height=1.2)
        modern_user = self.create_class("User", BLUE, LEFT, modern_ui, 2, height=1.2)
        adapter_class = self.create_class("LegacyUserSystemAdapter", YELLOW, DOWN, modern_ui, 2, height=1.2)
        legacy_system = self.create_class("LegacyUserSystem", RED, RIGHT, adapter_class, 2, height=1.2)
        legacy_user = self.create_class("LegacyUser", RED, DOWN, modern_user, 2, height=1.2)
        
        # Pfeile zwischen Klassen
        arrows = VGroup()
        impl_arrow, impl_text = self.create_arrow(adapter_class, modern_ui, "implementiert", LEFT)
        adapt_arrow, adapt_text = self.create_arrow(adapter_class, legacy_system, "adaptiert", UP)
        convert1_arrow, convert1_text = self.create_arrow(
            adapter_class, modern_user, "convertLegacyUser()", DOWN, max_tip_length_to_length_ratio=0.1
        )
        convert2_arrow, convert2_text = self.create_arrow(
            adapter_class, legacy_user, "convertToLegacyUser()", RIGHT, max_tip_length_to_length_ratio=0.1
        )
        
        arrows.add(
            impl_arrow, impl_text, 
            adapt_arrow, adapt_text,
            convert1_arrow, convert1_text,
            convert2_arrow, convert2_text
        )
        
        # Erklärungstext
        explanation = Text(
            "Der Adapter ermöglicht die Integration eines Legacy-Systems in eine moderne Architektur.",
            font_size=24
        ).to_edge(DOWN, buff=0.5)
        
        self.play(
            Create(modern_ui), Create(modern_user),
            Create(adapter_class), Create(legacy_system), Create(legacy_user),
            Create(arrows), run_time=2
        )
        self.play(Write(explanation))
        
        self.wait(3)
        self.play(
            FadeOut(modern_ui), FadeOut(modern_user),
            FadeOut(adapter_class), FadeOut(legacy_system), FadeOut(legacy_user),
            FadeOut(arrows), FadeOut(explanation), FadeOut(title)
        )
    
    def show_format_adapter(self):
        # Format-Adapter
        title = Text("Format-Adapter für Datenkonvertierung", font_size=48)
        self.play(Write(title))
        self.play(title.animate.to_edge(UP))
        
        # Zentrale Datenklasse
        data_obj = Rectangle(height=2, width=3, color=WHITE).set_fill(WHITE, opacity=0.1)
        data_text = VGroup(
            Text("DataObject", font_size=24, color=WHITE),
            Text("id, name, value", font_size=18, color=WHITE)
        ).arrange(DOWN, buff=0.1).move_to(data_obj)
        data_group = VGroup(data_obj, data_text).move_to(ORIGIN)
        
        # Formate
        xml = self.create_format("XML Format", BLUE, UP+LEFT, data_group, "<data>\n  <id>123</id>\n</data>")
        json = self.create_format("JSON Format", GREEN, UP+RIGHT, data_group, '{\n  "id": "123"\n}')
        csv = self.create_format("Legacy CSV Format", RED, DOWN, data_group, "123;Test;42")
        
        # Konvertierungspfeile
        arrows = VGroup()
        xml_data, xml_data_text = self.create_arrow(xml, data_group, "fromXml()", LEFT, reverse=True)
        data_xml, data_xml_text = self.create_arrow(data_group, xml, "toXml()", RIGHT)
        json_data, json_data_text = self.create_arrow(json, data_group, "fromJson()", LEFT, reverse=True)
        data_json, data_json_text = self.create_arrow(data_group, json, "toJson()", RIGHT)
        csv_data, csv_data_text = self.create_arrow(csv, data_group, "LegacyCSVAdapter", LEFT, reverse=True)
        data_csv, data_csv_text = self.create_arrow(data_group, csv, "convertToCSV()", RIGHT)
        
        arrows.add(
            xml_data, xml_data_text, data_xml, data_xml_text,
            json_data, json_data_text, data_json, data_json_text,
            csv_data, csv_data_text, data_csv, data_csv_text
        )
        
        self.play(Create(data_group))
        self.play(Create(xml), Create(json), Create(csv))
        self.play(Create(arrows), run_time=2)
        
        # Erklärungstext
        explanation = Text(
            "Der LegacyCSVAdapter ermöglicht die Konvertierung zwischen modernen Formaten\n" +
            "und dem Legacy-CSV-Format, das von älteren Systemen verwendet wird.",
            font_size=24
        ).to_edge(DOWN, buff=0.5)
        
        self.play(Write(explanation))
        
        self.wait(3)
        self.play(
            FadeOut(data_group), FadeOut(xml), FadeOut(json), FadeOut(csv),
            FadeOut(arrows), FadeOut(explanation), FadeOut(title)
        )
    
    def show_conclusion(self):
        # Zusammenfassung
        title = Text("Adapter-Pattern: Zusammenfassung", font_size=48)
        self.play(Write(title))
        self.play(title.animate.to_edge(UP))
        
        # Anwendungsfälle
        use_cases = VGroup(
            Text("Anwendungsfälle:", font_size=32),
            Text("• Protokolladaption (REST zu SOAP)", font_size=24),
            Text("• Legacy-System-Integration", font_size=24),
            Text("• Datenkonvertierung", font_size=24),
            Text("• API-Harmonisierung", font_size=24)
        ).arrange(DOWN, aligned_edge=LEFT, buff=0.2).to_edge(LEFT, buff=1)
        
        # Vorteile und Nachteile
        pros_cons = VGroup(
            Text("Vorteile:", font_size=28, color=GREEN),
            Text("• Wiederverwendbarkeit", font_size=20),
            Text("• Trennung von Schnittstellen", font_size=20),
            Text("Nachteile:", font_size=28, color=RED),
            Text("• Zusätzliche Komplexität", font_size=20),
            Text("• Leistungsoverhead", font_size=20)
        ).arrange(DOWN, aligned_edge=LEFT, buff=0.2).to_edge(RIGHT, buff=1)
        
        self.play(Write(use_cases), Write(pros_cons), run_time=3)
        
        # Fazit
        conclusion = Text(
            "Das Adapter-Pattern ist ein mächtiges Werkzeug in verteilten Systemen,\n" +
            "um unterschiedliche Komponenten nahtlos zu integrieren.",
            font_size=24
        ).to_edge(DOWN, buff=0.5)
        
        self.play(Write(conclusion))
        
        self.wait(3)
        self.play(FadeOut(title), FadeOut(use_cases), FadeOut(pros_cons), FadeOut(conclusion))
        
        # Endtitel
        end_title = Text("Adapter-Pattern in verteilten Systemen", font_size=56)
        by_line = Text("Lehrfilm für Informatik-Studierende", font_size=32).next_to(end_title, DOWN)
        
        self.play(Write(end_title), Write(by_line))
        self.wait(2)
        self.play(FadeOut(end_title), FadeOut(by_line))
    
    # Hilfsmethoden für wiederverwendbare Komponenten
    def create_class(self, name, color, position, relative_to=None, buff=0, width=3, height=1.5):
        """Erstellt eine Klasse mit Namen und Farbe an der angegebenen Position"""
        rect = Rectangle(height=height, width=width).set_fill(color, opacity=0.2)
        text = Text(name, font_size=min(24, 120/len(name))).move_to(rect)
        class_group = VGroup(rect, text)
        
        if relative_to:
            class_group.next_to(relative_to, position, buff=buff)
        else:
            class_group.move_to(position)
        
        return class_group
    
    def create_arrow(self, start, end, label_text, label_position, color=WHITE, 
                     reverse=False, max_tip_length_to_length_ratio=None):
        """Erstellt einen Pfeil zwischen zwei Objekten mit Beschriftung"""
        if reverse:
            arrow = Arrow(end.get_edge_center(LEFT), start.get_edge_center(RIGHT), buff=0.1, color=color)
        else:
            arrow = Arrow(start.get_edge_center(RIGHT), end.get_edge_center(LEFT), buff=0.1, color=color)
        
        if max_tip_length_to_length_ratio:
            arrow.max_tip_length_to_length_ratio = max_tip_length_to_length_ratio
        
        text = Text(label_text, font_size=16).next_to(arrow, label_position, buff=0.1)
        
        return arrow, text
    
    def create_lifelines(self, objects, color=WHITE):
        """Erstellt Lebenslinien für Sequenzdiagramme"""
        lifelines = []
        
        for obj in objects:
            line = DashedLine(
                obj.get_bottom(),
                obj.get_bottom() + DOWN * 6,
                color=obj[0].get_color()
            )
            lifelines.append(line)
        
        return lifelines
    
    def create_sequence_arrow(self, start_line, end_line, y_offset, text_label, color, reverse=False):
        """Erstellt einen Sequenzpfeil für Sequenzdiagramme"""
        start_point = start_line.get_start() + DOWN * y_offset
        end_point = end_line.get_start() + DOWN * y_offset
        
        if reverse:
            arrow = Arrow(
                end_point, start_point,
                buff=0.1, color=color
            )
        else:
            arrow = Arrow(
                start_point, end_point,
                buff=0.1, color=color
            )
        
        text = Text(text_label, font_size=16).next_to(arrow, UP, buff=0.1)
        
        return arrow, text
    
    def create_format(self, title, color, position, relative_to, content):
        """Erstellt ein Formatobjekt mit Titel und Inhalt"""
        rect = Rectangle(height=2, width=3, color=color).set_fill(color, opacity=0.1)
        
        format_text = VGroup(
            Text(title, font_size=20, color=color),
            Text(content, font_size=16)
        ).arrange(DOWN, buff=0.1).move_to(rect)
        
        format_group = VGroup(rect, format_text)
        format_group.next_to(relative_to, position, buff=1.5)
        
        return format_group
