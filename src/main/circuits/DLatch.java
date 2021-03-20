package main.circuits;

import main.BitStream;
import main.control.Splitter;
import main.gates.binary.AND;
import main.gates.binary.NOR;
import main.gates.unary.NOT;

import java.util.ArrayList;
import java.util.List;

public class DLatch implements Circuit {

    /**DLatch implementation. See documentation/DLatch.png for exact build.
     *
     * @param D - the D input BitStream
     * @param enable - BitStream used to enable the latch
     * @param Q - the Q output of the latch
     * @param notQ - the reverse of the Q output
     * @param name - the name of the latch
     * @param inDebuggerMode - specify if the latch is in debug mode
     * @param debugDepth - how many circuits deep should debugging go
     */
    private BitStream D, enable, Q, notQ;
    private String name;
    private boolean inDebuggerMode;
    private int debugDepth;

    /**Constructors for the DLatch class.
     *
     * @param D - the D input BitStream
     * @param enable - BitStream used to enable the latch
     * @param Q - the Q output of the latch
     * @param notQ - the reverse of the Q output
     * @param name - the name of the latch
     * @param inDebuggerMode - specify if the latch is in debug mode
     * @param debugDepth - how many circuits deep should debugging go
     */
    public DLatch(BitStream D, BitStream enable, BitStream Q, BitStream notQ,
                  String name, boolean inDebuggerMode, int debugDepth) {
        this.D = D;
        this.enable = enable;
        this.Q = Q;
        this.notQ = notQ;
        this.name = name;
        this.inDebuggerMode = inDebuggerMode;
        this.debugDepth = debugDepth;

        this.build();
    }

    public DLatch(BitStream D, BitStream enable, BitStream Q, BitStream notQ,
                  String name) {
        this(D, enable, Q, notQ, name, false, 0);
    }

    public DLatch(BitStream D, BitStream enable, BitStream Q, BitStream notQ,
                  boolean inDebuggerMode, int debugDepth) {
        this(D, enable, Q, notQ, "DLatch", inDebuggerMode, debugDepth);
    }

    public DLatch(BitStream D, BitStream enable, BitStream Q, BitStream notQ) {
        this(D, enable, Q, notQ, "DLatch", false, 0);
    }

    /**Getters for all the attributes.
     */
    public BitStream getD() {
        return D;
    }

    public BitStream getEnable() {
        return enable;
    }

    public BitStream getQ() {
        return Q;
    }

    public BitStream getNotQ() {
        return notQ;
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

    /**Setters for some of the attributes.
     * Setting BitStreams is not possible.
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

    /**Build the DLatch circuit as defined in the documentation.
     */
    @Override
    public void build() {
        boolean debugGates = this.debugDepth > 0 ? this.inDebuggerMode : false;
        int size = this.D.getSize();

        List<BitStream> enableInList = new ArrayList<>();
        enableInList.add(this.enable);

        BitStream splitterOut = new BitStream(size);
        List<BitStream> enableOutList = new ArrayList<>();
        enableOutList.add(splitterOut);

        Splitter enableSplitter = new Splitter(enableInList, enableOutList, "enableSplitter", debugGates);

        BitStream notOut = new BitStream(size);

        NOT dNot = new NOT(this.D, notOut, "dNot", debugGates);

        BitStream upperAndOut = new BitStream(size);
        BitStream lowerAndOut = new BitStream(size);

        AND upperAnd = new AND(notOut, splitterOut, upperAndOut, "upperAnd", debugGates);
        AND lowerAnd = new AND(this.D, splitterOut, lowerAndOut, "lowerAnd", debugGates);

        NOR upperNor = new NOR(upperAndOut, this.notQ, this.Q, "upperNor", debugGates);
        NOR lowerNor = new NOR(lowerAndOut, this.Q, this.notQ, "lowerAnd", debugGates);
    }
}
