package main.control;

import main.BitStream;
import main.Node;
import main.exceptions.IllegalSplitException;
import main.exceptions.InconsistentBitStreamSources;
import main.gates.OR;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SplitterTest {

    void run(List<Node> queue) {
        while (queue.size() > 0) {
            Node node = queue.remove(0);
            node.evaluate(queue);
        }
    }

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

        List<Node> queue = new ArrayList<>();
        queue.add(splitter);
        run(queue);

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

        List<Node> queue = new ArrayList<>();
        queue.add(splitter);
        run(queue);

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

        List<Node> queue = new ArrayList<>();
        queue.add(splitter);
        run(queue);

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

        List<Node> queue = new ArrayList<>();
        queue.add(splitter);
        run(queue);

        assertArrayEquals(new boolean[]{false, true}, out1.getData());
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

    @Test
    void testInconsistency1() {
        BitStream in1 = new BitStream(2);
        BitStream out1 = new BitStream(1);
        BitStream out2 = new BitStream(1);

        BitStream orIn1 = new BitStream(1);
        BitStream orIn2 = new BitStream(1);

        List<BitStream> in = new ArrayList<>();
        List<BitStream> out = new ArrayList<>();
        in.add(in1);
        out.addAll(List.of(out1, out2));

        in1.setData(new boolean[]{true, false});
        orIn1.setData(new boolean[]{true});
        orIn2.setData(new boolean[]{true});

        OR or = new OR(orIn1, orIn2, out2);

        assertThrows(InconsistentBitStreamSources.class, () -> {
            Splitter splitter = new Splitter(in, out);
        });
    }

    @Test
    void testInconsistency2() {
        BitStream in1 = new BitStream(2);
        BitStream in2 = new BitStream(2);
        BitStream out1 = new BitStream(4);

        BitStream orIn1 = new BitStream(4);
        BitStream orIn2 = new BitStream(4);

        List<BitStream> in = new ArrayList<>();
        List<BitStream> out = new ArrayList<>();
        in.add(in1);
        in.add(in2);
        out.addAll(List.of(out1));

        in1.setData(new boolean[]{true, true});
        in1.setData(new boolean[]{false, true});
        orIn1.setData(new boolean[]{true, false, false, false});
        orIn2.setData(new boolean[]{true, false, false, true});

        OR or = new OR(orIn1, orIn2, out1);

        assertThrows(InconsistentBitStreamSources.class, () -> {
            Splitter splitter = new Splitter(in, out);
        });
    }
}