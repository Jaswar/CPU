package main.memory;

import main.BitStream;
import main.Node;
import main.exceptions.BitStreamInputSizeMismatch;
import main.exceptions.InconsistentBitStreamSources;
import main.utils.DataConverter;
import main.utils.ProcessRunner;
import main.warnings.InconsistentBitStreamSourcesWarning;

import java.util.Arrays;
import java.util.List;


public class RAM implements Node {

    private BitStream address, dataIn, dataOut, read, write;
    private String name;
    private boolean inDebuggerMode;

    private boolean lastWriteSignal;
    private boolean[][] data;

    /**Constructors for the RAM class.
     *
     * @param address - the address BitStream
     * @param dataIn - the data line serving as an input to RAM
     * @param dataOut - the output from the RAM
     * @param write - signal to tell if data should be written to RAM
     *              (write happens when this line changes from 1 to 0)
     * @param read - line to specify if data should be read from RAM
     * @param name - the name of the memory
     * @param inDebuggerMode - boolean to specify if RAM is in debug mode
     */
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

    /**Getters for all the attributes.
     */
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

    /**Setters for some of the attributes of the class.
     * Setting BitStreams is not possible.
     */
    public void setName(String name) {
        this.name = name;
    }

    public void setInDebuggerMode(boolean inDebuggerMode) {
        this.inDebuggerMode = inDebuggerMode;
    }

    /**Setup the Node, i.e: check if all the sizes match and
     * run the circuit starting from this.
     */
    @Override
    public void setup() {
        this.checkIfSizesMatch();
        ProcessRunner.run(this);
    }

    /**Method to check if the sizes of the dataIn and dataOut match.
     * Also the write and read data lines can only be one bit.
     * Throw BitStreamInputSizeMismatch if any of these conditions do not hold.
     */
    @Override
    public void checkIfSizesMatch() {
        if (this.dataOut.getSize() != this.dataIn.getSize()
                || this.write.getSize() != 1
                || this.read.getSize() != 1) {
            throw new BitStreamInputSizeMismatch(this);
        }
    }

    /**Evaluate the node. Update the RAM and necessary and forward changes to the
     * neighbours.
     *
     * @param queue - the execution queue
     */
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

    /**Check if the source of dataOut is consistent with the data supposed to
     * be output by the RAM. Show InconsistentBitStreamSourcesWarning if not.
     *
     * @param newOutData - newly computed output data from RAM
     */
    @Override
    public void checkIfSourceIsConsistent(boolean[] newOutData) {
        if (this.dataOut.getSource() != null && this.dataOut.getSource() != this) {
            if (!Arrays.equals(this.dataOut.getData(), newOutData)) {
                InconsistentBitStreamSourcesWarning.show(this.dataOut.getSource(), this);
            }
        }
    }

    /**Decide if the evaluation of the circuit should continue
     * to the neighbours of the RAM.
     *
     * @param newOutData - the newly computed output of the RAM
     * @return - true if the evaluation should be carried further, false otherwise
     */
    @Override
    public boolean decideIfEvaluateFurther(boolean[] newOutData) {
        return !Arrays.equals(this.dataOut.getData(), newOutData);
    }

    /**Add all the neighbours of RAM to the queue.
     *
     * @param queue - the execution queue
     */
    @Override
    public void addNeighboursToQueue(List<Node> queue) {
        queue.addAll(this.dataOut.getAllNeighbours(this));
    }

    /**Set the source of the dataOut to RAM.
     */
    @Override
    public void setSourceForOutStream() {
        this.dataOut.setSource(this);
    }

    /**Display additional debug information.
     */
    @Override
    public void debug() {
        System.out.println("Evaluating " + this.name + ":\n"
                + "Address: " + DataConverter.convertBoolToBin(this.address.getData())
                + "\nData in: " + DataConverter.convertBoolToBin(this.dataIn.getData())
                + "\nData out: " + DataConverter.convertBoolToBin(this.dataOut.getData()));
    }

    /**Override the default toString method.
     *
     * @return - a String representation of this
     */
    @Override
    public String toString() {
        return "RAM<" + this.name + ", " + DataConverter.convertBoolToBin(this.address.getData()) +
                ", " + DataConverter.convertBoolToBin(this.dataIn.getData()) +
                ", " + DataConverter.convertBoolToBin(this.dataOut.getData()) + ">";
    }

}
