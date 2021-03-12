package main.gates;

import main.BitStream;
import main.exceptions.BitStreamInputSizeMismatch;
import main.utils.BitInformationConverter;

import java.util.List;

public abstract class MultiInputGate extends Gate {

    /**Class representing gates with multiple inputs.
     *
     * @param in - the list of input BitStreams
     */
    private List<BitStream> in;

    /**Constructor for the MultiInputGate class.
     *
     * @param in - the list of input BitStreams
     * @param out - the output BitStream
     * @param name - the name of the gate
     * @param inDebuggerMode - boolean to specify if the gate is evaluated in the debug mode
     */
    public MultiInputGate(List<BitStream> in, BitStream out, String name, boolean inDebuggerMode) {
        super(out, name, inDebuggerMode);
        this.in = in;

        for (BitStream inStream : this.in) {
            inStream.addNewEndpoint(this);
        }

        this.setup();
    }

    /**Getters for all the attributes of the class.
     */
    public List<BitStream> getIn() {
        return in;
    }

    /**Setters for all the attributes of the class.
     */
    public void setIn(List<BitStream> in) {
        for (BitStream inStream : this.in) {
            inStream.removeEndpoint(this);
        }

        this.in = in;
        for (BitStream inStream : this.in) {
            inStream.addNewEndpoint(this);
        }

        this.setup();
    }

    /**Method to check if all the sizes of the input streams and the output streams match.
     */
    @Override
    public void checkIfSizesMatch() {
        for (BitStream inStream : this.in) {
            if (inStream.getSize() != this.getOut().getSize()) {
                throw new BitStreamInputSizeMismatch(this);
            }
        }
    }

    /**Method to show additional debug information when the gate is evaluated.
     */
    @Override
    public void debug() {
        String msg = "Evaluating " + this.getName() + ":\n\tInputs:\n";
        for (BitStream inStream : this.in) {
            msg += "\t\t" + BitInformationConverter.convertBoolToBits(inStream.getData()) + "\n";
        }
        msg += "\tOutput: " + BitInformationConverter.convertBoolToBits(this.getOut().getData());
        System.out.println(msg);
    }

    /**Override the default toString method.
     *
     * @return - a String representation of this
     */
    @Override
    public String toString() {
        return "<" +this.getName() + ", " + this.in + ", " + this.getOut() + ">";
    }
}
