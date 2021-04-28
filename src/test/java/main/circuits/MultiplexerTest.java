package main.circuits;

import main.BitStream;
import main.control.Input;
import main.utils.ProcessRunner;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MultiplexerTest {

    @Test
    void testSize1() {
        BitStream in0 = new BitStream(4);
        BitStream in1 = new BitStream(4);

        BitStream out = new BitStream(4);

        BitStream select = new BitStream(1);

        List<BitStream> in = new ArrayList<>();
        in.addAll(List.of(in0, in1));

        Input input0 = new Input(new boolean[]{true, false, true, false}, in0);
        Input input1 = new Input(new boolean[]{false, false, false, true}, in1);

        Input selectInput = new Input(new boolean[]{false}, select);

        Multiplexer multiplexer = new Multiplexer(in, select, out);

        ProcessRunner.run(input0, input1, selectInput);

        assertArrayEquals(new boolean[]{true, false, true, false}, out.getData());

        selectInput.setData(new boolean[]{true});
        ProcessRunner.run(selectInput);

        assertArrayEquals(new boolean[]{false, false, false, true}, out.getData());

    }

    @Test
    void testSize2() {
        BitStream in0 = new BitStream(4);
        BitStream in1 = new BitStream(4);
        BitStream in2 = new BitStream(4);
        BitStream in3 = new BitStream(4);

        BitStream out = new BitStream(4);

        BitStream select = new BitStream(2);

        List<BitStream> in = new ArrayList<>();
        in.addAll(List.of(in0, in1, in2, in3));

        Input input0 = new Input(new boolean[]{true, false, true, false}, in0);
        Input input1 = new Input(new boolean[]{false, false, false, true}, in1);
        Input input2 = new Input(new boolean[]{true, false, true, true}, in2);
        Input input3 = new Input(new boolean[]{false, true, true, false}, in3);

        Input selectInput = new Input(new boolean[]{false, false}, select);

        Multiplexer multiplexer = new Multiplexer(in, select, out);

        ProcessRunner.run(input0, input1, input2, input3, selectInput);

        assertArrayEquals(new boolean[]{true, false, true, false}, out.getData());

        selectInput.setData(new boolean[]{false, true});
        ProcessRunner.run(selectInput);

        assertArrayEquals(new boolean[]{false, false, false, true}, out.getData());

        selectInput.setData(new boolean[]{true, false});
        ProcessRunner.run(selectInput);

        assertArrayEquals(new boolean[]{true, false, true, true}, out.getData());

        selectInput.setData(new boolean[]{true, true});
        ProcessRunner.run(selectInput);

        assertArrayEquals(new boolean[]{false, true, true, false}, out.getData());

    }

}