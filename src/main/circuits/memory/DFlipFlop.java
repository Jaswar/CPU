package main.circuits.memory;

import main.BitStream;
import main.circuits.Circuit;
import main.circuits.Multiplexer;
import main.control.Splitter;
import main.gates.binary.NOR;
import main.gates.multi.MultiNOR;
import main.gates.unary.NOT;

import java.util.ArrayList;
import java.util.List;

public class DFlipFlop extends FlipFlop {

    /**Constructors for the D Flip-Flop class.
     *
     * @param D - the input BitStream, often called D
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
    public DFlipFlop(BitStream D, BitStream clock, BitStream enable, BitStream preset, BitStream clear,
                     BitStream Q, BitStream notQ, boolean risingEdge,
                     String name, boolean inDebuggerMode, int debugDepth) {
        super(D, clock, enable, preset, clear, Q, notQ, risingEdge, name, inDebuggerMode, debugDepth);

        this.build();
    }

    public DFlipFlop(BitStream D, BitStream clock, BitStream enable, BitStream preset, BitStream clear,
                     BitStream Q, BitStream notQ, boolean risingEdge,
                     String name) {
        this(D, clock, enable, preset, clear, Q, notQ, risingEdge, name, false, 0);
    }

    public DFlipFlop(BitStream D, BitStream clock, BitStream enable, BitStream preset, BitStream clear,
                     BitStream Q, BitStream notQ, boolean risingEdge,
                     boolean inDebuggerMode, int debugDepth) {
        this(D, clock, enable, preset, clear, Q, notQ, risingEdge, "DFlipFlop", inDebuggerMode, debugDepth);
    }

    public DFlipFlop(BitStream D, BitStream clock, BitStream enable, BitStream preset, BitStream clear,
                     BitStream Q, BitStream notQ, boolean risingEdge) {
        this(D, clock, enable, preset, clear, Q, notQ, risingEdge, "DFlipFlop", false, 0);
    }

    /**Define the build method to construct the circuit as described in the documentation.
     */
    @Override
    public void build() {
        boolean debugGates = this.getDebugDepth() > 0 ? this.isInDebuggerMode() : false;
        int size = this.getInput().getSize();

        List<BitStream> inputMuxInputList = new ArrayList<>();
        inputMuxInputList.addAll(List.of(this.getInput(), this.getQ()));
        BitStream muxOut = new BitStream(size);
        Multiplexer inputMux = new Multiplexer(inputMuxInputList, this.getEnable(), muxOut,
                "inputMux", debugGates, this.getDebugDepth() - 1);

        BitStream preparedClock = this.getClock();
        if (this.isRisingEdge()) {
            BitStream clockNotOut = new BitStream(1);
            NOT clockNot = new NOT(this.getClock(), clockNotOut, "clockNot", debugGates);
            preparedClock = clockNotOut;
        }

        List<BitStream> clockSplitInputList = new ArrayList<>();
        clockSplitInputList.addAll(List.of(preparedClock));
        BitStream clockSplitOut = new BitStream(size);
        List<BitStream> clockSplitOutList = new ArrayList<>();
        clockSplitOutList.addAll(List.of(clockSplitOut));

        Splitter clockSplitter = new Splitter(clockSplitInputList, clockSplitOutList, "clockSplitter", debugGates);

        BitStream muxOutNotOutput = new BitStream(size);
        NOT muxOutNot = new NOT(muxOut, muxOutNotOutput, "muxOutNot", debugGates);

    }
}
