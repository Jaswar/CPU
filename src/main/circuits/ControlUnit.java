package main.circuits;

import main.BitStream;
import main.circuits.memory.DFlipFlop;
import main.circuits.memory.TFlipFlop;
import main.control.Splitter;
import main.gates.TriState;
import main.gates.binary.AND;
import main.gates.binary.OR;
import main.gates.unary.NOT;

import java.util.ArrayList;
import java.util.List;

public class ControlUnit implements Circuit {

    private BitStream input, clock, intermediate, RFIn, RFOut, RFAddrWrite, RFAddrRead,
            XIn, MUXConst, ALUOpcode, ZIn, ZOut, PCIn, PCOut, memRead, memWrite, memAddress,
            memDataIn, memDataOut;
    private String name;
    private boolean inDebuggerMode;
    private int debugDepth;

    private final static int NUM_MICROINSTRUCTIONS = 24;

    /**Constructors for the ControlUnit circuit. It controls what the components of the CPU should do.
     *
     * @param input - the main input to the unit
     * @param clock - the clock input to the circuit
     * @param intermediate - the output of the intermediate value
     * @param RFIn - BitStream specifying that the register file should read data
     * @param RFOut - BitStream specifying that the register file should output data
     * @param RFAddrWrite - BitStream to specify the address of the register that is written to
     * @param RFAddrRead - BitStream to specify the address of the register that is read from
     * @param XIn - specify if the X register should read data
     * @param MUXConst - specify if the input to the ALU is a constant value or X
     * @param ALUOpcode - BitStream to specify the operation that is performed by the ALU
     * @param ZIn - specify if the Z register should read data
     * @param ZOut - specify if the Z register should output data
     * @param PCIn - specify if the program counter should read data
     * @param PCOut - specify if the program counter should output data
     * @param memRead - specify if the memory should perform a read operation
     * @param memWrite - specify if the memory should perform a write operation
     * @param memAddress - specify if the memory address register should read data
     * @param memDataIn - specify if the memory data in register should read data
     * @param memDataOut - specify if the output of the memory should be read
     * @param name - the name of the circuit
     * @param inDebuggerMode - boolean to specify if the circuit is in debug mode
     * @param debugDepth - how deep should debugging go
     */
    public ControlUnit(BitStream input, BitStream clock, BitStream intermediate, BitStream RFIn, BitStream RFOut,
                       BitStream RFAddrWrite, BitStream RFAddrRead, BitStream XIn, BitStream MUXConst, BitStream ALUOpcode,
                       BitStream ZIn, BitStream ZOut, BitStream PCIn, BitStream PCOut, BitStream memRead,
                       BitStream memWrite, BitStream memAddress, BitStream memDataIn, BitStream memDataOut,
                       String name, boolean inDebuggerMode, int debugDepth) {
        this.input = input;
        this.clock = clock;
        this.intermediate = intermediate;
        this.RFIn = RFIn;
        this.RFOut = RFOut;
        this.RFAddrWrite = RFAddrWrite;
        this.RFAddrRead = RFAddrRead;
        this.XIn = XIn;
        this.MUXConst = MUXConst;
        this.ALUOpcode = ALUOpcode;
        this.ZIn = ZIn;
        this.ZOut = ZOut;
        this.PCIn = PCIn;
        this.PCOut = PCOut;
        this.memRead = memRead;
        this.memWrite = memWrite;
        this.memAddress = memAddress;
        this.memDataIn = memDataIn;
        this.memDataOut = memDataOut;
        this.name = name;
        this.inDebuggerMode = inDebuggerMode;
        this.debugDepth = debugDepth;

        this.build();
    }

    public ControlUnit(BitStream input, BitStream clock, BitStream intermediate, BitStream RFIn, BitStream RFOut,
                       BitStream RFAddrWrite, BitStream RFAddrRead, BitStream XIn, BitStream MUXConst, BitStream ALUOpcode,
                       BitStream ZIn, BitStream ZOut, BitStream PCIn, BitStream PCOut, BitStream memRead,
                       BitStream memWrite, BitStream memAddress, BitStream memDataIn, BitStream memDataOut,
                       String name) {
        this(input, clock, intermediate, RFIn, RFOut, RFAddrWrite, RFAddrRead, XIn, MUXConst, ALUOpcode,
                ZIn, ZOut, PCIn, PCOut, memRead, memWrite, memAddress, memDataIn, memDataOut, name, false, 0);
    }

