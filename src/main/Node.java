package main;

import main.exceptions.BitStreamInputSizeMismatch;

import java.util.List;

/**Interface describing each node/component in a circuit.
 */
public interface Node {

    int WORD_SIZE = 16;

    void setup();
    void checkIfSizesMatch();
    void evaluate(List<Node> queue);
    void checkIfSourceIsConsistent(boolean[] newOut);
    void addNeighboursToQueue(List<Node> queue);
    void setSourceForOutStream();
    boolean decideIfEvaluateFurther(boolean[] newOutData);
    void debug();
}
