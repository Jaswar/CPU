package main.circuits;

import main.BitStream;
import main.control.Input;
import main.control.Output;
import main.utils.ProcessRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterFileTest {

    Input input, RFInInput, RFOutInput, addressWriteInput, addressReadInput;
    Output output;

    @BeforeEach
    void setup() {
        BitStream in = new BitStream(4);
        BitStream out = new BitStream(4);
        BitStream RFIn = new BitStream(1);
        BitStream RFOut = new BitStream(1);
        BitStream addressWrite = new BitStream(3);
        BitStream addressRead = new BitStream(3);

        input = new Input(new boolean[]{false, false, false, false}, in);
        RFInInput = new Input(new boolean[]{false}, RFIn);
        RFOutInput = new Input(new boolean[]{false}, RFOut);
        addressWriteInput = new Input(new boolean[]{false, false, false}, addressWrite);
        addressReadInput = new Input(new boolean[]{false, false, false}, addressRead);

        output = new Output(out);

        RegisterFile rf = new RegisterFile(in, out, RFIn, RFOut, addressWrite, addressRead, true, 1);
    }

    @Test
    void testReg0() {
        input.setData(new boolean[]{true, true, true, true});
        RFInInput.setData(new boolean[]{true});
        ProcessRunner.run(input, RFInInput);

        RFInInput.setData(new boolean[]{false});
        ProcessRunner.run(RFInInput);

        assertArrayEquals(new boolean[]{false, false, false, false}, output.getData());

        RFOutInput.setData(new boolean[]{true});
        ProcessRunner.run(RFOutInput);

        assertArrayEquals(new boolean[]{true, true, true, true}, output.getData());
    }

    @Test
    void testReg1() {
        input.setData(new boolean[]{false, false, true, true});
        RFOutInput.setData(new boolean[]{true});
        addressWriteInput.setData(new boolean[]{false, false, true});
        addressReadInput.setData(new boolean[]{false, false, true});
        ProcessRunner.run(input, RFOutInput, addressWriteInput, addressReadInput);

        RFInInput.setData(new boolean[]{true});
        ProcessRunner.run(RFInInput);

        RFInInput.setData(new boolean[]{false});
        ProcessRunner.run(RFInInput);

        assertArrayEquals(new boolean[]{false, false, true, true}, output.getData());
    }

    @Test
    void testReg2() {
        input.setData(new boolean[]{false, false, true, true});
        RFOutInput.setData(new boolean[]{true});
        addressWriteInput.setData(new boolean[]{false, true, false});
        addressReadInput.setData(new boolean[]{false, true, false});
        RFInInput.setData(new boolean[]{true});
        ProcessRunner.run(input, RFOutInput, addressWriteInput, addressReadInput, RFInInput);

        RFInInput.setData(new boolean[]{false});
        ProcessRunner.run(RFInInput);

        assertArrayEquals(new boolean[]{false, false, true, true}, output.getData());
    }

    @Test
    void testReg5() {
        input.setData(new boolean[]{true, false, true, false});
        RFOutInput.setData(new boolean[]{true});
        addressWriteInput.setData(new boolean[]{true, false, true});
        addressReadInput.setData(new boolean[]{true, false, true});
        ProcessRunner.run(input, RFOutInput, addressWriteInput, addressReadInput);

        RFInInput.setData(new boolean[]{true});
        ProcessRunner.run(RFInInput);

        RFInInput.setData(new boolean[]{false});
        ProcessRunner.run(RFInInput);

        assertArrayEquals(new boolean[]{true, false, true, false}, output.getData());
    }

    @Test
    void testReg7() {
        input.setData(new boolean[]{false, true, true, false});
        RFOutInput.setData(new boolean[]{true});
        addressWriteInput.setData(new boolean[]{true, true, true});
        addressReadInput.setData(new boolean[]{true, true, true});
        ProcessRunner.run(input, RFOutInput, addressWriteInput, addressReadInput);

        RFInInput.setData(new boolean[]{true});
        ProcessRunner.run(RFInInput);

        RFInInput.setData(new boolean[]{false});
        ProcessRunner.run(RFInInput);

        assertArrayEquals(new boolean[]{false, true, true, false}, output.getData());
    }

    @Test
    void testRegisterSwitching() {
        //Save to reg2
        input.setData(new boolean[]{true, true, false, false});
        RFInInput.setData(new boolean[]{true});
        addressWriteInput.setData(new boolean[]{false, true, false});
        ProcessRunner.run(input, addressWriteInput, RFInInput);

        RFInInput.setData(new boolean[]{false});
        ProcessRunner.run(RFInInput);

        //Save to reg6
        input.setData(new boolean[]{false, true, true, false});
        RFInInput.setData(new boolean[]{true});
        addressWriteInput.setData(new boolean[]{true, true, false});
        ProcessRunner.run(input, addressWriteInput, RFInInput);

        RFInInput.setData(new boolean[]{false});
        ProcessRunner.run(RFInInput);

        //Save to reg3
        input.setData(new boolean[]{true, false, false, true});
        RFInInput.setData(new boolean[]{true});
        addressWriteInput.setData(new boolean[]{false, true, true});
        ProcessRunner.run(input, addressWriteInput, RFInInput);

        RFInInput.setData(new boolean[]{false});
        ProcessRunner.run(RFInInput);

        //Read reg6
        addressReadInput.setData(new boolean[]{true, true, false});
        RFOutInput.setData(new boolean[]{true});
        ProcessRunner.run(addressReadInput, RFOutInput);

        assertArrayEquals(new boolean[]{false, true, true, false}, output.getData());

        //Read reg3
        addressReadInput.setData(new boolean[]{false, true, true});
        ProcessRunner.run(addressReadInput, RFOutInput);

        assertArrayEquals(new boolean[]{true, false, false, true}, output.getData());

        //Read reg2
        addressReadInput.setData(new boolean[]{false, true, false});
        ProcessRunner.run(addressReadInput, RFOutInput);

        assertArrayEquals(new boolean[]{true, true, false, false}, output.getData());

        //Read reg6
        addressReadInput.setData(new boolean[]{true, true, false});
        ProcessRunner.run(addressReadInput, RFOutInput);

        assertArrayEquals(new boolean[]{false, true, true, false}, output.getData());
    }

}