package lanse.abstractt.parser;

import dev.dirs.ProjectDirectories;
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.exceptions.OllamaBaseException;
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

    public static boolean isAiEnabled = false;
    private static Process llamaProcess;

    //TODO - add the model String to Settings as an option so people can use any ai model they want.
    // default should  be codegemma:latest
    private static final String model = "codegemma:latest";
    private static final ProjectDirectories projectDirs = ProjectDirectories.from("dev", "Lanse", "Abstract");

    public static boolean tryStartOllama() {
        if (llamaProcess != null) return true;

        try {
            //the os code looked scary, so I moved it to a function
            OSInfo os = getOSInfo();
            installOllama();

            ProcessBuilder server = new ProcessBuilder(List.of("ollama" + os.binaryExtension(), "serve"));
            try {
                llamaProcess = server.start();
            } catch (IOException e) {
                server = new ProcessBuilder(List.of(Paths.get(projectDirs.dataDir, "llama", os.binDir(), "ollama" + os.binaryExtension()).toString(), "serve"));
                llamaProcess = server.start();
            }

            Runtime.getRuntime().addShutdownHook(new Thread(LLMManager::stopServer));

            OllamaAPI ollamaAPI = new OllamaAPI("http://localhost:11434");
            boolean modelExists = ollamaAPI.listModels().stream().anyMatch(m -> m.toString().equals(model));
            if (!modelExists) {
                try {
                    System.out.println("Pulling " + model);
                    //TODO: add some progress bar
                    ollamaAPI.pullModel(model);
                    System.out.println("Pulled " + model);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        } catch (IOException | InterruptedException e) {
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
        OSInfo os = getOSInfo();

        String exec = "ollama" + os.binaryExtension();
        boolean ollamaExists = Stream.of(System.getenv("PATH").split(Pattern.quote(File.pathSeparator)))
                .map(Paths::get)
                .anyMatch(path -> Files.exists(path.resolve(exec)));

        if (ollamaExists) {
            UniversalParser.aiCompiled = true;
            return;
        }

        File executable = new File(Paths.get(projectDirs.dataDir, "llama", os.binDir(), "ollama" + os.binaryExtension()).toString());
        if (executable.exists() && executable.canExecute()) {
            UniversalParser.aiCompiled = true;
            return;
        }

        URL url = new URL("https://github.com/ollama/ollama/releases/latest/download/ollama-" + os.osName() + "-amd64" + os.archiveExtension());
        Path compressed = Paths.get(projectDirs.cacheDir, "llama", "ollama-" + os.osName() + "-amd64" + os.archiveExtension());

        // If we've already installed it, we don't need to install it again!
        if (!compressed.toFile().exists()) {
            System.out.println("Downloading " + url);
            try (InputStream download = url.openStream()) {
                Files.createDirectories(compressed.getParent());
                Files.copy(download, compressed);
            }
        }

        System.out.println("Extracting " + compressed);
        File targetDir = Paths.get(projectDirs.dataDir, "llama").toFile();
        System.out.println("Extracting to " + targetDir);

        if (os.osName().equals("windows")) {
            try (ArchiveInputStream i = new ZipArchiveInputStream(new BufferedInputStream(compressed.toUri().toURL().openStream()))) {
                extractFile(i, targetDir);
            }
        } else {
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
        } catch (OllamaBaseException | IOException | InterruptedException e) {
            System.err.println("Error generating result!");
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static void extractFile(ArchiveInputStream i, File targetDir) throws IOException {
        ArchiveEntry entry;
        while ((entry = i.getNextEntry()) != null) {
            if (!i.canReadEntryData(entry)) continue;
            File f = new File(targetDir, entry.getName());
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
                    f.setExecutable(true);
                }
            }
        }
    }

    private record OSInfo(String osName, String archiveExtension, String binaryExtension, String binDir) {}

    //the os code looked scary so I moved it here
    private static OSInfo getOSInfo() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) return new OSInfo("windows", ".zip", ".exe", "");
        if (os.contains("darwin")) return new OSInfo("darwin", ".tgz", "", "bin/");
        if (os.contains("linux")) return new OSInfo("linux", ".tgz", "", "bin/");
        return new OSInfo("unknown", ".tgz", "", "bin/");
    }

    public static void stopServer() {
        if (llamaProcess != null && llamaProcess.isAlive()) {
            llamaProcess.destroy();
        }
    }
}
