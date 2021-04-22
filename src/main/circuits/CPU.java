package main.circuits;

import main.BitStream;
import main.circuits.memory.DFlipFlop;
import main.gates.TriState;
import main.utils.DataConverter;

import java.beans.BeanInfo;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;

public class CPU implements Circuit {

    private final BitStream clock, memRead, memWrite, memoryDataOut, memoryDataIn, memoryAddress;
    private final String name;
    private final boolean inDebuggerMode;
    private final int debugDepth;

    private BitStream bus;
    private RegisterFile registerFile;
    private IAG iag;
    private ALU alu;

    public CPU(BitStream clock, BitStream memRead, BitStream memWrite,
               BitStream memoryDataOut, BitStream memoryDataIn, BitStream memoryAddress,
               String name, boolean inDebuggerMode, int debugDepth) {
        this.clock = clock;
        this.memRead = memRead;
        this.memWrite = memWrite;
        this.memoryDataOut = memoryDataOut;
        this.memoryDataIn = memoryDataIn;
        this.memoryAddress = memoryAddress;
        this.name = name;
        this.inDebuggerMode = inDebuggerMode;
        this.debugDepth = debugDepth;

        this.build();
    }

    public CPU(BitStream clock, BitStream memRead, BitStream memWrite,
               BitStream memoryDataOut, BitStream memoryDataIn, BitStream memoryAddress,
               String name) {
        this(clock, memRead, memWrite, memoryDataOut, memoryDataIn, memoryAddress, name, false, 0);
    }

    public CPU(BitStream clock, BitStream memRead, BitStream memWrite,
               BitStream memoryDataOut, BitStream memoryDataIn, BitStream memoryAddress,
               boolean inDebuggerMode, int debugDepth) {
        this(clock, memRead, memWrite, memoryDataOut, memoryDataIn, memoryAddress, "CPU", inDebuggerMode, debugDepth);
    }

    public CPU(BitStream clock, BitStream memRead, BitStream memWrite,
               BitStream memoryDataOut, BitStream memoryDataIn, BitStream memoryAddress) {
        this(clock, memRead, memWrite, memoryDataOut, memoryDataIn, memoryAddress, "CPU", false, 0);
    }

    /**Getter for the bus of the CPU.
     *
     * @return - the bus
     */
    public BitStream getBus() {
        return bus;
    }

    /**Method to return the current state of the CPU.
     *
     * @return - the status of the CPU as String
     */
    public String requestStatus() {
        String status = "CPU (" + this.name + ")\n";
        status += "BUS: " + DataConverter.convertBoolToBin(this.bus.getData()) +
                " (" + DataConverter.convertBoolToUnsignedDec(this.bus.getData()) + ", " +
                DataConverter.convertBoolToSignedDec(this.bus.getData(), this.bus.getSize()) + ")\n";
        status += this.iag.requestStatus() + "\n";
        status += this.registerFile.requestStatus() + "\n";
        status += this.alu.requestStatus();
        return status;
    }

    @Override
    public void build() {
        boolean debugGates = this.debugDepth > 0 ? this.inDebuggerMode : false;
        int size = Microprocessor.WORD_SIZE;

        bus = new BitStream(size);

        BitStream RFIn = new BitStream(1);
        BitStream RFOut = new BitStream(1);
        BitStream rfAddrWrite = new BitStream(3);
        BitStream rfAddrRead = new BitStream(3);
        BitStream XIn = new BitStream(1);
        BitStream MUXConst = new BitStream(1);
        BitStream ALUOpcode = new BitStream(5);
        BitStream ZIn = new BitStream(1);
        BitStream ZOut = new BitStream(1);
        BitStream PCIn = new BitStream(1);
        BitStream PCOut = new BitStream(1);
        BitStream memAddress = new BitStream(1);
        BitStream memDataIn = new BitStream(1);
        BitStream memDataOut = new BitStream(1);

        ControlUnit controlUnit = new ControlUnit(bus, this.clock, bus, RFIn, RFOut, rfAddrWrite, rfAddrRead,
                XIn, MUXConst, ALUOpcode, ZIn, ZOut, PCIn, PCOut, memRead, memWrite, memAddress, memDataIn, memDataOut,
                "controlUnit", debugGates, this.debugDepth - 1);

        this.registerFile = new RegisterFile(bus, bus, RFIn, RFOut, rfAddrWrite, rfAddrRead,
                "registerFile", debugGates, this.debugDepth - 1);

        this.iag = new IAG(bus, bus, PCIn, PCOut, "IAG", debugGates, this.debugDepth - 1);

        BitStream enableMDIR = new BitStream(1);
        enableMDIR.setData(new boolean[]{true});
        DFlipFlop MDIR = new DFlipFlop(bus, memDataIn, enableMDIR, new BitStream(1), new BitStream(1),
                this.memoryDataIn, new BitStream(size), false,
                "MDIR", debugGates, this.debugDepth - 1);

        BitStream enableMAR = new BitStream(1);
        enableMAR.setData(new boolean[]{true});
        DFlipFlop MAR = new DFlipFlop(bus, memAddress, enableMAR, new BitStream(1), new BitStream(1),
                this.memoryAddress, new BitStream(size), false,
                "MAR", debugGates, this.debugDepth - 1);

        TriState dataOutTriState = new TriState(this.memoryDataOut, memDataOut, bus,
                "dataOutTriState", debugGates);

        BitStream xOutput = new BitStream(size);
        BitStream xOut = new BitStream(1);
        xOut.setData(new boolean[]{true});
        BitStream xEnable = new BitStream(1);
        xEnable.setData(new boolean[]{true});
        Register X = new Register(bus, xOutput, XIn, xOut, xEnable, "X", debugGates, this.debugDepth - 1);

        BitStream constant = new BitStream(size);
        constant.setData(new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, true});

        List<BitStream> aluMuxInputList = new ArrayList<>();
        aluMuxInputList.addAll(List.of(xOutput, constant));
        BitStream aluMuxOut = new BitStream(size);
        Multiplexer ALUMux = new Multiplexer(aluMuxInputList, MUXConst, aluMuxOut,
                "ALUMux", debugGates, this.debugDepth - 1);

        BitStream aluOutput = new BitStream(size);
        this.alu = new ALU(bus, aluMuxOut, aluOutput, ALUOpcode, new BitStream(1), new BitStream(1),
                "ALU", debugGates, this.debugDepth - 1);

        BitStream zEnable = new BitStream(1);
        zEnable.setData(new boolean[]{true});
        Register Z = new Register(aluOutput, bus, ZIn, ZOut, zEnable, "Z", debugGates, this.debugDepth - 1);
    }
}
