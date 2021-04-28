package main.circuits.memory;

import main.BitStream;
import main.gates.binary.AND;
import main.gates.binary.OR;
import main.gates.unary.NOT;

public class TFlipFlop extends FlipFlop {

    /**Constructors for the T Flip-Flop class.
     *
     * @param T - the input BitStream, often called T
     * @param clock - the BitStream coming from the clock controlling the data in the flip-flop
     * @param enable - the BitStream to control if the flip-flop should react to the clock
     * @param preset - BitStream to preset the flip flop to some value
     * @param clear - BitStream to clear the data hold by the flip flop
     * @param Q - the output
     * @param notQ - the complement of the output
     * @param risingEdge - boolean to specify if the flip-flop is rising or falling edge triggered
     * @param name - the name of the flip-flop
     * @param inDebuggerMode - boolean to tell if the circuit is in debug mode
     * @param debugDepth - how deep should debugging go
     */
    public TFlipFlop(BitStream T, BitStream clock, BitStream enable, BitStream preset, BitStream clear,
                     BitStream Q, BitStream notQ, boolean risingEdge,
                     String name, boolean inDebuggerMode, int debugDepth) {
        super(T, clock, enable, preset, clear, Q, notQ, risingEdge, name, inDebuggerMode, debugDepth);

        this.build();
    }

    public TFlipFlop(BitStream T, BitStream clock, BitStream enable, BitStream preset, BitStream clear,
                     BitStream Q, BitStream notQ, boolean risingEdge,
                     String name) {
        this(T, clock, enable, preset, clear, Q, notQ, risingEdge, name, false, 0);
    }

    public TFlipFlop(BitStream T, BitStream clock, BitStream enable, BitStream preset, BitStream clear,
                     BitStream Q, BitStream notQ, boolean risingEdge,
                     boolean inDebuggerMode, int debugDepth) {
        this(T, clock, enable, preset, clear, Q, notQ, risingEdge, "TFlipFlop", inDebuggerMode, debugDepth);
    }

    public TFlipFlop(BitStream T, BitStream clock, BitStream enable, BitStream preset, BitStream clear,
                     BitStream Q, BitStream notQ, boolean risingEdge) {
        this(T, clock, enable, preset, clear, Q, notQ, risingEdge, "TFlipFlop", false, 0);
    }

    /**Define the build method to construct the circuit as described in the documentation.
     */
    @Override
    public void build() {
        boolean debugGates = this.getDebugDepth() > 0 ? this.isInDebuggerMode() : false;
        int size = this.getInput().getSize();

        BitStream tNotOut = new BitStream(size);
        NOT tNot = new NOT(this.getInput(), tNotOut, "tNot", debugGates);

        BitStream upperAndOut = new BitStream(size);
        AND upperAnd = new AND(tNotOut, this.getQ(), upperAndOut, "upperAnd", debugGates);

        BitStream lowerAndOut = new BitStream(size);
        AND lowerAnd = new AND(this.getInput(), this.getNotQ(), lowerAndOut, "lowerAnd", debugGates);

        BitStream orOut = new BitStream(size);
        OR mainOr = new OR(upperAndOut, lowerAndOut, orOut, "mainOr", debugGates);

        DFlipFlop dFlipFlop = new DFlipFlop(orOut, this.getClock(), this.getEnable(), this.getPreset(), this.getClear(),
                this.getQ(), this.getNotQ(), this.isRisingEdge(), "dFlipFlop", debugGates, this.getDebugDepth() - 1);
    }

}
