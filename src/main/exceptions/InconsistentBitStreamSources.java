package main.exceptions;

/**Exception indicating that a BitStream has two sources that are inconsistent with each other.
 */
public class InconsistentBitStreamSources extends RuntimeException {

    public InconsistentBitStreamSources(String errorMessage) {
        super(errorMessage);
    }

}
