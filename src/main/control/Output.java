package main.control;

import main.BitStream;
import main.Node;
import main.utils.DataConverter;
import main.utils.ProcessRunner;

import java.util.ArrayList;
import java.util.List;

public class Output implements Node {

    private boolean[] data;
    private BitStream in;
    private String name;
    private boolean inDebuggerMode;

    /**Constructors of the Output class.
     *
     * @param in - the input bit stream, whose data will be shown by the output
     * @param name - the name of the output
     * @param inDebuggerMode - the boolean to specify if we are in the debug mode
     */
    public Output(BitStream in, String name, boolean inDebuggerMode) {
        this.in = in;
        this.name = name;
        this.inDebuggerMode = inDebuggerMode;
        this.data = new boolean[this.in.getSize()];

        this.in.addNewEndpoint(this);

        this.setup();
    }

    public Output(BitStream in, String name) {
        this(in, name, false);
    }

    public Output(BitStream in, boolean inDebuggerMode) {
        this(in, "Output", inDebuggerMode);
    }

    public Output(BitStream in) {
        this(in, "Output", false);
    }

    /**Getters for the attributes of the class.
     */
    public boolean[] getData() {
        return data;
    }

    public BitStream getIn() {
        return in;
    }

    public String getName() {
        return name;
    }

    public boolean isInDebuggerMode() {
        return inDebuggerMode;
    }

    /**Setters for some of the attributes of the class.
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
        ProcessRunner.run(this);
    }

    /**Method used to evaluate the output, ie: set its data to that of the input stream.
     *
     * @param queue - the execution queue (not used here)
     */
    @Override
    public void evaluate(List<Node> queue) {
        this.data = this.in.getData();

        if (this.isInDebuggerMode()) {
            this.debug();
        }
    }

    /**Method used to display debug information for this output.
     */
    public void debug() {
        System.out.println("Evaluating " + this.name + ":\n" +
                "\tOutput: " + DataConverter.convertBoolToBin(this.data));
    }

    /**Override the default toString method.
     *
     * @return - a String representation of this
     */
    @Override
    public String toString() {
        return "Output<" + this.name + ", " + this.data.length + ">";
    }

    /**Create empty methods to keep with the Node interface.
     */
    @Override
    public void checkIfSizesMatch() {}

    @Override
    public boolean decideIfEvaluateFurther(boolean[] newOutData) {return false;}

    @Override
    public void addNeighboursToQueue(List<Node> queue) {}

    @Override
    public void checkIfSourceIsConsistent(boolean[] newOut) {}

    @Override
    public void setSourceForOutStream() {}
}
