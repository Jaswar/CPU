package main.exceptions;

import main.control.Splitter;

/**Exception to be thrown when the splitting cannot be done in the splitter.
 * This happens when the sizes of the inputs and outputs don't match.
 */
public class IllegalSplitException extends RuntimeException {

    public IllegalSplitException(Splitter splitter) {
        super("Input size was " + splitter.getBitStreamListSize(splitter.getIn())
                + " but the output size was " + splitter.getBitStreamListSize(splitter.getOut())
                + " at " + splitter.toString());
    }

    public IllegalSplitException(String message) {
        super(message);
    }
}
