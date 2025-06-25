package lanse.abstractt.storage;

import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.exceptions.OllamaBaseException;
import io.github.ollama4j.models.response.OllamaResult;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;

public class LLMManager {
    private static Process llamaProcess;

    public static boolean tryStartOllama() {
        if (llamaProcess != null) {
            return true;
        }
        try {
            installOllama();
            ProcessBuilder server = new ProcessBuilder(List.of("ollama", "serve"));
            llamaProcess = server.start();
        }
        catch (IOException e) {
            return false;
        }
        return true;
    }

    public static void installOllama() throws IOException {
        String osType = System.getProperty("os.name");
        String script = (osType.toLowerCase().contains("windows")) ?
                "./llama/headless-ollama/preload.ps1" : "./llama/headless-ollama/preload.sh"
        ;
        ProcessBuilder pb = new ProcessBuilder(List.of(script));
        pb.start();
    }

    public static Optional<String> runLLM(String prompt) {
        if (!tryStartOllama()) {
            return Optional.empty();
        }
        try {
            OllamaAPI ollamaAPI = new OllamaAPI("http://localhost:11434");
            OllamaResult result = ollamaAPI.generate("tinyllama:latest", prompt, null);
            return Optional.of(result.getResponse());
        }
        catch (OllamaBaseException | IOException | InterruptedException e) {
            System.err.println("Error generating result!");
            e.printStackTrace();
            return Optional.empty();
        }
    }
    
    public static void stopServer() {
        if (llamaProcess != null && llamaProcess.isAlive()) {
            llamaProcess.destroyForcibly();
        }
    }

}
