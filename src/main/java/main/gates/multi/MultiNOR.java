package main.gates.multi;

import main.BitStream;

import java.util.List;

public class MultiNOR extends MultiInputGate {

    /**Constructors for the MultiNOR gate class.
     *
     * @param in - a list of input BitStreams
     * @param out - the output BitStream
     * @param name - the name of the gate
     * @param inDebuggerMode - boolean to specify if the gate should be evaluated in debug mode
     */
    public MultiNOR(List<BitStream> in, BitStream out, String name, boolean inDebuggerMode) {
        super(in, out, name, inDebuggerMode);
    }

    public MultiNOR(List<BitStream> in, BitStream out, String name) {
        super(in, out, name, false);
    }

    public MultiNOR(List<BitStream> in, BitStream out, boolean inDebuggerMode) {
        super(in, out, "MultiNOR", inDebuggerMode);
    }

    public MultiNOR(List<BitStream> in, BitStream out) {
        super(in, out, "MultiNOR", false);
    }

    /**Define the abstract compute method. This should evaluate NOR operation
     * on the inputs and return the result to an array.
     *
     * @return - the boolean array computed by applying NOR on all inputs
     */
    @Override
    public boolean[] compute() {
        boolean[] newOutData = new boolean[this.getOut().getSize()];

        for (int i = 0; i < this.getOut().getSize(); i++) {
            newOutData[i] = true;
            for (BitStream inStream : this.getIn()) {
                if (inStream.getData()[i]) {
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
        return "MultiNOR" + super.toString();
    }

}
