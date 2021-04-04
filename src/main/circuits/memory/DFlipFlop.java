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

public class DFlipFlop implements Circuit {

    private BitStream D, clock, enable, Q, notQ;
    private boolean risingEdge;
    private String name;
    private boolean inDebuggerMode;
    private int debugDepth;

    /**Constructors for the D Flip-Flop class.
     *
     * @param D - the input BitStream, often called D
     * @param clock - the BitStream coming from the clock controlling the data in the flip-flop
     * @param enable - the BitStream to control if the flip-flop should react to the clock
     * @param Q - the output
     * @param notQ - the complement of the output
     * @param risingEdge - boolean to specify if the flip-flop is rising or falling edge triggered
     * @param name - the name of the flip-flop
     * @param inDebuggerMode - boolean to tell if the circuit is in debug mode
     * @param debugDepth - how deep should debugging go
     */
    public DFlipFlop(BitStream D, BitStream clock, BitStream enable, BitStream Q, BitStream notQ, boolean risingEdge,
                     String name, boolean inDebuggerMode, int debugDepth) {
        this.D = D;
        this.clock = clock;
        this.enable = enable;
        this.Q = Q;
        this.notQ = notQ;
        this.risingEdge = risingEdge;

        this.name = name;
        this.inDebuggerMode = inDebuggerMode;
        this.debugDepth = debugDepth;

        this.build();
    }

    public DFlipFlop(BitStream D, BitStream clock, BitStream enable, BitStream Q, BitStream notQ, boolean risingEdge,
                     String name) {
        this(D, clock, enable, Q, notQ, risingEdge, name, false, 0);
    }

    public DFlipFlop(BitStream D, BitStream clock, BitStream enable, BitStream Q, BitStream notQ, boolean risingEdge,
                     boolean inDebuggerMode, int debugDepth) {
        this(D, clock, enable, Q, notQ, risingEdge, "DFlipFlop", inDebuggerMode, debugDepth);
    }

    public DFlipFlop(BitStream D, BitStream clock, BitStream enable, BitStream Q, BitStream notQ, boolean risingEdge) {
        this(D, clock, enable, Q, notQ, risingEdge, "DFlipFlop", false, 0);
    }

    /**Getters for all the attributes.
     */
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

    /**Setters for some of the attributes. Setting BitStreams and
     * risingEdge property is not possible.
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

    /**Define the build method to construct the circuit as described in the documentation.
     */
    @Override
    public void build() {
        boolean debugGates = this.debugDepth > 0 ? this.inDebuggerMode : false;
        int size = this.D.getSize();

        List<BitStream> multiplexerInput = new ArrayList<>();
        multiplexerInput.addAll(List.of(this.Q, this.D));

        BitStream multiplexerOutput = new BitStream(size);
        Multiplexer enableMultiplexer = new Multiplexer(multiplexerInput, this.enable, multiplexerOutput,
                "enableMultiplexer", debugGates, this.debugDepth - 1);

        BitStream preparedClock = this.clock;
        if (risingEdge) {
            BitStream notOut = new BitStream(1);
            NOT not = new NOT(preparedClock, notOut, "edgeRevertNot", debugGates);
            preparedClock = notOut;
        }

        List<BitStream> clockInputList = new ArrayList<>();
        clockInputList.add(preparedClock);

        BitStream splitterOut = new BitStream(size);
        List<BitStream> clockOutputList = new ArrayList<>();
        clockOutputList.add(splitterOut);

        Splitter mainSplitter = new Splitter(clockInputList, clockOutputList, "mainSplitter", debugGates);

        BitStream nor0Out = new BitStream(size);
        BitStream nor1Out = new BitStream(size);
        BitStream nor2Out = new BitStream(size);
        BitStream nor3Out = new BitStream(size);

        List<BitStream> nor1InList = new ArrayList<>();
        nor1InList.addAll(List.of(nor0Out, splitterOut, nor2Out));

        NOR nor0 = new NOR(multiplexerOutput, nor1Out, nor0Out, "nor0", debugGates);
        MultiNOR nor1 = new MultiNOR(nor1InList, nor1Out, "nor1", debugGates);
        NOR nor2 = new NOR(splitterOut, nor3Out, nor2Out, "nor2", debugGates);
        NOR nor3 = new NOR(nor2Out, nor0Out, nor3Out, "nor3", debugGates);
        NOR nor4 = new NOR(nor1Out, this.Q, this.notQ, "nor4", debugGates);
        NOR nor5 = new NOR(this.notQ, nor2Out, this.Q, "nor5", debugGates);
    }
}
