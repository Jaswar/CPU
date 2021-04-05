package main.memory;

import main.BitStream;
import main.Node;
import main.exceptions.BitStreamInputSizeMismatch;
import main.exceptions.InconsistentBitStreamSources;
import main.utils.DataConverter;
import main.utils.ProcessRunner;
import main.warnings.InconsistentBitStreamSourcesWarning;

import javax.xml.crypto.Data;
import java.beans.BeanInfo;
import java.util.Arrays;
import java.util.List;


public class RAM implements Node {

    private BitStream address, dataIn, dataOut, read, write;
    private String name;
    private boolean inDebuggerMode;

    private boolean lastWriteSignal;
    private boolean[][] data;

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

        this.lastWriteSignal = false;

        this.data = new boolean[1 << this.address.getSize()][this.dataOut.getSize()];

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

    @Override
    public void setup() {
        this.checkIfSizesMatch();
        ProcessRunner.run(this);
    }

    @Override
    public void checkIfSizesMatch() {
        if (this.dataOut.getSize() != this.dataIn.getSize()
                || this.write.getSize() != 1
                || this.read.getSize() != 1) {
            throw new BitStreamInputSizeMismatch(this);
        }
    }

    @Override
    public void evaluate(List<Node> queue) {
        if (!this.write.getData()[0] && this.lastWriteSignal) {
            this.data[DataConverter.convertBoolToUnsignedDec(this.address.getData())] = this.dataIn.getData().clone();
        }
        this.lastWriteSignal = this.write.getData()[0];

        boolean[] newOutData = this.dataOut.getData();

        if (this.read.getData()[0]) {
            newOutData = this.data[DataConverter.convertBoolToUnsignedDec(this.address.getData())];
        }

        this.checkIfSourceIsConsistent(newOutData);

        if (this.decideIfEvaluateFurther(newOutData)) {
            this.addNeighboursToQueue(queue);
        }

        this.dataOut.setData(newOutData);
        this.setSourceForOutStream();

        if (this.isInDebuggerMode()) {
            this.debug();
        }
    }

    @Override
    public void checkIfSourceIsConsistent(boolean[] newOutData) {
        if (this.dataOut.getSource() != null && this.dataOut.getSource() != this) {
            for (int i = 0; i < newOutData.length; i++) {
                if (this.dataOut.getData()[i] != newOutData[i]) {
                    InconsistentBitStreamSourcesWarning.show(this.dataOut.getSource(), this);
                    break;
                }
            }
        }
    }

    @Override
    public boolean decideIfEvaluateFurther(boolean[] newOutData) {
        for (int i = 0; i < newOutData.length; i++) {
            if (this.dataOut.getData()[i] != newOutData[i]) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addNeighboursToQueue(List<Node> queue) {
        queue.addAll(this.dataOut.getAllNeighbours(this));
    }

    @Override
    public void setSourceForOutStream() {
        this.dataOut.setSource(this);
    }

    @Override
    public void debug() {
        System.out.println("Evaluating " + this.name + ":\n"
                + "Address: " + DataConverter.convertBoolToBin(this.address.getData())
                + "\nData in: " + DataConverter.convertBoolToBin(this.dataIn.getData())
                + "\nData out: " + DataConverter.convertBoolToBin(this.dataOut.getData()));
    }

    @Override
    public String toString() {
        return "RAM<" + this.name + ", " + DataConverter.convertBoolToBin(this.address.getData()) +
                ", " + DataConverter.convertBoolToBin(this.dataIn.getData()) +
                ", " + DataConverter.convertBoolToBin(this.dataOut.getData()) + ">";
    }

}
