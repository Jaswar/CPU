package main.circuits;

import main.BitStream;
import main.Node;
import main.control.Splitter;
import main.gates.binary.AND;
import main.gates.multi.MultiOR;
import main.gates.binary.XOR;

import java.util.ArrayList;
import java.util.List;

public class AddSubtract implements Circuit {

    /**Circuit to perform addition and subtraction of two numbers.
     *
     * @param source - the source BitStream
     * @param destination - the destination BitStream
     * @param out - the output BitStream
     * @param control - the BitStream that specifies if addition or subtraction is performed
     *                (0 for addition, 1 for subtraction)
     * @param overflow - BitStream to specify if overflow occurs
     * @param name - the name of the circuit
     * @param inDebuggerMode - boolean to specify if the circuit should be evaluated in the debug mode
     * @param debugDepth - how deep should the debug go (in how many levels of circuitry)
     */
    private BitStream source, destination, out, control, overflow;
    private String name;
    private boolean inDebuggerMode;
    private int debugDepth;

    /**Constructors for the AddSubtract circuit.
     *
     * @param source - the source BitStream
     * @param destination - the destination BitStream
     * @param out - the output BitStream
     * @param control - the BitStream that specifies if addition or subtraction is performed
     *                (0 for addition, 1 for subtraction)
     * @param overflow - BitStream to specify if overflow occurs
     * @param name - the name of the circuit
     * @param inDebuggerMode - boolean to specify if the circuit should be evaluated in the debug mode
     * @param debugDepth - how deep should the debug go (in how many levels of circuitry)
     */
    public AddSubtract(BitStream source, BitStream destination,
                       BitStream out, BitStream control,
                       BitStream overflow, String name,
                       boolean inDebuggerMode, int debugDepth) {
        this.source = source;
        this.destination = destination;
        this.out = out;
        this.control = control;
        this.overflow = overflow;
        this.name = name;
        this.inDebuggerMode = inDebuggerMode;
        this.debugDepth = debugDepth;

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

    /**Getters for all the attributes of the class.
     */
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

    /**Setters for some of the attributes. Setting BitStreams is not possible.
     */
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

    /**Method to build the circuit as defined in documentation/addSub.png.
     */
    @Override
    public void build() {
        boolean debugGates = this.debugDepth > 0 ? this.inDebuggerMode : false;
        int size = this.source.getSize();

        //First indexes -> least significant bits
        List<BitStream> srcInList = new ArrayList<>();
        srcInList.add(this.source);

        List<BitStream> srcOutList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            BitStream bit = new BitStream(1);
            srcOutList.add(bit);
        }

        List<BitStream> dstInList = new ArrayList<>();
        dstInList.add(this.destination);

        List<BitStream> dstOutList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            BitStream bit = new BitStream(1);
            dstOutList.add(bit);
        }

        List<BitStream> outList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            BitStream bit = new BitStream(1);
            outList.add(bit);
        }

        BitStream carryIn = this.control;
        BitStream lastCarryIn = new BitStream(1);
        for (int i = 0; i < size; i++) {
            BitStream revXorLXor = new BitStream(1);
            BitStream lXorUXor = new BitStream(1);
            BitStream and0Or = new BitStream(1);
            BitStream and1Or = new BitStream(1);
            BitStream and2Or = new BitStream(1);

            List<BitStream> andOr = new ArrayList<>();
            andOr.addAll(List.of(and0Or, and1Or, and2Or));

            XOR revertXor = new XOR(this.control, dstOutList.get(size - i - 1), revXorLXor, "revXor" + i, debugGates);
            XOR lowerXor = new XOR(revXorLXor, srcOutList.get(size - i - 1), lXorUXor, "lXor" + i, debugGates);
            XOR upperXor = new XOR(carryIn, lXorUXor, outList.get(size - i - 1), "uXor" + i, debugGates);
            AND and0 = new AND(carryIn, revXorLXor, and0Or, "and" + i + "_0", debugGates);
            AND and1 = new AND(srcOutList.get(size - i - 1), carryIn, and1Or, "and" + i + "_1", debugGates);
            AND and2 = new AND(srcOutList.get(size - i - 1), revXorLXor, and2Or, "and" + i + "_2", debugGates);

            carryIn = new BitStream(1);

            MultiOR or = new MultiOR(andOr, carryIn, "or" + i, debugGates);

            if (i == size - 2) {
                lastCarryIn = carryIn;
            }
            if (i == size - 1) {
                XOR overflowXor = new XOR(lastCarryIn, carryIn, this.overflow, "overXor", debugGates);
            }
        }

        Splitter sourceSplitter = new Splitter(srcInList, srcOutList, "Source splitter", debugGates);
        Splitter destinationSplitter = new Splitter(dstInList, dstOutList, "Destination splitter", debugGates);

        List<BitStream> outBitStreamList = new ArrayList<>();
        outBitStreamList.add(this.out);

        Splitter outputSplitter = new Splitter(outList, outBitStreamList, "Output splitter", debugGates);
    }
}

