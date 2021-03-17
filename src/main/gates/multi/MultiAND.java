package main.gates.multi;

import main.BitStream;

import java.util.List;

public class MultiAND extends MultiInputGate {

    /**Constructors for the MultiAND gate class.
     *
     * @param in - a list of input BitStreams
     * @param out - the output BitStream
     * @param name - the name of the gate
     * @param inDebuggerMode - boolean to specify if the gate should be evaluated in debug mode
     */
    public MultiAND(List<BitStream> in, BitStream out, String name, boolean inDebuggerMode) {
        super(in, out, name, inDebuggerMode);
    }

    public MultiAND(List<BitStream> in, BitStream out, String name) {
        super(in, out, name, false);
    }

    public MultiAND(List<BitStream> in, BitStream out, boolean inDebuggerMode) {
        super(in, out, "MultiAND", inDebuggerMode);
    }

    public MultiAND(List<BitStream> in, BitStream out) {
        super(in, out, "MultiAND", false);
    }

    /**Define the abstract compute method. This should evaluate AND operation
     * on the inputs and return the result to an array.
     *
     * @return - the boolean array computed by applying AND on all inputs
     */
    @Override
    public boolean[] compute() {
        boolean[] newOutData = new boolean[this.getOut().getSize()];

        for (int i = 0; i < this.getOut().getSize(); i++) {
            newOutData[i] = true;
            for (BitStream inStream : this.getIn()) {
                if (!inStream.getData()[i]) {
                    newOutData[i] = false;
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
        return "MultiAND" + super.toString();
    }
}
