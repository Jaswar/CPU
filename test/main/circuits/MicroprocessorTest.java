package main.circuits;

import jdk.jfr.StackTrace;
import main.BitStream;
import main.control.Input;
import main.utils.ProcessRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.*;

class MicroprocessorTest {

    Input mainInput, IR1InControl, IR2InControl, clock, reset;
    BitStream source, destination, microinstruction, intermediate;

    @BeforeEach
    void setup() {
        BitStream input = new BitStream(16);
        BitStream IR1In = new BitStream(1);
        BitStream IR2In = new BitStream(1);
        BitStream clk = new BitStream(1);
        BitStream rst = new BitStream(1);
        source = new BitStream(3);
        destination = new BitStream(3);
        microinstruction = new BitStream(ControlUnit.NUM_MICROINSTRUCTIONS);
        intermediate = new BitStream(16);

        mainInput = new Input(new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false}, input);
        IR1InControl = new Input(new boolean[]{false}, IR1In);
        IR2InControl = new Input(new boolean[]{false}, IR2In);
        clock = new Input(new boolean[]{false}, clk);
        reset = new Input(new boolean[]{false}, rst);
        BitStream status = new BitStream(4);

        Microprocessor microprocessor = new Microprocessor(input, new BitStream(1), clk, new BitStream(1), rst, IR1In, IR2In,
                microinstruction, intermediate, source, destination, status, false, 1);
        ProcessRunner.run(mainInput, IR1InControl, IR2InControl, clock, reset);
    }

    @Test
    void testNoOperation() {
        assertArrayEquals(new boolean[]{true, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false}, microinstruction.getData());
    }

    @Test
    void testRegisterAddition() {
        mainInput.setData(new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, true, false, false, false});
        IR1InControl.setData(new boolean[]{true});
        ProcessRunner.run(mainInput, IR1InControl);

        testNoOperation();

        IR1InControl.setData(new boolean[]{false});
        ProcessRunner.run(IR1InControl);

        assertArrayEquals(new boolean[]{false, false, true, false, true, true, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false}, microinstruction.getData());
    }

    @Test
    void testProgressingRegisterAddition() {
        testRegisterAddition();

        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);

        assertArrayEquals(new boolean[]{false, false, true, false, false, false, false, false,
                false, false, false, true, true, false, false, false,
                false, false, false, false, false, false, false, false}, microinstruction.getData());

        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);
        assertArrayEquals(new boolean[]{false, false, true, false, false, false, false, false,
                false, false, false, true, true, false, false, false,
                false, false, false, false, false, false, false, false}, microinstruction.getData());
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);
        assertArrayEquals(new boolean[]{true, true, false, false, false, false, false, false,
                false, false, false, false, false, true, false, false,
                false, false, false, false, false, false, false, false}, microinstruction.getData());
    }

    @Test
    void testReset() {
        testProgressingRegisterAddition();

        reset.setData(new boolean[]{true});
        ProcessRunner.run(reset);

        assertArrayEquals(new boolean[]{false, false, true, false, true, true, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false}, microinstruction.getData());

        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);

        assertArrayEquals(new boolean[]{false, false, true, false, true, true, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false}, microinstruction.getData());

        reset.setData(new boolean[]{false});
        ProcessRunner.run(reset);

        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);

        assertArrayEquals(new boolean[]{false, false, true, false, false, false, false, false,
                false, false, false, true, true, false, false, false,
                false, false, false, false, false, false, false, false}, microinstruction.getData());
    }

    @Test
    void testIntermediate() {
        mainInput.setData(new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false});
        IR2InControl.setData(new boolean[]{true});
        ProcessRunner.run(mainInput, IR2InControl);

        IR2InControl.setData(new boolean[]{false});
        ProcessRunner.run(IR2InControl);

        assertArrayEquals(new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false}, intermediate.getData());
    }

    @Test
    void testBothInstructions() {
        testNoOperation();

        reset.setData(new boolean[]{true});
        ProcessRunner.run(reset);
        reset.setData(new boolean[]{false});
        ProcessRunner.run(reset);

        testProgressingRegisterAddition();
    }
}