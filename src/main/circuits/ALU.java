package main.circuits;

import main.BitStream;
import main.circuits.memory.DFlipFlop;
import main.circuits.memory.DLatch;
import main.gates.TriState;
import main.gates.binary.AND;
import main.gates.binary.OR;
import main.gates.multi.MultiOR;
import main.gates.unary.NOT;

import java.util.ArrayList;
import java.util.List;

public class ALU implements Circuit {

    private BitStream source, destination, out, opCode, aluIn, overflow;
    private String name;
    private boolean inDebuggerMode;
    private int debugDepth;

    /**Constructors for the ALU class.
     *
     * @param source - the source BitStream
     * @param destination - the destination BitStream
     * @param out - the output BitStream
     * @param opCode - the BitStream for selecting which operation should be performed
     * @param aluIn - BitStream to tell if the ALU should read source
     * @param overflow - BitStream specifying if overflow occurred
     * @param name - the name of the ALU
     * @param inDebuggerMode - boolean to specify if the unit is in debug mode
     * @param debugDepth - the depth of debugging
     */
    public ALU(BitStream source, BitStream destination, BitStream out,
               BitStream opCode, BitStream aluIn, BitStream overflow, String name,
               boolean inDebuggerMode, int debugDepth) {
        this.source = source;
        this.destination = destination;
        this.out = out;
        this.opCode = opCode;
        this.aluIn = aluIn;
        this.overflow = overflow;

        this.name = name;
        this.inDebuggerMode = inDebuggerMode;
        this.debugDepth = debugDepth;

        this.build();
    }

    public ALU(BitStream source, BitStream destination, BitStream out,
               BitStream opCode, BitStream aluIn, BitStream overflow, String name) {
        this(source, destination, out, opCode, aluIn, overflow, name, false, 0);
    }


    public ALU(BitStream source, BitStream destination, BitStream out,
               BitStream opCode, BitStream aluIn, BitStream overflow,
               boolean inDebuggerMode, int debugDepth) {
        this(source, destination, out, opCode, aluIn, overflow, "ALU", inDebuggerMode, debugDepth);
    }


    public ALU(BitStream source, BitStream destination, BitStream out,
               BitStream opCode, BitStream aluIn, BitStream overflow) {
        this(source, destination, out, opCode, aluIn, overflow, "ALU", false, 0);
    }

    /**Getters for all the attributes.
     */
    public BitStream getSource() {
        return source;
    }

    public BitStream getDestination() {
        return destination;
    }

    public BitStream getOut() {
        return out;
    }

    public BitStream getOpCode() {
        return opCode;
    }

    public BitStream getAluIn() {
        return aluIn;
    }

    public BitStream getOverflow() {
        return overflow;
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

    /**Define the build method to construct the ALU as described in documentation.
     */
    @Override
    public void build() {
        boolean debugGates = this.debugDepth > 0 ? this.inDebuggerMode : false;
        int size = this.source.getSize();

        BitStream srcDFlipFlopQ = new BitStream(size);
        BitStream srcDFlipFlopNotQ = new BitStream(size);

        BitStream sourceEnabled = new BitStream(1);
        sourceEnabled.setData(new boolean[]{true});

//        DFlipFlop sourceDFlipFlop = new DFlipFlop(this.source, this.aluIn, sourceEnabled, srcDFlipFlopQ, srcDFlipFlopNotQ, false,
//                "sourceDLatch", debugGates, this.debugDepth - 1);

        List<BitStream> decoderOut = new ArrayList<>();
        for (int i = 0; i < (int)Math.pow(2, this.opCode.getSize()); i++) {
            BitStream controlBit = new BitStream(1);
            decoderOut.add(controlBit);
        }

        Decoder opDecoder = new Decoder(this.opCode, decoderOut, "opDecoder", debugGates, this.debugDepth - 1);

        BitStream addNotOut = new BitStream(1);
        NOT addNot = new NOT(decoderOut.get(1), addNotOut, "addNot", debugGates);

        BitStream addSubAndOut = new BitStream(1);
        AND addSubAnd = new AND(decoderOut.get(2), addNotOut, addSubAndOut, "addSubAnd", debugGates);

        BitStream addSubControlOut = new BitStream(1);
        OR addSubControl = new OR(decoderOut.get(1), decoderOut.get(2), addSubControlOut, "addSubControl", debugGates);

        BitStream addSubOut = new BitStream(size);

        TriState addSubTriState = new TriState(addSubOut, addSubControlOut, this.out, "addSubTriState", debugGates);

        List<BitStream> logicUnitControls = new ArrayList<>();
        logicUnitControls.addAll(List.of(decoderOut.get(3), decoderOut.get(4), decoderOut.get(5),
                decoderOut.get(6), decoderOut.get(7), decoderOut.get(8)));

        BitStream logicControlOut = new BitStream(1);

        MultiOR logicControl = new MultiOR(logicUnitControls, logicControlOut, "logicControl", debugGates);

        BitStream logicUnitOut = new BitStream(size);

        TriState logicTriState = new TriState(logicUnitOut, logicControlOut, this.out, "logicTriState", debugGates);

        AddSubtract addSubtract = new AddSubtract(this.source, this.destination, addSubOut, addSubAndOut, this.overflow,
                "addSub", debugGates, this.debugDepth - 1);

        LogicUnit logicUnit = new LogicUnit(this.source, this.destination, logicUnitOut, logicUnitControls,
                "logicUnit", debugGates, this.debugDepth - 1);
    }
}
