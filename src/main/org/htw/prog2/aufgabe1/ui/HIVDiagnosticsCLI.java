package org.htw.prog2.aufgabe1.ui;

import org.apache.commons.cli.*;
import org.htw.prog2.aufgabe1.analysis.SequenceAnalysis;
import org.htw.prog2.aufgabe1.analysis.SequenceAnalysisManager;
import org.htw.prog2.aufgabe1.exceptions.FileFormatException;
import org.htw.prog2.aufgabe1.exceptions.NoValidReadersException;

import java.io.IOException;

// kommandozeileninterface - parst die argumente und gibt ergebnis auf der konsole aus
public class HIVDiagnosticsCLI {

    // fuehrt die analyse durch und gibt das ergebnis auf der konsole aus
    public HIVDiagnosticsCLI(String[] args) {
        CommandLine cmd = parseOptions(args);
        if (cmd == null) {
            printHelp();
            return;
        }

        try {
            SequenceAnalysis analysis = SequenceAnalysisManager.performAnalysis(
                    cmd.getOptionValue('r'),
                    cmd.getOptionValue('p'),
                    cmd.getOptionValue('m'));
            System.out.println("Best drug: " + analysis.getBestDrug());
            System.out.println("Best drug resistance: " + analysis.getBestDrugResistance());
        } catch (NoValidReadersException e) {
            System.err.println("Unsupported file format: " + e.getMessage());
        } catch (FileFormatException e) {
            System.err.println("Error parsing file: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error loading file: " + e.getMessage());
        }
    }

    // parst die kommandozeilenoptionen mit apache commons CLI und gibt null zurueck falls ungueltig
    public static CommandLine parseOptions(String[] args) {
        if (args == null || args.length == 0) {
            return null;
        }

        Options options = createOptions();
        CommandLineParser parser = new DefaultParser();
        try {
            return parser.parse(options, args);
        } catch (ParseException e) {
            return null;
        }
    }

    // legt die erlaubten kommandozeilenoptionen fest
    private static Options createOptions() {
        Options options = new Options();
        options.addOption(Option.builder("m").hasArg().required().desc("Mutation file").build());
        options.addOption(Option.builder("p").hasArg().required().desc("Patient sequence file").build());
        options.addOption(Option.builder("r").hasArg().required().desc("Reference sequence file").build());
        options.addOption(Option.builder("d").hasArg().required().desc("Drug class").build());
        return options;
    }

    // gibt die hilfe mit allen erlaubten optionen auf der konsole aus
    private void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar HIVDiagnostics.jar -m <mutation> -p <patient> -r <reference> -d <drug class>", createOptions());
    }
}
