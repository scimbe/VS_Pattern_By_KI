from manim import *

class LogAnimation(Scene):
    def construct(self):
        # Titel
        title = Text("Observer Pattern Log Animation").scale(0.8).to_edge(UP)
        self.play(Write(title))
        
        # Observer Kreise
        observer_1 = Circle(radius=0.5, color=BLUE).shift(LEFT * 3)
        observer_2 = Circle(radius=0.5, color=GREEN)
        observer_3 = Circle(radius=0.5, color=RED).shift(RIGHT * 3)
        observers = [observer_1, observer_2, observer_3]
        
        # Labels für die Observer
        labels = [
            Text("Observer-1").scale(0.5).next_to(observer_1, DOWN),
            Text("Observer-2").scale(0.5).next_to(observer_2, DOWN),
            Text("Observer-3").scale(0.5).next_to(observer_3, DOWN)
        ]
        
        # Observer erscheinen lassen
        for obs, lbl in zip(observers, labels):
            self.play(FadeIn(obs), FadeIn(lbl))
        
        # Logfile Simulation: Observer Reaktionen
        self.wait(1)
        
        # Datenänderung 1
        change_text1 = Text("Daten geändert zu: 'Neuer Datenwert'").scale(0.6).to_edge(DOWN)
        self.play(Write(change_text1))
        self.play(observer_1.animate.set_fill(BLUE, opacity=0.5), observer_2.animate.set_fill(GREEN, opacity=0.5))
        self.wait(1)
        self.play(FadeOut(change_text1))
        
        # Neuer Observer tritt bei
        self.play(FadeIn(observer_3), FadeIn(labels[2]))
        self.wait(1)
        
        # Datenänderung 2
        change_text2 = Text("Daten geändert zu: 'Aktualisierter Datenwert'").scale(0.6).to_edge(DOWN)
        self.play(Write(change_text2))
        self.play(observer_1.animate.set_fill(WHITE, opacity=0.2),  # Observer-1 ist entfernt
                  observer_2.animate.set_fill(GREEN, opacity=0.5),
                  observer_3.animate.set_fill(RED, opacity=0.5))
        self.wait(1)
        self.play(FadeOut(change_text2))
        
        # Letzte Aktualisierung
        change_text3 = Text("Daten geändert zu: 'Aktualisierte Daten: 1741004348291'").scale(0.6).to_edge(DOWN)
        self.play(Write(change_text3))
        self.play(observer_2.animate.set_fill(GREEN, opacity=0.8),
                  observer_3.animate.set_fill(RED, opacity=0.8))
        self.wait(2)
        self.play(FadeOut(change_text3))
        
        # Abschluss
        end_text = Text("Demonstration abgeschlossen").scale(0.8).to_edge(DOWN)
        self.play(Write(end_text))
        self.wait(2)
        self.play(FadeOut(title), FadeOut(end_text), *[FadeOut(obs) for obs in observers], *[FadeOut(lbl) for lbl in labels])
