package lanse.abstractt.storage;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.util.Optional;

public class LLMManager {
    private static Process llamaProcess;

    public static Optional<String> runLLM(String prompt) {
        try {
            URL url = new URL("http://localhost:8080/completion");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            String json = """
        {
          "prompt": "%s",
          "n_predict": 64,
          "temperature": 0.2
        }
        """.formatted(prompt.replace("\"", "\\\"").replace("\n", "\\n"));

            try (OutputStream os = connection.getOutputStream()) {
                os.write(json.getBytes());
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            return Optional.of(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static Process startLlamaServer() throws IOException {
        // Don't start if already running
        if (llamaProcess != null && llamaProcess.isAlive()) {
            return llamaProcess;
        }

        // Paths to the executable and model
        Path llamaServer = Paths.get("llama/bin/llama-server.exe");
        Path modelFile = Paths.get("llama/models/ggml-model-q4_0.gguf");

        // Check if both exist
        if (!Files.exists(llamaServer) || !Files.exists(modelFile)) {
            System.err.println("llama-server.exe or model file not found.");
            return null;
        }

        // Build the process
        ProcessBuilder pb = new ProcessBuilder(
                llamaServer.toAbsolutePath().toString(),
                "-m", modelFile.toAbsolutePath().toString(),
                "--port", "8080"
        );

        pb.inheritIO(); // log output to console
        llamaProcess = pb.start(); // Store the process for later shutdown

        return llamaProcess;
    }

    public static void stopServer() {
        if (llamaProcess != null && llamaProcess.isAlive()) {
            llamaProcess.destroyForcibly();
        }
    }

}
