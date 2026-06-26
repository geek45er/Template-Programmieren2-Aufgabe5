package org.htw.prog2.aufgabe1;
import org.htw.prog2.aufgabe1.ui.HIVDiagnosticsCLI;
import org.htw.prog2.aufgabe1.ui.HIVDiagnosticsGUI;

// einstiegspunkt des programms - startet entweder das GUI oder die CLI je nach argumenten
public class HIVDiagnostics {
    public static void main(String[] args) {
        // ohne argumente wird das grafische interface gestartet, sonst die kommandozeile
        if(args.length == 0) {
            new HIVDiagnosticsGUI();
        }
        else {
            new HIVDiagnosticsCLI(args);
        }
    }
}
