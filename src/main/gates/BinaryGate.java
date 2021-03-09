package main.gates;

import main.BitStream;
import main.Node;
import main.exceptions.BitStreamInputSizeMismatch;
import main.exceptions.InconsistentBitStreamSources;
import main.utils.BitInformationConverter;

import java.util.ArrayList;
import java.util.List;

public abstract class BinaryGate extends Gate {

    /**Abstract class to represent binary logic gates (ie logic gates with two inputs).
     *
     * @param in1 - the first input bit stream
     * @param in2 - the second input bit stream
     */
    private BitStream in1, in2;

    /**Constructor for the BinaryGate class.
     *
     * @param name - the name of the gate
     * @param inDebuggerMode - boolean to specify if additional debug information should be shown
     */
    public BinaryGate(BitStream in1, BitStream in2, BitStream out, String name, boolean inDebuggerMode) {
        super(out, name, inDebuggerMode);
        this.in1 = in1;
        this.in2 = in2;

        this.in1.addNewEndpoint(this);
        this.in2.addNewEndpoint(this);

        List<Node> queue = new ArrayList<>();
        queue.add(this);
        this.evaluate(queue);
    }

    /**Getters for all the attributes of the class.
     */
    public BitStream getIn1() {
        return in1;
    }

    public BitStream getIn2() {
        return in2;
    }

    /**Setters for all the attributes of the class.
     */
    public void setIn1(BitStream in1) {
        this.in1 = in1;
    }

    public void setIn2(BitStream in2) {
        this.in2 = in2;
    }

    /**Method to check if the sizes of the inputs and the output are correct.
     * Throws BitStreamInputSizeMismatch if not.
     */
    @Override
    public void checkIfSizesMatch() {
        if (this.in1.getSize() != this.in2.getSize()) {
            throw new BitStreamInputSizeMismatch("Input size mismatch at: " + this.toString());
        }
        else if (this.in1.getSize() != this.getOut().getSize()) {
            throw new BitStreamInputSizeMismatch("Input size mismatch at: " + this.toString());
        }
    }

    /**Method to define what should be displayed in the debug mode.
     */
    @Override
    public void debug() {
        String msg = "Evaluating " + this.getName() + ":\n"
                + "\tInputs: " + BitInformationConverter.convertBoolToBits(this.in1.getData()) + ", "
                    + BitInformationConverter.convertBoolToBits(this.in2.getData()) + "\n"
                + "\tOutput: " + BitInformationConverter.convertBoolToBits(this.getOut().getData());
        System.out.println(msg);
    }

    /**Override the default toString method.
     *
     * @return - a String representation of this
     */
    @Override
    public String toString() {
        return "<" + this.in1 + ", " + this.in2 + ", " + this.getOut() + ">";
    }
}
