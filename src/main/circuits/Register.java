package main.circuits;

import main.BitStream;
import main.gates.TriState;

public class Register implements Circuit {

    private BitStream input, output, regIn, regOut;
    private String name;
    private boolean inDebuggerMode;
    private int debugDepth;

    /**Constructors for the Register class.
     *
     * @param input - the input BitStream to the register
     * @param output - the output from the register
     * @param regIn - control line to tell if the register should read input
     * @param regOut - control line to tell if the register should output data
     * @param name - the name of the register
     * @param inDebuggerMode - boolean to specify if the register is in debug mode
     * @param debugDepth - integer to specify the depth of debugging
     */
    public Register(BitStream input, BitStream output, BitStream regIn, BitStream regOut,
                    String name, boolean inDebuggerMode, int debugDepth) {
        this.input = input;
        this.output = output;
        this.regIn = regIn;
        this.regOut = regOut;

        this.name = name;
        this.inDebuggerMode = inDebuggerMode;
        this.debugDepth = debugDepth;

        this.build();
    }

    public Register(BitStream input, BitStream output, BitStream regIn, BitStream regOut,
                    String name) {
        this(input, output, regIn, regOut, name, false, 0);
    }

    public Register(BitStream input, BitStream output, BitStream regIn, BitStream regOut,
                    boolean inDebuggerMode, int debugDepth) {
        this(input, output, regIn, regOut, "Register", inDebuggerMode, debugDepth);
    }

    public Register(BitStream input, BitStream output, BitStream regIn, BitStream regOut) {
        this(input, output, regIn, regOut, "Register", false, 0);
    }

    public BitStream getinput() {
        return input;
    }

    /**Getters for all the attributes of the class.
     */
    public BitStream getOutput() {
        return output;
    }

    public BitStream getRegIn() {
        return regIn;
    }

    public BitStream getRegOut() {
        return regOut;
    }

    public String getName() {
        return name;
    }

    public boolean isInDebuggerMode() {
        return inDebuggerMode;
    }

    public int getDebugDepth() {
        return debugDepth;
    }

    /**Setters for some of the attributes. Setting BitStreams is not possible.
     */
    public void setName(String name) {
        this.name = name;
    }

    public void setInDebuggerMode(boolean inDebuggerMode) {
        this.inDebuggerMode = inDebuggerMode;
    }

    public void setDebugDepth(int debugDepth) {
        this.debugDepth = debugDepth;
    }

    /**Define the build method to construct the register as described in the documentation.
     */
    @Override
    public void build() {
        boolean debugGates = this.debugDepth > 0 ? this.inDebuggerMode : false;
        int size = this.input.getSize();

        BitStream dLatchQ = new BitStream(size);
        BitStream dLatchNotQ = new BitStream(size);

        DLatch mainDLatch = new DLatch(this.input, this.regIn, dLatchQ, dLatchNotQ, "mainDLatch", debugGates, this.debugDepth - 1);

        TriState outTriState = new TriState(dLatchQ, this.regOut, this.output, "outTriState", debugGates);
    }
}
