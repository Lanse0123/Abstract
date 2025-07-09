package lanse.abstractt.core.bubble;

public class FunctionBubble extends Bubble {

    //This is just an easy way for me to tell if it's a function bubble or main bubble. This could change eventually.

    //TODO - There should also be an imports and fields bubble. Each function bubble should be able to see fields if the option is enabled by the user.

    protected int line;
    protected String structure;

    public FunctionBubble(String title, String description, String filePath, int line, String structure, boolean isClickable) {
        super(title, description, filePath, isClickable);
        this.line = line;
        this.structure = structure;
    }

    public String getStructureType() {
        return structure;
    }

    public int getLineNumber() {
        return this.line; // this is just the starting line of it's definition.
        //I might either store this as a range, or just find the next function and stop the line there.
    }

}
