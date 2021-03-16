package main.circuits;

import main.BitStream;
import main.Node;
import main.control.Input;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DecoderTest {

    void run(List<Node> queue) {
        while (queue.size() > 0) {
            Node node = queue.remove(0);
            node.evaluate(queue);
        }
    }

    @Test
    void testSize1() {
        BitStream input = new BitStream(1);

        Input inputObject = new Input(new boolean[]{false}, input);

        BitStream out0 = new BitStream(1);
        BitStream out1 = new BitStream(1);

        List<BitStream> outList = new ArrayList<>();
        outList.addAll(List.of(out0, out1));

        Decoder decoder = new Decoder(input, outList);

        List<Node> queue = new ArrayList<>();
        queue.add(inputObject);
        run(queue);

        assertArrayEquals(new boolean[]{true}, out0.getData());
        assertArrayEquals(new boolean[]{false}, out1.getData());

        inputObject.setData(new boolean[]{true});

        queue = new ArrayList<>();
        queue.add(inputObject);
        run(queue);

        assertArrayEquals(new boolean[]{false}, out0.getData());
        assertArrayEquals(new boolean[]{true}, out1.getData());
    }

    @Test
    void testSize2() {
        BitStream input = new BitStream(2);

        Input inputObject = new Input(new boolean[]{false, false}, input);

        BitStream out0 = new BitStream(1);
        BitStream out1 = new BitStream(1);
        BitStream out2 = new BitStream(1);
        BitStream out3 = new BitStream(1);

        List<BitStream> outList = new ArrayList<>();
        outList.addAll(List.of(out0, out1, out2, out3));

        Decoder decoder = new Decoder(input, outList);

        List<Node> queue = new ArrayList<>();
        queue.add(inputObject);
        run(queue);

        assertArrayEquals(new boolean[]{true}, out0.getData());
        assertArrayEquals(new boolean[]{false}, out1.getData());
        assertArrayEquals(new boolean[]{false}, out2.getData());
        assertArrayEquals(new boolean[]{false}, out3.getData());

        inputObject.setData(new boolean[]{false, true});

        queue = new ArrayList<>();
        queue.add(inputObject);
        run(queue);

        assertArrayEquals(new boolean[]{false}, out0.getData());
        assertArrayEquals(new boolean[]{true}, out1.getData());
        assertArrayEquals(new boolean[]{false}, out2.getData());
        assertArrayEquals(new boolean[]{false}, out3.getData());

        inputObject.setData(new boolean[]{true, false});

        queue = new ArrayList<>();
        queue.add(inputObject);
        run(queue);

        assertArrayEquals(new boolean[]{false}, out0.getData());
        assertArrayEquals(new boolean[]{false}, out1.getData());
        assertArrayEquals(new boolean[]{true}, out2.getData());
        assertArrayEquals(new boolean[]{false}, out3.getData());

        inputObject.setData(new boolean[]{true, true});

        queue = new ArrayList<>();
        queue.add(inputObject);
        run(queue);

        assertArrayEquals(new boolean[]{false}, out0.getData());
        assertArrayEquals(new boolean[]{false}, out1.getData());
        assertArrayEquals(new boolean[]{false}, out2.getData());
        assertArrayEquals(new boolean[]{true}, out3.getData());
    }

    @Test
    void testSize3() {
        BitStream input = new BitStream(3);

        Input inputObject = new Input(new boolean[]{false, false, false}, input);

        BitStream out0 = new BitStream(1);
        BitStream out1 = new BitStream(1);
        BitStream out2 = new BitStream(1);
        BitStream out3 = new BitStream(1);
        BitStream out4 = new BitStream(1);
        BitStream out5 = new BitStream(1);
        BitStream out6 = new BitStream(1);
        BitStream out7 = new BitStream(1);

        List<BitStream> outList = new ArrayList<>();
        outList.addAll(List.of(out0, out1, out2, out3, out4, out5, out6, out7));

        Decoder decoder = new Decoder(input, outList);

        List<Node> queue = new ArrayList<>();
        queue.add(inputObject);
        run(queue);

        assertArrayEquals(new boolean[]{true}, out0.getData());
        assertArrayEquals(new boolean[]{false}, out1.getData());
        assertArrayEquals(new boolean[]{false}, out4.getData());
        assertArrayEquals(new boolean[]{false}, out6.getData());

        inputObject.setData(new boolean[]{false, false, true});

        queue = new ArrayList<>();
        queue.add(inputObject);
        run(queue);

        assertArrayEquals(new boolean[]{true}, out1.getData());
        assertArrayEquals(new boolean[]{false}, out2.getData());
        assertArrayEquals(new boolean[]{false}, out4.getData());
        assertArrayEquals(new boolean[]{false}, out6.getData());

        inputObject.setData(new boolean[]{false, true, false});

        queue = new ArrayList<>();
        queue.add(inputObject);
        run(queue);

        assertArrayEquals(new boolean[]{true}, out2.getData());
        assertArrayEquals(new boolean[]{false}, out1.getData());
        assertArrayEquals(new boolean[]{false}, out4.getData());
        assertArrayEquals(new boolean[]{false}, out6.getData());

        inputObject.setData(new boolean[]{true, true, false});

        queue = new ArrayList<>();
        queue.add(inputObject);
        run(queue);

        assertArrayEquals(new boolean[]{true}, out6.getData());
        assertArrayEquals(new boolean[]{false}, out1.getData());
        assertArrayEquals(new boolean[]{false}, out4.getData());
        assertArrayEquals(new boolean[]{false}, out5.getData());

        inputObject.setData(new boolean[]{true, true, true});

        queue = new ArrayList<>();
        queue.add(inputObject);
        run(queue);

        assertArrayEquals(new boolean[]{true}, out7.getData());
        assertArrayEquals(new boolean[]{false}, out1.getData());
        assertArrayEquals(new boolean[]{false}, out4.getData());
        assertArrayEquals(new boolean[]{false}, out6.getData());
    }

    @Test
    void testSize8() {
        BitStream in = new BitStream(8);

        Input inputObject = new Input(new boolean[]{false, false, false, false,
                                                    false, false, false, false}, in);

        List<BitStream> outputs = new ArrayList<>();
        for (int i = 0; i < 256; i++) {
            outputs.add(new BitStream(1));
        }

        Decoder decoder = new Decoder(in, outputs);

        List<Node> queue = new ArrayList<>();
        queue.add(inputObject);
        run(queue);

        assertArrayEquals(new boolean[]{true}, outputs.get(0).getData());
        assertArrayEquals(new boolean[]{false}, outputs.get(8).getData());
        assertArrayEquals(new boolean[]{false}, outputs.get(15).getData());


        inputObject.setData(new boolean[]{false, false, false, false,
                                            true, false, false, false});

        queue = new ArrayList<>();
        queue.add(inputObject);
        run(queue);

        assertArrayEquals(new boolean[]{true}, outputs.get(8).getData());
        assertArrayEquals(new boolean[]{false}, outputs.get(26).getData());
        assertArrayEquals(new boolean[]{false}, outputs.get(62).getData());

        inputObject.setData(new boolean[]{false, true, false, false,
                true, false, true, false});

        queue = new ArrayList<>();
        queue.add(inputObject);
        run(queue);

        assertArrayEquals(new boolean[]{true}, outputs.get(74).getData());
        assertArrayEquals(new boolean[]{false}, outputs.get(187).getData());
        assertArrayEquals(new boolean[]{false}, outputs.get(23).getData());

        inputObject.setData(new boolean[]{true, false, false, false,
                true, false, false, false});

        queue = new ArrayList<>();
        queue.add(inputObject);
        run(queue);

        assertArrayEquals(new boolean[]{true}, outputs.get(136).getData());
        assertArrayEquals(new boolean[]{false}, outputs.get(234).getData());
        assertArrayEquals(new boolean[]{false}, outputs.get(255).getData());
    }
}