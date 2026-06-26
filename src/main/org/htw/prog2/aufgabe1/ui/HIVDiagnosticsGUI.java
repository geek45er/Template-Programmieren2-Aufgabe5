package org.htw.prog2.aufgabe1.ui;

import org.htw.prog2.aufgabe1.analysis.SequenceAnalysis;
import org.htw.prog2.aufgabe1.analysis.SequenceAnalysisManager;
import org.htw.prog2.aufgabe1.exceptions.FileFormatException;
import org.htw.prog2.aufgabe1.exceptions.NoValidReadersException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

// grafisches interface mit swing - ermoeglicht das laden von dateien und zeigt das ergebnis an
public class HIVDiagnosticsGUI extends JFrame {
    private String referenceFilePath;
    private String patientSequenceFilePath;
    private String mutationFilePath;

    private JLabel referenceLabel;
    private JLabel sequenceLabel;
    private JLabel mutationLabel;
    private JButton predictButton;
    private JLabel bestDrugResultLabel;
    private JLabel bestResistanceResultLabel;

    // abstrakte hilfsklasse damit der gemeinsame code fuer alle drei lade-buttons nicht dreimal geschrieben werden muss
    protected abstract class LoadListener implements ActionListener {
        private final FileNameExtensionFilter filter;

        // speichert den dateifilter fuer den dateidialog
        public LoadListener(String extensionDescription, String... extensions) {
            this.filter = new FileNameExtensionFilter(extensionDescription, extensions);
        }

        // oeffnet den dateidialog und ruft setData auf wenn eine datei ausgewaehlt wurde
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            JFileChooser chooser = new JFileChooser();
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.addChoosableFileFilter(filter);

            int result = chooser.showOpenDialog(HIVDiagnosticsGUI.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = chooser.getSelectedFile();
                setData(selectedFile);
                if (allFilesLoaded()) {
                    predictButton.setEnabled(true);
                }
            }
        }

        // speichert den dateipfad und aktualisiert das label - wird von jedem button anders implementiert
        protected abstract void setData(File file);
    }

    // erstellt das fenster und macht es sichtbar
    public HIVDiagnosticsGUI() {
        super("HIV Diagnostics");
        init();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 360);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // initialisiert layout, menue und datei-buttons
    private void init() {
        setLayout(new GridBagLayout());
        initMenuBar();
        initFileChoosers();
    }

    // erstellt das "File" menue mit about und exit
    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem aboutItem = new JMenuItem("About");
        JMenuItem exitItem = new JMenuItem("Exit");

        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "HIV Diagnostics\n\nLoad a reference file, a patient sequence file and a mutation file.\nThen predict the best drug using the loaded data.",
                "About",
                JOptionPane.INFORMATION_MESSAGE));

        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(aboutItem);
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    // legt alle buttons, labels und den predict-button im fenster an
    private void initFileChoosers() {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;

        referenceLabel = createInitialLabel("Reference file");
        sequenceLabel = createInitialLabel("Patient sequences");
        mutationLabel = createInitialLabel("Mutation file");

        JButton referenceButton = new JButton("Load reference");
        JButton sequenceButton = new JButton("Load sequences");
        JButton mutationButton = new JButton("Load mutations");

        addLoaders(referenceLabel, referenceButton, c, new LoadListener("FASTA reference file", "fasta") {
            @Override
            protected void setData(File file) {
                referenceFilePath = file.getAbsolutePath();
                referenceLabel.setText(file.getName());
                referenceLabel.setForeground(Color.BLACK);
            }
        });

        addLoaders(sequenceLabel, sequenceButton, c, new LoadListener("Sequence files", "fasta", "fastq") {
            @Override
            protected void setData(File file) {
                patientSequenceFilePath = file.getAbsolutePath();
                sequenceLabel.setText(file.getName());
                sequenceLabel.setForeground(Color.BLACK);
            }
        });

        addLoaders(mutationLabel, mutationButton, c, new LoadListener("Mutation CSV file", "csv") {
            @Override
            protected void setData(File file) {
                mutationFilePath = file.getAbsolutePath();
                mutationLabel.setText(file.getName());
                mutationLabel.setForeground(Color.BLACK);
            }
        });

        c.gridy++;
        c.gridx = 0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        predictButton = new JButton("Predict best drug");
        predictButton.setEnabled(false);
        add(predictButton, c);

        c.gridy++;
        c.gridx = 0;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.WEST;
        bestDrugResultLabel = new JLabel("Best drug: ");
        add(bestDrugResultLabel, c);

        c.gridy++;
        bestResistanceResultLabel = new JLabel("Best drug resistance: ");
        add(bestResistanceResultLabel, c);

        predictButton.addActionListener(e -> onPredict());
    }

    // erstellt ein label mit rotem text als platzhalter bis eine datei geladen wird
    private JLabel createInitialLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.RED);
        return label;
    }

    // fuegt einen button und sein label nebeneinander ins layout ein
    private void addLoaders(JLabel label, JButton button, GridBagConstraints c, ActionListener listener) {
        c.gridx = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        add(button, c);

        c.gridx = 1;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(label, c);

        button.addActionListener(listener);
        c.gridy++;
    }

    // prueft ob alle drei dateipfade gesetzt sind
    private boolean allFilesLoaded() {
        return referenceFilePath != null && !referenceFilePath.isEmpty()
                && patientSequenceFilePath != null && !patientSequenceFilePath.isEmpty()
                && mutationFilePath != null && !mutationFilePath.isEmpty();
    }

    // startet die analyse und zeigt das ergebnis an, oder eine fehlermeldung falls was schieflaeuft
    private void onPredict() {
        try {
            SequenceAnalysis analysis = SequenceAnalysisManager.performAnalysis(referenceFilePath, patientSequenceFilePath, mutationFilePath);
            bestDrugResultLabel.setText("Best drug: " + analysis.getBestDrug());
            bestResistanceResultLabel.setText("Best drug resistance: " + String.format("%.2f", analysis.getBestDrugResistance()));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error loading file", JOptionPane.ERROR_MESSAGE);
        } catch (NoValidReadersException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Unsupported file format", JOptionPane.ERROR_MESSAGE);
        } catch (FileFormatException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error parsing file", JOptionPane.ERROR_MESSAGE);
        }
    }
}
