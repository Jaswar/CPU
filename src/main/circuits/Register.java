package main.circuits;

import main.BitStream;
import main.gates.TriState;

public class Register implements Circuit {

    private BitStream input, output, regIn, regOut;
    private String name;
    private boolean inDebuggerMode;
    private int debugDepth;

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

    public void setName(String name) {
        this.name = name;
    }

    public void setInDebuggerMode(boolean inDebuggerMode) {
        this.inDebuggerMode = inDebuggerMode;
    }

    public void setDebugDepth(int debugDepth) {
        this.debugDepth = debugDepth;
    }

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
