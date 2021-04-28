package main.gates.multi;

import main.BitStream;
import main.utils.ProcessRunner;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MultiNANDTest {

    @Test
    void test1() {
        BitStream in1 = new BitStream(3);
        BitStream in2 = new BitStream(3);
        BitStream in3 = new BitStream(3);
        BitStream in4 = new BitStream(3);
        BitStream out = new BitStream(3);

        in1.setData(new boolean[]{false, true, true});
        in2.setData(new boolean[]{true, true, true});
        in3.setData(new boolean[]{false, true, false});
        in4.setData(new boolean[]{true, true, true});

        List<BitStream> input = new ArrayList<>();
        input.addAll(List.of(in1, in2, in3, in4));
        MultiNAND nand = new MultiNAND(input, out);

        ProcessRunner.run(nand);

        assertArrayEquals(new boolean[]{true, false, true}, out.getData());

        in1.setData(new boolean[]{true, true, true});
        in2.setData(new boolean[]{true, true, true});
        in3.setData(new boolean[]{true, false, false});
        in4.setData(new boolean[]{true, true, true});

        ProcessRunner.run(nand);
        assertArrayEquals(new boolean[]{false, true, true}, out.getData());

    }

}