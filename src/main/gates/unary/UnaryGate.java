package main.gates.unary;

import main.BitStream;
import main.exceptions.BitStreamInputSizeMismatch;
import main.gates.Gate;
import main.utils.DataConverter;

public abstract class UnaryGate extends Gate {

    /**Abstract class to represent unary logic gates (ie logic gates with one input).
     *
     * @param in - the input bit stream
     */
    private BitStream in;

    /**Constructor for the UnaryGate class.
     *
     * @param in - the input bit stream
     * @param out - the output bit stream
     * @param name - the name of the gate
     * @param inDebuggerMode - boolean to specify if additional debug information should be shown
     */
    public UnaryGate(BitStream in, BitStream out, String name, boolean inDebuggerMode) {
        super(out, name, inDebuggerMode);
        this.in = in;
        this.in.addNewEndpoint(this);

        this.setup();
    }

    /**Getters for all the attributes.
     */
    public BitStream getIn() {
        return in;
    }

    /**Method to check if the sizes of the inputs and the output are correct.
     * Throws BitStreamInputSizeMismatch if not.
     */
    @Override
    public void checkIfSizesMatch() {
        if (this.in == null || this.getOut() == null) {
            return;
        }
        if (this.in.getSize() != this.getOut().getSize()) {
            throw new BitStreamInputSizeMismatch(this);
        }
    }

    /**Method to define what should be displayed in the debug mode.
     */
    @Override
    public void debug() {
        String msg = "Evaluating " + this.getName() + ":\n"
                + "\tInput: " + DataConverter.convertBoolToBin(this.in.getData()) + "\n"
                + "\tOutput: " + DataConverter.convertBoolToBin(this.getOut().getData());
        System.out.println(msg);
    }

    /**Override the default toString method.
     *
     * @return - a String representation of this
     */
    @Override
    public String toString() {
        return "<" + this.getName() + ", " + this.in + ", " + this.getOut() + ">";
    }
}
