package main.circuits;

import main.BitStream;
import main.circuits.memory.DFlipFlop;
import main.circuits.memory.TFlipFlop;
import main.control.Splitter;
import main.gates.binary.*;
import main.gates.multi.MultiAND;
import main.gates.multi.MultiNOR;
import main.gates.multi.MultiOR;
import main.gates.unary.NOT;
import main.memory.ROM;

import java.util.ArrayList;
import java.util.List;
import java.util.spi.AbstractResourceBundleProvider;

public class Microprocessor implements Circuit {

    private final BitStream input, clock, reset,  IR1In, IR2In,
            microinstruction, intermediate, source, destination, status;
    private final String name;
    private final boolean inDebuggerMode;
    private final int debugDepth;

    private Register IR1;
    private Register IR2;

    public static final int WORD_SIZE = 16;
    public static final int INSTRUCTION_SIZE = 8;
    public static final int MAPPING_OUT_SIZE = 8;
    public static final int NUM_ROM_MICROINSTRUCTIONS = 38;

    /**Constructors for the Microprocessor class. This component is a part of the Control Unit.
     *
     * @param input - the main input to the circuit
     * @param clock - the clock input
     * @param reset - BitStream used to reset the microinstruction counter
     * @param IR1In - specify if IR1 should read data
     * @param IR2In - specify if IR2 should read data
     * @param microinstruction - output BitStream to read microinstructions
     * @param intermediate - the output of the intermediate value
     * @param source - the source register as read from the instruction
     * @param destination - the destination register as read from the instruction
     * @param name - the name of the circuit
     * @param inDebuggerMode - boolean to specify if the circuit is in debug mode
     * @param debugDepth - how deep should debugging go
     */
    public Microprocessor(BitStream input, BitStream clock, BitStream reset, BitStream IR1In, BitStream IR2In,
                          BitStream microinstruction, BitStream intermediate, BitStream source, BitStream destination,
                          BitStream status, String name, boolean inDebuggerMode, int debugDepth) {
        this.input = input;
        this.clock = clock;
        this.reset = reset;
        this.IR1In = IR1In;
        this.IR2In = IR2In;

        this.microinstruction = microinstruction;
        this.intermediate = intermediate;
        this.source = source;
        this.destination = destination;
        this.status = status;

        this.name = name;
        this.inDebuggerMode = inDebuggerMode;
        this.debugDepth = debugDepth;

        this.build();
    }

    public Microprocessor(BitStream input, BitStream clock, BitStream reset, BitStream IR1In, BitStream IR2In,
                          BitStream microinstruction, BitStream intermediate, BitStream source, BitStream destination,
                          BitStream status, String name) {
        this(input, clock, reset, IR1In, IR2In, microinstruction, intermediate, source, destination, status,
                name, false, 0);
    }

    public Microprocessor(BitStream input, BitStream clock, BitStream reset, BitStream IR1In, BitStream IR2In,
                          BitStream microinstruction, BitStream intermediate, BitStream source, BitStream destination,
                          BitStream status, boolean inDebuggerMode, int debugDepth) {
        this(input, clock, reset, IR1In, IR2In, microinstruction, intermediate, source, destination, status,
                "Microprocessor", inDebuggerMode, debugDepth);
    }

    public Microprocessor(BitStream input, BitStream clock, BitStream reset, BitStream IR1In, BitStream IR2In,
                          BitStream microinstruction, BitStream intermediate, BitStream source, BitStream destination,
                          BitStream status) {
        this(input, clock, reset, IR1In, IR2In, microinstruction, intermediate, source, destination, status,
                "Microprocessor", false, 0);
    }

    /**Getter for the first instruction register of the microprocessor.
     *
     * @return - the first instruction register
     */
    public Register getIR1() {
        return IR1;
    }

    /**Getter for the second instruction register of the microprocessor.
     *
     * @return - the second instruction register
     */
    public Register getIR2() {
        return IR2;
    }

