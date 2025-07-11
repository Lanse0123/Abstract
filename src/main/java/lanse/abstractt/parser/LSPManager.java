package lanse.abstractt.parser;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;

import javax.swing.text.Document;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class LSPManager implements LanguageClient {

    public static List<DocumentSymbol> doStuff(String LSPLink, File file) {
        System.out.println("LSPLink: " + LSPLink + " for file " + file);
        try {
            System.out.println("Starting LSP");
            ProcessBuilder pb = new ProcessBuilder(List.of(LSPLink, "-v"));
            pb.directory(file.getParentFile());
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            Process lsp = pb.start();
            Launcher<LanguageServer> launcher = LSPLauncher.createClientLauncher(new LSPManager(), lsp.getInputStream(), lsp.getOutputStream());
            System.out.println("About to listen");
            launcher.startListening();
            System.out.println("About to initialize");
            InitializeParams init_params = new InitializeParams();

            TextDocumentClientCapabilities text_caps = new TextDocumentClientCapabilities();
            DocumentSymbolCapabilities doc_caps = new DocumentSymbolCapabilities();
            doc_caps.setLabelSupport(true);
            doc_caps.setSymbolKind(new SymbolKindCapabilities());
            doc_caps.setHierarchicalDocumentSymbolSupport(true);
            text_caps.setDocumentSymbol(doc_caps);

            init_params.setCapabilities(new ClientCapabilities(new WorkspaceClientCapabilities(), text_caps, new Object()));
            CompletableFuture<InitializeResult> initializeResponse = launcher.getRemoteProxy().initialize(init_params);
            System.out.println("About to wait for initialization response");
            initializeResponse.join();
            launcher.getRemoteProxy().initialized(new InitializedParams());
            System.out.println("Requesting document symbols");

            DocumentSymbolParams doc_params = new DocumentSymbolParams();
            doc_params.setTextDocument(new TextDocumentIdentifier(file.toURI().toURL().toString()));
            CompletableFuture<List<Either<SymbolInformation, DocumentSymbol>>> symbols = launcher.getRemoteProxy().getTextDocumentService().documentSymbol(doc_params);
            System.out.println("About to wait for document symbols");
            return symbols.get().stream().map(Either::getRight).toList();
        }
        catch (IOException e) {
            System.err.println("Error running LSP: " + e);
        } catch (InterruptedException e) {
            System.err.println("Interrupted while getting document symbols: " + e);
        } catch (ExecutionException e) {
            System.err.println("Unexpected completion of future: " + e);
            throw new RuntimeException(e);
        }
        //do stuff or something idk
        //(the string is the name of the function, or if its fields, or imports, or etc. int is the lineNumber it starts at. This might change
        return new ArrayList<>();
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
