package main.circuits;

import main.BitStream;
import main.control.Splitter;

import java.util.ArrayList;
import java.util.List;

public class Microprocessor implements Circuit {

    private BitStream input, clock, reset,  IR1In, IR2In,
            microinstruction, intermediate, source, destination;
    private String name;
    private boolean inDebuggerMode;
    private int debugDepth;

    public static final int WORD_SIZE = 16;
    public static final int INSTRUCTION_SIZE = 8;

    public Microprocessor(BitStream input, BitStream clock, BitStream reset, BitStream IR1In, BitStream IR2In,
                          BitStream microinstruction, BitStream intermediate, BitStream source, BitStream destination,
                          String name, boolean inDebuggerMode, int debugDepth) {
        this.input = input;
        this.clock = clock;
        this.reset = reset;
        this.IR1In = IR1In;
        this.IR2In = IR2In;

        this.microinstruction = microinstruction;
        this.intermediate = intermediate;
        this.source = source;
        this.destination = destination;

        this.name = name;
        this.inDebuggerMode = inDebuggerMode;
        this.debugDepth = debugDepth;

        this.build();
    }

    public Microprocessor(BitStream input, BitStream clock, BitStream reset, BitStream IR1In, BitStream IR2In,
                          BitStream microinstruction, BitStream intermediate, BitStream source, BitStream destination,
                          String name) {
        this(input, clock, reset, IR1In, IR2In, microinstruction, intermediate, source, destination,
                name, false, 0);
    }

    public Microprocessor(BitStream input, BitStream clock, BitStream reset, BitStream IR1In, BitStream IR2In,
                          BitStream microinstruction, BitStream intermediate, BitStream source, BitStream destination,
                          boolean inDebuggerMode, int debugDepth) {
        this(input, clock, reset, IR1In, IR2In, microinstruction, intermediate, source, destination,
                "Microprocessor", inDebuggerMode, debugDepth);
    }

    public Microprocessor(BitStream input, BitStream clock, BitStream reset, BitStream IR1In, BitStream IR2In,
                          BitStream microinstruction, BitStream intermediate, BitStream source, BitStream destination) {
        this(input, clock, reset, IR1In, IR2In, microinstruction, intermediate, source, destination,
                "Microprocessor", false, 0);
    }

    public BitStream getInput() {
        return input;
    }

    public BitStream getClock() {
        return clock;
    }

    public BitStream getReset() {
        return reset;
    }

    public BitStream getIR1In() {
        return IR1In;
    }

    public BitStream getIR2In() {
        return IR2In;
    }

    public BitStream getMicroinstruction() {
        return microinstruction;
    }

    public BitStream getIntermediate() {
        return intermediate;
    }

    public BitStream getSource() {
        return source;
    }

    public BitStream getDestination() {
        return destination;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setInDebuggerMode(boolean inDebuggerMode) {
        this.inDebuggerMode = inDebuggerMode;
    }

    public void setDebugDepth(int debugDepth) {
        this.debugDepth = debugDepth;
    }

    @Override
    public void build() {
        boolean debugGates = this.debugDepth > 0 ? this.inDebuggerMode : false;

        BitStream IR1Out = new BitStream(WORD_SIZE);
        BitStream IR1OutControl = new BitStream(1);
        IR1OutControl.setData(new boolean[]{true});
        BitStream IR1Enable = new BitStream(1);
        IR1Enable.setData(new boolean[]{true});
        Register IR1 = new Register(this.input, IR1Out, this.IR1In, IR1OutControl, IR1Enable,
                "IR1", debugGates, this.debugDepth - 1);

        BitStream IR2OutControl = new BitStream(1);
        IR2OutControl.setData(new boolean[]{true});
        BitStream IR2Enable = new BitStream(1);
        IR2Enable.setData(new boolean[]{true});
        Register IR2 = new Register(this.input, this.intermediate, this.IR2In, IR2OutControl, IR2Enable,
                "IR2", debugGates, this.debugDepth - 1);

        List<BitStream> instructionSplitterOutList = new ArrayList<>();
        BitStream instruction = new BitStream(INSTRUCTION_SIZE);
        BitStream empty = new BitStream(WORD_SIZE - this.source.getSize()
                - this.destination.getSize() - INSTRUCTION_SIZE);
        instructionSplitterOutList.addAll(List.of(instruction, this.source, this.destination, empty));
        List<BitStream> instructionSplitterInputList = new ArrayList<>();
        instructionSplitterInputList.add(IR1Out);

        Splitter instructionSplitter = new Splitter(instructionSplitterInputList, instructionSplitterOutList,
                "instructionSplitter", debugGates);


    }
}
