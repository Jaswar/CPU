package main.circuits.memory;

import main.BitStream;
import main.control.Input;
import main.utils.ProcessRunner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DFlipFlopTest {

    @Test
    void testPreset() {
        BitStream D = new BitStream(4);
        BitStream clock = new BitStream(1);
        BitStream enable = new BitStream(1);
        enable.setData(new boolean[]{true});
        BitStream Q = new BitStream(4);
        BitStream notQ = new BitStream(4);

        BitStream preset = new BitStream(4);

        DFlipFlop dFlipFlop = new DFlipFlop(D, clock, enable,
                preset, new BitStream(1), Q, notQ, false, false, 1);
        Input presetInput = new Input(new boolean[]{false, false, false, false}, preset);

        Input dInput = new Input(new boolean[]{true, false, false, true}, D);
        Input clockInput = new Input(new boolean[]{true}, clock);
        ProcessRunner.run(dInput, clockInput);

        clockInput.setData(new boolean[]{false});
        ProcessRunner.run(clockInput);

        assertArrayEquals(new boolean[]{true, false, false, true}, Q.getData());
        assertArrayEquals(new boolean[]{false, true, true, false}, notQ.getData());

        presetInput.setData(new boolean[]{true, true, true, true});
        ProcessRunner.run(presetInput);

        assertArrayEquals(new boolean[]{true, true, true, true}, Q.getData());
    }

    @Test
    void testReset() {
        BitStream D = new BitStream(4);
        BitStream clock = new BitStream(1);
        BitStream enable = new BitStream(1);
        enable.setData(new boolean[]{true});
        BitStream Q = new BitStream(4);
        BitStream notQ = new BitStream(4);

        BitStream preset = new BitStream(4);
        BitStream clear = new BitStream(1);
        Input clearInput = new Input(new boolean[]{false}, clear);
        Input presetInput = new Input(new boolean[]{false, false, false, false}, preset);

        DFlipFlop dFlipFlop = new DFlipFlop(D, clock, enable,
                preset, clear, Q, notQ, false, false, 1);

        Input dInput = new Input(new boolean[]{true, false, false, true}, D);
        Input clockInput = new Input(new boolean[]{true}, clock);
        ProcessRunner.run(dInput, clockInput);

        clockInput.setData(new boolean[]{false});
        ProcessRunner.run(clockInput);

        assertArrayEquals(new boolean[]{true, false, false, true}, Q.getData());
        assertArrayEquals(new boolean[]{false, true, true, false}, notQ.getData());

        clearInput.setData(new boolean[]{true});
        ProcessRunner.run(clearInput);

        assertArrayEquals(new boolean[]{false, false, false, false}, Q.getData());

        clockInput.setData(new boolean[]{true});
        ProcessRunner.run(clockInput);

        clockInput.setData(new boolean[]{false});
        ProcessRunner.run(clockInput);

        assertArrayEquals(new boolean[]{false, false, false, false}, Q.getData());

        clearInput.setData(new boolean[]{false});
        ProcessRunner.run(clearInput);

        assertArrayEquals(new boolean[]{false, false, false, false}, Q.getData());

        clockInput.setData(new boolean[]{true});
        ProcessRunner.run(clockInput);

        clockInput.setData(new boolean[]{false});
        ProcessRunner.run(clockInput);

        assertArrayEquals(new boolean[]{true, false, false, true}, Q.getData());

    }

    @Test
    void testFallingEdge() {
        BitStream D = new BitStream(4);
        BitStream clock = new BitStream(1);
        BitStream enable = new BitStream(1);
        enable.setData(new boolean[]{true});
        BitStream Q = new BitStream(4);
        BitStream notQ = new BitStream(4);

        Input dInput = new Input(new boolean[]{true, false, false, true}, D);
        Input clockInput = new Input(new boolean[]{true}, clock);

        DFlipFlop dFlipFlop = new DFlipFlop(D, clock, enable,
                new BitStream(4), new BitStream(1), Q, notQ, false);
        ProcessRunner.run(dInput, clockInput);

        clockInput.setData(new boolean[]{false});
        ProcessRunner.run(clockInput);

        assertArrayEquals(new boolean[]{true, false, false, true}, Q.getData());
        assertArrayEquals(new boolean[]{false, true, true, false}, notQ.getData());

        dInput.setData(new boolean[]{false, true, false, true});
        clockInput.setData(new boolean[]{true});
        ProcessRunner.run(dInput, clockInput);

        assertArrayEquals(new boolean[]{true, false, false, true}, Q.getData());
        assertArrayEquals(new boolean[]{false, true, true, false}, notQ.getData());

        clockInput.setData(new boolean[]{false});
        ProcessRunner.run(clockInput);

        assertArrayEquals(new boolean[]{false, true, false, true}, Q.getData());
        assertArrayEquals(new boolean[]{true, false, true, false}, notQ.getData());
    }

    @Test
    void testRisingEdge() {
        BitStream D = new BitStream(4);
        BitStream clock = new BitStream(1);
        BitStream enable = new BitStream(1);
        enable.setData(new boolean[]{true});
        BitStream Q = new BitStream(4);
        BitStream notQ = new BitStream(4);

        Input dInput = new Input(new boolean[]{true, false, false, true}, D);
        Input clockInput = new Input(new boolean[]{false}, clock);

        DFlipFlop dFlipFlop = new DFlipFlop(D, clock, enable,
                new BitStream(4), new BitStream(1), Q, notQ, true, false, 1);
        ProcessRunner.run(dInput, clockInput);

        clockInput.setData(new boolean[]{true});
        ProcessRunner.run(clockInput);

        assertArrayEquals(new boolean[]{true, false, false, true}, Q.getData());
        assertArrayEquals(new boolean[]{false, true, true, false}, notQ.getData());

        dInput.setData(new boolean[]{true, true, true, true});
        ProcessRunner.run(dInput);

        assertArrayEquals(new boolean[]{true, false, false, true}, Q.getData());
        assertArrayEquals(new boolean[]{false, true, true, false}, notQ.getData());

        clockInput.setData(new boolean[]{false});
        ProcessRunner.run(clockInput);

        assertArrayEquals(new boolean[]{true, false, false, true}, Q.getData());
        assertArrayEquals(new boolean[]{false, true, true, false}, notQ.getData());

        clockInput.setData(new boolean[]{true});
        ProcessRunner.run(clockInput);

        assertArrayEquals(new boolean[]{true, true, true, true}, Q.getData());
        assertArrayEquals(new boolean[]{false, false, false, false}, notQ.getData());

        clockInput.setData(new boolean[]{false});
        ProcessRunner.run(clockInput);

        assertArrayEquals(new boolean[]{true, true, true, true}, Q.getData());
        assertArrayEquals(new boolean[]{false, false, false, false}, notQ.getData());
    }
}