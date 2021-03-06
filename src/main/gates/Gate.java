package main.gates;

import main.BitStream;
import main.Node;
import main.exceptions.InconsistentBitStreamSources;
import main.utils.ProcessRunner;
import main.warnings.InconsistentBitStreamSourcesWarning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Gate implements Node {

    private BitStream out;
    private String name;
    private boolean inDebuggerMode;

    /**Constructor for the Gate class.
     *
     * @param out - the output stream from the gate
     * @param name - the name of the gate
     * @param inDebuggerMode - boolean to specify if additional debug information should be shown
     */
    public Gate(BitStream out, String name, boolean inDebuggerMode) {
        this.out = out;
        this.name = name;
        this.inDebuggerMode = inDebuggerMode;

        this.out.addNewEndpoint(this);
    }

    /**Getters for all the attributes of the class.
     */
    public BitStream getOut() {
        return out;
    }

    public String getName() {
        return name;
    }

    public boolean isInDebuggerMode() {
        return inDebuggerMode;
    }

    /**Setters for some of the attributes of the class.
     */
    public void setName(String name) {
        this.name = name;
    }

    public void setInDebuggerMode(boolean inDebuggerMode) {
        this.inDebuggerMode = inDebuggerMode;
    }

    /**Method to be defined by the children of the class to check if all the inputs and
     * the output match with respect to the size.
     */
    public abstract void checkIfSizesMatch();

    /**Abstract class used by the evaluate() method to calculate the actual logic output.
     * This method has to be defined by all the logic gates.
     *
     * @return - a boolean array corresponding to the computed logic result
     */
    public abstract boolean[] compute();

    /**Method to setup the circuit starting in "this".
     */
    public void setup() {
        this.checkIfSizesMatch();
        ProcessRunner.run(this);
    }

    /**Evaluate the logic gate. This also includes checking if evaluation is possible, setting the
     * source of the out stream to this logic gate and adding all neighbours to the execution queue.
     *
     * @param queue - the execution queue
     */
    @Override
    public void evaluate(List<Node> queue) {
        boolean[] newOutData = compute();

        this.checkIfSourceIsConsistent(newOutData);

        if (this.decideIfEvaluateFurther(newOutData)) {
            this.addNeighboursToQueue(queue);
        }

        this.out.setData(newOutData);
        this.setSourceForOutStream();

        if (this.isInDebuggerMode()) {
            this.debug();
        }
    }

    /**Set the source of the out stream.
     */
    @Override
    public void setSourceForOutStream() {
        this.out.setSource(this);
    }

    /**Method used to decide if execution should be continued further. That is, if the newly evaluated output
     * is different from the old one, then the evaluation/execution needs to be continued.
     *
     * @param newOutData - the newly calculated data for this logic gate
     * @return - true if execution should be continued, false otherwise
     */
    @Override
    public boolean decideIfEvaluateFurther(boolean[] newOutData) {
        return !Arrays.equals(this.out.getData(), newOutData);
    }

    /**Method to check if the sources of the out stream are consistent. In particular, check if the source
     * of the out stream is the current logic gate and if not check if it is possible to merge the old source
     * with the current logic gate.
     *
     * @param newOutData - the newly calculated output of the branch, to be compared with the old value of the
     * out stream. If the source is not the current logic gate, newOutData is compared with old BitStream and
     * if they differ at any bit, the method throws an InconsistentBitStreamSources error.
     */
    @Override
    public void checkIfSourceIsConsistent(boolean[] newOutData) {
        if (this.out.getSource() != null && this.out.getSource() != this) {
            if (!Arrays.equals(this.out.getData(), newOutData)) {
                InconsistentBitStreamSourcesWarning.show(this.out.getSource(), this);
            }
        }
    }

    /**Add all neighbours of this logic gate, that are connected to it with the out BitStream, to
     * the execution queue.
     *
     * @param queue - the execution queue
     */
    @Override
    public void addNeighboursToQueue(List<Node> queue) {
        queue.addAll(this.out.getAllNeighbours(this));
    }
}
