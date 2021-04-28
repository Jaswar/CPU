package main.gates.binary;

import main.BitStream;

public class AND extends BinaryGate {

    /**Constructors for the AND gate class
     *
     * @param in1 - the first input to the logic gate
     * @param in2 - the second input to the logic gate
     * @param out - the output from the logic gate
     * @param name - the name of the gate
     * @param inDebuggerMode - a boolean to specify if additional debug information should be displayed
     */
    public AND(BitStream in1, BitStream in2, BitStream out, String name, boolean inDebuggerMode) {
        super(in1, in2, out, name, inDebuggerMode);
    }

    public AND(BitStream in1, BitStream in2, BitStream out, String name) {
        super(in1, in2, out, name, false);
    }

    public AND(BitStream in1, BitStream in2, BitStream out, boolean inDebuggerMode) {
        super(in1, in2, out, "AND GATE", inDebuggerMode);
    }

    public AND(BitStream in1, BitStream in2, BitStream out) {
        super(in1, in2, out, "AND GATE", false);
    }

    /**Define the abstract compute method from BinaryGate class. Used to perform the
     * evaluation of the gate (see BinaryGate.evaluate() method for usage).
     *
     * @return - the data computed by the logic gate from the input streams (the data is
     * not automatically forwarded to the out stream)
     */
    @Override
    public boolean[] compute() {
        boolean[] newOutData = new boolean[this.getIn1().getSize()];

        for (int i = 0; i < newOutData.length; i++) {
            newOutData[i] = false;
            if (this.getIn1().getData()[i] && this.getIn2().getData()[i]) {
                newOutData[i] = true;
            }
        }

        return newOutData;
    }

    /**Override the default toString method.
     *
     * @return - a String representation of this
     */
    @Override
    public String toString() {
        return "AND" + super.toString();
    }

}
