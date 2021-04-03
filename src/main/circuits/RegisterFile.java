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
            BitStream regIn = new BitStream(1);
            BitStream regOut = new BitStream(1);

            AND regInAnd = new AND(this.RFIn, addrWriteDecoderOutList.get(i), regIn, "regInAnd" + i, debugGates);
            AND regOutAnd = new AND(this.RFOut, addrReadDecoderOutList.get(i), regOut, "regOutAnd" + i, debugGates);

            Register register = new Register(this.input, this.output, regIn, regOut, "reg" + i, debugGates, this.debugDepth - 1);
        }
    }
}
