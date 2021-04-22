package main.circuits.memory;

import main.BitStream;
import main.circuits.Circuit;

public abstract class FlipFlop implements Circuit {

    private final BitStream input, clock, enable, preset, clear, Q, notQ;
    private final boolean risingEdge;
    private final String name;
    private final boolean inDebuggerMode;
    private final int debugDepth;

    /**Constructor for the abstract FlipFlop class, used to represent flip-flops.
     *
     * @param input - the main input to the flip-flop
     * @param clock - the clock input
     * @param enable - BitStream used to control if the flip-flop should be enabled or not
     * @param preset - BitStream to preset value of the flip-flop to 1
     * @param clear - BitStream to clear the value in the flip-flop
     * @param Q - the main output of the flip-flop
     * @param notQ - the complement of the main output
     * @param risingEdge - boolean to specify if the flip-flop should be rising or falling edge
     * @param name - the name of the flip-flop
     * @param inDebuggerMode - boolean to specify if the circuit is in debug mode
     * @param debugDepth - how deep should the debugging of gates go
     */
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

    /**Getters for all the attributes of the class.
     */
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
}
