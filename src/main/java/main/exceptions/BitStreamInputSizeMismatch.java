package main.exceptions;

/**Exception indicating that some streams have incorrect/mismatched sizes
 */
public class BitStreamInputSizeMismatch extends RuntimeException {

    public BitStreamInputSizeMismatch(Object node) {
        super("Input size mismatch at: " + node.toString());
    }

    public BitStreamInputSizeMismatch(String errorMessage) {
        super(errorMessage);
    }
}
