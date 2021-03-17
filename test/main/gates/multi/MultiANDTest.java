package main.gates.multi;

import main.BitStream;
import main.Node;
import main.exceptions.BitStreamInputSizeMismatch;
import main.utils.ProcessRunner;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MultiANDTest {

    @Test
    void test1() {
        BitStream in1 = new BitStream(4);
        BitStream in2 = new BitStream(4);
        BitStream in3 = new BitStream(4);

        BitStream out = new BitStream(4);

        List<BitStream> inputs = new ArrayList<>();
        inputs.addAll(List.of(in1, in2, in3));

        in1.setData(new boolean[]{false, true, false, true});
        in2.setData(new boolean[]{false, true, false, true});
        in3.setData(new boolean[]{false, true, true, false});

        MultiAND and = new MultiAND(inputs, out);

        ProcessRunner.run(and);

        assertArrayEquals(new boolean[]{false, true, false, false}, out.getData());
    }

    @Test
    void test2() {
        BitStream in1 = new BitStream(4);
        BitStream in2 = new BitStream(4);
        BitStream in3 = new BitStream(4);

        BitStream out = new BitStream(4);

        List<BitStream> inputs = new ArrayList<>();
        inputs.addAll(List.of(in1, in2, in3));

        in1.setData(new boolean[]{false, true, true, true});
        in2.setData(new boolean[]{true, true, true, true});
        in3.setData(new boolean[]{false, true, true, true});

        MultiAND and = new MultiAND(inputs, out);

        ProcessRunner.run(and);

        assertArrayEquals(new boolean[]{false, true, true, true}, out.getData());
    }

    @Test
    void test3() {
        BitStream in1 = new BitStream(4);
        BitStream in2 = new BitStream(4);
        BitStream in3 = new BitStream(4);

        BitStream out = new BitStream(4);

        List<BitStream> inputs = new ArrayList<>();
        inputs.addAll(List.of(in1, in2, in3));

        in1.setData(new boolean[]{false, true, true, false});
        in2.setData(new boolean[]{true, false, false, true});
        in3.setData(new boolean[]{false, true, true, false});

        MultiAND and = new MultiAND(inputs, out);

        ProcessRunner.run(and);

        assertArrayEquals(new boolean[]{false, false, false, false}, out.getData());
    }

    @Test
    void testThrowsError() {
        BitStream in1 = new BitStream(4);
        BitStream in2 = new BitStream(4);
        BitStream in3 = new BitStream(3);

        BitStream out = new BitStream(4);

        List<BitStream> inputs = new ArrayList<>();
        inputs.addAll(List.of(in1, in2, in3));

        in1.setData(new boolean[]{false, true, true, true});
        in2.setData(new boolean[]{true, true, true, true});
        in3.setData(new boolean[]{false, true, true});

        assertThrows(BitStreamInputSizeMismatch.class, () -> {
            MultiAND and = new MultiAND(inputs, out);
        });
    }
}