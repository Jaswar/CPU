package main.gates;

import main.BitStream;

public class NOT extends UnaryGate {

    /**Constructors for the NOT gate class
     *
     * @param name - the name of the gate
     * @param inDebuggerMode - a boolean to specify if additional debug information should be displayed
     */
    public NOT(String name, boolean inDebuggerMode) {
        super(name, inDebuggerMode);
    }

    public NOT(String name) {
        super(name, false);
    }

    public NOT(boolean inDebuggerMode) {
        super("NOT GATE", inDebuggerMode);
    }

    public NOT() {
        super("NOT GATE", false);
    }

    /**Define the abstract compute method from UnaryGate class. Used to perform the
     * evaluation of the gate (see UnaryGate.evaluate() method for usage).
     *
     * @return - the data computed by the logic gate from the input streams (the data is
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

    /**Override the default toString method
     *
     * @return - a String representation of this
     */
    @Override
    public String toString() {
        return "NOT" + super.toString();
    }
}