    public ControlUnit(BitStream input, BitStream clock, BitStream intermediate, BitStream RFIn, BitStream RFOut,
                       BitStream RFAddrWrite, BitStream RFAddrRead, BitStream XIn, BitStream MUXConst, BitStream ALUOpcode,
                       BitStream ZIn, BitStream ZOut, BitStream PCIn, BitStream PCOut, BitStream memRead,
                       BitStream memWrite, BitStream memAddress, BitStream memDataIn, BitStream memDataOut,
                       boolean inDebuggerMode, int debugDepth) {
        this(input, clock, intermediate, RFIn, RFOut, RFAddrWrite, RFAddrRead, XIn, MUXConst, ALUOpcode,
                ZIn, ZOut, PCIn, PCOut, memRead, memWrite, memAddress, memDataIn, memDataOut,
                "ControlUnit", inDebuggerMode, debugDepth);
    }

    public ControlUnit(BitStream input, BitStream clock, BitStream intermediate, BitStream RFIn, BitStream RFOut,
                       BitStream RFAddrWrite, BitStream RFAddrRead, BitStream XIn, BitStream MUXConst, BitStream ALUOpcode,
                       BitStream ZIn, BitStream ZOut, BitStream PCIn, BitStream PCOut, BitStream memRead,
                       BitStream memWrite, BitStream memAddress, BitStream memDataIn, BitStream memDataOut) {
        this(input, clock, intermediate, RFIn, RFOut, RFAddrWrite, RFAddrRead, XIn, MUXConst, ALUOpcode,
                ZIn, ZOut, PCIn, PCOut, memRead, memWrite, memAddress, memDataIn, memDataOut,
                "ControlUnit", false, 0);
    }

