package org.htw.prog2.aufgabe1.analysis;

import org.htw.prog2.aufgabe1.files.MutationFile;
import org.htw.prog2.aufgabe1.files.SequenceFile;

import java.util.HashMap;
import java.util.Map;

// abstrakte basisklasse fuer die sequenzanalyse - haelt die geladenen daten und berechnet das beste medikament
public abstract class SequenceAnalysis {
    private final String reference;
    private final SequenceFile sequences;
    private final MutationFile mutations;
    private HashMap<String, Double> resistances = new HashMap<>();

    // speichert die drei eingabedaten fuer die spaetere analyse
    public SequenceAnalysis(String reference, SequenceFile sequences, MutationFile mutations) {
        this.reference = reference;
        this.sequences = sequences;
        this.mutations = mutations;
    }

    // muss von der unterklasse implementiert werden, berechnet die resistenzwerte
    public abstract void calculateResistances();
    

    // gibt die berechneten resistenzwerte zurueck
    public HashMap<String, Double> getResistances() {
        return resistances;
    }

    // setzt die resistenzwerte, wird von der unterklasse nach der berechnung aufgerufen
    protected void setResistances(HashMap<String, Double> resistances) {
        this.resistances = resistances;
    }

    // gibt das medikament mit der niedrigsten vorhergesagten resistenz zurueck
    public String getBestDrug() {
        if (resistances == null || resistances.isEmpty()) {
            return "";
        }
        String bestDrug = "";
        double bestResistance = Double.MAX_VALUE;
        for (Map.Entry<String, Double> entry : resistances.entrySet()) {
            double resistance = entry.getValue();
            if (Double.isNaN(resistance)) {
                continue;
            }
            if (bestDrug.isEmpty() || resistance < bestResistance) {
                bestDrug = entry.getKey();
                bestResistance = resistance;
            }
        }
        return bestDrug;
    }

    // gibt den resistenzwert des besten medikaments zurueck
    public double getBestDrugResistance() {
        String bestDrug = getBestDrug();
        if (bestDrug.isEmpty()) {
            return 0.0;
        }
        return resistances.getOrDefault(bestDrug, 0.0);
    }

    // gibt alle medikamente mit ihren resistenzwerten als text aus
    public String getDrugDescriptions() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Double> entry : resistances.entrySet()) {
            builder.append(entry.getKey()).append(": ").append(entry.getValue()).append(System.lineSeparator());
        }
        return builder.toString().trim();
    }

    // getter fuer die patientensequenzen
    public SequenceFile getSequences() {
        return sequences;
    }

    // getter fuer die mutationsdaten
    public MutationFile getMutations() {
        return mutations;
    }

    // getter fuer die referenzsequenz
    public String getReference() {
        return reference;
    }
}
