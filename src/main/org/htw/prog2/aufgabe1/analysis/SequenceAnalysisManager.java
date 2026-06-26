package org.htw.prog2.aufgabe1.analysis;

import org.htw.prog2.aufgabe1.exceptions.FileFormatException;
import org.htw.prog2.aufgabe1.exceptions.NoValidReadersException;
import org.htw.prog2.aufgabe1.files.MutationFile;
import org.htw.prog2.aufgabe1.files.SequenceFile;
import org.htw.prog2.aufgabe1.readers.CSVFileReader;
import org.htw.prog2.aufgabe1.readers.FASTAFileReader;
import org.htw.prog2.aufgabe1.readers.FASTQFileReader;
import org.htw.prog2.aufgabe1.readers.MutationFileReader;
import org.htw.prog2.aufgabe1.readers.ReaderManager;
import org.htw.prog2.aufgabe1.readers.SequenceFileReader;

import java.io.IOException;

// kapselt die analyse-logik, damit sowohl CLI als auch GUI sie gleich aufrufen koennen
public class SequenceAnalysisManager {
    // laedt alle drei dateien, fuehrt die analyse durch und gibt das ergebnis zurueck
    public static SequenceAnalysis performAnalysis(String referenceFileName, String patientSequenceFileName,
                                                   String mutationFileName) throws NoValidReadersException, FileFormatException, IOException {
        ReaderManager<SequenceFileReader> sequenceReaderManager = new ReaderManager<>();
        sequenceReaderManager.addReader(new FASTAFileReader());
        sequenceReaderManager.addReader(new FASTQFileReader());

        SequenceFile referenceFile = sequenceReaderManager.getReaderForFile(referenceFileName).readFile(referenceFileName);
        SequenceFile patientSequences = sequenceReaderManager.getReaderForFile(patientSequenceFileName).readFile(patientSequenceFileName);

        ReaderManager<MutationFileReader> mutationReaderManager = new ReaderManager<>();
        mutationReaderManager.addReader(new CSVFileReader());
        MutationFile mutationFile = mutationReaderManager.getReaderForFile(mutationFileName).readFile(mutationFileName);

        String reference = referenceFile.getFirstSequence();
        return new FullLengthSequenceAnalysis(reference, patientSequences, mutationFile);
    }
}
