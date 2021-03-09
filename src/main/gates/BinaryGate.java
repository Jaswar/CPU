package main.gates;

import main.BitStream;
import main.Node;
import main.exceptions.BitStreamInputSizeMismatch;
import main.exceptions.InconsistentBitStreamSources;
import main.utils.BitInformationConverter;

import java.util.List;

public abstract class BinaryGate implements Node {

    /**Abstract class to represent binary logic gates (ie logic gates with two inputs).
     *
     * @param in1 - the first input bit stream
     * @param in2 - the second input bit stream
     * @param out - the output stream
     * @param name - the name of the gate
     * @param inDebuggerMode - boolean to specify if additional debug information should be shown
     */
    private BitStream in1, in2, out;
    private String name;
    private boolean inDebuggerMode;

    /**Constructors for the BinaryGate class.
     *
     * @param in1 - the first input bit stream
     * @param in2 - the second input bit stream
     * @param out - the output stream
     * @param name - the name of the gate
     * @param inDebuggerMode - boolean to specify if additional debug information should be shown
     */
    public BinaryGate(BitStream in1, BitStream in2, BitStream out, String name, boolean inDebuggerMode) {
        this.in1 = in1;
        this.in2 = in2;
        this.out = out;
        this.name = name;
        this.inDebuggerMode = inDebuggerMode;

        this.in1.addNewEndpoint(this);
        this.in2.addNewEndpoint(this);
        this.out.addNewEndpoint(this);
    }

    /**Getters for all the attributes of the class.
     */
    public BitStream getIn1() {
        return in1;
    }

    public BitStream getIn2() {
        return in2;
    }

    public BitStream getOut() {
        return out;
    }

    public String getName() {
        return name;
    }

    public boolean isInDebuggerMode() {
        return inDebuggerMode;
    }

    /**Setters for all the attributes of the class.
     */
    public void setIn1(BitStream in1) {
        this.in1 = in1;
    }

    public void setIn2(BitStream in2) {
        this.in2 = in2;
    }

    public void setOut(BitStream out) {
        this.out = out;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInDebuggerMode(boolean inDebuggerMode) {
        this.inDebuggerMode = inDebuggerMode;
    }

    /**Abstract class used by the evaluate() method to calculate the actual logic output.
     * This method has to be defined by all the logic gates.
     *
     * @return - a boolean array corresponding to the computed logic result
     */
    public abstract boolean[] compute();

    /**Evaluate the logic gate. This also includes checking if evaluation is possible, setting the
     * source of the out stream to this logic gate and adding all neighbours to the execution queue.
     *
     * @param queue - the execution queue
     */
    @Override
    public void evaluate(List<Node> queue) {
        this.checkIfSizesMatch();

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
        for (int i = 0; i < newOutData.length; i++) {
            if (newOutData[i] != this.out.getData()[i]) {
                return true;
            }
        }
        return false;
    }

    /**Method to check if the sizes of the inputs and the output are correct.
     * Throws BitStreamInputSizeMismatch if not.
     */
    @Override
    public void checkIfSizesMatch() {
        if (this.getIn1().getSize() != this.getIn2().getSize()) {
            throw new BitStreamInputSizeMismatch("Input size mismatch at: " + this.toString());
        }
        else if (this.getIn1().getSize() != this.getOut().getSize()) {
            throw new BitStreamInputSizeMismatch("Input size mismatch at: " + this.toString());
        }
    }

    /**Method to check if the sources of the out stream are consistent. In particular, check if the source
     * of the out stream is the current logic gate and if not check if it is possible to merge the old source
     * with the current logic gate.
     *
     * @param newOut - the newly calculated output of the branch, to be compared with the old value of the
     * out stream. If the source is not the current logic gate, newOut is compared with old BitStream and
     * if they differ at any bit, the method throws an InconsistentBitStreamSources error.
     */
    @Override
    public void checkIfSourceIsConsistent(boolean[] newOut) {
        if (this.out.getSource() != null && this.out.getSource() != this) {
            for (int i = 0; i < newOut.length; i++) {
                if (newOut[i] != this.out.getData()[i]) {
                    throw new InconsistentBitStreamSources("Inconsistency detected at " + this.toString() +
                            "-> " + BitInformationConverter.convertBoolToBits(newOut) + " and " +
                            BitInformationConverter.convertBoolToBits(this.out.getData()));
                }
            }
        }
    }

    /**Add all neighbours of this logic gate, that are connected to it with the out BitStream, to
     * the execution queue.
     *
     * @param queue - the execution queue
     */
    public void addNeighboursToQueue(List<Node> queue) {
        queue.addAll(this.out.getAllNeighbours(this));
    }

    /**Method to define what should be displayed in the debug mode.
     */
    @Override
    public void debug() {
        String msg = "Evaluating " + this.name + ":\n"
                + "\tInputs: " + BitInformationConverter.convertBoolToBits(this.in1.getData()) + ", "
                    + BitInformationConverter.convertBoolToBits(this.in2.getData()) + "\n"
                + "\tOutput: " + BitInformationConverter.convertBoolToBits(this.out.getData());
        System.out.println(msg);
    }
}
