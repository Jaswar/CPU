package main.circuits;

import main.BitStream;
import main.control.Input;
import main.control.Output;
import main.utils.ProcessRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ALUTest {

    Input sourceInput, destinationInput, opCodeInput, aluInInput;
    Output mainOutput, overflowOutput;

    @BeforeEach
    void setup() {
        BitStream source = new BitStream(4);
        BitStream destination = new BitStream(4);
        BitStream output = new BitStream(4);
        BitStream opCode = new BitStream(5);
        BitStream aluIn = new BitStream(1);
        BitStream overflow = new BitStream(1);

        sourceInput = new Input(new boolean[]{false, false, false, false}, source);
        destinationInput = new Input(new boolean[]{false, false, false, false}, destination);
        opCodeInput = new Input(new boolean[]{false, false, false, false, false}, opCode);
        aluInInput = new Input(new boolean[]{false}, aluIn);

        mainOutput = new Output(output);
        overflowOutput = new Output(overflow);

        ALU alu = new ALU(source, destination, output, opCode, aluIn, overflow);
    }

    @Test
    void testSum() {
        sourceInput.setData(new boolean[]{true, true, false, true});
        destinationInput.setData(new boolean[]{false, true, true, true});
        opCodeInput.setData(new boolean[]{false, false, false, false, true});
        aluInInput.setData(new boolean[]{true});
        ProcessRunner.run(aluInInput);

        ProcessRunner.run(sourceInput, destinationInput, opCodeInput);
        assertArrayEquals(new boolean[]{false, true, false, false}, mainOutput.getData());
        assertArrayEquals(new boolean[]{false}, overflowOutput.getData());
    }

    @Test
    void testSumOverflow() {
        sourceInput.setData(new boolean[]{false, true, false, true});
        destinationInput.setData(new boolean[]{false, true, true, false});
        opCodeInput.setData(new boolean[]{false, false, false, false, true});
        aluInInput.setData(new boolean[]{true});
        ProcessRunner.run(aluInInput);

        ProcessRunner.run(sourceInput, destinationInput, opCodeInput);
        assertArrayEquals(new boolean[]{true, false, true, true}, mainOutput.getData());
        assertArrayEquals(new boolean[]{true}, overflowOutput.getData());
    }

    @Test
    void testSub() {
        sourceInput.setData(new boolean[]{true, true, true, false});
        destinationInput.setData(new boolean[]{true, false, true, true});
        opCodeInput.setData(new boolean[]{false, false, false, true, false});
        aluInInput.setData(new boolean[]{true});
        ProcessRunner.run(aluInInput);

        ProcessRunner.run(sourceInput, destinationInput, opCodeInput);
        assertArrayEquals(new boolean[]{false, false, true, true}, mainOutput.getData());
        assertArrayEquals(new boolean[]{false}, overflowOutput.getData());
    }

    @Test
    void testSubOverflow() {
        sourceInput.setData(new boolean[]{true, true, true, false});
        destinationInput.setData(new boolean[]{false, true, true, true});
        opCodeInput.setData(new boolean[]{false, false, false, true, false});
        aluInInput.setData(new boolean[]{true});
        ProcessRunner.run(aluInInput);

        ProcessRunner.run(sourceInput, destinationInput, opCodeInput);
        assertArrayEquals(new boolean[]{false, true, true, true}, mainOutput.getData());
        assertArrayEquals(new boolean[]{true}, overflowOutput.getData());
    }

    @Test
    void testNot() {
        sourceInput.setData(new boolean[]{true, false, true, false});
        destinationInput.setData(new boolean[]{false, false, true, true});
        opCodeInput.setData(new boolean[]{false, false, false, true, true});
        aluInInput.setData(new boolean[]{true});
        ProcessRunner.run(aluInInput);

        ProcessRunner.run(sourceInput, destinationInput, opCodeInput);
        assertArrayEquals(new boolean[]{false, true, false, true}, mainOutput.getData());
    }

    @Test
    void testOr() {
        sourceInput.setData(new boolean[]{true, false, true, false});
        destinationInput.setData(new boolean[]{false, false, true, true});
        opCodeInput.setData(new boolean[]{false, false, true, false, false});
        aluInInput.setData(new boolean[]{true});
        ProcessRunner.run(aluInInput);

        ProcessRunner.run(sourceInput, destinationInput, opCodeInput);
        assertArrayEquals(new boolean[]{true, false, true, true}, mainOutput.getData());
    }

    @Test
    void testAnd() {
        sourceInput.setData(new boolean[]{true, false, true, false});
        destinationInput.setData(new boolean[]{false, false, true, true});
        opCodeInput.setData(new boolean[]{false, false, true, false, true});
        aluInInput.setData(new boolean[]{true});
        ProcessRunner.run(aluInInput);

        ProcessRunner.run(sourceInput, destinationInput, opCodeInput);
        assertArrayEquals(new boolean[]{false, false, true, false}, mainOutput.getData());
    }

    @Test
    void testXor() {
        sourceInput.setData(new boolean[]{true, false, true, false});
        destinationInput.setData(new boolean[]{false, false, true, true});
        opCodeInput.setData(new boolean[]{false, false, true, true, false});
        aluInInput.setData(new boolean[]{true});
        ProcessRunner.run(aluInInput);

        ProcessRunner.run(sourceInput, destinationInput, opCodeInput);
        assertArrayEquals(new boolean[]{true, false, false, true}, mainOutput.getData());
    }

    @Test
    void testNand() {
        sourceInput.setData(new boolean[]{true, false, true, false});
        destinationInput.setData(new boolean[]{false, false, true, true});
        opCodeInput.setData(new boolean[]{false, false, true, true, true});
        aluInInput.setData(new boolean[]{true});
        ProcessRunner.run(aluInInput);

        ProcessRunner.run(sourceInput, destinationInput, opCodeInput);
        assertArrayEquals(new boolean[]{true, true, false, true}, mainOutput.getData());
    }

    @Test
    void testNor() {
        sourceInput.setData(new boolean[]{true, false, true, false});
        destinationInput.setData(new boolean[]{false, false, true, true});
        opCodeInput.setData(new boolean[]{false, true, false, false, false});
        aluInInput.setData(new boolean[]{true});
        ProcessRunner.run(aluInInput);

        ProcessRunner.run(sourceInput, destinationInput, opCodeInput);
        assertArrayEquals(new boolean[]{false, true, false, false}, mainOutput.getData());
    }

}