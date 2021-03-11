package main.exceptions;

/**Exception to be thrown when the splitting cannot be done in the splitter.
 * This happens when the sizes of the inputs and outputs don't match.
 */
public class IllegalSplitException extends RuntimeException {

    public IllegalSplitException(String message) {
        super(message);
    }
}