    /**Build the circuit as is defined in the documentation.
     */
    @Override
    public void build() {
        boolean debugGates = this.debugDepth > 0 ? this.inDebuggerMode : false;

        BitStream IR1Out = new BitStream(WORD_SIZE);
        BitStream IR1OutControl = new BitStream(1);
        IR1OutControl.setData(new boolean[]{true});
        BitStream IR1Enable = new BitStream(1);
        IR1Enable.setData(new boolean[]{true});
        this.IR1 = new Register(this.input, IR1Out, this.IR1In, IR1OutControl, IR1Enable,
                "IR1", debugGates, this.debugDepth - 1);

        BitStream IR2OutControl = new BitStream(1);
        IR2OutControl.setData(new boolean[]{true});
        BitStream IR2Enable = new BitStream(1);
        IR2Enable.setData(new boolean[]{true});
        this.IR2 = new Register(this.input, this.intermediate, this.IR2In, IR2OutControl, IR2Enable,
                "IR2", debugGates, this.debugDepth - 1);

        List<BitStream> instructionSplitterOutList = new ArrayList<>();
        BitStream instruction = new BitStream(INSTRUCTION_SIZE);
        BitStream empty = new BitStream(WORD_SIZE - this.source.getSize()
                - this.destination.getSize() - INSTRUCTION_SIZE);
        instructionSplitterOutList.addAll(List.of(empty, this.destination, this.source, instruction));
        List<BitStream> instructionSplitterInputList = new ArrayList<>();
        instructionSplitterInputList.add(IR1Out);

        Splitter instructionSplitter = new Splitter(instructionSplitterInputList, instructionSplitterOutList,
                "instructionSplitter", debugGates);

        BitStream mapRomOut = new BitStream(MAPPING_OUT_SIZE);

        ROM mapRom = new ROM("./storage/main/microMapping.stg", instruction, mapRomOut, "mapRom", debugGates);

        BitStream constant = new BitStream(1);
        constant.setData(new boolean[]{true});
        BitStream enable = new BitStream(1);
        enable.setData(new boolean[]{true});

        BitStream tFlipFlop0Q = new BitStream(1);
        BitStream tFlipFlop0NotQ = new BitStream(1);
        TFlipFlop tFlipFlop0 = new TFlipFlop(constant, this.clock, enable, new BitStream(1), this.reset,
                tFlipFlop0Q, tFlipFlop0NotQ, true, "tFlipFlop0", debugGates, debugDepth - 1);

        BitStream tFlipFlop1Q = new BitStream(1);
        BitStream tFlipFlop1NotQ = new BitStream(1);
        TFlipFlop tFlipFlop1 = new TFlipFlop(constant, tFlipFlop0NotQ, enable, new BitStream(1), this.reset,
                tFlipFlop1Q, tFlipFlop1NotQ, true, "tFlipFlop1", debugGates, debugDepth - 1);

        BitStream tFlipFlop2Q = new BitStream(1);
        BitStream tFlipFlop2NotQ = new BitStream(1);
        TFlipFlop tFlipFlop2 = new TFlipFlop(constant, tFlipFlop1NotQ, enable, new BitStream(1), this.reset,
                tFlipFlop2Q, tFlipFlop2NotQ, true, "tFlipFlop2", debugGates, debugDepth - 1);

        List<BitStream> counterSplitterInputList = new ArrayList<>();
        counterSplitterInputList.addAll(List.of(new BitStream(MAPPING_OUT_SIZE - 3),
                tFlipFlop2Q, tFlipFlop1Q, tFlipFlop0Q));

        BitStream counterSplitterOut = new BitStream(MAPPING_OUT_SIZE);
        List<BitStream> counterSplitterOutList = new ArrayList<>();
        counterSplitterOutList.add(counterSplitterOut);

        Splitter counterSplitter = new Splitter(counterSplitterInputList, counterSplitterOutList,
                "counterSplitter", debugGates);

        BitStream addSubOutput = new BitStream(MAPPING_OUT_SIZE);
        AddSubtract addSub = new AddSubtract(counterSplitterOut, mapRomOut, addSubOutput,
                new BitStream(1), new BitStream(1), "addSub", debugGates, this.debugDepth - 1);

        BitStream microInstructionROMOut = new BitStream(NUM_ROM_MICROINSTRUCTIONS);
        ROM microinstructionRom = new ROM("./storage/main/microinstructions.stg", addSubOutput, microInstructionROMOut,
                "microinstructionRom", debugGates);

        BitStream isJump = new BitStream(1);
        BitStream jmpInterest = new BitStream(4);
        BitStream jmpCond = new BitStream(4);
        BitStream clrFlags = new BitStream(1);
        BitStream setFlag = new BitStream(4);

        BitStream tempMicroinstruction = new BitStream(ControlUnit.NUM_MICROINSTRUCTIONS);

        List<BitStream> microSplitterInputList = new ArrayList<>();
        microSplitterInputList.add(microInstructionROMOut);
        List<BitStream> microSplitterOutList = new ArrayList<>();
        microSplitterOutList.addAll(List.of(tempMicroinstruction, setFlag, clrFlags, jmpCond, jmpInterest, isJump));

        Splitter microSplitter = new Splitter(microSplitterInputList, microSplitterOutList, "microSplitter", debugGates);

        BitStream statusNeg = new BitStream(1);
        BitStream statusZero = new BitStream(1);
        BitStream statusPos = new BitStream(1);
        BitStream statusOver = new BitStream(1);

        List<BitStream> statusSplitterInputList = new ArrayList<>();
        statusSplitterInputList.add(this.status);
        List<BitStream> statusSplitterOutList = new ArrayList<>();
        statusSplitterOutList.addAll(List.of(statusOver, statusPos, statusZero, statusNeg));

        Splitter statusSplitter = new Splitter(statusSplitterInputList, statusSplitterOutList, "statusSplitter", debugGates);

        BitStream setNeg = new BitStream(1);
        BitStream setZero = new BitStream(1);
        BitStream setPos = new BitStream(1);
        BitStream setOver = new BitStream(1);

        List<BitStream> setFlagsSplitterInputList = new ArrayList<>();
        setFlagsSplitterInputList.add(setFlag);
        List<BitStream> setFlagsSplitterOutList = new ArrayList<>();
        setFlagsSplitterOutList.addAll(List.of(setOver, setPos, setZero, setNeg));

        Splitter setFlagsSplitter = new Splitter(setFlagsSplitterInputList, setFlagsSplitterOutList, "setFlagsSplitter", debugGates);

        BitStream setNegAndOut = new BitStream(1);
        BitStream setZeroAndOut = new BitStream(1);
        BitStream setPosAndOut = new BitStream(1);
        BitStream setOverAndOut = new BitStream(1);

        AND setNegAnd = new AND(this.clock, setNeg, setNegAndOut, "setNegAnd", debugGates);
        AND setZeroAnd = new AND(this.clock, setZero, setZeroAndOut, "setZeroAnd", debugGates);
        AND setPosAnd = new AND(this.clock, setPos, setZeroAndOut, "setPosAnd", debugGates);
        AND setOverAnd = new AND(this.clock, setOver, setOverAndOut, "setOverAnd", debugGates);

        BitStream negFlag = new BitStream(1);
        BitStream zeroFlag = new BitStream(1);
        BitStream posFlag = new BitStream(1);
        BitStream overFlag = new BitStream(1);

        BitStream enableStatus = new BitStream(1);
        enableStatus.setData(new boolean[]{true});
        DFlipFlop negFlipFlop = new DFlipFlop(statusNeg, setNegAndOut, enableStatus, new BitStream(1), clrFlags,
                negFlag, new BitStream(1), false, "negFlipFlop", debugGates, this.debugDepth - 1);
        DFlipFlop zeroFlipFlop = new DFlipFlop(statusZero, setZeroAndOut, enableStatus, new BitStream(1), clrFlags,
                zeroFlag, new BitStream(1), false, "zeroFlipFlop", debugGates, this.debugDepth - 1);
        DFlipFlop posFlipFlop = new DFlipFlop(statusPos, setPosAndOut, enableStatus, new BitStream(1), clrFlags,
                posFlag, new BitStream(1), false, "posFlipFlop", debugGates, this.debugDepth - 1);
        DFlipFlop overFlipFlop = new DFlipFlop(statusOver, setOverAndOut, enableStatus, new BitStream(1), clrFlags,
                overFlag, new BitStream(1), false, "overFlipFlop", debugGates, this.debugDepth - 1);

        BitStream statusFlipFlopSplitterOut = new BitStream(4);
        List<BitStream> statusFlipFlopSplitterInputList = new ArrayList<>();
        statusFlipFlopSplitterInputList.addAll(List.of(overFlag, posFlag, zeroFlag, negFlag));
        List<BitStream> statusFlipFlopSplitterOutList = new ArrayList<>();
        statusFlipFlopSplitterOutList.add(statusFlipFlopSplitterOut);

        Splitter statusFlipFlopSplitter = new Splitter(statusFlipFlopSplitterInputList, statusFlipFlopSplitterOutList,
                "statusFlipFlopSplitter", debugGates);

        BitStream goodStatusXorOut = new BitStream(4);
        XOR goodStatusXor = new XOR(statusFlipFlopSplitterOut, jmpCond, goodStatusXorOut,
                "goodStatusXor", debugGates);

        BitStream interestNandOut = new BitStream(4);
        NAND interestNand = new NAND(goodStatusXorOut, jmpInterest, interestNandOut, "interestNand", debugGates);

        List<BitStream> interestNandSplitterInputList = new ArrayList<>();
        interestNandSplitterInputList.add(interestNandOut);
        List<BitStream> interestNandSplitterOutList = new ArrayList<>();
        interestNandSplitterOutList.addAll(List.of(new BitStream(1), new BitStream(1),
                new BitStream(1), new BitStream(1)));

        Splitter interestNandSplitter = new Splitter(interestNandSplitterInputList, interestNandSplitterOutList,
                "interestNandSplitter", debugGates);

        BitStream jumpAndOut = new BitStream(1);
        MultiAND jumpAnd = new MultiAND(interestNandSplitterOutList, jumpAndOut, "jumpAnd", debugGates);

        BitStream condJumpAndOut = new BitStream(1);
        AND condJumpAnd = new AND(jumpAndOut, isJump, condJumpAndOut, "condJumpAnd", debugGates);

        List<BitStream> jmpInterestSplitterInputList = new ArrayList<>();
        jmpInterestSplitterInputList.add(jmpInterest);
        List<BitStream> jmpInterestSplitterOutList = new ArrayList<>();
        jmpInterestSplitterOutList.addAll(List.of(new BitStream(1), new BitStream(1),
                new BitStream(1), new BitStream(1)));

        Splitter jmpInterestSplitter = new Splitter(jmpInterestSplitterInputList, jmpInterestSplitterOutList,
                "jmpInterestSplitter", debugGates);

        BitStream interestNorOut = new BitStream(1);
        MultiNOR interestNor = new MultiNOR(jmpInterestSplitterOutList, interestNorOut, "interestNor", debugGates);

        BitStream noCondAndOut = new BitStream(1);
        AND noCondAnd = new AND(interestNorOut, isJump, noCondAndOut, "noCondAnd", debugGates);

        BitStream makeJump = new BitStream(1);
        OR makeJumpOr = new OR(condJumpAndOut, noCondAndOut, makeJump, "makeJumpOr", debugGates);

        BitStream jumpNotOut = new BitStream(1);
        NOT jumpNot = new NOT(isJump, jumpNotOut, "jumpNot", debugGates);

        BitStream makeMI = new BitStream(1);
        OR makeMIOr = new OR(makeJump, jumpNotOut, makeMI, "makeMIOr", debugGates);

        BitStream noOp = new BitStream(ControlUnit.NUM_MICROINSTRUCTIONS);
        noOp.setData(new boolean[]{true, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false});
        List<BitStream> makeJumpMuxInputList = new ArrayList<>();
        makeJumpMuxInputList.addAll(List.of(noOp, tempMicroinstruction));

        Multiplexer makeJumpMux = new Multiplexer(makeJumpMuxInputList, makeMI, this.microinstruction,
                "makeJumpMux", debugGates, this.debugDepth - 1);
    }
}
