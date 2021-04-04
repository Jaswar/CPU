package main;

import main.circuits.ALU;
import main.circuits.Register;
import main.circuits.RegisterFile;
import main.circuits.memory.DLatch;
import main.control.Input;
import main.control.Output;
import main.gates.TriState;
import main.gates.binary.AND;
import main.gates.binary.NOR;
import main.gates.binary.OR;
import main.utils.DataConverter;
import main.utils.ProcessRunner;

public class Main {


    public static void main(String[] args) {
        BitStream RFIn = new BitStream(1);
        BitStream RFOut = new BitStream(1);
        BitStream addressWrite = new BitStream(3);
        BitStream addressRead = new BitStream(3);
        BitStream XIn = new BitStream(1);
        BitStream ZIn = new BitStream(1);
        BitStream ZOut = new BitStream(1);
        BitStream ALUIn = new BitStream(1);
        BitStream ALUOpCode = new BitStream(5);

        Input RFInInput = new Input(new boolean[]{false}, RFIn);
        Input RFOutInput = new Input(new boolean[]{false}, RFOut);
        Input addressWriteInput = new Input(new boolean[]{false, false, false}, addressWrite);
        Input addressReadInput = new Input(new boolean[]{false, false, false}, addressRead);
        Input XInInput = new Input(new boolean[]{false}, XIn);
        Input ZInInput = new Input(new boolean[]{false}, ZIn);
        Input ZOutInput = new Input(new boolean[]{false}, ZOut);
        Input ALUInInput = new Input(new boolean[]{true}, ALUIn);
        Input AlUOpCodeInput = new Input(new boolean[]{false, false, false, false, false}, ALUOpCode);

        ControlCircuit controlCircuit = new ControlCircuit(RFInInput, RFOutInput, addressWriteInput, addressReadInput,
                XInInput, ZInInput, ZOutInput, ALUInInput, AlUOpCodeInput);

        BitStream inputToTriState = new BitStream(4);
        BitStream bus = new BitStream(4);
        BitStream control = new BitStream(1);
        Input mainInput = new Input(new boolean[]{false, false, false, false}, inputToTriState);
        Input controlInput = new Input(new boolean[]{false}, control);
        TriState triState = new TriState(inputToTriState, control, bus);

        RegisterFile rf = new RegisterFile(bus, bus, RFIn, RFOut, addressWrite, addressRead);

        BitStream xOutput = new BitStream(4);
        DLatch X = new DLatch(bus, XIn, xOutput, new BitStream(4));

        BitStream aluOut = new BitStream(4);
        BitStream enableZ = new BitStream(1);
        enableZ.setData(new boolean[]{true});
        Register Z = new Register(aluOut, bus, ZIn, ZOut, enableZ);

        ALU alu = new ALU(bus, xOutput, aluOut, ALUOpCode, ALUIn, new BitStream(1));

        //Save to reg0
        mainInput.setData(new boolean[]{false, false, true, false});
        controlInput.setData(new boolean[]{true});
        RFInInput.setData(new boolean[]{true});
        addressWriteInput.setData(new boolean[]{false, false, false});
        ProcessRunner.run(mainInput, controlInput, RFInInput, addressWriteInput);

        RFInInput.setData(new boolean[]{false});
        controlInput.setData(new boolean[]{false});
        ProcessRunner.run(RFInInput, controlInput);

        System.out.println(DataConverter.convertBoolToBin(bus.getData()));

        //Save to reg1
        mainInput.setData(new boolean[]{false, false, true, true});
        controlInput.setData(new boolean[]{true});
        RFInInput.setData(new boolean[]{true});
        addressWriteInput.setData(new boolean[]{false, false, true});
        ProcessRunner.run(mainInput, controlInput, RFInInput, addressWriteInput);

        RFInInput.setData(new boolean[]{false});
        controlInput.setData(new boolean[]{false});
        ProcessRunner.run(RFInInput, controlInput);

        System.out.println(DataConverter.convertBoolToBin(bus.getData()));

        controlCircuit.run();

        System.out.println(DataConverter.convertBoolToBin(bus.getData()));

        //Read reg0
        RFOutInput.setData(new boolean[]{true});
        addressReadInput.setData(new boolean[]{false, false, false});
        ProcessRunner.run(RFOutInput, addressReadInput);

        System.out.println(DataConverter.convertBoolToBin(bus.getData()));

    }
}
