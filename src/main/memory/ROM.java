//package main.memory;
//
//import main.BitStream;
//import main.Node;
//import main.exceptions.BitStreamInputSizeMismatch;
//import main.exceptions.InconsistentBitStreamSources;
//import main.utils.DataConverter;
//import main.utils.ProcessRunner;
//
//import javax.xml.crypto.Data;
//import java.util.Arrays;
//import java.util.List;
//
//public class ROM implements Node {
//
//    private Storage storage;
//    private String filepath;
//
//    private BitStream address, output;
//    private String name;
//    private boolean inDebuggerMode;
//
//    public ROM(String filepath, BitStream address, BitStream output, String name, boolean inDebuggerMode) {
//        this.storage = Storage.read(filepath);
//        this.filepath = filepath;
//
//        this.address = address;
//        this.address.addNewEndpoint(this);
//        this.output = output;
//        this.output.addNewEndpoint(this);
//        this.name = name;
//        this.inDebuggerMode = inDebuggerMode;
//
//        this.setup();
//    }
//
//    public ROM(String filepath, BitStream address, BitStream output, String name) {
//        this(filepath, address, output, name, false);
//    }
//
//    public ROM(String filepath, BitStream address, BitStream output, boolean inDebuggerMode) {
//        this(filepath, address, output, "ROM", inDebuggerMode);
//    }
//    public ROM(String filepath, BitStream address, BitStream output) {
//        this(filepath, address, output, "ROM", false);
//    }
//
//    public Storage getStorage() {
//        return storage;
//    }
//
//    public String getFilepath() {
//        return filepath;
//    }
//
//    public BitStream getAddress() {
//        return address;
//    }
//
//    public BitStream getOutput() {
//        return output;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public boolean isInDebuggerMode() {
//        return inDebuggerMode;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public void setInDebuggerMode(boolean inDebuggerMode) {
//        this.inDebuggerMode = inDebuggerMode;
//    }
//
//    @Override
//    public void setup() {
//        this.checkIfSizesMatch();
//        ProcessRunner.run(this);
//    }
//
//    @Override
//    public void checkIfSizesMatch() {
//        if (this.output.getSize() != this.storage.getWordSize()) {
//            throw new BitStreamInputSizeMismatch(this);
//        }
//    }
//
//    @Override
//    public void evaluate(List<Node> queue) {
//        int address = DataConverter.convertBoolToUnsignedDec(this.address.getData());
//        boolean[] newOutData = this.storage.getData()[address];
//
//        this.checkIfSourceIsConsistent(newOutData);
//
//        if (this.decideIfEvaluateFurther(newOutData)) {
//            this.addNeighboursToQueue(queue);
//        }
//
//        this.output.setData(newOutData);
//        this.setSourceForOutStream();
//
//        if (this.isInDebuggerMode()) {
//            this.debug();
//        }
//    }
//
//    @Override
//    public void checkIfSourceIsConsistent(boolean[] newOutData) {
//        if (this.output.getSource() != this && this.output.getSource() != null) {
//            if (!Arrays.equals(this.output.getData(), newOutData)) {
//                  InconsistentBitStreamSourcesWarning.show(this.output.getSource(), this);
//            }
//        }
//    }
//
//    @Override
//    public boolean decideIfEvaluateFurther(boolean[] newOutData) {
//        return !Arrays.equals(this.output.getData(), newOutData);
//    }
//
//}
