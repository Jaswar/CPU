package main.circuits;

import main.BitStream;
import main.control.Splitter;
import main.gates.binary.AND;
import main.gates.multi.MultiOR;

import java.util.ArrayList;
import java.util.List;

public class Multiplexer implements Circuit {

    private final List<BitStream> input;
    private final BitStream output, select;
    private final String name;
    private final boolean inDebuggerMode;
    private final int debugDepth;

    /**Constructors for the Multiplexer class.
     *
     * @param input - list of the inputs to the multiplexer
     * @param select - BitStream to specify which input should be selected
     * @param output - the selected output
     * @param name - the name of the gate
     * @param inDebuggerMode - boolean to specify if the multiplexer is in debug mode
     * @param debugDepth - how deep should the debugging of gates go
     */
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

    /**Define the build method for the Multiplexer as described in the documentation.
     */
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
                "selectDecoder in " + this.name, debugGates, this.debugDepth - 1);

        List<BitStream> andOutputs = new ArrayList<>();
        for (int i = 0; i < this.input.size(); i++) {
            List<BitStream> splitterIn = new ArrayList<>();
            splitterIn.add(decoderOut.get(i));

            List<BitStream> splitterOut = new ArrayList<>();
            BitStream splitterOutStream = new BitStream(size);
            splitterOut.add(splitterOutStream);

            Splitter splitter = new Splitter(splitterIn, splitterOut, "splitter" + i + " in " + this.name, debugGates);

            BitStream andOutput = new BitStream(size);
            AND inAnd = new AND(this.input.get(i), splitterOutStream, andOutput, "in" + i + "And in " + this.name, debugGates);

            andOutputs.add(andOutput);
        }

        MultiOR mainOr = new MultiOR(andOutputs, this.output, "mainOr in " + this.name, debugGates);
    }
}
