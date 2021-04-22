package main.circuits.memory;

import main.BitStream;
import main.control.Input;
import main.utils.ProcessRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TFlipFlopTest {

    Input tInput, clock, enableInput, presetInput, clearInput;
    BitStream Q, notQ;

    @BeforeEach
    void setup() {
        BitStream T = new BitStream(1);
        BitStream clk = new BitStream(1);
        BitStream clear = new BitStream(1);
        BitStream preset = new BitStream(1);
        Q = new BitStream(1);
        notQ = new BitStream(1);
        BitStream enable = new BitStream(1);

        enableInput = new Input(new boolean[]{false}, enable);
        tInput = new Input(new boolean[]{false}, T);
        clock = new Input(new boolean[]{false}, clk);
        presetInput = new Input(new boolean[]{false}, preset);
        clearInput = new Input(new boolean[]{false}, clear);

        TFlipFlop flipFlop = new TFlipFlop(T, clk, enable, preset, clear, Q, notQ, true);
    }

    @Test
    void testT0() {
        enableInput.setData(new boolean[]{true});
        ProcessRunner.run(enableInput);

        assertArrayEquals(new boolean[]{false}, Q.getData());
        assertArrayEquals(new boolean[]{true}, notQ.getData());

        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);
        assertArrayEquals(new boolean[]{false}, Q.getData());
        assertArrayEquals(new boolean[]{true}, notQ.getData());
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);

        assertArrayEquals(new boolean[]{false}, Q.getData());
        assertArrayEquals(new boolean[]{true}, notQ.getData());

        presetInput.setData(new boolean[]{true});
        ProcessRunner.run(presetInput);

        assertArrayEquals(new boolean[]{true}, Q.getData());
        assertArrayEquals(new boolean[]{false}, notQ.getData());

        presetInput.setData(new boolean[]{false});
        ProcessRunner.run(presetInput);
        assertArrayEquals(new boolean[]{true}, Q.getData());
        assertArrayEquals(new boolean[]{false}, notQ.getData());
        clearInput.setData(new boolean[]{true});
        ProcessRunner.run(clearInput);

        assertArrayEquals(new boolean[]{false}, Q.getData());
        assertArrayEquals(new boolean[]{true}, notQ.getData());
    }

    @Test
    void testSimple() {
        enableInput.setData(new boolean[]{true});
        ProcessRunner.run(enableInput);

        assertArrayEquals(new boolean[]{false}, Q.getData());
        assertArrayEquals(new boolean[]{true}, notQ.getData());

        tInput.setData(new boolean[]{true});
        ProcessRunner.run(tInput);
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);
        assertArrayEquals(new boolean[]{true}, Q.getData());
        assertArrayEquals(new boolean[]{false}, notQ.getData());

        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);
        assertArrayEquals(new boolean[]{false}, Q.getData());
        assertArrayEquals(new boolean[]{true}, notQ.getData());
    }

    @Test
    void testDisabled() {
        tInput.setData(new boolean[]{true});
        ProcessRunner.run(tInput);
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);
        assertArrayEquals(new boolean[]{false}, Q.getData());

        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);
        assertArrayEquals(new boolean[]{false}, Q.getData());
    }

    @Test
    void testPreset() {
        testSimple();

        presetInput.setData(new boolean[]{true});
        ProcessRunner.run(presetInput);

        assertArrayEquals(new boolean[]{true}, Q.getData());
        assertArrayEquals(new boolean[]{false}, notQ.getData());

        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);

        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);

        assertArrayEquals(new boolean[]{true}, Q.getData());
        assertArrayEquals(new boolean[]{false}, notQ.getData());

        presetInput.setData(new boolean[]{false});
        ProcessRunner.run(presetInput);

        assertArrayEquals(new boolean[]{true}, Q.getData());
        assertArrayEquals(new boolean[]{false}, notQ.getData());

        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);

        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);

        assertArrayEquals(new boolean[]{false}, Q.getData());
        assertArrayEquals(new boolean[]{true}, notQ.getData());
    }

    @Test
    void testClear() {
        testSimple();

        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);

        assertArrayEquals(new boolean[]{true}, Q.getData());
        assertArrayEquals(new boolean[]{false}, notQ.getData());

        clearInput.setData(new boolean[]{true});
        ProcessRunner.run(clearInput);
        assertArrayEquals(new boolean[]{false}, Q.getData());
        assertArrayEquals(new boolean[]{true}, notQ.getData());

        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);

        assertArrayEquals(new boolean[]{false}, Q.getData());
        assertArrayEquals(new boolean[]{true}, notQ.getData());

        clearInput.setData(new boolean[]{false});
        ProcessRunner.run(clearInput);

        assertArrayEquals(new boolean[]{false}, Q.getData());
        assertArrayEquals(new boolean[]{true}, notQ.getData());

        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);

        assertArrayEquals(new boolean[]{true}, Q.getData());
        assertArrayEquals(new boolean[]{false}, notQ.getData());
    }

}