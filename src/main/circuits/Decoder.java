package main.circuits;

import main.BitStream;
import main.control.Splitter;
import main.exceptions.BitStreamInputSizeMismatch;
import main.gates.binary.AND;
import main.gates.multi.MultiAND;
import main.gates.unary.NOT;
import main.utils.DataConverter;

import java.util.ArrayList;
import java.util.List;

public class Decoder implements Circuit {

    private BitStream input;
    private List<BitStream> outputs;
    private String name;
    private boolean inDebuggerMode;
    private int debugDepth;

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

    public BitStream getInputs() {
        return input;
    }

    public List<BitStream> getOutputs() {
        return outputs;
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

    private String padZeros(String binary, int size) {
        String outString = binary;
        for (int i = 0; i < size - binary.length(); i++) {
            outString = "0" + outString;
        }
        return outString;
    }
}
