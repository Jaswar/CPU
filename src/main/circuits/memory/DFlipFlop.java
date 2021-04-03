package main.circuits.memory;

import main.BitStream;
import main.circuits.Circuit;

public class DFlipFlop implements Circuit {

    private BitStream D, enable, Q, notQ;
    private String name;
    private boolean inDebuggerMode;
    private int debugDepth;

    public DFlipFlop(BitStream D, BitStream enable, BitStream Q, BitStream notQ,
                     String name, boolean inDebuggerMode, int debugDepth) {
        this.D = D;
        this.enable = enable;
        this.Q = Q;
        this.notQ = notQ;

        this.name = name;
        this.inDebuggerMode = inDebuggerMode;
        this.debugDepth = debugDepth;

        this.build();
    }

    public DFlipFlop(BitStream D, BitStream enable, BitStream Q, BitStream notQ,
                     String name) {
        this(D, enable, Q, notQ, name, false, 0);
    }

    public DFlipFlop(BitStream D, BitStream enable, BitStream Q, BitStream notQ,
                     boolean inDebuggerMode, int debugDepth) {
        this(D, enable, Q, notQ, "DFlipFlop", inDebuggerMode, debugDepth);
    }

    public DFlipFlop(BitStream D, BitStream enable, BitStream Q, BitStream notQ) {
        this(D, enable, Q, notQ, "DFlipFlop", false, 0);
    }

    public BitStream getD() {
        return D;
    }

    public BitStream getEnable() {
        return enable;
    }

    public BitStream getQ() {
        return Q;
    }

    public BitStream getNotQ() {
        return notQ;
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

    }
}
