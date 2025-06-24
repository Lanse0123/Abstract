package lanse.abstractt.storage;

import lanse.abstractt.storage.languages.LanguageManager;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class UniversalParser {

    public static void handleFile(String filePath, Container parent) {
        if (!LanguageManager.isFileParsable(filePath)) {
            System.out.println("Skipping file (not parseable): " + filePath);
            return;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("File does not exist: " + filePath);
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

        // Determine the language from extension
        String extension = LanguageManager.getExtension(filePath);
        if (!extension.startsWith(".")) {
            extension = "." + extension;
        }

        // Print full prompts with template
        System.out.println("=== TEMPLATED PROMPTS FOR: " + file.getName() + " ===");
        for (String prompt : prompts) {
            String mergedPrompt = """
            #CONTEXT:
            You are part of a universal coding IDE. Your job is to extract structural code information from a file written in %s.
            
            #GOAL:
            Find all defining lines in the code: functions, classes, imports, fields, or other structural elements.
            
            #INPUT:
            %s
            
            #OUTPUT RULES:
            - Only respond with line numbers and their type.
            - Use this format: 12: function, 24: class
            - If nothing is defining, respond with: no
            - Use only one line in your response.
            - Do not add explanations or extra formatting.
            """.formatted(extension, prompt);

            System.out.println(mergedPrompt);
            System.out.println("--------");
        }

        //TODO - then, for each prompt, ask mobiLlama if it is containing anything important, like fields, classes, functions, imports, or something else.
        // It should either respond with NO, or line number + what it is. Make sure it has context like the language type, and file name.

        //TODO - FINALLY, once it has done this for all the lines in that file, use it to create the bubbles like handleDirectory does.
        // each of these bubbles should have the class / file name. If there is more than 1 class, create class bubbles, and
        // those class bubbles will contain the function bubbles. If there is only 1 class, make the bubbles split by functions.
        // There should also be an imports and fields bubble. Each function bubble should be able to see fields if the option is enabled by the user.

        //TODO - LASTLY, make sure this is added to storage, so it never needs to do this again unless the code is edited, or
        // if the user refreshes it. It can also do this smartly, by saving the code, and checking for parts that changed.
        // then only call the AI for those parts, because going through each line each update might take a while.
    }

}
