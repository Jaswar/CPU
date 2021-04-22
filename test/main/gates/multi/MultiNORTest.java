package main.gates.multi;

import main.BitStream;
import main.utils.ProcessRunner;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MultiNORTest {

    @Test
    void test1() {
        BitStream in1 = new BitStream(1);
        BitStream in2 = new BitStream(1);
        BitStream in3 = new BitStream(1);
        BitStream out = new BitStream(1);

        in1.setData(new boolean[]{false});
        in2.setData(new boolean[]{false});
        in3.setData(new boolean[]{false});

        List<BitStream> in = new ArrayList<>();
        in.addAll(List.of(in1, in2, in3));

        MultiNOR nor = new MultiNOR(in, out);
        ProcessRunner.run(nor);

        assertArrayEquals(new boolean[]{true}, out.getData());
    }

    @Test
    void test2() {
        BitStream in1 = new BitStream(1);
        BitStream in2 = new BitStream(1);
        BitStream in3 = new BitStream(1);
        BitStream out = new BitStream(1);

        in1.setData(new boolean[]{true});
        in2.setData(new boolean[]{false});
        in3.setData(new boolean[]{false});

        List<BitStream> in = new ArrayList<>();
        in.addAll(List.of(in1, in2, in3));

        MultiNOR nor = new MultiNOR(in, out);
        ProcessRunner.run(nor);

        assertArrayEquals(new boolean[]{false}, out.getData());
    }

    @Test
    void test3() {
        BitStream in1 = new BitStream(1);
        BitStream in2 = new BitStream(1);
        BitStream in3 = new BitStream(1);
        BitStream out = new BitStream(1);

        in1.setData(new boolean[]{true});
        in2.setData(new boolean[]{false});
        in3.setData(new boolean[]{true});

        List<BitStream> in = new ArrayList<>();
        in.addAll(List.of(in1, in2, in3));

        MultiNOR nor = new MultiNOR(in, out);
        ProcessRunner.run(nor);

        assertArrayEquals(new boolean[]{false}, out.getData());
    }

    @Test
    void test4() {
        BitStream in1 = new BitStream(1);
        BitStream in2 = new BitStream(1);
        BitStream in3 = new BitStream(1);
        BitStream out = new BitStream(1);

        in1.setData(new boolean[]{true});
        in2.setData(new boolean[]{true});
        in3.setData(new boolean[]{true});

        List<BitStream> in = new ArrayList<>();
        in.addAll(List.of(in1, in2, in3));

        MultiNOR nor = new MultiNOR(in, out);
        ProcessRunner.run(nor);

        assertArrayEquals(new boolean[]{false}, out.getData());
    }

}