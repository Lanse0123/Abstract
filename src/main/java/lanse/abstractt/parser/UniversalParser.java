package lanse.abstractt.parser;

import lanse.abstractt.core.bubble.Bubble;
import lanse.abstractt.core.bubble.CodeBubble;
import lanse.abstractt.core.bubble.FunctionBubble;
import lanse.abstractt.core.bubble.PictureBubble;
import lanse.abstractt.core.screens.bars.ProgressBarPanel;
import lanse.abstractt.storage.Storage;
import lanse.abstractt.storage.languages.LanguageManager;
import org.eclipse.lsp4j.DocumentSymbol;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class UniversalParser {

    public static boolean aiCompiled = false;

    //TODO - this might be useful eventually for adding more languages:
    // https://gist.github.com/ppisarczyk/43962d06686722d26d176fad46879d41

    public static void handleFile(String filePath, Container parent) {

        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("File does not exist: " + filePath);
            return;
        }

        if (LanguageManager.getLanguageName(filePath).equals("image")){
            PictureBubble.createPictureBubble(filePath, parent);
            return;
        } else {
            CodeBubble.createCodeBubble(filePath, parent, Optional.empty());
        }

        // Check if the file is parseable via its language definition
        if (!LanguageManager.isFileParsable(filePath)) {
            Bubble bubble = Storage.load(filePath, false);
            parent.setLayout(null);
            parent.add(bubble);

            parent.revalidate();
            parent.repaint();
            System.out.println("Skipping file (not parseable): " + filePath);
            return;
        }

        String LSPLink = LanguageManager.languageHasLSP(filePath);
        List<DocumentSymbol> structuralList; //this will be used to store the functions and other important structural things

        if (!Objects.equals(LSPLink, "false")) {
            ProgressBarPanel.setLoading(true, "Generating Function Bubbles");
            ProgressBarPanel.show();

            structuralList = LSPManager.doStuff(LSPLink, LanguageManager.getLanguageName(filePath), file);

            ProgressBarPanel.hide();

            for (DocumentSymbol entry : structuralList) {
                Storage.updateStructure(filePath, entry.getKind().toString(), entry.getName(), Optional.empty(), Optional.of(entry.getRange().getStart().getLine()), Optional.of(entry.getRange().getEnd().getLine()));
            }
        } else {
            if (!aiCompiled){
                System.out.println("Ai is not ready.");
                return;
            }
            java.util.List<String> prompts = new java.util.ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder currentBlock = new StringBuilder();
                int lineNumber = 1;
                int maxChars = 200;
                String line;

                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) {
                        lineNumber++;
                        continue;
                    }

                    String lineWithNumber = lineNumber + ": " + line + "\n";

                    if (currentBlock.length() + lineWithNumber.length() > maxChars) {
                        prompts.add(currentBlock.toString());
                        currentBlock.setLength(0);
                    }

                    currentBlock.append(lineWithNumber);
                    lineNumber++;
                }

                if (!currentBlock.isEmpty()) {
                    prompts.add(currentBlock.toString());
                }

            } catch (IOException e) {
                System.err.println("Failed to read file: " + filePath);
                e.printStackTrace();
                return;
            }

            // Determine language from file extension
            String language = LanguageManager.getLanguageName(filePath);

            ProgressBarPanel.setLoading(true, "Generating Function Bubbles");
            ProgressBarPanel.show();
            int maxLen = prompts.size();
            int count = 1;

            for (String prompt : prompts) {
                String mergedPrompt = """
                        You are part of a universal coding IDE. Your job is to define structural code information from a file written in %s.
                        
                        For all lines in INPUT, find all defining lines in the code: defining a function, classes, imports, fields, or other structural elements.
                        
                        INPUT:
                        %s
                        
                        OUTPUT RULES:
                        - Only respond with line numbers and their type.
                        - Use this format: 12: function: getName, 24: class: className
                        - If nothing is defining, respond with: no
                        - Use only one line in your response.
                        - Do not add explanations or extra formatting.
                        - Do not say anything other than from these rules.
                        """.formatted(language, prompt);

                System.out.println(mergedPrompt);

                Optional<String> response = LLMManager.runLLM(mergedPrompt);
                if (response.isPresent()) {
                    String answer = response.get();
                    try {
                        System.out.println("[Answer] " + answer);
                        if (!answer.toLowerCase().contains("no")) {
                            // Expected format: 12: function: getName, 24: class: MyClass
                            String[] entries = answer.split(",");
                            for (String entry : entries) {
                                String[] parts = entry.trim().split(":");
                                if (parts.length == 3) {
                                    int lineNum = Integer.parseInt(parts[0].trim());
                                    String structure = parts[1].trim();
                                    String name = parts[2].trim();
                                    Storage.addStructure(filePath, structure, name, "", lineNum, Optional.empty());
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("[Error] Failed to parse JSON: " + e.getMessage());
                    }
                } else {
                    System.out.println("[Error] No response from LLM.");
                }

                System.out.println("--------");
                ProgressBarPanel.setProgress((double) count / maxLen);
                count++;
            }
        } //end of call for LLM

        ProgressBarPanel.hide();

        System.out.println("Finished Parsing");

        FunctionBubble[] newBubbles = Storage.loadFunctionBubbles(filePath);
        parent.setLayout(null);
        for (Bubble bubble : newBubbles) parent.add(bubble);

        parent.revalidate();
        parent.repaint();

        ProgressBarPanel.setLoading(true, "Generating Function Bubble Descriptions");
        ProgressBarPanel.show();
        int maxLen = newBubbles.length;
        int count = 1;

        //this should sort the bubbles by size, so the smallest are used first. (NEEDS TO BE TESTED)
        Arrays.sort(newBubbles, Comparator.comparingInt(FunctionBubble::getLineCount));

        for (FunctionBubble bubble : newBubbles) {
            bubble.generateDescription();

            ProgressBarPanel.setProgress((double) count / maxLen);

            parent.revalidate();
            parent.repaint();
            count++;
        }

        ProgressBarPanel.hide();
    }
}