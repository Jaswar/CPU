package main.gates;

import main.BitStream;
import main.Node;
import main.exceptions.BitStreamInputSizeMismatch;
import main.utils.DataConverter;

import java.util.List;

public class TriState extends Gate {

    private BitStream in, control;

    /**Constructors for the TriState class.
     *
     * @param in - the input BitStream
     * @param control - BitStream specifying if the gate is enabled
     * @param out - the output BitStream
     * @param name - the name of the gate
     * @param inDebuggerMode - boolean to specify if the gate is in debug mode
     */
    public TriState(BitStream in, BitStream control, BitStream out,
                    String name, boolean inDebuggerMode) {
        super(out, name, inDebuggerMode);

        this.in = in;
        this.in.addNewEndpoint(this);

        this.control = control;
        this.control.addNewEndpoint(this);

        this.setup();
    }

    public TriState(BitStream in, BitStream control, BitStream out,
                    String name) {
        this(in, control, out, name, false);
    }

    public TriState(BitStream in, BitStream control, BitStream out,
                   boolean inDebuggerMode) {
        this(in, control, out, "TriState", inDebuggerMode);
    }

    public TriState(BitStream in, BitStream control, BitStream out) {
        this(in, control, out, "TriState", false);
    }

    /**Getters for the attributes.
     */
    public BitStream getIn() {
        return in;
    }

    public BitStream getControl() {
        return control;
    }

    /**Override the evaluate gate definition from the parent class Gate.
     * Here the input of the gate is forwarded to the output if the gate is enabled.
     *
     * @param queue - the execution queue
     */
    @Override
    public void evaluate(List<Node> queue) {

        if (this.control.getData()[0]) {

            boolean[] newOutData = this.in.getData();

            this.checkIfSourceIsConsistent(newOutData);

            if (this.decideIfEvaluateFurther(newOutData)) {
                this.addNeighboursToQueue(queue);
            }

            this.getOut().setData(newOutData);
            this.setSourceForOutStream();

        }
        else {
            if (this.getOut().getSource() == this) {
                this.getOut().setSource(null);
            }
        }

        if (this.isInDebuggerMode()) {
            this.debug();
        }
    }

    /**Method to check if the input and the output have the same sizes.
     * The control BitStream can also only be one bit.
     */
    @Override
    public void checkIfSizesMatch() {
        if (this.in.getSize() != this.getOut().getSize() || this.control.getSize() != 1) {
            throw new BitStreamInputSizeMismatch(this);
        }
    }

    /**Method used to display additional debug info.
     */
    @Override
    public void debug() {
        System.out.println("Evaluating " + this.getName() + ":\n"
                + "\tInput: " + DataConverter.convertBoolToBin(this.in.getData()) + "\n"
                + "\tControl: " + this.control.getData()[0]);
    }

    /**Override the default toString method.
     *
     * @return - a String represenation of this
     */
    @Override
    public String toString() {
        return "TriState<" + this.getName() + ", "
                + this.in + ", "
                + this.control.getData()[0] + ", "
                + this.getOut() + ">";
    }

    /**Define an empty compute method defined in Gate.
     */
    @Override
    public boolean[] compute() {return new boolean[this.in.getSize()];}
}
