package main.circuits;


import main.BitStream;
import main.gates.binary.AND;

import java.util.ArrayList;
import java.util.List;

public class RegisterFile implements Circuit {

    private BitStream input, output, RFIn, RFOut, addressWrite, addressRead;
    private String name;
    private boolean inDebuggerMode;
    private int debugDepth;

    /**Constructors for the RegisterFile class.
     *
     * @param input - the input to the register file
     * @param output - the output from the register file
     * @param RFIn - BitStream to specify if values are written to the register file
     * @param RFOut - BitStream to specify if values are read from the register file
     * @param addressWrite - control which register is written to
     * @param addressRead - control which register outputs its value
     * @param name - the name of the circuit
     * @param inDebuggerMode - boolean to specify if the circuit is in the debug mode
     * @param debugDepth - how deep should debugging go
     */
    public RegisterFile(BitStream input, BitStream output, BitStream RFIn, BitStream RFOut,
                        BitStream addressWrite, BitStream addressRead, String name,
                        boolean inDebuggerMode, int debugDepth) {
        this.input = input;
        this.output = output;
        this.RFIn = RFIn;
        this.RFOut = RFOut;
        this.addressWrite = addressWrite;
        this.addressRead = addressRead;

        this.name = name;
        this.inDebuggerMode = inDebuggerMode;
        this.debugDepth = debugDepth;

        this.build();
    }

    public RegisterFile(BitStream input, BitStream output, BitStream RFIn, BitStream RFOut,
                        BitStream addressWrite, BitStream addressRead, String name) {
        this(input, output, RFIn, RFOut, addressWrite, addressRead, name, false, 0);
    }

    public RegisterFile(BitStream input, BitStream output, BitStream RFIn, BitStream RFOut,
                        BitStream addressWrite, BitStream addressRead,
                        boolean inDebuggerMode, int debugDepth) {
        this(input, output, RFIn, RFOut, addressWrite, addressRead, "RegisterFile", inDebuggerMode, debugDepth);
    }

    public RegisterFile(BitStream input, BitStream output, BitStream RFIn, BitStream RFOut,
                        BitStream addressWrite, BitStream addressRead) {
        this(input, output, RFIn, RFOut, addressWrite, addressRead, "RegisterFile", false, 0);
    }

    /**Getters for all the attributes.
     */
    public BitStream getInput() {
        return input;
    }

    public BitStream getOutput() {
        return output;
    }

    public BitStream getRFIn() {
        return RFIn;
    }

    public BitStream getRFOut() {
        return RFOut;
    }

    public BitStream getAddressWrite() {
        return addressWrite;
    }

    public BitStream getAddressRead() {
        return addressRead;
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

    /**Setters for some of the attributes. Setting BitStreams is not possible.
     */
    public void setName(String name) {
        this.name = name;
    }

    public void setInDebuggerMode(boolean inDebuggerMode) {
        this.inDebuggerMode = inDebuggerMode;
    }

    public void setDebugDepth(int debugDepth) {
        this.debugDepth = debugDepth;
    }

    /**Define the build method to construct the circuit as described in the documentation.
     */
    @Override
    public void build() {
        boolean debugGates = this.debugDepth > 0 ? this.inDebuggerMode : false;
        int size = this.input.getSize();
        int registerCount = 1 << this.addressRead.getSize();

        List<BitStream> addrWriteDecoderOutList = new ArrayList<>();
        List<BitStream> addrReadDecoderOutList = new ArrayList<>();
        for (int i = 0; i < registerCount; i++) {
            BitStream addrWriteDecoderOut = new BitStream(1);
            BitStream addrReadDecoderOut = new BitStream(1);

            addrWriteDecoderOutList.add(addrWriteDecoderOut);
            addrReadDecoderOutList.add(addrReadDecoderOut);
        }

        Decoder addrWriteDecoder = new Decoder(this.addressWrite, addrWriteDecoderOutList,
                "addrWriteDecoder", debugGates, this.debugDepth - 1);
        Decoder addrReadDecoder = new Decoder(this.addressRead, addrReadDecoderOutList,
                "addrReadDecoder", debugGates, this.debugDepth - 1);

        for (int i = 0; i < registerCount; i++) {
            BitStream regOut = new BitStream(1);

            AND regOutAnd = new AND(this.RFOut, addrReadDecoderOutList.get(i), regOut, "regOutAnd" + i, debugGates);

            Register register = new Register(this.input, this.output, this.RFIn, regOut, addrWriteDecoderOutList.get(i),
                    "reg" + i, debugGates, this.debugDepth - 1);
        }
    }
}
