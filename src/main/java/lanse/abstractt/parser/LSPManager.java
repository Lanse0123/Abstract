package lanse.abstractt.parser;

import lanse.abstractt.core.screens.bars.ProgressBarPanel;
import lanse.abstractt.storage.Settings;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

public class LSPManager implements LanguageClient {

    //TODO - the link to LSP stuff
    // https://microsoft.github.io/language-server-protocol/implementors/servers/

    public static List<DocumentSymbol> doStuff(String LSPLink, String languageId, File file) {
        //TODO: re-use same connection for new files
        try {
            List<String> command = new ArrayList<>();
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                command.add("cmd.exe");
                command.add("/c");
            }
            command.add(LSPLink);

            ProgressBarPanel.setProgress(0.1);

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(new File(Settings.selectedProjectPath));
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            Process lsp = pb.start();

            Launcher<LanguageServer> launcher = LSPLauncher.createClientLauncher(new LSPManager(), lsp.getInputStream(), lsp.getOutputStream());
            launcher.startListening();
            InitializeParams init_params = new InitializeParams();

            ProgressBarPanel.setProgress(0.2);

            TextDocumentClientCapabilities text_caps = new TextDocumentClientCapabilities();
            DocumentSymbolCapabilities doc_caps = new DocumentSymbolCapabilities();
            doc_caps.setLabelSupport(true);
            doc_caps.setSymbolKind(new SymbolKindCapabilities());
            doc_caps.setHierarchicalDocumentSymbolSupport(true);
            text_caps.setDocumentSymbol(doc_caps);

            List<WorkspaceFolder> workspace_folders = List.of(new WorkspaceFolder(new File(Settings.selectedProjectPath).toURI().toString(), Settings.selectedProjectPath));
            init_params.setWorkspaceFolders(workspace_folders);

            ProgressBarPanel.setProgress(0.3);

            init_params.setCapabilities(new ClientCapabilities(new WorkspaceClientCapabilities(), text_caps, new Object()));
            CompletableFuture<InitializeResult> initializeResponse = launcher.getRemoteProxy().initialize(init_params);
            initializeResponse.join();
            launcher.getRemoteProxy().initialized(new InitializedParams());

            ProgressBarPanel.setProgress(0.5);

            String contents = Files.readString(file.toPath());
            launcher.getRemoteProxy().getTextDocumentService().didOpen(new DidOpenTextDocumentParams(new TextDocumentItem(file.toURI().toString(), languageId, 0, contents)));

            ProgressBarPanel.setProgress(0.8);

            DocumentSymbolParams doc_params = new DocumentSymbolParams();
            doc_params.setTextDocument(new TextDocumentIdentifier(file.toURI().toString()));
            List<Either<SymbolInformation, DocumentSymbol>> symbols = launcher.getRemoteProxy().getTextDocumentService().documentSymbol(doc_params).get();
            lsp.destroy();

            ProgressBarPanel.setProgress(1);
            return flattenSymbols(symbols.stream().map(Either::getRight)).toList();
        }
        catch (IOException e) {
            System.err.println("Error running LSP: " + e);
        } catch (InterruptedException e) {
            System.err.println("Interrupted while getting document symbols: " + e);
        } catch (ExecutionException e) {
            System.err.println("Unexpected completion of future: " + e);
            throw new RuntimeException(e);
        }

        //it shouldn't make it here
        return new ArrayList<>();
    }

    public static Stream<DocumentSymbol> flattenSymbols(Stream<DocumentSymbol> symbolStream) {
        return symbolStream.flatMap(symbol -> {
            try {
                Stream<DocumentSymbol> children = flattenSymbols(symbol.getChildren().stream());
                return Stream.concat(Stream.of(symbol), children);
            }
            catch (NullPointerException e) {
                return Stream.of(symbol);
            }
        });
    }

    @Override
    public void telemetryEvent(Object o) {
        System.out.println("Telemetry event" + o);
    }

    @Override
    public void publishDiagnostics(PublishDiagnosticsParams publishDiagnosticsParams) {

    }

    @Override
    public void showMessage(MessageParams messageParams) {
        System.out.println("Show message: " + messageParams);
    }

    @Override
    public CompletableFuture<MessageActionItem> showMessageRequest(ShowMessageRequestParams showMessageRequestParams) {
        System.out.println("Request to show message: " + showMessageRequestParams);
        return null;
    }

    @Override
    public void logMessage(MessageParams messageParams) {
        System.out.println("Log message: " + messageParams);
    }
}
