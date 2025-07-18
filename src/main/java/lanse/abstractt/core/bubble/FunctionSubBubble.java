package lanse.abstractt.core.bubble;

import java.util.Optional;

public class FunctionSubBubble extends FunctionBubble{


    //TODO - this will be function bubbles that can be split further to even smaller bits explaining certain blocks of code or something

    public FunctionSubBubble(String title, String description, String filePath, int startLine, Optional<Integer> endLine, String structure, boolean isClickable) {
        super(title, description, filePath, startLine, endLine, structure, isClickable);
    }
}
