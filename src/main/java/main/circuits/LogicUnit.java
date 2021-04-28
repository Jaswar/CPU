package main.circuits;

import main.BitStream;
import main.gates.TriState;
import main.gates.binary.*;
import main.gates.unary.NOT;

import java.util.List;

public class LogicUnit implements Circuit {

    private final BitStream source;
    private final BitStream destination;
    private final BitStream output;
    private final List<BitStream> controls;
    private final String name;
    private final boolean inDebuggerMode;
    private final int debugDepth;

    /**Constructors for the LogicUnit class.
     *
     * @param source - the source BitStream
     * @param destination - the destination BitStream
     * @param output - the output BitStream
     * @param controls - the BitStreams controlling which operation should be undertaken.
     *                 The order is: NOT, OR, AND, XOR, NAND, NOR.
     * @param name - the name of the logic unit
     * @param inDebuggerMode - boolean to specify if the circuit is in the debug mode
     * @param debugDepth - how deep should debugging go
     */
    public LogicUnit(BitStream source, BitStream destination,
                     BitStream output, List<BitStream> controls,
                     String name, boolean inDebuggerMode, int debugDepth) {
        this.source = source;
        this.destination = destination;
        this.output = output;
        this.controls = controls;
        this.name = name;
        this.inDebuggerMode = inDebuggerMode;
        this.debugDepth = debugDepth;

        this.build();
    }

    public LogicUnit(BitStream source, BitStream destination,
                     BitStream output, List<BitStream> controls,
                     String name) {
        this(source, destination, output, controls, name, false, 0);
    }

    public LogicUnit(BitStream source, BitStream destination,
                     BitStream output, List<BitStream> controls,
                     boolean inDebuggerMode, int debugDepth) {
        this(source, destination, output, controls, "Logic Unit", inDebuggerMode, debugDepth);
    }

    public LogicUnit(BitStream source, BitStream destination,
                     BitStream output, List<BitStream> controls) {
        this(source, destination, output, controls, "Logic Unit", false, 0);
    }

    /**Build the circuit with logic gates. Exact build is as in documentation/LogicUnit.png.
     */
    @Override
    public void build() {
        boolean debugGates = this.debugDepth > 0 ? this.inDebuggerMode : false;
        int size = this.source.getSize();

        BitStream notToTriState = new BitStream(size);
        BitStream orToTriState = new BitStream(size);
        BitStream andToTriState = new BitStream(size);
        BitStream xorToTriState = new BitStream(size);
        BitStream nandToTriState = new BitStream(size);
        BitStream norToTriState = new BitStream(size);

        NOT not = new NOT(this.source, notToTriState, "mainNot", debugGates);
        OR or = new OR(this.source, this.destination, orToTriState, "mainOr", debugGates);
        AND and = new AND(this.source, this.destination, andToTriState, "mainAnd", debugGates);
        XOR xor = new XOR(this.source, this.destination, xorToTriState, "mainXor", debugGates);
        NAND nand = new NAND(this.source, this.destination, nandToTriState, "mainNand", debugGates);
        NOR nor = new NOR(this.source, this.destination, norToTriState, "mainNor", debugGates);

        TriState notTriState = new TriState(notToTriState, this.controls.get(0), this.output,
                "notTriState", debugGates);
        TriState orTriState = new TriState(orToTriState, this.controls.get(1), this.output,
                "orTriState", debugGates);
        TriState andTriState = new TriState(andToTriState, this.controls.get(2), this.output,
                "andTriState", debugGates);
        TriState xorTriState = new TriState(xorToTriState, this.controls.get(3), this.output,
                "xorTriState", debugGates);
        TriState nandTriState = new TriState(nandToTriState, this.controls.get(4), this.output,
                "nandTriState", debugGates);
        TriState norTriState = new TriState(norToTriState, this.controls.get(5), this.output,
                "norTriState", debugGates);
    }
}
