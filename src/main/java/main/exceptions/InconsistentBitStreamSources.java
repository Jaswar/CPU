package main.exceptions;

import main.Node;

/**Exception indicating that a BitStream has two sources that are inconsistent with each other.
 */
public class InconsistentBitStreamSources extends RuntimeException {

    public InconsistentBitStreamSources(Node oldSource, Node newSource) {
        super("Inconsistency detected between\n" + oldSource.toString() + " and\n" + newSource.toString());
    }

    public InconsistentBitStreamSources(String errorMessage) {
        super(errorMessage);
    }

}
