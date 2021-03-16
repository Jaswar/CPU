package main.warnings;

import main.Node;

/**Warning to be show when a BitStream has inconsistent sources.
 */
public class InconsistentBitStreamSourcesWarning {

    public static void show(Node oldSource, Node newSource) {
        System.err.println("Warning! Possible inconsistency between:\n" + oldSource + " and\n" + newSource);
    }

    public static void show(String message) {
        System.err.println(message);
    }
}