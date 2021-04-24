package main.circuits;

import main.BitStream;

public class IAG implements Circuit {

    private final BitStream input, output, PCIn, PCOut;
    private final String name;
    private final boolean inDebuggerMode;
    private final int debugDepth;

    private Register programCounter;

    /**Constructors for the Instruction Address Generator class.
     *
     * @param input - the input to the IAG (most likely the bus)
     * @param output - the output from the IAG (most likely the bus as well)
     * @param PCIn - control if the PC should take input
     * @param PCOut - control if the PC should output its result
     * @param name - the name of the circuit
     * @param inDebuggerMode - boolean to specify if the circuit is in debug mode
     * @param debugDepth - how deep should debugging go
     */
    public IAG(BitStream input, BitStream output, BitStream PCIn, BitStream PCOut,
               String name, boolean inDebuggerMode, int debugDepth) {
        this.input = input;
        this.output = output;
        this.PCIn = PCIn;
        this.PCOut = PCOut;

        this.name = name;
        this.inDebuggerMode = inDebuggerMode;
        this.debugDepth = debugDepth;

        this.build();
    }

    public IAG(BitStream input, BitStream output, BitStream PCIn, BitStream PCOut,
               String name) {
        this(input, output, PCIn, PCOut, name, false, 0);
    }

    public IAG(BitStream input, BitStream output, BitStream PCIn, BitStream PCOut,
               boolean inDebuggerMode, int debugDepth) {
        this(input, output, PCIn, PCOut, "IAG", false, 0);
    }

    public IAG(BitStream input, BitStream output, BitStream PCIn, BitStream PCOut) {
        this(input, output, PCIn, PCOut, "IAG", false, 0);
    }

    /**Method to return the current state of the IAG.
     *
     * @return - the status of the IAG as String
     */
    public String requestStatus() {
        String status = "IAG (" + this.name + "):\n";
        status += "PCIn: " + this.PCIn.getData()[0] + "\tPCOut: " + this.PCOut.getData()[0] + "\n";
        status += this.programCounter.requestStatus();
        return status;
    }

    public Register getPC() {
        return this.programCounter;
    }

    /**Build the circuit as defined in the documentation.
     */
    @Override
    public void build() {
        boolean debugGates = this.debugDepth > 0 ? this.inDebuggerMode : false;
        int size = this.input.getSize();

        BitStream enable = new BitStream(1);
        enable.setData(new boolean[]{true});
        this.programCounter = new Register(this.input, this.output, this.PCIn, this.PCOut, enable,
                "PC", debugGates, this.debugDepth - 1);
    }
}
