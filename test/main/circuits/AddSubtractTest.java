package main.circuits;

import main.BitStream;
import main.Node;
import main.control.Input;
import main.control.Output;
import main.utils.DataConverter;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class AddSubtractTest {

    void run(List<Node> queue) {
        while (queue.size() > 0) {
            Node node = queue.remove(0);
            node.evaluate(queue);
        }
    }

    @Test
    void testSimple1() {
        BitStream source = new BitStream(Node.WORD_SIZE);
        BitStream destination = new BitStream(Node.WORD_SIZE);
        BitStream output = new BitStream(Node.WORD_SIZE);
        BitStream control = new BitStream(1);
        BitStream overflow = new BitStream(1);

        Input sourceInput = new Input(new boolean[]
                {true, false, true, false, false, false, false, false,
                        false, false, false, false, false, false, false, false}, source);
        Input destinationInput = new Input(new boolean[]
                {false, false, false, true, true, true, true, true,
                        true, true, true, true, true, true, true, true}, destination);
        Input controlInput = new Input(new boolean[]{false}, control);

        Output outputObject = new Output(output);
        Output overflowOutput = new Output(overflow);

        AddSubtract addSubtract = new AddSubtract(source, destination, output, control, overflow, true, 1);

        List<Node> queue = new ArrayList<>();
        queue.addAll(List.of(sourceInput, destinationInput, controlInput));
        run(queue);

        assertArrayEquals(new boolean[]
                {true, false, true, true, true, true, true, true,
                        true, true, true, true, true, true, true, true}, outputObject.getData());
        assertArrayEquals(new boolean[]{false}, overflowOutput.getData());
    }

    @Test
    void randomTest() {
        BitStream source = new BitStream(Node.WORD_SIZE);
        BitStream destination = new BitStream(Node.WORD_SIZE);
        BitStream output = new BitStream(Node.WORD_SIZE);
        BitStream control = new BitStream(1);
        BitStream overflow = new BitStream(1);

        Input sourceInput = new Input(new boolean[16], source);
        Input destinationInput = new Input(new boolean[16], destination);
        Input controlInput = new Input(new boolean[]{false}, control);

        Output outputObject = new Output(output);
        Output overflowOutput = new Output(overflow);

        AddSubtract addSubtract = new AddSubtract(source, destination, output, control, overflow, false, 1);

        for (int iteration = 0; iteration < 1000; iteration++) {
            boolean[] randomSource = new boolean[Node.WORD_SIZE];
            boolean[] randomDestination = new boolean[Node.WORD_SIZE];

            for (int i = 0; i < Node.WORD_SIZE; i++) {
                Random random = new Random();
                randomSource[i] = random.nextBoolean();
                randomDestination[i] = random.nextBoolean();
            }

            int src = DataConverter.convertBoolToSignedDec(randomSource, Node.WORD_SIZE);
            int dst = DataConverter.convertBoolToSignedDec(randomDestination, Node.WORD_SIZE);
            int expectedSum = src + dst;
            int expectedSub = src - dst;

            boolean shouldOverflowAdd = Math.abs(expectedSum) >= 32767;
            boolean shouldOverflowSub = Math.abs(expectedSub) >= 32767;

            sourceInput.setData(randomSource);
            destinationInput.setData(randomDestination);
            controlInput.setData(new boolean[]{false});

            List<Node> queue = new ArrayList<>();
            queue.addAll(List.of(sourceInput, destinationInput, controlInput));
            run(queue);

            if (!shouldOverflowAdd) {
                assertEquals(expectedSum, DataConverter.convertBoolToSignedDec(outputObject.getData(), Node.WORD_SIZE));
            }
            assertArrayEquals(new boolean[]{shouldOverflowAdd}, overflow.getData());

            controlInput.setData(new boolean[]{true});

            queue = new ArrayList<>();
            queue.addAll(List.of(sourceInput, destinationInput, controlInput));
            run(queue);

            if (!shouldOverflowSub) {
                assertEquals(expectedSub, DataConverter.convertBoolToSignedDec(outputObject.getData(), Node.WORD_SIZE));
            }
            assertArrayEquals(new boolean[]{shouldOverflowSub}, overflow.getData());
        }
    }

}