package lanse.abstractt.parser;

import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.exceptions.OllamaBaseException;
import io.github.ollama4j.models.response.Model;
import io.github.ollama4j.models.response.OllamaResult;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class LLMManager {
    private static Process llamaProcess;
    private static final String model = "codegemma:latest";

    public static boolean tryStartOllama() {
        if (llamaProcess != null) {
            return true;
        }
        try {
            installOllama();
            String binary_extension = "";
            String bindir = "bin/";
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                binary_extension = ".exe";
                bindir = "";
            }
            ProcessBuilder server = new ProcessBuilder(List.of("ollama" + binary_extension, "serve"));
            try {
                llamaProcess = server.start();
            }
            catch (IOException e) {
                server = new ProcessBuilder(List.of("./llama/" + bindir + "ollama" + binary_extension, "serve"));
                llamaProcess = server.start();
            }
            Runtime.getRuntime().addShutdownHook(new Thread(LLMManager::stopServer));
            OllamaAPI ollamaAPI = new OllamaAPI("http://localhost:11434");
            if (!ollamaAPI.listModels().stream().map(Model::toString).toList().contains(model)) {
                try {
                    System.out.println("Pulling " + model);
                    //TODO: add some progress bar
                    ollamaAPI.pullModel(model);
                    System.out.println("Pulled " + model);
                }
                catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        catch (IOException | InterruptedException e) {
            return false;
        } catch (OllamaBaseException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            System.err.println("Invalid model URI!");
            throw new RuntimeException(e);
        }
        return true;
    }

    public static void installOllama() throws IOException, InterruptedException {
        //Figure out what OS we're on, and set variables based on that.
        String osType = System.getProperty("os.name");
        String osName = "unknown";
        String extension = ".tgz";
        String binary_extension = "";
        String bindir = "bin/";
        if (osType.toLowerCase().contains("windows")) {
            osName = "windows";
            extension = ".zip";
            binary_extension = ".exe";
            bindir = "";
        }
        else if (osType.toLowerCase().contains("darwin")) {
            osName = "darwin";
        }
        else if (osType.toLowerCase().contains("linux")) {
            osName = "linux";
        }

        // If it exists, we don't need to install it!
        String exec = "ollama" + binary_extension;
        boolean ollamaExists = Stream.of(System.getenv("PATH").split(Pattern.quote(File.pathSeparator)))
                .map(Paths::get)
                .anyMatch(path -> Files.exists(path.resolve(exec)));

        if (ollamaExists) {
            UniversalParser.aiCompiled = true;
            return;
        }

        // If we've already installed it, we don't need to install it again!
        File executable =  new File("llama/" + bindir + "ollama" + binary_extension);
        if (executable.exists() && executable.canExecute()) {
            UniversalParser.aiCompiled = true;
            return;
        }

        URL url = new URL("https://github.com/ollama/ollama/releases/latest/download/ollama-" + osName + "-amd64" + extension);
        Path compressed = Paths.get("llama/ollama-" + osName + "-amd64" + extension);
        if (!compressed.toFile().exists()) {
            System.out.println("Downloading " + url);
            try (InputStream download = url.openStream()) {
                Files.createDirectories(compressed.getParent());
                Files.copy(download, compressed);
            }
        }

        System.out.println("Extracting " + compressed);
        File targetDir = new File("llama");
        if (osName.equals("windows")) {
            try (ArchiveInputStream i = new ZipArchiveInputStream(new BufferedInputStream(compressed.toUri().toURL().openStream()))) {
                extractFile(i, targetDir);
            }
        }
        else {
            try (InputStream fi = Files.newInputStream(compressed);
                 InputStream bi = new BufferedInputStream(fi);
                 InputStream gzi = new GzipCompressorInputStream(bi);
                 ArchiveInputStream i = new TarArchiveInputStream(gzi)) {
                extractFile(i, targetDir);
            }
        }
        System.out.println("Done installing ollama!");
        UniversalParser.aiCompiled = true;
    }

    public static Optional<String> runLLM(String prompt) {
        try {
            OllamaAPI ollamaAPI = new OllamaAPI("http://localhost:11434");

            OllamaResult result = ollamaAPI.generate(model, prompt, null);
            return Optional.of(result.getResponse());
        }
        catch (OllamaBaseException | IOException | InterruptedException e) {
            System.err.println("Error generating result!");
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static void extractFile(ArchiveInputStream i, File targetDir) throws IOException {
        ArchiveEntry entry = null;
        while ((entry = i.getNextEntry()) != null) {
            if (!i.canReadEntryData(entry)) {
                // log something?
                continue;
            }
            File f = new File(targetDir.getName() + "/" + entry.getName());
            if (entry.isDirectory()) {
                if (!f.isDirectory() && !f.mkdirs()) {
                    throw new IOException("failed to create directory " + f);
                }
            } else {
                File parent = f.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("failed to create directory " + parent);
                }
                try (OutputStream o = Files.newOutputStream(f.toPath())) {
                    IOUtils.copy(i, o);
                    f.setExecutable(true); //TODO: not safe for generically, but works for everything we currently extract
                }
            }
        }
    }

    public static void stopServer() {
        if (llamaProcess != null && llamaProcess.isAlive()) {
            llamaProcess.destroy();
        }
    }
}
