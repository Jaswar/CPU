package main.gates.multi;

import main.BitStream;

import java.util.List;

public class MultiNAND extends MultiInputGate {

    /**Constructors for the MultiNAND gate class.
     *
     * @param in - a list of input BitStreams
     * @param out - the output BitStream
     * @param name - the name of the gate
     * @param inDebuggerMode - boolean to specify if the gate should be evaluated in debug mode
     */
    public MultiNAND(List<BitStream> in, BitStream out, String name, boolean inDebuggerMode) {
        super(in, out, name, inDebuggerMode);
    }

    public MultiNAND(List<BitStream> in, BitStream out, String name) {
        super(in, out, name, false);
    }

    public MultiNAND(List<BitStream> in, BitStream out, boolean inDebuggerMode) {
        super(in, out, "MultiNAND", inDebuggerMode);
    }

    public MultiNAND(List<BitStream> in, BitStream out) {
        super(in, out, "MultiNAND", false);
    }

    /**Define the abstract compute method. This should evaluate NAND operation
     * on the inputs and return the result to an array.
     *
     * @return - the boolean array computed by applying NAND on all inputs
     */
    @Override
    public boolean[] compute() {
        boolean[] newOutData = new boolean[this.getOut().getSize()];

        for (int i = 0; i < this.getOut().getSize(); i++) {
            newOutData[i] = false;
            for (BitStream inStream : this.getIn()) {
                if (!inStream.getData()[i]) {
                    newOutData[i] = true;
                    break;
                }
            }
        }

        return newOutData;
    }

    /**Override the toString method.
     *
     * @return - a String representation of this
     */
    @Override
    public String toString() {
        return "MultiNAND" + super.toString();
    }
}
