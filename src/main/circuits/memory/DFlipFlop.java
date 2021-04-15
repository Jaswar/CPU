package main.circuits.memory;

import main.BitStream;
import main.circuits.Circuit;
import main.circuits.Multiplexer;
import main.control.Input;
import main.control.Splitter;
import main.gates.binary.NAND;
import main.gates.binary.NOR;
import main.gates.multi.MultiNAND;
import main.gates.multi.MultiNOR;
import main.gates.unary.NOT;
import main.utils.ProcessRunner;

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
        inputMuxInputList.addAll(List.of(this.getQ(), this.getInput()));
        BitStream muxOut = new BitStream(size);
        Multiplexer inputMux = new Multiplexer(inputMuxInputList, this.getEnable(), muxOut,
                "inputMux in " + this.getName(), debugGates, this.getDebugDepth() - 1);

        BitStream preparedClock = this.getClock();
        if (this.isRisingEdge()) {
            BitStream clockNotOut = new BitStream(1);
            NOT clockNot = new NOT(this.getClock(), clockNotOut, "clockNot in " + this.getName(), debugGates);
            preparedClock = clockNotOut;
        }

        List<BitStream> clockSplitInputList = new ArrayList<>();
        clockSplitInputList.addAll(List.of(preparedClock));
        BitStream clockSplitOut = new BitStream(size);
        List<BitStream> clockSplitOutList = new ArrayList<>();
        clockSplitOutList.addAll(List.of(clockSplitOut));

        Splitter clockSplitter = new Splitter(clockSplitInputList, clockSplitOutList, "clockSplitter in "  + this.getName(), debugGates);

        BitStream muxOutNotOutput = new BitStream(size);
        NOT muxOutNot = new NOT(muxOut, muxOutNotOutput, "muxOutNot in " + this.getName(), debugGates);

        BitStream nand1Out = new BitStream(size);
        NAND nand1 = new NAND(muxOut, clockSplitOut, nand1Out, "nand1 in " + this.getName(), debugGates);

        BitStream nand5Out = new BitStream(size);
        NAND nand5 = new NAND(muxOutNotOutput, clockSplitOut, nand5Out, "nand5 in " + this.getName(), debugGates);

        List<BitStream> clearSplitInputList = new ArrayList<>();
        clearSplitInputList.addAll(List.of(this.getClear()));
        BitStream clearSplitOut = new BitStream(size);
        List<BitStream> clearSplitOutList = new ArrayList<>();
        clearSplitOutList.addAll(List.of(clearSplitOut));

        Splitter clearSplitter = new Splitter(clearSplitInputList, clearSplitOutList, "clearSplitter in " + this.getName(), debugGates);

        BitStream clearNotOutput = new BitStream(size);
        NOT clearNot = new NOT(clearSplitOut, clearNotOutput, "clearNot in " + this.getName(), debugGates);

        BitStream presetNotOutput = new BitStream(size);
        NOT presetNot = new NOT(this.getPreset(), presetNotOutput, "presetNot in " + this.getName(), debugGates);

        BitStream nand2Out = new BitStream(size);
        BitStream nand6Out = new BitStream(size);

        List<BitStream> nand6InputList = new ArrayList<>();
        nand6InputList.addAll(List.of(nand2Out, nand5Out, clearNotOutput));
        MultiNAND nand6 = new MultiNAND(nand6InputList, nand6Out, "nand6 in " + this.getName(), debugGates);

        List<BitStream> nand2InputList = new ArrayList<>();
        nand2InputList.addAll(List.of(nand1Out, presetNotOutput, nand6Out));
        MultiNAND nand2 = new MultiNAND(nand2InputList, nand2Out, "nand2 in " + this.getName(), debugGates);


        BitStream flipNotOutput = new BitStream(size);
        NOT flipNot = new NOT(clockSplitOut, flipNotOutput, "flipNot in " + this.getName(), debugGates);

        BitStream nand3Out = new BitStream(size);
        NAND nand3 = new NAND(nand2Out, flipNotOutput, nand3Out, "nand3 in " + this.getName(), debugGates);

        BitStream nand7Out = new BitStream(size);
        NAND nand7 = new NAND(flipNotOutput, nand6Out, nand7Out, "nand7 in " + this.getName(), debugGates);

        List<BitStream> nand8InputList = new ArrayList<>();
        nand8InputList.addAll(List.of(this.getQ(), nand7Out, clearNotOutput));
        MultiNAND nand8 = new MultiNAND(nand8InputList, this.getNotQ(), "nand8 in " + this.getName(), debugGates);

        List<BitStream> nand4InputList = new ArrayList<>();
        nand4InputList.addAll(List.of(presetNotOutput, nand3Out, this.getNotQ()));
        MultiNAND nand4 = new MultiNAND(nand4InputList, this.getQ(), "nand4 in " + this.getName(), debugGates);

    }
}
