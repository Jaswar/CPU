package main.gates;

import main.BitStream;
import main.Node;
import main.control.Input;
import main.control.Output;
import main.utils.BitInformationConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NORTest {

    BitStream stream1, stream2, stream3;
    Input input1, input2;
    Output output;
    NOR and1;

    @BeforeEach
    void setup() {
        stream1 = new BitStream(1);
        stream2 = new BitStream(1);
        stream3 = new BitStream(1);

        input1 = new Input(new boolean[]{true}, stream1);
        input2 = new Input(new boolean[]{true}, stream2);

        and1 = new NOR(stream1, stream2, stream3);

        output = new Output(stream3);
    }

    void run(List<Node> queue) {
        while (queue.size() > 0) {
            Node node = queue.remove(0);
            node.evaluate(queue);
        }
    }

    @Test
    void test1() {
        input1.setData(new boolean[]{false});
        input2.setData(new boolean[]{false});

        List<Node> queue = new ArrayList<>();
        queue.addAll(List.of(input1, input2));

        run(queue);

        assertEquals("1", BitInformationConverter.convertBoolToBits(output.getData()));
    }

    @Test
    void test2() {
        input1.setData(new boolean[]{true});
        input2.setData(new boolean[]{false});

        List<Node> queue = new ArrayList<>();
        queue.addAll(List.of(input1, input2));

        run(queue);

        assertEquals("0", BitInformationConverter.convertBoolToBits(output.getData()));
    }

    @Test
    void test3() {
        input1.setData(new boolean[]{false});
        input2.setData(new boolean[]{true});

        List<Node> queue = new ArrayList<>();
        queue.addAll(List.of(input1, input2));

        run(queue);

        assertEquals("0", BitInformationConverter.convertBoolToBits(output.getData()));
    }

    @Test
    void test4() {
        input1.setData(new boolean[]{true});
        input2.setData(new boolean[]{true});

        List<Node> queue = new ArrayList<>();
        queue.addAll(List.of(input1, input2));

        run(queue);

        assertEquals("0", BitInformationConverter.convertBoolToBits(output.getData()));
    }
}