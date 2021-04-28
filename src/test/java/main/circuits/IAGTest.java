package main.circuits;

import main.BitStream;
import main.control.Input;
import main.utils.ProcessRunner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IAGTest {

    @Test
    void test1() {
        BitStream input = new BitStream(4);
        BitStream output = new BitStream(4);
        BitStream PCIn = new BitStream(1);
        BitStream PCOut = new BitStream(1);

        IAG iag = new IAG(input, output, PCIn, PCOut);

        Input in = new Input(new boolean[]{false, false, false, false}, input);
        Input PCInInput = new Input(new boolean[]{false}, PCIn);
        Input PCOutInput = new Input(new boolean[]{false}, PCOut);

        in.setData(new boolean[]{true, false, false, true});
        PCInInput.setData(new boolean[]{true});
        PCOutInput.setData(new boolean[]{true});
        ProcessRunner.run(in, PCInInput, PCOutInput);

        PCInInput.setData(new boolean[]{false});
        ProcessRunner.run(PCInInput);

        assertArrayEquals(new boolean[]{true, false, false, true}, output.getData());

        in.setData(new boolean[]{false, true, true, false});
        PCInInput.setData(new boolean[]{true});
        ProcessRunner.run(in, PCInInput);
        assertArrayEquals(new boolean[]{true, false, false, true}, output.getData());

        PCOutInput.setData(new boolean[]{false});
        ProcessRunner.run(PCOutInput);

        assertArrayEquals(new boolean[]{true, false, false, true}, output.getData());

        PCInInput.setData(new boolean[]{false});
        ProcessRunner.run(PCInInput);

        assertArrayEquals(new boolean[]{true, false, false, true}, output.getData());

        PCOutInput.setData(new boolean[]{true});
        ProcessRunner.run(PCOutInput);
        assertArrayEquals(new boolean[]{false, true, true, false}, output.getData());

    }

}