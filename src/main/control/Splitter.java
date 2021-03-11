package main.control;

import main.BitStream;
import main.Node;
import main.exceptions.IllegalSplitException;
import main.exceptions.InconsistentBitStreamSources;
import main.utils.BitInformationConverter;

import java.util.ArrayList;
import java.util.List;

public class Splitter implements Node {

    private List<BitStream> in;
    private List<BitStream> out;
    private String name;
    private boolean inDebuggerMode;

    public Splitter(List<BitStream> in, List<BitStream> out, String name, boolean inDebuggerMode) {
        this.in = in;
        for (BitStream inStream : this.in) {
            inStream.addNewEndpoint(this);
        }

        this.out = out;
        for (BitStream outStream : this.out) {
            outStream.addNewEndpoint(this);
        }

        this.name = name;
        this.inDebuggerMode = inDebuggerMode;

        this.setup();
    }

    public Splitter(List<BitStream> in, List<BitStream> out, String name) {
        this(in, out, name, false);
    }

    public Splitter(List<BitStream> in, List<BitStream> out, boolean inDebuggerMode) {
        this(in, out, "Splitter", inDebuggerMode);
    }

    public Splitter(List<BitStream> in, List<BitStream> out) {
        this(in, out, "Splitter", false);
    }

    public List<BitStream> getIn() {
        return in;
    }

    public List<BitStream> getOut() {
        return out;
    }

    public String getName() {
        return name;
    }

    public boolean isInDebuggerMode() {
        return inDebuggerMode;
    }

    public void setIn(List<BitStream> in) {
        for (BitStream inStream : this.in) {
            inStream.removeEndpoint(this);
        }

        this.in = in;
        for (BitStream inStream : this.in) {
            inStream.addNewEndpoint(this);
        }

        this.setup();
    }

    public void setOut(List<BitStream> out) {
        for (BitStream outStream : this.out) {
            outStream.removeEndpoint(this);
        }

        this.out = out;
        for (BitStream outStream : this.out) {
            outStream.addNewEndpoint(this);
        }

        this.setup();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInDebuggerMode(boolean inDebuggerMode) {
        this.inDebuggerMode = inDebuggerMode;
    }

    private int getBitStreamListSize(List<BitStream> bitStreams) {
        int size = 0;
        for (BitStream stream : bitStreams) {
            size += stream.getSize();
        }
        return size;
    }

    @Override
    public void setup() {
        boolean temp = this.inDebuggerMode;
        this.inDebuggerMode = false;

        List<Node> queue = new ArrayList<>();
        queue.add(this);
        while (queue.size() > 0) {
            Node node = queue.remove(0);
            node.evaluate(queue);
        }

        this.inDebuggerMode = temp;
    }

    @Override
    public void evaluate(List<Node> queue) {
        this.checkIfSizesMatch();

        int inSize = this.getBitStreamListSize(this.in);
        boolean[] newOutData = new boolean[inSize];

        int count = 0;
        for (BitStream inStream : this.in) {
            for (boolean bit : inStream.getData()) {
                newOutData[count++] = bit;
            }
        }

        this.checkIfSourceIsConsistent(newOutData);

        if (this.decideIfEvaluateFurther(newOutData)) {
            this.addNeighboursToQueue(queue);
        }

        this.setOutData(newOutData);
        this.setSourceForOutStream();

        if (this.inDebuggerMode) {
            this.debug();
        }
    }

    @Override
    public void checkIfSizesMatch() {
        int inSize = this.getBitStreamListSize(this.in);
        int outSize = this.getBitStreamListSize(this.out);
        if (inSize != outSize) {
            throw new IllegalSplitException("Impossible split at: " + this.toString()
                    + ". Input size was " + inSize + " output size was " + outSize + ".");
        }
    }

    @Override
    public void checkIfSourceIsConsistent(boolean[] newOutData) {
        int count = 0;
        for (BitStream outStream : this.out) {
            if (outStream.getSource() != null && outStream.getSource() != this) {
                for (boolean bit : outStream.getData()) {
                    if (newOutData[count++] != bit) {
                        throw new InconsistentBitStreamSources("Inconsistency detected at "
                                + this.toString() + " -> " + BitInformationConverter.convertBoolToBits(newOutData)
                                + " and " + BitInformationConverter.convertBoolToBits(outStream.getData()));
                    }
                }
            } else {
                count += outStream.getSize();
            }
        }
    }

    @Override
    public boolean decideIfEvaluateFurther(boolean[] newOutData) {
        int outSize = this.getBitStreamListSize(this.out);
        boolean[] oldOutData = new boolean[outSize];

        int count = 0;
        for (BitStream outStream : this.out) {
            for (boolean bit : outStream.getData()) {
                oldOutData[count++] = bit;
            }
        }

        for (int i = 0; i < outSize; i++) {
            if (newOutData[i] != oldOutData[i]) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addNeighboursToQueue(List<Node> queue) {
        for (BitStream outStream : this.out) {
            queue.addAll(outStream.getAllNeighbours(this));
        }
    }

    private void setOutData(boolean[] newOutData) {
        int count = 0;
        for (BitStream outStream : this.out) {
            boolean[] streamOutData = new boolean[outStream.getSize()];
            for (int i = 0; i < outStream.getSize(); i++) {
                streamOutData[i] = newOutData[count++];
            }
            outStream.setData(streamOutData);
        }
    }

    @Override
    public void setSourceForOutStream() {
        for (BitStream outStream : this.out) {
            outStream.setSource(this);
        }
    }

    @Override
    public void debug() {
        String msg = "Evaluating " + this.name + ":\n\tInputs:\n";
        for (BitStream inStream : this.in) {
            msg += "\t\t" + BitInformationConverter.convertBoolToBits(inStream.getData()) + "\n";
        }
        msg += "\tOutputs:\n";
        for (BitStream outStream : this.out) {
            msg += "\t\t" + BitInformationConverter.convertBoolToBits(outStream.getData()) + "\n";
        }
        System.out.println(msg);
    }

}
