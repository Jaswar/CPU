package main.circuits;

import main.BitStream;
import main.circuits.memory.DFlipFlop;
import main.circuits.memory.DLatch;
import main.gates.TriState;
import main.gates.binary.AND;
import main.gates.binary.OR;
import main.gates.multi.MultiOR;
import main.gates.unary.NOT;
import main.utils.DataConverter;

import java.util.ArrayList;
import java.util.List;

public class ALU implements Circuit {

    private final BitStream source, destination, out, opCode, aluIn, overflow;
    private final String name;
    private final boolean inDebuggerMode;
    private final int debugDepth;

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

    /**Method to return the current state of the ALU.
     *
     * @return - the status of the ALU as String
     */
    public String requestStatus() {
        String status = "ALU (" + this.name + ")\n";
        status += "OPCode: " + DataConverter.convertBoolToBin(this.opCode.getData()) +
                " (" + DataConverter.convertBoolToUnsignedDec(this.opCode.getData()) + ")\t" +
                "Overflow: " + this.overflow.getData()[0] + "\n";
        status += "SRC: " + DataConverter.convertBoolToBin(this.source.getData()) +
                " (" + DataConverter.convertBoolToUnsignedDec(this.source.getData()) + ", " +
                DataConverter.convertBoolToSignedDec(this.source.getData(), this.source.getSize()) + ")\n";
        status += "DEST: " + DataConverter.convertBoolToBin(this.destination.getData()) +
                " (" + DataConverter.convertBoolToUnsignedDec(this.destination.getData()) + ", " +
                DataConverter.convertBoolToSignedDec(this.destination.getData(), this.destination.getSize()) + ")";
        return status;
    }

    /**Define the build method to construct the ALU as described in documentation.
     */
    @Override
    public void build() {
        boolean debugGates = this.debugDepth > 0 ? this.inDebuggerMode : false;
        int size = this.source.getSize();

        BitStream sourceEnabled = new BitStream(1);
        sourceEnabled.setData(new boolean[]{true});

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
