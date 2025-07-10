package lanse.abstractt.core.bubble;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CodeBubble extends JPanel {

    //TODO - this will display the code in a rectangle bubble next to the description bubbles.

    public static void createCodeBubble(String filePath, Container parent){

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
    }
}
