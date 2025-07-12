package lanse.abstractt.core.bubble;

import java.util.Optional;

public class FunctionBubble extends Bubble {

    //This is just an easy way for me to tell if it's a function bubble or main bubble. This could change eventually.

    //TODO - There should also be an imports and fields bubble. Each function bubble should be able to see fields if the option is enabled by the user.

    protected int startLine;
    protected Optional<Integer> endLine;
    protected String structure;

    public FunctionBubble(String title, String description, String filePath, int startLine, Optional<Integer> endLine, String structure, boolean isClickable) {
        super(title, description, filePath, isClickable);
        this.startLine = startLine;
        this.endLine = endLine;
        this.structure = structure;
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
}
