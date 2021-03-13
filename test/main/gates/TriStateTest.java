package main.gates;

import main.BitStream;
import main.Node;
import main.control.Input;
import main.exceptions.InconsistentBitStreamSources;
import main.gates.binary.AND;
import main.gates.binary.OR;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TriStateTest {

    void run(List<Node> queue) {
        while (queue.size() > 0) {
            Node node = queue.remove(0);
            node.evaluate(queue);
        }
    }

    @Test
    void test1() {
        BitStream in1 = new BitStream(2);
        BitStream in2 = new BitStream(2);
        BitStream in3 = new BitStream(2);
        BitStream in4 = new BitStream(2);
        BitStream control1 = new BitStream(1);
        BitStream control2 = new BitStream(1);
        BitStream outOr = new BitStream(2);
        BitStream outAnd = new BitStream(2);
        BitStream out = new BitStream(2);

        Input input1 = new Input(new boolean[]{true, false}, in1);
        Input input2 = new Input(new boolean[]{false, false}, in2);
        Input input3 = new Input(new boolean[]{false, true}, in3);
        Input input4 = new Input(new boolean[]{false, true}, in4);

        Input inputControl1 = new Input(new boolean[]{false}, control1);
        Input inputControl2 = new Input(new boolean[]{true}, control2);

        OR or = new OR(in1, in2, outOr);
        AND and = new AND(in3, in4, outAnd);
        TriState triStateOr = new TriState(outOr, control1, out);
        TriState triStateAnd = new TriState(outAnd, control2, out);

        List<Node> queue = new ArrayList<>();
        queue.addAll(List.of(input1, input2, input3, input4, inputControl1, inputControl2));
        run(queue);

        assertArrayEquals(new boolean[]{false, true}, out.getData());

        inputControl1.setData(new boolean[]{true});
        inputControl2.setData(new boolean[]{false});

        queue = new ArrayList<>();
        queue.addAll(List.of(input1, input2, input3, input4, inputControl1, inputControl2));
        run(queue);

        assertArrayEquals(new boolean[]{true, false}, out.getData());

        inputControl1.setData(new boolean[]{false});
        inputControl2.setData(new boolean[]{false});

        queue = new ArrayList<>();
        queue.addAll(List.of(or, and));
        run(queue);

        assertArrayEquals(new boolean[]{true, false}, out.getData());

//        inputControl1.setData(new boolean[]{true});
//        inputControl2.setData(new boolean[]{true});
//
//        assertThrows(InconsistentBitStreamSources.class, () -> {
//            List<Node> q = new ArrayList<>();
//            q.addAll(List.of(or, and));
//            run(q);
//        });
    }
}