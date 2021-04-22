package main;

import main.control.Input;
import main.utils.DataConverter;
import main.utils.ProcessRunner;

import javax.xml.crypto.Data;
import java.io.PrintWriter;
import java.util.Arrays;

public class ControlCircuit {

    private Input RFIn, RFOut, addressWrite, addressRead,
            XIn, ZIn, ZOut, ALUIn, ALUOpCode;

    public ControlCircuit(Input RFIn, Input RFOut, Input addressWrite, Input addressRead,
                          Input XIn, Input ZIn, Input ZOut, Input ALUIn, Input ALUOpCode) {
        this.RFIn = RFIn;
        this.RFOut = RFOut;
        this.addressWrite = addressWrite;
        this.addressRead = addressRead;
        this.XIn = XIn;
        this.ZIn = ZIn;
        this.ZOut = ZOut;
        this.ALUIn = ALUIn;
        this.ALUOpCode = ALUOpCode;
    }

    public void run() {
        String[] microInstructions = new String[]{"011000000000", "010101000010", "100010000001"};

        int i = 0;
        for (String instruction : microInstructions) {
            boolean[] boolInstruction = DataConverter.convertBinToBool(instruction);
            this.RFIn.setData(new boolean[]{boolInstruction[0]});
            this.RFOut.setData(new boolean[]{boolInstruction[1]});
            this.XIn.setData(new boolean[]{boolInstruction[2]});
            this.ZIn.setData(new boolean[]{boolInstruction[3]});
            this.ZOut.setData(new boolean[]{boolInstruction[4]});
            this.ALUIn.setData(new boolean[]{boolInstruction[5]});
            this.ALUOpCode.setData(Arrays.copyOfRange(boolInstruction, 6, 11));

            if (i == 0) {
                this.addressRead.setData(new boolean[]{false, false, false});
            }
            else if (i == 1) {
                this.addressRead.setData(new boolean[]{false, false, true});
            }
            else if (i == 2) {
                this.addressWrite.setData(new boolean[]{false, false, false});
            }
            ProcessRunner.run(RFIn, RFOut, XIn, ZIn, ZOut, ALUIn, ALUOpCode, addressRead, addressWrite);

            turnOffWriteSignals();

            i += 1;
        }
    }

    private void turnOffWriteSignals() {
        this.RFIn.setData(new boolean[]{false});
        this.XIn.setData(new boolean[]{false});
        this.ZIn.setData(new boolean[]{false});
        this.ALUIn.setData(new boolean[]{false});
        ProcessRunner.run(RFIn, XIn, ZIn, ALUIn);
    }

}
