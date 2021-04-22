package main.circuits;

import main.BitStream;
import main.circuits.memory.DFlipFlop;
import main.circuits.memory.DLatch;
import main.gates.TriState;
import main.utils.DataConverter;

public class Register implements Circuit {

    private BitStream input, output, regIn, regOut, enable;
    private String name;
    private boolean inDebuggerMode;
    private int debugDepth;

    private BitStream dFlipFlopQ;

    /**Constructors for the Register class.
     *
     * @param input - the input BitStream to the register
     * @param output - the output from the register
     * @param regIn - control line to tell if the register should read input
     * @param regOut - control line to tell if the register should output data
     * @param enable - control line to tell if the register is enabled
     * @param name - the name of the register
     * @param inDebuggerMode - boolean to specify if the register is in debug mode
     * @param debugDepth - integer to specify the depth of debugging
     */
    public Register(BitStream input, BitStream output, BitStream regIn, BitStream regOut, BitStream enable,
                    String name, boolean inDebuggerMode, int debugDepth) {
        this.input = input;
        this.output = output;
        this.regIn = regIn;
        this.regOut = regOut;
        this.enable = enable;

        this.name = name;
        this.inDebuggerMode = inDebuggerMode;
        this.debugDepth = debugDepth;

        this.build();
    }

    public Register(BitStream input, BitStream output, BitStream regIn, BitStream regOut, BitStream enable,
                    String name) {
        this(input, output, regIn, regOut, enable, name, false, 0);
    }

    public Register(BitStream input, BitStream output, BitStream regIn, BitStream regOut, BitStream enable,
                    boolean inDebuggerMode, int debugDepth) {
        this(input, output, regIn, regOut, enable,"Register", inDebuggerMode, debugDepth);
    }

    public Register(BitStream input, BitStream output, BitStream regIn, BitStream regOut, BitStream enable) {
        this(input, output, regIn, regOut, enable, "Register", false, 0);
    }

    /**Method to return the current state of the Register.
     *
     * @return - the status of the Register as String
     */
    public String requestStatus() {
        String status = this.name + ": " + DataConverter.convertBoolToBin(this.dFlipFlopQ.getData()) +
                " (" + DataConverter.convertBoolToUnsignedDec(this.dFlipFlopQ.getData()) + ", " +
                DataConverter.convertBoolToSignedDec(this.dFlipFlopQ.getData(), this.dFlipFlopQ.getSize()) + ")";
        return status;
    }

    /**Define the build method to construct the register as described in the documentation.
     */
    @Override
    public void build() {
        boolean debugGates = this.debugDepth > 0 ? this.inDebuggerMode : false;
        int size = this.input.getSize();

        dFlipFlopQ = new BitStream(size);
        BitStream dFlipFlopNotQ = new BitStream(size);

        DFlipFlop mainDFlipFlop = new DFlipFlop(this.input, this.regIn, this.enable, new BitStream(size), new BitStream(1),
                dFlipFlopQ, dFlipFlopNotQ, false, "mainDFlipFlop", debugGates, this.debugDepth - 1);

        TriState outTriState = new TriState(dFlipFlopQ, this.regOut, this.output, "outTriState in " + this.name, debugGates);
    }
}
