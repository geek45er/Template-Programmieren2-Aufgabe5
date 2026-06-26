package org.htw.prog2.aufgabe1.analysis;

import org.htw.prog2.aufgabe1.files.Mutation;
import org.htw.prog2.aufgabe1.files.MutationFile;
import org.htw.prog2.aufgabe1.files.SequenceFile;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// vergleicht jede patientensequenz zeichenweise mit der referenz und berechnet die medikamentenresistenzen
public class FullLengthSequenceAnalysis extends SequenceAnalysis {

    // ruft den konstruktor der basisklasse auf und startet direkt die berechnung
    public FullLengthSequenceAnalysis(String reference, SequenceFile sequences, MutationFile mutations) {
        super(reference, sequences, mutations);
        calculateResistances();
    }

    // geht alle sequenzen durch und schaut welche mutation vorliegt, dann wird die resistenz gesetzt
    public void calculateResistances() {
        HashMap<String, Double> resistances = new HashMap<>();
        for (String drug : getMutations().getDrugs()) {
            resistances.put(drug, 0.0);
        }

        HashMap<String, Mutation> mutationMap = new HashMap<>();
        for (Mutation mutation : getMutations().getMutations()) {
            mutationMap.put(mutation.getVariant(), mutation);
        }

        String reference = getReference();
        for (String sequence : getSequences().getSequences()) {
            String exactVariant = calculateVariant(reference, sequence);
            Mutation mutation = mutationMap.get(exactVariant);
            if (mutation == null) {
                continue;
            }
            for (Map.Entry<String, Double> entry : mutation.getResistances().entrySet()) {
                String drug = entry.getKey();
                Double value = entry.getValue();
                if (value == null || value.isNaN()) {
                    continue;
                }
                resistances.put(drug, Math.max(resistances.getOrDefault(drug, 0.0), value));
            }
        }

        setResistances(resistances);
    }

    // vergleicht zwei sequenzen und gibt die unterschiede als variantenstring zurueck, z.b. "42C,100T"
    private String calculateVariant(String reference, String sequence) {
        if (reference == null || sequence == null || reference.length() != sequence.length()) {
            return "";
        }
        StringBuilder variantBuilder = new StringBuilder();
        for (int i = 0; i < reference.length(); i++) {
            if (reference.charAt(i) != sequence.charAt(i)) {
                if (variantBuilder.length() > 0) {
                    variantBuilder.append(",");
                }
                variantBuilder.append(i + 1).append(sequence.charAt(i));
            }
        }
        return variantBuilder.toString();
    }

    // prueft ob eine sequenz einem bestimmten mutationsmuster entspricht
    private boolean matchesMutation(String reference, String sequence, String variant) {
        if (reference == null || sequence == null || reference.length() != sequence.length()) {
            return false;
        }
        Pattern pattern = Pattern.compile("(\\d+)([A-Z])");
        Matcher matcher = pattern.matcher(variant);
        while (matcher.find()) {
            int position = Integer.parseInt(matcher.group(1)) - 1;
            char expected = matcher.group(2).charAt(0);
            if (position < 0 || position >= sequence.length()) {
                return false;
            }
            if (sequence.charAt(position) != expected) {
                return false;
            }
        }
        return true;
    }
}
