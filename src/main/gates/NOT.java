package main.gates;

import main.BitStream;

public class NOT extends UnaryGate {

    /**Constructors for the NOT gate class
     *
     * @param in - the input to the logic gate
     * @param out - the output from the logic gate
     * @param name - the name of the gate
     * @param inDebuggerMode - a boolean to specify if additional debug information should be displayed
     */
    public NOT(BitStream in, BitStream out, String name, boolean inDebuggerMode) {
        super(in, out, name, inDebuggerMode);
    }

    public NOT(BitStream in, BitStream out, String name) {
        super(in, out, name, false);
    }

    public NOT(BitStream in, BitStream out, boolean inDebuggerMode) {
        super(in, out, "NOT GATE", inDebuggerMode);
    }

    public NOT(BitStream in, BitStream out) {
        super(in, out, "NOT GATE", false);
    }

    /**Define the abstract compute method from UnaryGate class. Used to perform the
     * evaluation of the gate (see UnaryGate.evaluate() method for usage).
     *
     * @return - the data computed by the logic gate from the input stream (the data is
     * not automatically forwarded to the out stream)
     */
    @Override
    public boolean[] compute() {
        boolean[] newOutData = new boolean[this.getIn().getSize()];

        for (int i = 0; i < newOutData.length; i++) {
            newOutData[i] = !this.getIn().getData()[i];
        }

        return newOutData;
    }

    /**Override the default toString method.
     *
     * @return - a String representation of this
     */
    @Override
    public String toString() {
        return "NOT" + super.toString();
    }
}
