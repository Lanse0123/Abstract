package lanse.abstractt.storage;
import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;

public class LLMManager {

    public static Optional<String> runLLM(String prompt) {
        try {
            // Update these paths if needed
            Path llamaExe = Paths.get("llama/bin/llama-cli.exe");
            Path modelFile = Paths.get("llama/models/ggml-model-q4_0.gguf");

            if (!Files.exists(llamaExe) || !Files.exists(modelFile)) {
                System.err.println("LLM binary or model file not found.");
                return Optional.empty();
            }

            List<String> command = List.of(
                    llamaExe.toAbsolutePath().toString(),
                    "-m", modelFile.toAbsolutePath().toString(),
                    "-p", prompt,
                    "-n", "128"  // Number of tokens to generate
            );

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true); // Merge stderr with stdout
            Process process = pb.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return Optional.of(output.toString().trim());
            } else {
                System.err.println("Error - LLM exited with code: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
}