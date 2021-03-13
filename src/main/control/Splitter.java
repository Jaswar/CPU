package main.control;

import main.BitStream;
import main.Node;
import main.exceptions.IllegalSplitException;
import main.exceptions.InconsistentBitStreamSources;
import main.utils.DataConverter;
import main.warnings.InconsistentBitStreamSourcesWarning;

import java.util.ArrayList;
import java.util.List;

public class Splitter implements Node {

    /**Class responsible for splitting multiple BitStreams into other BitStreams.
     *
     * @param in - a list of the input BitStreams
     * @param out - a list of the output BitStreams
     * @param name - the name of the splitter
     * @param inDebuggerMode - a boolean to specify if additional debug data should be shown
     */
    private List<BitStream> in;
    private List<BitStream> out;
    private String name;
    private boolean inDebuggerMode;

    /**Constructors for the Splitter class.
     *
     * @param in - a list of the input BitStreams
     * @param out - a list of the output BitStreams
     * @param name - the name of the splitter
     * @param inDebuggerMode - a boolean to specify if additional debug data should be shown
     */
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

    /**Getters for all the attributes of the class.
     */
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

    /**Setters for some of the attributes of the class.
     */
    public void setName(String name) {
        this.name = name;
    }

    public void setInDebuggerMode(boolean inDebuggerMode) {
        this.inDebuggerMode = inDebuggerMode;
    }

    /**Get the total size of a list of BitStreams.
     *
     * @param bitStreams - the list of BitStreams
     * @return - the total number of bits in the BitStreams specified
     */
    public int getBitStreamListSize(List<BitStream> bitStreams) {
        int size = 0;
        for (BitStream stream : bitStreams) {
            size += stream.getSize();
        }
        return size;
    }

    /**Method to setup the circuit starting in "this".
     */
    @Override
    public void setup() {
        List<Node> queue = new ArrayList<>();
        queue.add(this);
        while (queue.size() > 0) {
            Node node = queue.remove(0);
            node.evaluate(queue);
        }
    }

    /**Evaluate the splitter. This includes checking the sizes of the BitStreams and their sources
     * to check for any inconsistencies.
     *
     * @param queue - the execution queue
     */
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

    /**Check if the total number of bits in the input BitStreams is the same as
     * the number of bits in the output BitStreams. Throw IllegalSplitException
     * if not.
     */
    @Override
    public void checkIfSizesMatch() {
        int inSize = this.getBitStreamListSize(this.in);
        int outSize = this.getBitStreamListSize(this.out);
        if (inSize != outSize) {
            throw new IllegalSplitException(this);
        }
    }

    /**Check if the sources of the output BitStreams are consistent.
     *
     * @param newOutData - data taken from the input BitStreams, changed to
     *                   a 1D array
     */
    @Override
    public void checkIfSourceIsConsistent(boolean[] newOutData) {
        int count = 0;
        for (BitStream outStream : this.out) {
            if (outStream.getSource() != null && outStream.getSource() != this) {
                for (boolean bit : outStream.getData()) {
                    if (newOutData[count++] != bit) {
                        InconsistentBitStreamSourcesWarning.show(outStream.getSource(), this);
                    }
                }
            } else {
                count += outStream.getSize();
            }
        }
    }

    /**Decide if the system should be evaluated further. That should happen
     * if the new data is different from the old one.
     *
     * @param newOutData - data taken from the input BitStreams, changed to
     *                   a 1D array
     * @return - true if the system should be evaluated further, false otherwise
     */
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

    /**Add all neighbours of the Splitter to the queue.
     *
     * @param queue - the execution queue to add the neighbours to
     */
    @Override
    public void addNeighboursToQueue(List<Node> queue) {
        for (BitStream outStream : this.out) {
            queue.addAll(outStream.getAllNeighbours(this));
        }
    }

    /**Method used to set the data of the output streams to the correct values.
     *
     * @param newOutData - data taken from the input BitStreams, changed to
     *                   a 1D array
     */
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

    /**Set the source of the out streams to the splitter.
     */
    @Override
    public void setSourceForOutStream() {
        for (BitStream outStream : this.out) {
            outStream.setSource(this);
        }
    }

    /**Method used to display additional debug information.
     */
    @Override
    public void debug() {
        String msg = "Evaluating " + this.name + ":\n\tInputs:\n";
        for (BitStream inStream : this.in) {
            msg += "\t\t" + DataConverter.convertBoolToBin(inStream.getData()) + "\n";
        }
        msg += "\tOutputs:\n";
        for (BitStream outStream : this.out) {
            msg += "\t\t" + DataConverter.convertBoolToBin(outStream.getData()) + "\n";
        }
        System.out.println(msg);
    }

    @Override
    public String toString() {
        return "Splitter<" + this.name + ", " + this.in.size() + ", " + this.out.size() + ">";
    }

}
