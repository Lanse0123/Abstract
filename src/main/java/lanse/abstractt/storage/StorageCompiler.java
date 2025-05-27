package lanse.abstractt.storage;

import lanse.abstractt.core.screens.ProgressBarPanel;
import org.json.JSONObject;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class StorageCompiler {

    //TODO - this will be the green arrow that generates a default project of abstract from a project (dont do this part yet)

    private static int totalItems = 0;
    private static int processedItems = 0;

    public static void generateProjectDefaults() {
        //This is asynchronous. Took a few tries to get working
        SwingWorker<Void, Double> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                File selectedProjectPath = new File(Settings.selectedProjectPath);
                if (!selectedProjectPath.exists() || !selectedProjectPath.isDirectory()) {
                    System.err.println("Invalid selectedProjectPath");
                    return null;
                }

                File storageRoot = new File(selectedProjectPath, "AbstractionVisualizerStorage");
                if (!storageRoot.exists() && !storageRoot.mkdirs()) {
                    System.err.println("Failed to create storage root folder.");
                    return null;
                }

                // Start progress bar
                totalItems = countItems(selectedProjectPath);
                processedItems = 0;
                ProgressBarPanel.setLoading(true, "Generating Project");
                ProgressBarPanel.show();
                processDirectory(selectedProjectPath, storageRoot);

                return null;
            }

            @Override
            protected void done() {
                ProgressBarPanel.setLoading(false, "Waiting...");
                ProgressBarPanel.hide();
            }
        };

        //der gona take er jobs
        worker.execute();
    }

    private static void processDirectory(File originalDir, File mirroredDir) {
        File[] entries = originalDir.listFiles();
        if (entries == null) return;

        for (File entry : entries) {
            if (entry.getName().contains("AbstractionVisualizerStorage")) continue;

            if (entry.isDirectory()) {
                File newDir = new File(mirroredDir, entry.getName());
                if (!newDir.mkdirs() && !newDir.exists()) {
                    System.err.println("Failed to create directory: " + newDir.getAbsolutePath());
                    continue;
                }

                writeMetadataJson(entry, newDir);
                processDirectory(entry, newDir);
            } else {
                writeMetadataJson(entry, mirroredDir);
            }

            processedItems++;
            SwingUtilities.invokeLater(() -> {
                ProgressBarPanel.setProgress((double) processedItems / totalItems);
                ProgressBarPanel.getPanel().repaint();
            });
        }
    }

    private static void writeMetadataJson(File originalFile, File targetDir) {
        File jsonFile = new File(targetDir, originalFile.getName() + ".json");

        if (jsonFile.exists()) {
            // Don't overwrite existing metadata
            return;
        }

        JSONObject json = new JSONObject();
        json.put("name", originalFile.getName());
        json.put("path", originalFile.getAbsolutePath());
        json.put("description", "");

        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write(json.toString(4)); // pretty print with indent
        } catch (IOException e) {
            System.err.println("Error writing JSON for " + originalFile.getName() + ": " + e.getMessage());
        }
    }

    private static int countItems(File dir) {
        File[] entries = dir.listFiles();
        if (entries == null) return 0;

        int count = 0;
        for (File entry : entries) {
            if (entry.getName().equals("AbstractionVisualizerStorage")) continue;
            count++; // count each file or directory
            if (entry.isDirectory()) {
                count += countItems(entry);
            }
        }
        return count;
    }

}
