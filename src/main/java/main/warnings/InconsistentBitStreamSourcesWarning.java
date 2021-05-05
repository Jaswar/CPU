package main.warnings;

import main.Node;

/**Warning to be show when a BitStream has inconsistent sources.
 */
public class InconsistentBitStreamSourcesWarning {

    private static boolean suppress = false;

    public static void show(Node oldSource, Node newSource) {
        if (!suppress) {
            System.err.println("Warning! Possible inconsistency between:\n" + oldSource + " and\n" + newSource);
        }
    }

    public static void show(String message) {
        if (!suppress) {
            System.err.println(message);
        }
    }

    /**Allow for setting of the suppression of the warning.
     *
     * @param s - the new value to set the suppression to
     */
    public static void setSuppress(boolean s) {
        suppress = s;
    }
}