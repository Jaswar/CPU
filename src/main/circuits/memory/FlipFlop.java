package main.circuits.memory;

import main.BitStream;
import main.circuits.Circuit;

public abstract class FlipFlop implements Circuit {

    private BitStream input, clock, enable, preset, clear, Q, notQ;
    private boolean risingEdge;
    private String name;
    private boolean inDebuggerMode;
    private int debugDepth;

    public FlipFlop(BitStream input, BitStream clock, BitStream enable, BitStream preset, BitStream clear,
                    BitStream Q, BitStream notQ, boolean risingEdge,
                    String name, boolean inDebuggerMode, int debugDepth) {
        this.input = input;
        this.clock = clock;
        this.enable = enable;
        this.preset = preset;
        this.clear = clear;
        this.Q = Q;
        this.notQ = notQ;

        this.risingEdge = risingEdge;
        this.name = name;
        this.inDebuggerMode = inDebuggerMode;
        this.debugDepth = debugDepth;
    }

    public BitStream getInput() {
        return input;
    }

    public BitStream getClock() {
        return clock;
    }

    public BitStream getEnable() {
        return enable;
    }

    public BitStream getPreset() {
        return preset;
    }

    public BitStream getClear() {
        return clear;
    }

    public BitStream getQ() {
        return Q;
    }

    public BitStream getNotQ() {
        return notQ;
    }

    public boolean isRisingEdge() {
        return risingEdge;
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
}
