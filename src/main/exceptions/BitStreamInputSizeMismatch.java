package main.exceptions;

/**Exception indicating that some streams have incorrect/mismatched sizes
 */
public class BitStreamInputSizeMismatch extends RuntimeException {

    public BitStreamInputSizeMismatch(String errorMessage) {
        super(errorMessage);
    }

}
