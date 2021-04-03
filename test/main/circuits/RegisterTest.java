package main.circuits;

import main.BitStream;
import main.control.Input;
import main.utils.ProcessRunner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterTest {

    @Test
    void test1() {
        BitStream input = new BitStream(4);
        BitStream output = new BitStream(4);
        BitStream regIn = new BitStream(1);
        BitStream regOut = new BitStream(1);

        Input mainInput = new Input(new boolean[]{true, true, false, true}, input);
        Input regInInput = new Input(new boolean[]{true}, regIn);
        Input regOutInput = new Input(new boolean[]{true}, regOut);

        Register register = new Register(input, output, regIn, regOut);
        ProcessRunner.run(regInInput, regOutInput);
        ProcessRunner.run(mainInput);

        assertArrayEquals(new boolean[]{true, true, false, true}, output.getData());

        mainInput.setData(new boolean[]{false, true, false, false});
        regInInput.setData(new boolean[]{false});
        regOutInput.setData(new boolean[]{true});

        ProcessRunner.run(regInInput, regOutInput);
        ProcessRunner.run(mainInput);

        assertArrayEquals(new boolean[]{true, true, false, true}, output.getData());

        mainInput.setData(new boolean[]{false, true, false, false});
        regInInput.setData(new boolean[]{true});
        regOutInput.setData(new boolean[]{false});

        ProcessRunner.run(regOutInput);
        ProcessRunner.run(regInInput, mainInput);

        assertArrayEquals(new boolean[]{true, true, false, true}, output.getData());

        mainInput.setData(new boolean[]{false, true, false, false});
        regInInput.setData(new boolean[]{true});
        regOutInput.setData(new boolean[]{true});

        ProcessRunner.run(regOutInput);
        ProcessRunner.run(regInInput, mainInput);

        assertArrayEquals(new boolean[]{false, true, false, false}, output.getData());
    }

}