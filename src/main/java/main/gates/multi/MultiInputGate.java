package main.gates.multi;

import main.BitStream;
import main.exceptions.BitStreamInputSizeMismatch;
import main.gates.Gate;
import main.utils.DataConverter;

import java.util.List;

public abstract class MultiInputGate extends Gate {

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
            msg += "\t\t" + DataConverter.convertBoolToBin(inStream.getData()) + "\n";
        }
        msg += "\tOutput: " + DataConverter.convertBoolToBin(this.getOut().getData());
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
