package lanse.abstractt.storage;

import lanse.abstractt.core.bubble.TopBubble;
import lanse.abstractt.core.screens.bars.ProgressBarPanel;
import lanse.abstractt.storage.languages.LanguageManager;
import org.json.JSONObject;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

public class StorageCompiler {

    private static int totalItems = 0;
    private static int processedItems = 0;
    private static final Semaphore rootWritten = new Semaphore(0);

    public static void waitForRoot() throws InterruptedException {
        synchronized (rootWritten) {
            rootWritten.acquire();
        }
    }

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
                TopBubble.totalFiles = totalItems;
                processedItems = 0;

                ProgressBarPanel.setLoading(true, "Generating Project");
                ProgressBarPanel.show();
                writeMetadataJson(selectedProjectPath, storageRoot);

                rootWritten.release();
                processDirectory(selectedProjectPath, storageRoot);

                return null;
            }

            @Override
            protected void done() {
                ProgressBarPanel.hide();
                TopBubble.calculateLanguageBar();
            }
        };

        //der gona take er jobs
        worker.execute();
    }

    //TODO - if DisplayModeSelector.displayMode = DisplayMode.GOURCE_MAP (or any other static map)
    // once this is all done, do pretty much the same thing to load ALL bubbles, and make use of GourceMap.getGourceMap
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

                writeMetadataJson(entry, mirroredDir);
                processDirectory(entry, newDir);
            } else {
                writeMetadataJson(entry, mirroredDir);
                String extension = LanguageManager.getExtension(String.valueOf(entry));
                if (TopBubble.languageMap.containsKey(extension)) {
                    TopBubble.languageMap.merge(extension, 1, Integer::sum); //add 1 to the count that one has
                } else {
                    TopBubble.languageMap.put(extension, 1);
                }
            }

            processedItems++;
            if (processedItems % 25 == 0) {
                SwingUtilities.invokeLater(() -> {
                    ProgressBarPanel.setProgress((double) processedItems / totalItems);
                    ProgressBarPanel.getPanel().repaint();
                });
            }
        }
    }

    private static void writeMetadataJson(File originalFile, File targetDir) {
        String filename = originalFile.getName();
        if (originalFile.getAbsolutePath().equals(Settings.selectedProjectPath)) {
            filename = "";
        }
        File jsonFile = new File(targetDir, filename + ".json");

        if (jsonFile.exists()) {
            // Don't overwrite existing metadata
            return;
        }

        JSONObject json = new JSONObject();
        json.put("name", originalFile.getName());
        json.put("path", originalFile.getAbsolutePath());
        json.put("desc", "");

        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write(json.toString(4)); // pretty print with indent
        } catch (IOException e) {
            System.err.println("Error writing JSON for " + originalFile.getName() + ": " + e.getMessage());
        }
    }

    private static int countItems(File dir) {
        if (dir == null || !dir.exists()) return 0;

        File[] entries = dir.listFiles();
        if (entries == null) return 0;

        return Arrays.stream(entries)
                .parallel()
                //TODO - make sure this filter uses ExcludedBubbleList
                .filter(entry -> !entry.getName().equals("AbstractionVisualizerStorage"))
                .mapToInt(entry -> {
                    if (entry.isDirectory()) {
                        return 1 + countItems(entry);
                    } else {
                        return 1;
                    }
                }).sum();
    }

}
