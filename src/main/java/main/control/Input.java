package main.control;

import main.BitStream;
import main.Node;
import main.exceptions.BitStreamInputSizeMismatch;
import main.utils.DataConverter;
import main.utils.ProcessRunner;
import main.warnings.InconsistentBitStreamSourcesWarning;

import java.util.Arrays;
import java.util.List;

public class Input implements Node {

    private boolean[] data;
    private BitStream out;
    private String name;
    private boolean inDebuggerMode;

    /**Constructors for the Input class.
     *
     * @param data - the input data
     * @param out - the output bit stream
     * @param name - the name of the input
     * @param inDebuggerMode - boolean to specify if we are in the debug mode
     */
    public Input(boolean[] data, BitStream out, String name, boolean inDebuggerMode) {
        this.out = out;
        this.name = name;
        this.inDebuggerMode = inDebuggerMode;
        this.data = data;

        this.out.addNewEndpoint(this);

        this.setup();
    }

    public Input(boolean[] data, BitStream out, String name) {
        this(data, out, name, false);
    }

    public Input(boolean[] data, BitStream out, boolean inDebuggerMode) {
        this(data, out,"Input", inDebuggerMode);
    }

    public Input(boolean[] data, BitStream out) {
        this(data, out, "Input", false);
    }

    /**Getters for all the attributes.
     */
    public BitStream getOut() {
        return out;
    }

    public boolean[] getData() {
        return data;
    }

    public String getName() {
        return name;
    }

    public boolean isInDebuggerMode() {
        return inDebuggerMode;
    }

    /**Setters for some of the attributes.
     */
    public void setData(boolean[] data) {
        this.data = data;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInDebuggerMode(boolean inDebuggerMode) {
        this.inDebuggerMode = inDebuggerMode;
    }

    /**Method to setup the circuit starting in "this".
     */
    public void setup() {
        this.checkIfSizesMatch();
        ProcessRunner.run(this);
    }

    /**Evaluate the Input, i.e: if possible forward the input data to the "out" BitStream,
     * otherwise throw correct errors.
     *
     * @param queue - the execution queue
     */
    @Override
    public void evaluate(List<Node> queue) {
        this.checkIfSourceIsConsistent(this.data);

        this.addNeighboursToQueue(queue);

        this.out.setData(this.data);
        this.setSourceForOutStream();

        if (this.inDebuggerMode) {
            this.debug();
        }
    }

    /**Set the source for the "out" BitStream. This step is required for checking if the sources of that
     * BitStream are consistent.
     */
    @Override
    public void setSourceForOutStream() {
        this.out.setSource(this);
    }

    /**Add all neighbours of the Input to the execution queue.
     *
     * @param queue - the execution queue
     */
    @Override
    public void addNeighboursToQueue(List<Node> queue) {
        queue.addAll(this.out.getAllNeighbours(this));
    }

    /**Check if the sizes of the input data and the out stream match.
     */
    @Override
    public void checkIfSizesMatch() {
        if (this.data.length != this.getOut().getSize()) {
            throw new BitStreamInputSizeMismatch(this);
        }
    }

    /**Check if the current source of the "out" stream is consistent with the input data.
     *
     * @param newOutData - the input data
     */
    @Override
    public void checkIfSourceIsConsistent(boolean[] newOutData) {
        if (this.out.getSource() != null && this.out.getSource() != this) {
            if (!Arrays.equals(this.out.getData(), newOutData)) {
                InconsistentBitStreamSourcesWarning.show(this.out.getSource(), this);
            }
        }
    }

    /**Function to define what should happen in the debug mode (what kind of message will be displayed).
     */
    @Override
    public void debug() {
        System.out.println("Evaluating " + this.name + ":\n" +
                "\tInput: " + DataConverter.convertBoolToBin(this.data));
    }

    /**Override the default toString method.
     *
     * @return - a String representation of this.
     */
    @Override
    public String toString() {
        return "Input<" + this.name + ", " +
                DataConverter.convertBoolToBin(this.data) + ">";
    }

    /**Define a method from the Node interface. Here there is no need to check if we need to evaluate further,
     * hence the method is never used (we always want to evaluate further).
     */
    @Override
    public boolean decideIfEvaluateFurther(boolean[] newOutData) {return true;}
}
