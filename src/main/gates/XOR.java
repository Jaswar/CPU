package main.gates;

import main.BitStream;
import main.Node;

import java.util.List;

public class XOR extends BinaryGate {

    /**Constructors for the XOR gate class
     *
     * @param name - the name of the gate
     * @param inDebuggerMode - a boolean to specify if additional debug information should be displayed
     */
    public XOR(String name, boolean inDebuggerMode) {
        super(name, inDebuggerMode);
    }

    public XOR(String name) {
        super(name, false);
    }

    public XOR(boolean inDebuggerMode) {
        super("XOR GATE", inDebuggerMode);
    }

    public XOR() {
        super("XOR GATE", false);
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
            if (this.getIn1().getData()[i] || this.getIn2().getData()[i]) {
                newOutData[i] = true;
            }
            if (this.getIn1().getData()[i] && this.getIn2().getData()[i]) {
                newOutData[i] = false;
            }
        }

        return newOutData;
    }

    /**Override the default toString method
     *
     * @return - a String representation of this
     */
    @Override
    public String toString() {
        return "XOR" + super.toString();
    }

}
