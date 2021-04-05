package main.memory;

import main.BitStream;
import main.Node;

import java.beans.BeanInfo;


public class RAM implements Node {

    private BitStream address, dataIn, dataOut, read, write;
    private String name;
    private boolean inDebuggerMode;

    public RAM(BitStream address, BitStream dataIn, BitStream dataOut, BitStream write, BitStream read,
               String name, boolean inDebuggerMode) {
        this.address = address;
        this.address.addNewEndpoint(this);

        this.dataIn = dataIn;
        this.dataIn.addNewEndpoint(this);

        this.dataOut = dataOut;
        this.dataOut.addNewEndpoint(this);

        this.write = write;
        this.write.addNewEndpoint(this);

        this.read = read;
        this.read.addNewEndpoint(this);

        this.name = name;
        this.inDebuggerMode = inDebuggerMode;

        this.setup();
    }

    public RAM(BitStream address, BitStream dataIn, BitStream dataOut, BitStream write, BitStream read,
               String name) {
        this(address, dataIn, dataOut, write, read, name, false);
    }

    public RAM(BitStream address, BitStream dataIn, BitStream dataOut, BitStream write, BitStream read,
               boolean inDebuggerMode) {
        this(address, dataIn, dataOut, write, read, "RAM", inDebuggerMode);
    }

    public RAM(BitStream address, BitStream dataIn, BitStream dataOut, BitStream write, BitStream read) {
        this(address, dataIn, dataOut, write, read, "RAM", false);
    }

    public BitStream getAddress() {
        return address;
    }

    public BitStream getDataIn() {
        return dataIn;
    }

    public BitStream getDataOut() {
        return dataOut;
    }

    public BitStream getRead() {
        return read;
    }

    public BitStream getWrite() {
        return write;
    }

    public String getName() {
        return name;
    }

    public boolean isInDebuggerMode() {
        return inDebuggerMode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInDebuggerMode(boolean inDebuggerMode) {
        this.inDebuggerMode = inDebuggerMode;
    }


}
