package main.circuits;

import main.BitStream;
import main.circuits.memory.DFlipFlop;
import main.control.Input;
import main.gates.TriState;
import main.utils.DataConverter;
import main.utils.ProcessRunner;

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
    private Register X;
    private BitStream XIn;
    private Register Z;
    private BitStream ZIn;
    private BitStream ZOut;
    private ControlUnit controlUnit;
    private Input clockInput;

    /**Constructors for the CPU class.
     *
     * @param memRead - the BitStream to send a read signal to the memory
     * @param memWrite - the BitStream to send a write signal to the memory
     * @param memoryDataOut - the BitStream with the data from the memory
     * @param memoryDataIn - the BitStream with the data coming from the CPU to the memory
     * @param memoryAddress - the address BitStream for the memory
     * @param name - the name of the circuit
     * @param inDebuggerMode - boolean to specify if the circuit is in debug mode
     * @param debugDepth - how deep should debugging go
     */
    public CPU(BitStream memRead, BitStream memWrite,
               BitStream memoryDataOut, BitStream memoryDataIn, BitStream memoryAddress,
               String name, boolean inDebuggerMode, int debugDepth) {
        this.clock = new BitStream(1);
        this.clockInput = new Input(new boolean[]{false}, this.clock);

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

    public CPU(BitStream memRead, BitStream memWrite,
               BitStream memoryDataOut, BitStream memoryDataIn, BitStream memoryAddress,
               String name) {
        this(memRead, memWrite, memoryDataOut, memoryDataIn, memoryAddress, name, false, 0);
    }

    public CPU(BitStream memRead, BitStream memWrite,
               BitStream memoryDataOut, BitStream memoryDataIn, BitStream memoryAddress,
               boolean inDebuggerMode, int debugDepth) {
        this(memRead, memWrite, memoryDataOut, memoryDataIn, memoryAddress, "CPU", inDebuggerMode, debugDepth);
    }

    public CPU(BitStream memRead, BitStream memWrite,
               BitStream memoryDataOut, BitStream memoryDataIn, BitStream memoryAddress) {
        this(memRead, memWrite, memoryDataOut, memoryDataIn, memoryAddress, "CPU", false, 0);
    }

    /**Getter for the bus of the CPU.
     *
     * @return - the bus
     */
    public BitStream getBus() {
        return bus;
    }

    /**Getter for the register file of the CPU.
     *
     * @return - the register file that is a part of this CPU
     */
    public RegisterFile getRegisterFile() {
        return registerFile;
    }

    /**Getter for the control unit of the CPU
     *
     * @return - the control unit
     */
    public ControlUnit getControlUnit() {
        return controlUnit;
    }

    /**Getter for the IAG of the CPU
     *
     * @return - the control unit
     */
    public IAG getIag() {
        return this.iag;
    }

    /**Getter for the clock of the CPU
     *
     * @return - the clock
     */
    public Input getClockInput() {
        return clockInput;
    }

    /**Method to return the current state of the CPU.
     *
     * @return - the status of the CPU as String
     */
    public String requestStatus() {
        String status = "CPU (" + this.name + "):\n";
        status += "BUS: " + DataConverter.convertBoolToBin(this.bus.getData()) +
                " (" + DataConverter.convertBoolToUnsignedDec(this.bus.getData()) + ", " +
                DataConverter.convertBoolToSignedDec(this.bus.getData(), this.bus.getSize()) + ")\n";
        status += this.controlUnit.requestStatus() + "\n";
        status += this.iag.requestStatus() + "\n";
        status += this.registerFile.requestStatus() + "\n";
        status += "X Register:\n";
        status += "XIn: " + this.XIn.getData()[0] + "\n";
        status += this.X.requestStatus() + "\n";
        status += this.alu.requestStatus() + "\n";
        status += "Z Register:\n";
        status += "ZIn: " + this.ZIn.getData()[0] + "\tZOut: " + this.ZOut.getData()[0] + "\n";
        status += this.Z.requestStatus() + "\n";
        status += "Memory Interface:\n";
        status += "MA: " + DataConverter.convertBoolToBin(this.memoryAddress.getData()) +
                " (" + DataConverter.convertBoolToUnsignedDec(this.memoryAddress.getData()) + ")\n";
        status += "MDI: " + DataConverter.convertBoolToBin(this.memoryDataIn.getData()) +
                " (" + DataConverter.convertBoolToUnsignedDec(this.memoryDataIn.getData()) + ", " +
                DataConverter.convertBoolToSignedDec(this.memoryDataIn.getData(), this.memoryDataIn.getSize()) + ")\n";
        status += "MDO: " + DataConverter.convertBoolToBin(this.memoryDataOut.getData()) +
                " (" + DataConverter.convertBoolToUnsignedDec(this.memoryDataOut.getData()) + ", " +
                DataConverter.convertBoolToSignedDec(this.memoryDataOut.getData(), this.memoryDataOut.getSize()) + ")\n";
        return status;
    }

    /**Run the CPU. Also enable an option to show additional information or run
     * a selected number of instructions.
     *
     * @param showStatus - boolean to specify if additional information should be shown or not
     * @param numInstructions - specify the number of instructions that should be ran
     */
    public void run(boolean showStatus, int numInstructions) {
        int currentInstruction = 0;
        int currentClockCycle = 0;
        while (currentInstruction < numInstructions
                || numInstructions == -1) {
            clockInput.setData(new boolean[]{true});
            ProcessRunner.run(clockInput);
            clockInput.setData(new boolean[]{false});
            ProcessRunner.run(clockInput);
            currentClockCycle++;
            clockInput.setData(new boolean[]{true});
            ProcessRunner.run(clockInput);
            clockInput.setData(new boolean[]{false});
            ProcessRunner.run(clockInput);
            currentClockCycle++;

            if (this.controlUnit.getMicroinstruction().getData()[0]) {
                currentInstruction++;
                if (showStatus) {
                    System.out.println("Executed clock cycles: " + currentClockCycle);
                    System.out.println("Executed instructions: " + currentInstruction);
                    System.out.println(this.requestStatus());
                }
            }
        }
    }

    public void run(boolean showStatus) {
        this.run(showStatus, -1);
    }

    /**Build the circuit as defined in the documentation.
     */
    @Override
    public void build() {
        boolean debugGates = this.debugDepth > 0 ? this.inDebuggerMode : false;
        int size = Microprocessor.WORD_SIZE;

        bus = new BitStream(size);

        BitStream RFIn = new BitStream(1);
        BitStream RFOut = new BitStream(1);
        BitStream rfAddrWrite = new BitStream(3);
        BitStream rfAddrRead = new BitStream(3);
        XIn = new BitStream(1);
        BitStream MUXConst = new BitStream(1);
        BitStream ALUOpcode = new BitStream(5);
        ZIn = new BitStream(1);
        ZOut = new BitStream(1);
        BitStream PCIn = new BitStream(1);
        BitStream PCOut = new BitStream(1);
        BitStream memAddress = new BitStream(1);
        BitStream memDataIn = new BitStream(1);
        BitStream memDataOut = new BitStream(1);

        BitStream status = new BitStream(4);

        this.controlUnit = new ControlUnit(bus, this.clock, status, bus, RFIn, RFOut, rfAddrWrite, rfAddrRead,
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
        this.X = new Register(bus, xOutput, XIn, xOut, xEnable, "X", debugGates, this.debugDepth - 1);

        BitStream constant = new BitStream(size);
        constant.setData(new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, true});

        List<BitStream> aluMuxInputList = new ArrayList<>();
        aluMuxInputList.addAll(List.of(xOutput, constant));
        BitStream aluMuxOut = new BitStream(size);
        Multiplexer ALUMux = new Multiplexer(aluMuxInputList, MUXConst, aluMuxOut,
                "ALUMux", debugGates, this.debugDepth - 1);

        BitStream aluOutput = new BitStream(size);
        this.alu = new ALU(bus, aluMuxOut, aluOutput, ALUOpcode, status,
                "ALU", debugGates, this.debugDepth - 1);

        BitStream zEnable = new BitStream(1);
        zEnable.setData(new boolean[]{true});
        this.Z = new Register(aluOutput, bus, ZIn, ZOut, zEnable, "Z", debugGates, this.debugDepth - 1);
    }
}
