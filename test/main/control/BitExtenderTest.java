package main.control;

import main.BitStream;
import main.exceptions.BitStreamInputSizeMismatch;
import main.utils.ProcessRunner;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BitExtenderTest {

    @Test
    void test1() {
        BitStream in1 = new BitStream(1);

        BitStream out1 = new BitStream(4);

        List<BitStream> inputs = new ArrayList<>();
        inputs.add(in1);
        List<BitStream> outputs = new ArrayList<>();
        outputs.add(out1);

        BitExtender extender = new BitExtender(inputs, outputs);

        in1.setData(new boolean[]{true});

        ProcessRunner.run(extender);

        assertArrayEquals(new boolean[]{true, true, true, true}, out1.getData());

        in1.setData(new boolean[]{false});

        ProcessRunner.run(extender);

        assertArrayEquals(new boolean[]{false, false, false, false}, out1.getData());
    }

    @Test
    void test2() {
        BitStream in1 = new BitStream(1);
        BitStream in2 = new BitStream(1);

        BitStream out1 = new BitStream(4);

        List<BitStream> inputs = new ArrayList<>();
        inputs.addAll(List.of(in1, in2));
        List<BitStream> outputs = new ArrayList<>();
        outputs.add(out1);

        BitExtender extender = new BitExtender(inputs, outputs);

        in1.setData(new boolean[]{true});
        in2.setData(new boolean[]{false});

        ProcessRunner.run(extender);

        assertArrayEquals(new boolean[]{true, true, false, false}, out1.getData());

        in1.setData(new boolean[]{false});
        in2.setData(new boolean[]{false});

        ProcessRunner.run(extender);

        assertArrayEquals(new boolean[]{false, false, false, false}, out1.getData());

        in1.setData(new boolean[]{true});
        in2.setData(new boolean[]{true});

        ProcessRunner.run(extender);

        assertArrayEquals(new boolean[]{true, true, true, true}, out1.getData());
    }

    @Test
    void test3() {
        BitStream in1 = new BitStream(1);
        BitStream in2 = new BitStream(1);

        BitStream out1 = new BitStream(2);
        BitStream out2 = new BitStream(2);

        List<BitStream> inputs = new ArrayList<>();
        inputs.addAll(List.of(in1, in2));
        List<BitStream> outputs = new ArrayList<>();
        outputs.addAll(List.of(out1, out2));

        BitExtender extender = new BitExtender(inputs, outputs);

        in1.setData(new boolean[]{false});
        in2.setData(new boolean[]{true});

        ProcessRunner.run(extender);

        assertArrayEquals(new boolean[]{false, false}, out1.getData());
        assertArrayEquals(new boolean[]{true, true}, out2.getData());

        in1.setData(new boolean[]{true});
        in2.setData(new boolean[]{true});

        ProcessRunner.run(extender);

        assertArrayEquals(new boolean[]{true, true}, out1.getData());
        assertArrayEquals(new boolean[]{true, true}, out2.getData());

        in1.setData(new boolean[]{false});
        in2.setData(new boolean[]{false});

        ProcessRunner.run(extender);

        assertArrayEquals(new boolean[]{false, false}, out1.getData());
        assertArrayEquals(new boolean[]{false, false}, out2.getData());
    }

    @Test
    void test4() {
        BitStream in1 = new BitStream(3);
        BitStream in2 = new BitStream(3);

        BitStream out1 = new BitStream(4);
        BitStream out2 = new BitStream(4);
        BitStream out3 = new BitStream(4);

        List<BitStream> inputs = new ArrayList<>();
        inputs.addAll(List.of(in1, in2));
        List<BitStream> outputs = new ArrayList<>();
        outputs.addAll(List.of(out1, out2, out3));

        BitExtender extender = new BitExtender(inputs, outputs);

        in1.setData(new boolean[]{true, false, false});
        in2.setData(new boolean[]{false, true, false});

        ProcessRunner.run(extender);

        assertArrayEquals(new boolean[]{true, true, false, false}, out1.getData());
        assertArrayEquals(new boolean[]{false, false, false, false}, out2.getData());
        assertArrayEquals(new boolean[]{true, true, false, false}, out3.getData());

        in1.setData(new boolean[]{false, true, true});
        in2.setData(new boolean[]{true, false, true});

        ProcessRunner.run(extender);

        assertArrayEquals(new boolean[]{false, false, true, true}, out1.getData());
        assertArrayEquals(new boolean[]{true, true, true, true}, out2.getData());
        assertArrayEquals(new boolean[]{false, false, true, true}, out3.getData());
    }

    @Test
    void testMismatch1() {
        BitStream in1 = new BitStream(3);
        BitStream in2 = new BitStream(3);

        BitStream out1 = new BitStream(4);
        BitStream out2 = new BitStream(4);

        List<BitStream> inputs = new ArrayList<>();
        inputs.addAll(List.of(in1, in2));
        List<BitStream> outputs = new ArrayList<>();
        outputs.addAll(List.of(out1, out2));

        assertThrows(BitStreamInputSizeMismatch.class, () -> {
            BitExtender extender = new BitExtender(inputs, outputs);
        });
    }

    @Test
    void testMismatch2() {
        BitStream in1 = new BitStream(3);
        BitStream in2 = new BitStream(3);

        BitStream out1 = new BitStream(4);
        BitStream out2 = new BitStream(2);

        List<BitStream> inputs = new ArrayList<>();
        inputs.addAll(List.of(in1, in2));
        List<BitStream> outputs = new ArrayList<>();
        outputs.addAll(List.of(out1, out2));

        assertThrows(BitStreamInputSizeMismatch.class, () -> {
            BitExtender extender = new BitExtender(inputs, outputs);
        });
    }

    @Test
    void testMismatch3() {
        BitStream in1 = new BitStream(4);
        BitStream in2 = new BitStream(2);

        BitStream out1 = new BitStream(3);
        BitStream out2 = new BitStream(3);

        List<BitStream> inputs = new ArrayList<>();
        inputs.addAll(List.of(in1, in2));
        List<BitStream> outputs = new ArrayList<>();
        outputs.addAll(List.of(out1, out2));

        assertThrows(BitStreamInputSizeMismatch.class, () -> {
            BitExtender extender = new BitExtender(inputs, outputs);
        });
    }

    @Test
    void testMismatch4() {
        BitStream in1 = new BitStream(5);

        BitStream out1 = new BitStream(3);
        BitStream out2 = new BitStream(3);

        List<BitStream> inputs = new ArrayList<>();
        inputs.addAll(List.of(in1));
        List<BitStream> outputs = new ArrayList<>();
        outputs.addAll(List.of(out1, out2));

        assertThrows(BitStreamInputSizeMismatch.class, () -> {
            BitExtender extender = new BitExtender(inputs, outputs);
        });
    }
}