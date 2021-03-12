package main.gates;

import main.BitStream;

import java.util.List;

public class MultiOR extends MultiInputGate {

    /**Constructors for the MultiOR gate class.
     *
     * @param in - the list of input BitStreams
     * @param out - the output BitStream
     * @param name - the name of the gate
     * @param inDebuggerMode - boolean to specify if the gate is in debug mode
     */
    public MultiOR(List<BitStream> in, BitStream out, String name, boolean inDebuggerMode){
        super(in, out, name, inDebuggerMode);
    }

    public MultiOR(List<BitStream> in, BitStream out, String name){
        super(in, out, name, false);
    }

    public MultiOR(List<BitStream> in, BitStream out, boolean inDebuggerMode){
        super(in, out, "MultiOR gate", inDebuggerMode);
    }

    public MultiOR(List<BitStream> in, BitStream out){
        super(in, out, "MultiOR gate", false);
    }

    /**Define the compute method which computes what should be on the output
     * provided the list of inputs to the gate.
     *
     * @return - OR evaluated boolean array computed from the input BitStreams
     */
    @Override
    public boolean[] compute() {
        boolean[] newOutData = new boolean[this.getOut().getSize()];

        for (int i = 0; i < this.getOut().getSize(); i++) {
            newOutData[i] = false;
            for (BitStream inStream : this.getIn()) {
                if (inStream.getData()[i]) {
                    newOutData[i] = true;
                    break;
                }
            }
        }

        return newOutData;
    }

    /**Override the toString method.
     *
     * @return - a String represenation of this
     */
    @Override
    public String toString() {
        return "MultiOR" + super.toString();
    }
}
