package main.memory;

import main.BitStream;
import main.Node;
import main.exceptions.BitStreamInputSizeMismatch;
import main.exceptions.InconsistentBitStreamSources;
import main.utils.DataConverter;
import main.utils.ProcessRunner;
import main.warnings.InconsistentBitStreamSourcesWarning;

import javax.xml.crypto.Data;
import java.util.Arrays;
import java.util.List;

public class ROM implements Node {

    private Storage storage;
    private String filepath;

    private BitStream address, output;
    private String name;
    private boolean inDebuggerMode;

    /**Constructors for the ROM class (Read-only memory).
     *
     * @param filepath - the path of the file where ROM is saved
     * @param address - BitStream used to specify the address of the data that should be read
     * @param output - the output from the ROM
     * @param name - the name of ROM
     * @param inDebuggerMode - boolean to specify if the memory is in debug mode
     */
    public ROM(String filepath, BitStream address, BitStream output, String name, boolean inDebuggerMode) {
        this.storage = Storage.read(filepath);
        this.filepath = filepath;

        this.address = address;
        this.address.addNewEndpoint(this);
        this.output = output;
        this.output.addNewEndpoint(this);
        this.name = name;
        this.inDebuggerMode = inDebuggerMode;

        this.setup();
    }

    public ROM(String filepath, BitStream address, BitStream output, String name) {
        this(filepath, address, output, name, false);
    }

    public ROM(String filepath, BitStream address, BitStream output, boolean inDebuggerMode) {
        this(filepath, address, output, "ROM", inDebuggerMode);
    }
    public ROM(String filepath, BitStream address, BitStream output) {
        this(filepath, address, output, "ROM", false);
    }

    /**Getters for the attributes of the class.
     */
    public Storage getStorage() {
        return storage;
    }

    public String getFilepath() {
        return filepath;
    }

    public BitStream getAddress() {
        return address;
    }

    public BitStream getOutput() {
        return output;
    }

    public String getName() {
        return name;
    }

    public boolean isInDebuggerMode() {
        return inDebuggerMode;
    }

    /**Setters for some of the attributes. Setting BitStreams is not possible.
     */
    public void setName(String name) {
        this.name = name;
    }

    public void setInDebuggerMode(boolean inDebuggerMode) {
        this.inDebuggerMode = inDebuggerMode;
    }

    /**Method to setup the circuit starting in "this".
     */
    @Override
    public void setup() {
        this.checkIfSizesMatch();
        ProcessRunner.run(this);
    }

    /**Method to check if the size of the output matches the word size of
     * the storage. Throws BitStreamInputSizeMismatch if not.
     */
    @Override
    public void checkIfSizesMatch() {
        if (this.output.getSize() != this.storage.getWordSize()) {
            throw new BitStreamInputSizeMismatch(this);
        }
    }

    /**Evaluate the ROM. This also includes checking if evaluation is possible, setting the
     * source of the output stream to this ROM and adding all neighbours to the execution queue.
     *
     * @param queue - the execution queue
     */
    @Override
    public void evaluate(List<Node> queue) {
        int address = DataConverter.convertBoolToUnsignedDec(this.address.getData());
        boolean[] newOutData = this.storage.getData()[address];

        this.checkIfSourceIsConsistent(newOutData);

        if (this.decideIfEvaluateFurther(newOutData)) {
            this.addNeighboursToQueue(queue);
        }

        this.output.setData(newOutData);
        this.setSourceForOutStream();

        if (this.isInDebuggerMode()) {
            this.debug();
        }
    }

    /**Check if the current source of the "out" stream is consistent with the input data.
     *
     * @param newOutData - the input data
     */
    @Override
    public void checkIfSourceIsConsistent(boolean[] newOutData) {
        if (this.output.getSource() != this && this.output.getSource() != null) {
            if (!Arrays.equals(this.output.getData(), newOutData)) {
                  InconsistentBitStreamSourcesWarning.show(this.output.getSource(), this);
            }
        }
    }

    /**Decide if the circuit should be evaluated further.
     *
     * @param newOutData - the data that should be the new output of the ROM
     * @return - true if the circuit should be evaluated further, false otherwise
     */
    @Override
    public boolean decideIfEvaluateFurther(boolean[] newOutData) {
        return !Arrays.equals(this.output.getData(), newOutData);
    }

    /**Add all neighbours of the ROM to the queue.
     *
     * @param queue - the execution queue
     */
    @Override
    public void addNeighboursToQueue(List<Node> queue) {
        queue.addAll(this.output.getAllNeighbours(this));
    }

    /**Set the source of the output stream to this.
     */
    @Override
    public void setSourceForOutStream() {
        this.output.setSource(this);
    }

    /**Display additional debug information.
     */
    @Override
    public void debug() {
        System.out.println("Evaluating " + this.name + ":\n"
                + "Address: " + DataConverter.convertBoolToBin(this.address.getData())
                + "\nOutput: " + DataConverter.convertBoolToBin(this.output.getData()));
    }

}
