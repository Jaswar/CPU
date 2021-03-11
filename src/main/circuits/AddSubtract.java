package main.circuits;

import main.BitStream;
import main.Node;
import main.control.Splitter;
import main.gates.AND;
import main.gates.OR;
import main.gates.XOR;

import java.util.ArrayList;
import java.util.List;

public class AddSubtract implements Node {

    private BitStream source, destination, out, control, overflow;
    private String name;
    private boolean inDebuggerMode;
    private int debugDepth;

    public AddSubtract(BitStream source, BitStream destination,
                       BitStream out, BitStream control,
                       BitStream overflow, String name,
                       boolean inDebuggerMode, int debugDepth) {
        this.source = source;
        this.source.addNewEndpoint(this);

        this.destination = destination;
        this.destination.addNewEndpoint(this);

        this.out = out;
        this.out.addNewEndpoint(this);

        this.control = control;
        this.control.addNewEndpoint(this);

        this.overflow = overflow;
        this.overflow.addNewEndpoint(this);

        this.name = name;
        this.inDebuggerMode = inDebuggerMode;
        this.debugDepth = debugDepth;

        this.setup();
        this.build();
    }

    public AddSubtract(BitStream source, BitStream destination,
                       BitStream out, BitStream control,
                       BitStream overflow, String name) {
        this(source, destination, out, control, overflow, name, false, 0);
    }

    public AddSubtract(BitStream source, BitStream destination,
                       BitStream out, BitStream control,
                       BitStream overflow,
                       boolean inDebuggerMode, int debugDepth) {
        this(source, destination, out, control, overflow, "ADD/SUB", inDebuggerMode, debugDepth);
    }

    public AddSubtract(BitStream source, BitStream destination,
                       BitStream out, BitStream control,
                       BitStream overflow) {
        this(source, destination, out, control, overflow, "ADD/SUB", false, 0);
    }

    public BitStream getSource() {
        return source;
    }

    public BitStream getDestination() {
        return destination;
    }

    public BitStream getOut() {
        return out;
    }

    public BitStream getControl() {
        return control;
    }

    public BitStream getOverflow() {
        return overflow;
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

    public void setSource(BitStream source) {
        this.source.removeEndpoint(this);

        this.source = source;
        this.source.addNewEndpoint(this);

        this.setup();
    }

    public void setDestination(BitStream destination) {
        this.destination.removeEndpoint(this);

        this.destination = destination;
        this.destination.addNewEndpoint(this);

        this.setup();
    }

    public void setOut(BitStream out) {
        this.out.removeEndpoint(this);

        this.out = out;
        this.out.addNewEndpoint(this);

        this.setup();
    }

    public void setControl(BitStream control) {
        this.control.removeEndpoint(this);

        this.control = control;
        this.control.addNewEndpoint(this);

        this.setup();
    }

    public void setOverflow(BitStream overflow) {
        this.overflow.removeEndpoint(this);

        this.overflow = overflow;
        this.overflow.addNewEndpoint(this);

        this.setup();
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
        boolean debugGates = this.debugDepth > 0 ? true : false;

        //First indexes -> least significant bits
        List<BitStream> srcInList = new ArrayList<>();
        srcInList.add(this.source);

        List<BitStream> srcOutList = new ArrayList<>();
        for (int i = 0; i < Node.WORD_SIZE; i++) {
            BitStream bit = new BitStream(1);
            srcOutList.add(bit);
        }

        List<BitStream> dstInList = new ArrayList<>();
        dstInList.add(this.destination);

        List<BitStream> dstOutList = new ArrayList<>();
        for (int i = 0; i < Node.WORD_SIZE; i++) {
            BitStream bit = new BitStream(1);
            dstOutList.add(bit);
        }

        Splitter sourceSplitter = new Splitter(srcInList, srcOutList, "Source splitter", debugGates);
        Splitter destinationSplitter = new Splitter(dstInList, dstOutList, "Destination splitter", debugGates);

        List<BitStream> outList = new ArrayList<>();
        for (int i = 0; i < Node.WORD_SIZE; i++) {
            BitStream bit = new BitStream(1);
            outList.add(bit);
        }

        BitStream carryIn = this.control;
        for (int i = 0; i < Node.WORD_SIZE; i++) {
            BitStream revXorLXor = new BitStream(1);
            BitStream lXorUXor = new BitStream(1);
            BitStream and0Or = new BitStream(1);
            BitStream and1Or = new BitStream(1);
            BitStream and2Or = new BitStream(1);

            XOR revertXor = new XOR(this.control, dstOutList.get(i), revXorLXor, "revXor" + i, debugGates);
            XOR lowerXor = new XOR(revXorLXor, srcOutList.get(i), lXorUXor, "lXor" + i, debugGates);
            XOR upperXor = new XOR(carryIn, lXorUXor, outList.get(i), "uXor" + i, debugGates);
            AND and0 = new AND(carryIn, revXorLXor, and0Or, "and0_" + i, debugGates);
            AND and1 = new AND(srcOutList.get(i), carryIn, and1Or, "and1_" + i, debugGates);
            AND and2 = new AND(srcOutList.get(i), revXorLXor, and2Or, "and2_" + i, debugGates);
            OR or1 = new OR();
        }
    }
}
