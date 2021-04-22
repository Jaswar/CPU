package main.circuits;

import main.BitStream;
import main.control.Splitter;
import main.exceptions.BitStreamInputSizeMismatch;
import main.gates.multi.MultiAND;
import main.gates.unary.NOT;
import main.utils.DataConverter;

import java.util.ArrayList;
import java.util.List;

public class Decoder implements Circuit {

    private final BitStream input;
    private final List<BitStream> outputs;
    private final String name;
    private final boolean inDebuggerMode;
    private final int debugDepth;

    /**Constructors for the Decoder class. See documentation/Decoder.png for the build.
     *
     * @param input - the input BitStream
     * @param outputs - the list of output BitStreams
     * @param name - the name of the decoder
     * @param inDebuggerMode - boolean to specify if the circuit is in debug mode
     * @param debugDepth - how deep should debugging go
     */
    public Decoder(BitStream input, List<BitStream> outputs,
                   String name, boolean inDebuggerMode, int debugDepth) {
        this.input = input;
        this.outputs = outputs;
        this.name = name;
        this.inDebuggerMode = inDebuggerMode;
        this.debugDepth = debugDepth;

        this.build();
    }

    public Decoder(BitStream input, List<BitStream> outputs,
                   String name) {
        this(input, outputs, name, false, 0);
    }

    public Decoder(BitStream input, List<BitStream> outputs,
                   boolean inDebuggerMode, int debugDepth) {
        this(input, outputs, "Decoder", inDebuggerMode, debugDepth);
    }

    public Decoder(BitStream input, List<BitStream> outputs) {
        this(input, outputs, "Decoder", false, 0);
    }

    /**Builds the decoder circuit for the given input and the list of outputs.
     * The sizes must match, ie: #outputs = 2^#input, otherwise an error is thrown.
     */
    public void build() {
        boolean debugGates = this.debugDepth > 0 ? this.inDebuggerMode : false;
        if (this.outputs.size() != 1 << this.input.getSize()) {
            throw new BitStreamInputSizeMismatch(this);
        }

        List<BitStream> splitterInput = new ArrayList<>();
        splitterInput.add(this.input);

        List<BitStream> splitterOutput = new ArrayList<>();
        for (int i = 0; i < this.input.getSize(); i++) {
            splitterOutput.add(new BitStream(1));
        }

        Splitter inputSplitter = new Splitter(splitterInput, splitterOutput, "inputSplitter", debugGates);

        for (int i = 0; i < this.outputs.size(); i++) {
            List<BitStream> andInputs = new ArrayList<>();
            for (BitStream splitterOut : splitterOutput) {
                andInputs.add(splitterOut);
            }

            String indexInBinary = DataConverter.convertUnsignedDecToBin(i);
            indexInBinary = this.padZeros(indexInBinary, this.input.getSize());

            for (int j = indexInBinary.length() - 1; j >= 0; j--) {
                if (indexInBinary.charAt(j) == '0') {
                    BitStream notOut = new BitStream(1);
                    NOT not = new NOT(andInputs.get(j), notOut,
                            "not" + i + (indexInBinary.length() - 1 - j), debugGates);
                    andInputs.set(j, notOut);
                }
            }

            MultiAND and = new MultiAND(andInputs, this.outputs.get(i), "and" + i, debugGates);
        }
    }

    /**Method to pad a binary to number to a specific length.
     * Eg: padZeros("1001", 6) = "001001".
     *
     * @param binary - the binary number to pad with zeros
     * @param size - the desired size of the binary number
     * @return - the padded binary
     */
    private String padZeros(String binary, int size) {
        String outString = binary;
        for (int i = 0; i < size - binary.length(); i++) {
            outString = "0" + outString;
        }
        return outString;
    }
}
