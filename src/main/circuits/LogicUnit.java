package main.circuits;

import jdk.jshell.Snippet;
import main.BitStream;
import main.Node;
import main.gates.TriState;
import main.gates.binary.*;
import main.gates.unary.NOT;

import java.util.List;

public class LogicUnit implements Circuit {

    /**Class to represent the logic unit as designed in documentation/logicUnit.png.
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
    private BitStream source;
    private BitStream destination;
    private BitStream output;
    private List<BitStream> controls;
    private String name;
    private boolean inDebuggerMode;
    private int debugDepth;

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

    /**Getters for all the attributes of the class.
     */
    public BitStream getSource() {
        return source;
    }

    public BitStream getDestination() {
        return destination;
    }

    public BitStream getOutput() {
        return output;
    }

    public List<BitStream> getcontrols() {
        return controls;
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

    /**Setters for some of the attributes. Setting BitStreams is not possible.
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

    /**Build the circuit with logic gates. Exact build is as in documentation/logicUnit.png.
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