    /**Build the circuit as defined in the documentation.
     */
    @Override
    public void build() {
        boolean debugGates = this.debugDepth > 0 ? this.inDebuggerMode : false;

        BitStream common0 = new BitStream(NUM_MICROINSTRUCTIONS);
        common0.setData(new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, true,
                false, false, true, false, true, false, false, false});
        BitStream common1 = new BitStream(NUM_MICROINSTRUCTIONS);
        common1.setData(new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                true, false, false, false, false, false, true, false});
        BitStream common2 = new BitStream(NUM_MICROINSTRUCTIONS);
        common2.setData(new boolean[]{false, false, false, false, false, false, true, false,
                false, false, false, true, true, false, false, true,
                false, false, false, false, false, false, false, false});
        BitStream common3 = new BitStream(NUM_MICROINSTRUCTIONS);
        common3.setData(new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, true, true, false,
                false, false, true, false, true, false, false, false});
        BitStream common4 = new BitStream(NUM_MICROINSTRUCTIONS);
        common4.setData(new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, true, false, false, false, false, true, false});
        BitStream common5 = new BitStream(NUM_MICROINSTRUCTIONS);
        common5.setData(new boolean[]{false, false, false, false, false, false, true, false,
                false, false, false, true, true, false, false, true,
                false, false, false, false, false, false, false, false});
        BitStream common6 = new BitStream(NUM_MICROINSTRUCTIONS);
        common6.setData(new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, true, true, false,
                false, false, false, false, false, false, false, false});

        BitStream commonBus = new BitStream(NUM_MICROINSTRUCTIONS);

        BitStream clockTFlipFlopOut = new BitStream(1);

        BitStream constant = new BitStream(1);
        constant.setData(new boolean[]{true});
        TFlipFlop clockTFlipFlop = new TFlipFlop(constant, this.clock, constant, new BitStream(1), new BitStream(1),
                clockTFlipFlopOut, new BitStream(1), true,
                "clockTFlipFlop", debugGates, this.debugDepth - 1);

        BitStream state0Out = new BitStream(1);
        state0Out.setData(new boolean[]{true});
        BitStream state1Out = new BitStream(1);
        BitStream state2Out = new BitStream(1);
        BitStream state3Out = new BitStream(1);
        BitStream state4Out = new BitStream(1);
        BitStream state5Out = new BitStream(1);
        BitStream state6Out = new BitStream(1);
        BitStream state7Out = new BitStream(1);

        BitStream enableStates = new BitStream(1);

        DFlipFlop state0 = new DFlipFlop(state7Out, clockTFlipFlopOut, enableStates, new BitStream(1), new BitStream(1),
                state0Out, new BitStream(1), true,
                "state0", debugGates, this.debugDepth - 1);
        DFlipFlop state1 = new DFlipFlop(state0Out, clockTFlipFlopOut, enableStates, new BitStream(1), new BitStream(1),
                state1Out, new BitStream(1), true,
                "state0", debugGates, this.debugDepth - 1);
        DFlipFlop state2 = new DFlipFlop(state1Out, clockTFlipFlopOut, enableStates, new BitStream(1), new BitStream(1),
                state2Out, new BitStream(1), true,
                "state0", debugGates, this.debugDepth - 1);
        DFlipFlop state3 = new DFlipFlop(state2Out, clockTFlipFlopOut, enableStates, new BitStream(1), new BitStream(1),
                state3Out, new BitStream(1), true,
                "state0", debugGates, this.debugDepth - 1);
        DFlipFlop state4 = new DFlipFlop(state3Out, clockTFlipFlopOut, enableStates, new BitStream(1), new BitStream(1),
                state4Out, new BitStream(1), true,
                "state0", debugGates, this.debugDepth - 1);
        DFlipFlop state5 = new DFlipFlop(state4Out, clockTFlipFlopOut, enableStates, new BitStream(1), new BitStream(1),
                state5Out, new BitStream(1), true,
                "state0", debugGates, this.debugDepth - 1);
        DFlipFlop state6 = new DFlipFlop(state5Out, clockTFlipFlopOut, enableStates, new BitStream(1), new BitStream(1),
                state6Out, new BitStream(1), true,
                "state0", debugGates, this.debugDepth - 1);
        DFlipFlop state7 = new DFlipFlop(state6Out, clockTFlipFlopOut, enableStates, new BitStream(1), new BitStream(1),
                state7Out, new BitStream(1), true,
                "state0", debugGates, this.debugDepth - 1);


        BitStream microprocessorOut = new BitStream(NUM_MICROINSTRUCTIONS);

        TriState comTriState0 = new TriState(common0, state0Out, commonBus, "comTriState0", debugGates);
        TriState comTriState1 = new TriState(common1, state1Out, commonBus, "comTriState1", debugGates);
        TriState comTriState2 = new TriState(common2, state2Out, commonBus, "comTriState2", debugGates);
        TriState comTriState3 = new TriState(common3, state3Out, commonBus, "comTriState3", debugGates);
        TriState comTriState4 = new TriState(common4, state4Out, commonBus, "comTriState4", debugGates);
        TriState comTriState5 = new TriState(common5, state5Out, commonBus, "comTriState5", debugGates);
        TriState comTriState6 = new TriState(common6, state6Out, commonBus, "comTriState6", debugGates);
        TriState comTriState7 = new TriState(microprocessorOut, state7Out, commonBus, "comTriState7", debugGates);

        BitStream clockNotOut = new BitStream(1);
        NOT clockNot = new NOT(clockTFlipFlopOut, clockNotOut, "clockNot", debugGates);
        BitStream processMicroinstruction = new BitStream(1);
        AND clockAnd = new AND(clockNotOut, this.clock, processMicroinstruction, "clockAnd", debugGates);

        BitStream IR1InControl = new BitStream(1);
        BitStream IR1In = new BitStream(1);
        BitStream IR2InControl = new BitStream(1);
        BitStream IR2In = new BitStream(1);
        AND ir1And = new AND(processMicroinstruction, IR1InControl, IR1In, "IR1And", debugGates);
        AND ir2And = new AND(processMicroinstruction, IR2InControl, IR2In, "IR2And", debugGates);

        BitStream source = new BitStream(3);
        BitStream destination = new BitStream(3);
        BitStream microprocessorInter = new BitStream(this.input.getSize());
        Microprocessor microprocessor = new Microprocessor(this.input, clockTFlipFlopOut, state6Out, IR1In, IR2In,
                microprocessorOut, microprocessorInter, source, destination,
                "microprocessor", debugGates, this.debugDepth - 1);

        BitStream enable = new BitStream(1);
        enable.setData(new boolean[]{true});
        BitStream notMicroinstruction = new BitStream(NUM_MICROINSTRUCTIONS);
        DFlipFlop microDFlipFlop = new DFlipFlop(commonBus, clockTFlipFlopOut, enable, new BitStream(1), new BitStream(1),
                new BitStream(NUM_MICROINSTRUCTIONS), notMicroinstruction, false,
                "microDFlipFlop", debugGates, this.debugDepth - 1);

        BitStream microinstruction = new BitStream(NUM_MICROINSTRUCTIONS);
        NOT microinstructionNot = new NOT(notMicroinstruction, microinstruction, "microinstructionNot", debugGates);

        BitStream end = new BitStream(1);
        BitStream rfInControl = new BitStream(1);
        BitStream rfWriteSrc = new BitStream(1);
        BitStream rfReadDest = new BitStream(1);
        BitStream xInControl = new BitStream(1);
        BitStream zInControl = new BitStream(1);
        BitStream pcInControl = new BitStream(1);
        BitStream memWriteControl = new BitStream(1);
        BitStream memAddressControl = new BitStream(1);
        BitStream memDataInControl = new BitStream(1);
        BitStream interOut = new BitStream(1);

        List<BitStream> mainSplitterInputList = new ArrayList<>();
        mainSplitterInputList.add(microinstruction);
        List<BitStream> mainSplitterOutList = new ArrayList<>();
        mainSplitterOutList.addAll(List.of(end, rfInControl, this.RFOut, rfWriteSrc, rfReadDest, xInControl,
                this.MUXConst, this.ALUOpcode, zInControl, this.ZOut, pcInControl, this.PCOut, IR1InControl, IR2InControl,
                this.memRead, memWriteControl, memAddressControl, memDataInControl, this.memDataOut, interOut));

        Splitter mainSplitter = new Splitter(mainSplitterInputList, mainSplitterOutList, "mainSplitter", debugGates);

        BitStream state7NotOut = new BitStream(1);
        NOT state7Not = new NOT(state7Out, state7NotOut, "state7Not", debugGates);
        OR enableOr = new OR(state7NotOut, end, enableStates, "enableOr", debugGates);

        TriState interTriState = new TriState(microprocessorInter, interOut, this.intermediate,
                "interTriState", debugGates);

        AND rfInAnd = new AND(rfInControl, processMicroinstruction, this.RFIn, "rfInAnd", debugGates);
        AND xInAnd = new AND(xInControl, processMicroinstruction, this.XIn, "xInAnd", debugGates);
        AND zInAnd = new AND(zInControl, processMicroinstruction, this.ZIn, "zInAnd", debugGates);
        AND pcInAnd = new AND(pcInControl, processMicroinstruction, this.PCIn, "pcInAnd", debugGates);
        AND writeAnd = new AND(memWriteControl, processMicroinstruction, this.memWrite, "writeAnd", debugGates);
        AND addressAnd = new AND(memAddressControl, processMicroinstruction, this.memAddress, "addressAnd", debugGates);
        AND dataInAnd = new AND(memDataInControl, processMicroinstruction, this.memDataIn, "dataInAnd", debugGates);

        List<BitStream> rfAddrReadMuxList = new ArrayList<>();
        rfAddrReadMuxList.addAll(List.of(source, destination));
        Multiplexer rfAddrReadMux = new Multiplexer(rfAddrReadMuxList, rfReadDest, this.RFAddrRead,
                "rfAddrReadMux", debugGates, this.debugDepth - 1);

        List<BitStream> rfAddrWriteMuxList = new ArrayList<>();
        rfAddrWriteMuxList.addAll(List.of(destination, source));
        Multiplexer rfAddrWriteMux = new Multiplexer(rfAddrWriteMuxList, rfWriteSrc, this.RFAddrWrite,
                "rfAddrWriteMux", debugGates, this.debugDepth - 1);
    }
}
