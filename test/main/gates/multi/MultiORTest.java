package main.gates.multi;

import main.BitStream;
import main.Node;
import main.exceptions.BitStreamInputSizeMismatch;
import main.exceptions.InconsistentBitStreamSources;
import main.gates.binary.AND;
import main.gates.multi.MultiOR;
import main.utils.ProcessRunner;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MultiORTest {

    @Test
    void test1() {
        BitStream in1 = new BitStream(1);
        BitStream in2 = new BitStream(1);
        BitStream in3 = new BitStream(1);
        BitStream in4 = new BitStream(1);
        BitStream out = new BitStream(1);
        List<BitStream> input = new ArrayList<>();
        input.addAll(List.of(in1, in2, in3, in4));

        in1.setData(new boolean[]{false});
        in2.setData(new boolean[]{true});
        in3.setData(new boolean[]{true});
        in4.setData(new boolean[]{false});

        MultiOR or = new MultiOR(input, out);

        ProcessRunner.run(or);

        assertArrayEquals(new boolean[]{true}, out.getData());
    }

    @Test
    void test2() {
        BitStream in1 = new BitStream(1);
        BitStream in2 = new BitStream(1);
        BitStream in3 = new BitStream(1);
        BitStream out = new BitStream(1);
        List<BitStream> input = new ArrayList<>();
        input.addAll(List.of(in1, in2, in3));

        in1.setData(new boolean[]{false});
        in2.setData(new boolean[]{false});
        in3.setData(new boolean[]{false});

        MultiOR or = new MultiOR(input, out);

        ProcessRunner.run(or);

        assertArrayEquals(new boolean[]{false}, out.getData());
    }

    @Test
    void testIncorrectSize() {
        BitStream in1 = new BitStream(1);
        BitStream in2 = new BitStream(1);
        BitStream in3 = new BitStream(2);
        BitStream out = new BitStream(1);
        List<BitStream> input = new ArrayList<>();
        input.addAll(List.of(in1, in2, in3));

        in1.setData(new boolean[]{false});
        in2.setData(new boolean[]{false, true});
        in3.setData(new boolean[]{false});

        assertThrows(BitStreamInputSizeMismatch.class, () -> {
            MultiOR or = new MultiOR(input, out);
        });
    }

//    @Test
//    void testInconsistency() {
//        BitStream in1 = new BitStream(1);
//        BitStream in2 = new BitStream(1);
//        BitStream in3 = new BitStream(1);
//        BitStream out = new BitStream(1);
//        List<BitStream> input = new ArrayList<>();
//        input.addAll(List.of(in1, in2, in3));
//
//        in1.setData(new boolean[]{false});
//        in2.setData(new boolean[]{true});
//        in3.setData(new boolean[]{false});
//
//        AND and = new AND(in1, in3, out);
//
//        assertThrows(InconsistentBitStreamSources.class, () -> {
//            MultiOR or = new MultiOR(input, out);
//        });
//    }
}