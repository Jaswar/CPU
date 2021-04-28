package main.gates.binary;

import main.BitStream;
import main.Node;
import main.control.Input;
import main.control.Output;
import main.gates.binary.AND;
import main.utils.DataConverter;
import main.utils.ProcessRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ANDTest {

    BitStream stream1, stream2, stream3;
    Input input1, input2;
    Output output;
    AND and1;

    @BeforeEach
    void setup() {
        stream1 = new BitStream(1);
        stream2 = new BitStream(1);
        stream3 = new BitStream(1);

        input1 = new Input(new boolean[]{true}, stream1);
        input2 = new Input(new boolean[]{true}, stream2);

        and1 = new AND(stream1, stream2, stream3);

        output = new Output(stream3);
    }

    @Test
    void test1() {
        input1.setData(new boolean[]{false});
        input2.setData(new boolean[]{false});

        ProcessRunner.run(input1, input2);

        assertEquals("0", DataConverter.convertBoolToBin(output.getData()));
    }

    @Test
    void test2() {
        input1.setData(new boolean[]{true});
        input2.setData(new boolean[]{false});

        ProcessRunner.run(input1, input2);

        assertEquals("0", DataConverter.convertBoolToBin(output.getData()));
    }

    @Test
    void test3() {
        input1.setData(new boolean[]{false});
        input2.setData(new boolean[]{true});

        ProcessRunner.run(input1, input2);

        assertEquals("0", DataConverter.convertBoolToBin(output.getData()));
    }

    @Test
    void test4() {
        input1.setData(new boolean[]{true});
        input2.setData(new boolean[]{true});

        ProcessRunner.run(input1, input2);

        assertEquals("1", DataConverter.convertBoolToBin(output.getData()));
    }

}