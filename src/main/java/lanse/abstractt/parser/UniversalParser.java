package lanse.abstractt.parser;

import lanse.abstractt.core.bubble.Bubble;
import lanse.abstractt.storage.Storage;
import lanse.abstractt.storage.languages.LanguageManager;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class UniversalParser {

    //TODO - this might be useful eventually for adding more languages:
    // https://gist.github.com/ppisarczyk/43962d06686722d26d176fad46879d41

    public static void handleFile(String filePath, Container parent) {
        // Check if the file is parseable via its language definition
        if (!LanguageManager.isFileParsable(filePath)) {
            System.out.println("Skipping file (not parseable): " + filePath);
            return;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("File does not exist: " + filePath);
            return;
        }

        String LSPLink = LanguageManager.languageHasLSP(filePath);
        Map<Integer, String> structuralList = new HashMap<>(); //this will be used to store the functions and other important structural things

        if (!Objects.equals(LSPLink, "false")){
            structuralList = LSPManager.doStuff(LSPLink);
        }
        else {
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
            String extension = LanguageManager.getExtension(filePath);
            if (!extension.startsWith(".")) extension = "." + extension;

            // Print full prompts with template
            System.out.println("=== TEMPLATED PROMPTS FOR: " + file.getName() + " ===");
            for (String prompt : prompts) {
                String mergedPrompt = """
                        <s>[INST] 
                        You are part of a universal coding IDE. Your job is to define structural code information from a file written in %s.
                                    
                        For all lines in INPUT, find all defining lines in the code: defining a function, classes, imports, fields, or other structural elements.
                                    
                        INPUT:
                        %s
                                    
                        OUTPUT RULES:
                        - Only respond with line numbers and their type.
                        - Use this format: 12: function, 24: class
                        - If nothing is defining, respond with: no
                        - Use only one line in your response.
                        - Do not add explanations or extra formatting.
                        - Do not say anything other than from these rules.
                        [/INST]
                        """.formatted(extension, prompt);

                System.out.println(mergedPrompt);

                Optional<String> response = LLMManager.runLLM(mergedPrompt);
                if (response.isPresent()) {
                    String answer = response.get();
                    try {
                        System.out.println("[Answer] " + answer);
                        if (!answer.toLowerCase().contains("no")) {
                            // Expected format: 24: function, 25: function, 26: field
                            String[] entries = answer.split(",");
                            for (String entry : entries) {
                                String[] parts = entry.trim().split(":");
                                if (parts.length == 2) {
                                    int lineNumber = Integer.parseInt(parts[0].trim());
                                    String structure = parts[1].trim();
                                    structuralList.put(lineNumber, structure); // assumes lineNumber is unique
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
            }
        } //end of call for LLM

        for (Map.Entry<Integer, String> entry : structuralList.entrySet()) {
            Storage.addStructure(filePath, entry.getValue(), entry.getKey());
        }

        //TODO - FINALLY, use it to create the bubbles like handleDirectory does here.
        // each of these bubbles should have the class / file name. If there is more than 1 class, create class bubbles, and
        // those class bubbles will contain the function bubbles. If there is only 1 class, make the bubbles split by functions.
        // There should also be an imports and fields bubble. Each function bubble should be able to see fields if the option is enabled by the user.

        Bubble newBubble = Storage.load(filePath, true); //might change this
        parent.setLayout(null);
        parent.add(newBubble);
    }

}
