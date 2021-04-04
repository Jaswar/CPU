import main.BitStream;
import main.circuits.ALU;
import main.circuits.memory.DLatch;
import main.circuits.Register;
import main.circuits.RegisterFile;
import main.control.Input;
import main.gates.TriState;
import main.utils.DataConverter;
import main.utils.ProcessRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ALURFTest {

    Input busControl, busInput, opCodeInput, xInInput, aluInInput, zInInput, zOutInput, RFInInput, RFOutInput,
            addressWriteInput, addressReadInput;
    BitStream bus;

    @BeforeEach
    void setup() {
        bus = new BitStream(4);

        BitStream inToTriState = new BitStream(4);
        BitStream controlBusInput = new BitStream(1);
        busInput = new Input(new boolean[]{false, false, false, false}, inToTriState);
        busControl = new Input(new boolean[]{false}, controlBusInput);

        TriState busTriState = new TriState(inToTriState, controlBusInput, bus, "busTriState");

        BitStream XIn = new BitStream(1);
        xInInput = new Input(new boolean[]{false}, XIn);

        BitStream destination = new BitStream(4);
        BitStream aluOut = new BitStream(4);

        BitStream opCode = new BitStream(5);
        opCodeInput = new Input(new boolean[]{false, false, false, false, false}, opCode);

        BitStream aluIn = new BitStream(1);
        aluInInput = new Input(new boolean[]{false}, aluIn);

        BitStream overflow = new BitStream(1);
        BitStream zIn = new BitStream(1);
        zInInput = new Input(new boolean[]{true}, zIn);

        BitStream zOut = new BitStream(1);
        zOutInput = new Input(new boolean[]{false}, zOut);

        BitStream RFIn = new BitStream(1);
        RFInInput = new Input(new boolean[]{false}, RFIn);

        BitStream RFOut = new BitStream(1);
        RFOutInput = new Input(new boolean[]{false}, RFOut);

        BitStream addressWrite = new BitStream(3);
        addressWriteInput = new Input(new boolean[]{false, false, false}, addressWrite);

        BitStream addressRead = new BitStream(3);
        addressReadInput = new Input(new boolean[]{false, false, false}, addressRead);

        BitStream XNotQ = new BitStream(4);
        DLatch X = new DLatch(bus, XIn, destination, XNotQ, "X");

        ALU alu = new ALU(bus, destination, aluOut, opCode, aluIn, overflow);

        BitStream zNotQ = new BitStream(4);
        BitStream zQ = new BitStream(4);
        DLatch Z = new DLatch(aluOut, zIn, zQ, zNotQ, "Z");
        TriState zTriState = new TriState(zQ, zOut, bus, "zTriState");

        RegisterFile rf = new RegisterFile(bus, bus, RFIn, RFOut, addressWrite, addressRead);
    }

    @Test
    void testSubtractingRegisters() {
        //Save to reg3
        busInput.setData(new boolean[]{true, false, true, true});
        busControl.setData(new boolean[]{true});
        addressWriteInput.setData(new boolean[]{false, true, true});
        ProcessRunner.run(busInput, busControl, addressWriteInput);

        RFInInput.setData(new boolean[]{true});
        busControl.setData(new boolean[]{false});
        ProcessRunner.run(RFInInput, busControl);

        RFInInput.setData(new boolean[]{false});
        ProcessRunner.run(RFInInput);

        //Save to reg5
        busInput.setData(new boolean[]{true, true, true, false});
        busControl.setData(new boolean[]{true});
        addressWriteInput.setData(new boolean[]{true, false, true});
        ProcessRunner.run(busInput, busControl, addressWriteInput);

        RFInInput.setData(new boolean[]{true});
        busControl.setData(new boolean[]{false});
        ProcessRunner.run(RFInInput, busControl);

        RFInInput.setData(new boolean[]{false});
        ProcessRunner.run(RFInInput);

        //Move reg5 to X
        addressReadInput.setData(new boolean[]{true, false, true});
        RFOutInput.setData(new boolean[]{true});
        ProcessRunner.run(addressReadInput, RFOutInput);

        assertArrayEquals(new boolean[]{true, true, true, false}, bus.getData());

        xInInput.setData(new boolean[]{true});
        ProcessRunner.run(xInInput);

        xInInput.setData(new boolean[]{false});
        RFOutInput.setData(new boolean[]{false});
        ProcessRunner.run(xInInput, RFOutInput);

        xInInput.setData(new boolean[]{false});
        ProcessRunner.run(xInInput);

        //Move reg3 to bus and do subtraction
        addressReadInput.setData(new boolean[]{false, true, true});
        RFOutInput.setData(new boolean[]{true});
        ProcessRunner.run(addressReadInput, RFOutInput);

        assertArrayEquals(new boolean[]{true, false, true, true}, bus.getData());

        aluInInput.setData(new boolean[]{true});
        RFOutInput.setData(new boolean[]{false});
        opCodeInput.setData(new boolean[]{false, false, false, true, false});
        ProcessRunner.run(RFOutInput, aluInInput, opCodeInput);

        //Move Z to bus
        aluInInput.setData(new boolean[]{false});
        zOutInput.setData(new boolean[]{true});
        ProcessRunner.run(aluInInput, zOutInput);


        assertArrayEquals(new boolean[]{true, true, false, true}, bus.getData());
    }

    @Test
    void testFibonacci() {
        //Save 0 to reg0
        busInput.setData(new boolean[]{false, false, false, false});
        busControl.setData(new boolean[]{true});
        addressWriteInput.setData(new boolean[]{false, false, false});
        ProcessRunner.run(busInput, busControl, addressWriteInput);

        RFInInput.setData(new boolean[]{true});
        busControl.setData(new boolean[]{false});
        ProcessRunner.run(RFInInput, busControl);

        RFInInput.setData(new boolean[]{false});
        ProcessRunner.run(RFInInput);

        //Save 1 to reg1
        busInput.setData(new boolean[]{false, false, false, true});
        busControl.setData(new boolean[]{true});
        addressWriteInput.setData(new boolean[]{false, false, true});
        ProcessRunner.run(busInput, busControl, addressWriteInput);

        RFInInput.setData(new boolean[]{true});
        busControl.setData(new boolean[]{false});
        ProcessRunner.run(RFInInput, busControl);

        RFInInput.setData(new boolean[]{false});
        ProcessRunner.run(RFInInput);

        int a = 0; int b = 1;
        for (int i = 0; i < 4; i++) {
            //Move reg0 to X
            addressReadInput.setData(new boolean[]{false, false, false});
            RFOutInput.setData(new boolean[]{true});
            ProcessRunner.run(addressReadInput, RFOutInput);

            assertArrayEquals(DataConverter.convertSignedDecToBool(a, 4), bus.getData());

            xInInput.setData(new boolean[]{true});
            ProcessRunner.run(xInInput);

            xInInput.setData(new boolean[]{false});
            RFOutInput.setData(new boolean[]{false});
            ProcessRunner.run(xInInput, RFOutInput);

            xInInput.setData(new boolean[]{false});
            ProcessRunner.run(xInInput);

            //Move reg1 to bus and do addition
            addressReadInput.setData(new boolean[]{false, false, true});
            RFOutInput.setData(new boolean[]{true});
            ProcessRunner.run(addressReadInput, RFOutInput);

            assertArrayEquals(DataConverter.convertSignedDecToBool(b, 4), bus.getData());

            aluInInput.setData(new boolean[]{true});
            RFOutInput.setData(new boolean[]{false});
            opCodeInput.setData(new boolean[]{false, false, false, false, true});
            ProcessRunner.run(RFOutInput, aluInInput, opCodeInput);

            //Move Z to bus
            aluInInput.setData(new boolean[]{false});
            zOutInput.setData(new boolean[]{true});
            ProcessRunner.run(aluInInput, zOutInput);

            assertArrayEquals(DataConverter.convertSignedDecToBool(a + b, 4), bus.getData());

            //Move from bus to reg2
            addressWriteInput.setData(new boolean[]{false, true, false});
            zOutInput.setData(new boolean[]{false});
            ProcessRunner.run(addressWriteInput, zOutInput);

            RFInInput.setData(new boolean[]{true});
            ProcessRunner.run(RFInInput);

            RFInInput.setData(new boolean[]{false});
            ProcessRunner.run(RFInInput);

            assertArrayEquals(DataConverter.convertSignedDecToBool(a + b, 4), bus.getData());

            //Move reg1 to reg0
            addressWriteInput.setData(new boolean[]{false, false, false});
            RFOutInput.setData(new boolean[]{true});
            addressReadInput.setData(new boolean[]{false, false, true});
            zOutInput.setData(new boolean[]{false});
            ProcessRunner.run(addressWriteInput, zOutInput, addressReadInput, RFOutInput);

            RFInInput.setData(new boolean[]{true});
            ProcessRunner.run(RFInInput);

            RFInInput.setData(new boolean[]{false});
            RFOutInput.setData(new boolean[]{false});
            ProcessRunner.run(RFInInput);

            //Move reg2 to reg1
            addressWriteInput.setData(new boolean[]{false, false, true});
            RFOutInput.setData(new boolean[]{true});
            addressReadInput.setData(new boolean[]{false, true, false});
            zOutInput.setData(new boolean[]{false});
            ProcessRunner.run(addressWriteInput, zOutInput, addressReadInput, RFOutInput);

            RFInInput.setData(new boolean[]{true});
            ProcessRunner.run(RFInInput);

            RFInInput.setData(new boolean[]{false});
            RFOutInput.setData(new boolean[]{false});
            ProcessRunner.run(RFInInput);

            int temp = a + b;
            a = b;
            b = temp;

        }
    }

}
