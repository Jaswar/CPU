package main.control;

import main.BitStream;
import main.Node;
import main.exceptions.IllegalSplitException;
import main.exceptions.InconsistentBitStreamSources;
import main.gates.binary.OR;
import main.utils.ProcessRunner;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SplitterTest {

    @Test
    void testSplitting1() {
        BitStream in1 = new BitStream(4);
        BitStream out1 = new BitStream(1);
        BitStream out2 = new BitStream(1);
        BitStream out3 = new BitStream(1);
        BitStream out4 = new BitStream(1);

        List<BitStream> in = new ArrayList<>();
        List<BitStream> out = new ArrayList<>();
        in.add(in1);
        out.addAll(List.of(out1, out2, out3, out4));

        Splitter splitter = new Splitter(in, out);
        in1.setData(new boolean[]{true, true, false, true});

        ProcessRunner.run(splitter);

        assertArrayEquals(new boolean[]{true}, out1.getData());
        assertArrayEquals(new boolean[]{true}, out2.getData());
        assertArrayEquals(new boolean[]{false}, out3.getData());
        assertArrayEquals(new boolean[]{true}, out4.getData());
    }

    @Test
    void testSplitting2() {
        BitStream in1 = new BitStream(4);
        BitStream out1 = new BitStream(2);
        BitStream out2 = new BitStream(2);

        List<BitStream> in = new ArrayList<>();
        List<BitStream> out = new ArrayList<>();
        in.add(in1);
        out.addAll(List.of(out1, out2));

        Splitter splitter = new Splitter(in, out);
        in1.setData(new boolean[]{false, true, true, false});

        ProcessRunner.run(splitter);

        assertArrayEquals(new boolean[]{false, true}, out1.getData());
        assertArrayEquals(new boolean[]{true, false}, out2.getData());
    }

    @Test
    void testMerging1() {
        BitStream in1 = new BitStream(6);
        BitStream in2 = new BitStream(6);
        BitStream out1 = new BitStream(4);
        BitStream out2 = new BitStream(4);
        BitStream out3 = new BitStream(4);

        List<BitStream> in = new ArrayList<>();
        List<BitStream> out = new ArrayList<>();
        in.add(in1);
        in.add(in2);
        out.addAll(List.of(out1, out2, out3));

        Splitter splitter = new Splitter(in, out);
        in1.setData(new boolean[]{false, true, true, false, true, true});
        in2.setData(new boolean[]{true, false, true, true, false, true});

        ProcessRunner.run(splitter);

        assertArrayEquals(new boolean[]{false, true, true, false}, out1.getData());
        assertArrayEquals(new boolean[]{true, true, true, false}, out2.getData());
        assertArrayEquals(new boolean[]{true, true, false, true}, out3.getData());
    }

    @Test
    void testMerging2() {
        BitStream in1 = new BitStream(1);
        BitStream in2 = new BitStream(1);
        BitStream out1 = new BitStream(2);

        List<BitStream> in = new ArrayList<>();
        List<BitStream> out = new ArrayList<>();
        in.add(in1);
        in.add(in2);
        out.addAll(List.of(out1));

        Splitter splitter = new Splitter(in, out);
        in1.setData(new boolean[]{false});
        in2.setData(new boolean[]{true});

        ProcessRunner.run(splitter);

        assertArrayEquals(new boolean[]{false, true}, out1.getData());
    }

    @Test
    void testMultiply1() {
        BitStream in1 = new BitStream(1);

        BitStream out1 = new BitStream(4);

        List<BitStream> inputs = new ArrayList<>();
        inputs.add(in1);
        List<BitStream> outputs = new ArrayList<>();
        outputs.add(out1);

        Splitter extender = new Splitter(inputs, outputs);

        in1.setData(new boolean[]{true});

        ProcessRunner.run(extender);

        assertArrayEquals(new boolean[]{true, true, true, true}, out1.getData());

        in1.setData(new boolean[]{false});

        ProcessRunner.run(extender);

        assertArrayEquals(new boolean[]{false, false, false, false}, out1.getData());
    }

    @Test
    void testMultiply2() {
        BitStream in1 = new BitStream(1);
        BitStream in2 = new BitStream(1);

        BitStream out1 = new BitStream(4);

        List<BitStream> inputs = new ArrayList<>();
        inputs.addAll(List.of(in1, in2));
        List<BitStream> outputs = new ArrayList<>();
        outputs.add(out1);

        Splitter extender = new Splitter(inputs, outputs);

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
    void testMultiply3() {
        BitStream in1 = new BitStream(1);
        BitStream in2 = new BitStream(1);

        BitStream out1 = new BitStream(2);
        BitStream out2 = new BitStream(2);

        List<BitStream> inputs = new ArrayList<>();
        inputs.addAll(List.of(in1, in2));
        List<BitStream> outputs = new ArrayList<>();
        outputs.addAll(List.of(out1, out2));

        Splitter extender = new Splitter(inputs, outputs);

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
    void testMultiply4() {
        BitStream in1 = new BitStream(3);
        BitStream in2 = new BitStream(3);

        BitStream out1 = new BitStream(4);
        BitStream out2 = new BitStream(4);
        BitStream out3 = new BitStream(4);

        List<BitStream> inputs = new ArrayList<>();
        inputs.addAll(List.of(in1, in2));
        List<BitStream> outputs = new ArrayList<>();
        outputs.addAll(List.of(out1, out2, out3));

        Splitter extender = new Splitter(inputs, outputs);

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

        assertThrows(IllegalSplitException.class, () -> {
            Splitter extender = new Splitter(inputs, outputs);
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

        assertThrows(IllegalSplitException.class, () -> {
            Splitter extender = new Splitter(inputs, outputs);
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

        assertThrows(IllegalSplitException.class, () -> {
            Splitter extender = new Splitter(inputs, outputs);
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

        assertThrows(IllegalSplitException.class, () -> {
            Splitter extender = new Splitter(inputs, outputs);
        });
    }

    @Test
    void testIllegalSplit1() {
        BitStream in1 = new BitStream(4);
        BitStream out1 = new BitStream(1);
        BitStream out2 = new BitStream(1);
        BitStream out3 = new BitStream(1);

        List<BitStream> in = new ArrayList<>();
        List<BitStream> out = new ArrayList<>();
        in.add(in1);
        out.addAll(List.of(out1, out2, out3));

        assertThrows(IllegalSplitException.class, () -> {
            Splitter splitter = new Splitter(in, out);
        });
    }

    @Test
    void testIllegalSplit2() {
        BitStream in1 = new BitStream(4);
        BitStream out1 = new BitStream(1);
        BitStream out2 = new BitStream(1);
        BitStream out3 = new BitStream(2);
        BitStream out4 = new BitStream(1);

        List<BitStream> in = new ArrayList<>();
        List<BitStream> out = new ArrayList<>();
        in.add(in1);
        out.addAll(List.of(out1, out2, out3, out4));

        assertThrows(IllegalSplitException.class, () -> {
            Splitter splitter = new Splitter(in, out);
        });
    }

    @Test
    void testIllegalSplit3() {
        BitStream in1 = new BitStream(4);
        BitStream in2 = new BitStream(2);
        BitStream out1 = new BitStream(1);
        BitStream out2 = new BitStream(1);
        BitStream out3 = new BitStream(2);
        BitStream out4 = new BitStream(3);

        List<BitStream> in = new ArrayList<>();
        List<BitStream> out = new ArrayList<>();
        in.add(in1);
        in.add(in2);
        out.addAll(List.of(out1, out2, out3, out4));

        assertThrows(IllegalSplitException.class, () -> {
            Splitter splitter = new Splitter(in, out);
        });
    }

//    @Test
//    void testInconsistency1() {
//        BitStream in1 = new BitStream(2);
//        BitStream out1 = new BitStream(1);
//        BitStream out2 = new BitStream(1);
//
//        BitStream orIn1 = new BitStream(1);
//        BitStream orIn2 = new BitStream(1);
//
//        List<BitStream> in = new ArrayList<>();
//        List<BitStream> out = new ArrayList<>();
//        in.add(in1);
//        out.addAll(List.of(out1, out2));
//
//        in1.setData(new boolean[]{true, false});
//        orIn1.setData(new boolean[]{true});
//        orIn2.setData(new boolean[]{true});
//
//        OR or = new OR(orIn1, orIn2, out2);
//
//        assertThrows(InconsistentBitStreamSources.class, () -> {
//            Splitter splitter = new Splitter(in, out);
//        });
//    }
//
//    @Test
//    void testInconsistency2() {
//        BitStream in1 = new BitStream(2);
//        BitStream in2 = new BitStream(2);
//        BitStream out1 = new BitStream(4);
//
//        BitStream orIn1 = new BitStream(4);
//        BitStream orIn2 = new BitStream(4);
//
//        List<BitStream> in = new ArrayList<>();
//        List<BitStream> out = new ArrayList<>();
//        in.add(in1);
//        in.add(in2);
//        out.addAll(List.of(out1));
//
//        in1.setData(new boolean[]{true, true});
//        in1.setData(new boolean[]{false, true});
//        orIn1.setData(new boolean[]{true, false, false, false});
//        orIn2.setData(new boolean[]{true, false, false, true});
//
//        OR or = new OR(orIn1, orIn2, out1);
//
//        assertThrows(InconsistentBitStreamSources.class, () -> {
//            Splitter splitter = new Splitter(in, out);
//        });
//    }
}