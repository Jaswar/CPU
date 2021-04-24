package main.circuits;

import main.BitStream;
import main.circuits.memory.DFlipFlop;
import main.circuits.memory.DLatch;
import main.control.Splitter;
import main.gates.TriState;
import main.gates.binary.AND;
import main.gates.binary.NOR;
import main.gates.binary.OR;
import main.gates.multi.MultiNOR;
import main.gates.multi.MultiOR;
import main.gates.unary.NOT;
import main.utils.DataConverter;

import java.beans.BeanInfo;
import java.util.ArrayList;
import java.util.List;

public class ALU implements Circuit {

    private final BitStream source, destination, out, opCode, status;
    private final String name;
    private final boolean inDebuggerMode;
    private final int debugDepth;

    /**Constructors for the ALU class.
     *
     * @param source - the source BitStream
     * @param destination - the destination BitStream
     * @param out - the output BitStream
     * @param opCode - the BitStream for selecting which operation should be performed
     * @param status - BitStream to specify the status flags
     * @param name - the name of the ALU
     * @param inDebuggerMode - boolean to specify if the unit is in debug mode
     * @param debugDepth - the depth of debugging
     */
    public ALU(BitStream source, BitStream destination, BitStream out,
               BitStream opCode, BitStream status, String name,
               boolean inDebuggerMode, int debugDepth) {
        this.source = source;
        this.destination = destination;
        this.out = out;
        this.opCode = opCode;
        this.status = status;

        this.name = name;
        this.inDebuggerMode = inDebuggerMode;
        this.debugDepth = debugDepth;

        this.build();
    }

    public ALU(BitStream source, BitStream destination, BitStream out,
               BitStream opCode, BitStream status, String name) {
        this(source, destination, out, opCode, status, name, false, 0);
    }


    public ALU(BitStream source, BitStream destination, BitStream out,
               BitStream opCode, BitStream status,
               boolean inDebuggerMode, int debugDepth) {
        this(source, destination, out, opCode, status, "ALU", inDebuggerMode, debugDepth);
    }


    public ALU(BitStream source, BitStream destination, BitStream out,
               BitStream opCode, BitStream status) {
        this(source, destination, out, opCode, status, "ALU", false, 0);
    }

    /**Method to return the current state of the ALU.
     *
     * @return - the status of the ALU as String
     */
    public String requestStatus() {
        String status = "ALU (" + this.name + "):\n";
        status += "OPCode: " + DataConverter.convertBoolToBin(this.opCode.getData()) +
                " (" + DataConverter.convertBoolToUnsignedDec(this.opCode.getData()) + ")\t" +
                "Status: " + DataConverter.convertBoolToBin(this.status.getData()) + "\n";
        status += "SRC: " + DataConverter.convertBoolToBin(this.source.getData()) +
                " (" + DataConverter.convertBoolToUnsignedDec(this.source.getData()) + ", " +
                DataConverter.convertBoolToSignedDec(this.source.getData(), this.source.getSize()) + ")\t";
        status += "DEST: " + DataConverter.convertBoolToBin(this.destination.getData()) +
                " (" + DataConverter.convertBoolToUnsignedDec(this.destination.getData()) + ", " +
                DataConverter.convertBoolToSignedDec(this.destination.getData(), this.destination.getSize()) + ")\n";
        status += "OUT: " + DataConverter.convertBoolToBin(this.out.getData()) +
                " (" + DataConverter.convertBoolToUnsignedDec(this.out.getData()) + ", " +
                DataConverter.convertBoolToSignedDec(this.out.getData(), this.out.getSize()) + ")";
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

        BitStream overflow = new BitStream(1);
        AddSubtract addSubtract = new AddSubtract(this.source, this.destination, addSubOut, addSubAndOut, overflow,
                "addSub", debugGates, this.debugDepth - 1);

        LogicUnit logicUnit = new LogicUnit(this.source, this.destination, logicUnitOut, logicUnitControls,
                "logicUnit", debugGates, this.debugDepth - 1);

        List<BitStream> outputSplitterOutList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            BitStream bit = new BitStream(1);
            outputSplitterOutList.add(bit);
        }
        List<BitStream> outputSplitterInputList = new ArrayList<>();
        outputSplitterInputList.add(this.out);

        Splitter outputSplitter = new Splitter(outputSplitterInputList, outputSplitterOutList, "outputSplitter", debugGates);

        BitStream isZero = new BitStream(1);
        MultiNOR isZeroNor = new MultiNOR(outputSplitterOutList, isZero, "isZeroNor", debugGates);

        BitStream isNegative = outputSplitterOutList.get(0);
        BitStream isPositive = new BitStream(1);
        NOR isPositiveNor = new NOR(isNegative, isZero, isPositive, "isPositiveNor", debugGates);

        List<BitStream> statusSplitterInputList = new ArrayList<>();
        statusSplitterInputList.addAll(List.of(overflow, isPositive, isZero, isNegative));
        List<BitStream> statusSplitterOutList = new ArrayList<>();
        statusSplitterOutList.add(this.status);

        Splitter statusSplitter = new Splitter(statusSplitterInputList, statusSplitterOutList, "statusSplitter", debugGates);
    }
}
