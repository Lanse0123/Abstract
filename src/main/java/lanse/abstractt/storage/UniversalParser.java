package lanse.abstractt.storage;

import lanse.abstractt.core.bubble.Bubble;
import lanse.abstractt.storage.languages.LanguageManager;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import org.json.JSONObject;

public class UniversalParser {

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
        String[] structuralList = new String[0]; //this will be used to store the functions and other important structural things
        //TODO - this should probably be a map

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
                        You are part of a universal coding IDE. Your job is to extract structural code information from a file written in %s.
                                    
                        Find all defining lines in the code: functions, classes, imports, fields, or other structural elements.
                                    
                        INPUT:
                        %s
                                    
                        OUTPUT RULES:
                        - Only respond with line numbers and their type.
                        - Use this format: 12: function, 24: class
                        - If nothing is defining, respond with: no
                        - Use only one line in your response.
                        - Do not add explanations or extra formatting.
                        [/INST]
                        """.formatted(extension, prompt);

                System.out.println(mergedPrompt);

                Optional<String> response = LLMManager.runLLM(mergedPrompt);
                if (response.isPresent()) {
                    try {
                        System.out.println("[Answer] " + response.get());
                    } catch (Exception e) {
                        System.out.println("[Error] Failed to parse JSON: " + e.getMessage());
                    }
                } else {
                    System.out.println("[Error] No response from LLM.");
                }

                System.out.println("--------");
            }

            //TODO: Here is where you'd call your local LLM executable with mergedPrompt.
            // For now, this is where MobiLlama input/output integration will go.

        } //end of call for LLM

        //TODO - for each in structuralList (change this to a map or something), do the TODOs from below
        structuralList = structuralList;
        String functionName = "function"; // this should be changed for each function. Fields will be stored as "Fields". "Imports" for Imports, etc.

        //TODO - FINALLY, once it has done this for all the lines in that file, use it to create the bubbles like handleDirectory does.
        // each of these bubbles should have the class / file name. If there is more than 1 class, create class bubbles, and
        // those class bubbles will contain the function bubbles. If there is only 1 class, make the bubbles split by functions.
        // There should also be an imports and fields bubble. Each function bubble should be able to see fields if the option is enabled by the user.

        //TODO - add a check to make sure if the flag from addStructure is already in the code, then it doesnt try
        // to do LSP or AI unless the user overrides that or something.

        for (int i = 0; i != 0; i++) { //Simulating a for each statement, obviously change this loop once its good.
            Storage.addStructure(filePath, functionName);
        }

        Bubble newBubble = Storage.load(filePath); //might change this
        parent.setLayout(null);
        parent.add(newBubble);
    }

}
