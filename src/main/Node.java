package main;

import main.exceptions.BitStreamInputSizeMismatch;

import java.util.List;

/**Interface describing each node/component in a circuit.
 */
public interface Node {

    int WORD_SIZE = 16;

    void setup();
    void evaluate(List<Node> queue);
    void debug();
    void checkIfSizesMatch();
    void checkIfSourceIsConsistent(boolean[] newOut);
    void addNeighboursToQueue(List<Node> queue);
    void setSourceForOutStream();
    boolean decideIfEvaluateFurther(boolean[] newOutData);
}
