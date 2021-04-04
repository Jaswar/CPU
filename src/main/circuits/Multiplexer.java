package main.circuits;

import main.BitStream;
import main.control.Splitter;
import main.gates.binary.AND;
import main.gates.multi.MultiOR;

import java.util.ArrayList;
import java.util.List;

public class Multiplexer implements Circuit {

    private List<BitStream> input;
    private BitStream output, select;
    private String name;
    private boolean inDebuggerMode;
    private int debugDepth;

    public Multiplexer(List<BitStream> input, BitStream select, BitStream output,
                       String name, boolean inDebuggerMode, int debugDepth) {
        this.input = input;
        this.select = select;
        this.output = output;

        this.name = name;
        this.inDebuggerMode = inDebuggerMode;
        this.debugDepth = debugDepth;

        this.build();
    }

    public Multiplexer(List<BitStream> input, BitStream select, BitStream output,
                       String name) {
        this(input, select, output, name, false, 0);
    }

    public Multiplexer(List<BitStream> input, BitStream select, BitStream output,
                       boolean inDebuggerMode, int debugDepth) {
        this(input, select, output, "Multiplexer", false, 0);
    }

    public Multiplexer(List<BitStream> input, BitStream select, BitStream output) {
        this(input, select, output, "Multiplexer", false, 0);
    }

    public List<BitStream> getInput() {
        return input;
    }

    public BitStream getOutput() {
        return output;
    }

    public BitStream getSelect() {
        return select;
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
        int size = this.output.getSize();

        List<BitStream> decoderOut = new ArrayList<>();
        for (int i = 0; i < this.input.size(); i++) {
            BitStream newOut = new BitStream(1);
            decoderOut.add(newOut);
        }

        Decoder selectDecoder = new Decoder(this.select, decoderOut,
                "selectDecoder", debugGates, this.debugDepth - 1);

        List<BitStream> andOutputs = new ArrayList<>();
        for (int i = 0; i < this.input.size(); i++) {
            List<BitStream> splitterIn = new ArrayList<>();
            splitterIn.add(decoderOut.get(i));

            List<BitStream> splitterOut = new ArrayList<>();
            BitStream splitterOutStream = new BitStream(size);
            splitterOut.add(splitterOutStream);

            Splitter splitter = new Splitter(splitterIn, splitterOut, "splitter" + i, debugGates);

            BitStream andOutput = new BitStream(size);
            AND inAnd = new AND(this.input.get(i), splitterOutStream, andOutput, "in" + i + "And", debugGates);

            andOutputs.add(andOutput);
        }

        MultiOR mainOr = new MultiOR(andOutputs, this.output, "mainOr", debugGates);
    }
}
