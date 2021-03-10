package main;

import main.exceptions.BitStreamInputSizeMismatch;

import java.util.List;

public interface Node {

    /**Interface describing each node/component in a circuit.
     */
    public void setup();
    public void evaluate(List<Node> queue);
    public void debug();
    public void checkIfSizesMatch();
    public void checkIfSourceIsConsistent(boolean[] newOut);
    public void addNeighboursToQueue(List<Node> queue);
    public void setSourceForOutStream();
    public boolean decideIfEvaluateFurther(boolean[] newOutData);
}
