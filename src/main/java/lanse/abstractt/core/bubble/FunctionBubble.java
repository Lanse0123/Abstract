package lanse.abstractt.core.bubble;

import lanse.abstractt.core.WorldMap;
import lanse.abstractt.core.screens.WorkSpaceScreen;
import lanse.abstractt.core.screens.bars.ProgressBarPanel;
import lanse.abstractt.parser.LLMManager;
import lanse.abstractt.storage.Storage;
import lanse.abstractt.storage.languages.LanguageManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.Arrays;
import java.util.Optional;

public class FunctionBubble extends Bubble {

    //This is just an easy way for me to tell if it's a function bubble or main bubble. This could change eventually.

    //TODO - There should also be an imports and fields bubble. Each function bubble should be able to see fields if the option is enabled by the user.

    protected int startLine;
    protected Optional<Integer> endLine;
    protected String structure;

    static final String prompt = """
            You are part of a universal coding IDE. Your job is to generate a simple description of a function in %s.
            
            INPUT:
            %s
            
            OUTPUT RULES:
            -Format the description in a simple and brief 1 - 2 sentences.
            -Avoid using overly technical language unless necessary.
            -Your response will directly be used as the code's description.
            -Do not add explanations or extra formatting.
            -A brief and simple description that matches the code, and follows these rules.""";

    public FunctionBubble(String title, String description, String filePath, int startLine, Optional<Integer> endLine, String structure, boolean isClickable) {
        super(title, description, filePath, isClickable);
        this.startLine = startLine;
        this.endLine = endLine;
        this.structure = structure;

        registerMouseListeners(isClickable);
    }

    public String getStructureType() {
        return structure;
    }

    public int getStartLineNumber() {
        return this.startLine; // this is just the starting line of it's definition.
        //I might either store this as a range, or just find the next function and stop the line there.
    }

    public Optional<Integer> getEndLineNumber() {
        return this.endLine;
    }

    public void generateDescription() {
        if (description != null && !description.isEmpty()) return;

        //TODO - i want to limit the ai on doing too big stuff. For example, if theres a bubble with like 2000 lines of code,
        // it might not be good to have ai try to make sense of it.

        try (BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)))) {
            int i;
            for (i = 0; i < startLine; ++i) {
                reader.readLine();
            }
            StringBuilder context = new StringBuilder();
            if (endLine.isEmpty()) {
                context.append(reader.readLine());
            }
            else {
                for (; i <= endLine.get(); ++i) {
                    context.append(reader.readLine());
                }
            }
            System.out.println(prompt.formatted(LanguageManager.getLanguageName(filePath), context));
            Optional<String> response = LLMManager.runLLM(prompt.formatted(LanguageManager.getLanguageName(filePath), context));
            if (response.isPresent()) {
                description = response.get();
                System.out.println(description);
                Storage.updateStructure(filePath, structure, title, Optional.of(description), Optional.empty(), Optional.empty());
                initUI();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getLineCount() {return endLine.map(end -> Math.max(1, end - startLine + 1)).orElse(1); }

    //TODO - endLine maybe shouldnt be optional. That or calculate the endline here.
    public int[] getLineSpan(){return new int[]{startLine, endLine.orElse(-40404)};}


    private void registerMouseListeners(boolean isClickable) {

        addMouseListener(new FunctionBubble.HandleClick(isClickable));
    }

    private class HandleClick extends MouseAdapter {
        private final boolean isClickable;

        public HandleClick(boolean isClickable) {
            this.isClickable = isClickable;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (!isClickable || ProgressBarPanel.isLoading()) return;
            if (e.getSource() == editIconLabel) return;
            if (isABubbleBeingEdited) return;

            File file = new File(filePath);
            if (!file.exists()) {
                JLabel error = new JLabel("Invalid file path: " + title, SwingConstants.CENTER);
                error.setForeground(Color.RED);
                Container parent = getParent();
                if (parent != null) parent.add(error);
                return;
            }

            Storage.increaseDepth(filePath);

            Container parent = getParent();
            if (parent == null) return;

            Storage.saveAllBubbles(true, parent);

            new FunctionSubBubble(title, description, filePath, startLine, endLine, structure, false);
            //Storage.loadFunctionSubBubbles(filePath);

            int[] span = getLineSpan(); //optionals is pain
            Integer[] wrapperArray = Arrays.stream(span).boxed().toArray(Integer[]::new);
            Optional<Integer[]> wrapperSpan = Optional.of(wrapperArray);

            CodeBubble.createCodeBubble(filePath, parent, wrapperSpan);

            WorldMap.setCameraCoordinates(0, 0);
            if (parent instanceof WorkSpaceScreen workspace) workspace.refreshSidebar();
            parent.revalidate();
            parent.repaint();
        }
    }
}