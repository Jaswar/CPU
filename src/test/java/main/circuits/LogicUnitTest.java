package main.circuits;

import main.BitStream;
import main.Node;
import main.control.Input;
import main.control.Output;
import main.utils.ProcessRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LogicUnitTest {

    Input sourceInput, destinationInput, notControlInput, orControlInput, andControlInput,
            xorControlInput, nandControlInput, norControlInput;
    Output outputObject;

    @BeforeEach
    void setup() {
        BitStream source = new BitStream(4);
        BitStream destination = new BitStream(4);
        BitStream output = new BitStream(4);

        BitStream notControl = new BitStream(1);
        BitStream orControl = new BitStream(1);
        BitStream andControl = new BitStream(1);
        BitStream xorControl = new BitStream(1);
        BitStream nandControl = new BitStream(1);
        BitStream norControl = new BitStream(1);

        List<BitStream> controls = new ArrayList<>();
        controls.addAll(List.of(notControl, orControl, andControl, xorControl, nandControl, norControl));

        sourceInput = new Input(new boolean[]{true, false, true, true}, source);
        destinationInput = new Input(new boolean[]{false, false, true, false}, destination);

        notControlInput = new Input(new boolean[]{false}, notControl);
        orControlInput = new Input(new boolean[]{false}, orControl);
        andControlInput = new Input(new boolean[]{false}, andControl);
        xorControlInput = new Input(new boolean[]{false}, xorControl);
        nandControlInput = new Input(new boolean[]{false}, nandControl);
        norControlInput = new Input(new boolean[]{false}, norControl);

        outputObject = new Output(output);

        LogicUnit logicUnit = new LogicUnit(source, destination, output, controls);
    }

    @Test
    void testNot() {
        notControlInput.setData(new boolean[]{true});

        ProcessRunner.run(sourceInput, destinationInput, notControlInput, orControlInput,
                andControlInput, xorControlInput, nandControlInput, norControlInput);

        assertArrayEquals(new boolean[]{false, true, false, false}, outputObject.getData());
    }

    @Test
    void testOr() {
        orControlInput.setData(new boolean[]{true});

        ProcessRunner.run(sourceInput, destinationInput, notControlInput, orControlInput,
                andControlInput, xorControlInput, nandControlInput, norControlInput);

        assertArrayEquals(new boolean[]{true, false, true, true}, outputObject.getData());
    }

    @Test
    void testAnd() {
        andControlInput.setData(new boolean[]{true});

        ProcessRunner.run(sourceInput, destinationInput, notControlInput, orControlInput,
                andControlInput, xorControlInput, nandControlInput, norControlInput);

        assertArrayEquals(new boolean[]{false, false, true, false}, outputObject.getData());
    }

    @Test
    void testXor() {
        xorControlInput.setData(new boolean[]{true});

        ProcessRunner.run(sourceInput, destinationInput, notControlInput, orControlInput,
                andControlInput, xorControlInput, nandControlInput, norControlInput);

        assertArrayEquals(new boolean[]{true, false, false, true}, outputObject.getData());
    }

    @Test
    void testNand() {
        nandControlInput.setData(new boolean[]{true});

        ProcessRunner.run(sourceInput, destinationInput, notControlInput, orControlInput,
                andControlInput, xorControlInput, nandControlInput, norControlInput);

        assertArrayEquals(new boolean[]{true, true, false, true}, outputObject.getData());
    }

    @Test
    void testNor() {
        norControlInput.setData(new boolean[]{true});

        ProcessRunner.run(sourceInput, destinationInput, notControlInput, orControlInput,
                andControlInput, xorControlInput, nandControlInput, norControlInput);

        assertArrayEquals(new boolean[]{false, true, false, false}, outputObject.getData());
    }

    @Test
    void testMultiple1() {
        testNor();
        norControlInput.setData(new boolean[]{false});
        testAnd();
        andControlInput.setData(new boolean[]{false});
        testOr();
    }

    @Test
    void testMultiple2() {
        testNot();
        notControlInput.setData(new boolean[]{false});
        testOr();
        orControlInput.setData(new boolean[]{false});
        testAnd();
        andControlInput.setData(new boolean[]{false});
        testXor();
        xorControlInput.setData(new boolean[]{false});
        testNand();
        nandControlInput.setData(new boolean[]{false});
        testNor();
    }
}