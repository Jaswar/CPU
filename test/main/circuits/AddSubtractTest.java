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
    void testVerySimple() {
        BitStream source = new BitStream(4);
        BitStream destination = new BitStream(4);
        BitStream output = new BitStream(4);
        BitStream control = new BitStream(1);
        BitStream overflow = new BitStream(1);

        Input sourceInput = new Input(new boolean[]{false, false, true, false}, source);
        Input destinationInput = new Input(new boolean[]{false, false, true, true}, destination);
        Input controlInput = new Input(new boolean[]{false}, control);

        AddSubtract addSubtract = new AddSubtract(source, destination, output, control, overflow);

        List<Node> queue = new ArrayList<>();
        queue.addAll(List.of(sourceInput, destinationInput, controlInput));
        run(queue);

        assertArrayEquals(new boolean[]{false, true, false, true}, output.getData());
        assertArrayEquals(new boolean[]{false}, overflow.getData());
    }

    @Test
    void testSimple1() {
        BitStream source = new BitStream(Node.WORD_SIZE);
        BitStream destination = new BitStream(Node.WORD_SIZE);
        BitStream output = new BitStream(Node.WORD_SIZE);
        BitStream control = new BitStream(1);
        BitStream overflow = new BitStream(1);

        Input sourceInput = new Input(DataConverter.convertBinToBool("1110011101111010"), source);
        Input destinationInput = new Input(DataConverter.convertBinToBool("0000000001110011"), destination);
        Input controlInput = new Input(new boolean[]{true}, control);

        Output outputObject = new Output(output);
        Output overflowOutput = new Output(overflow);

        AddSubtract addSubtract = new AddSubtract(source, destination, output, control, overflow, false, 1);

        int src = DataConverter.convertBoolToSignedDec(DataConverter.convertBinToBool("1110011101111010"), Node.WORD_SIZE);
        int dst = DataConverter.convertBoolToSignedDec(DataConverter.convertBinToBool("0000000001110011"), Node.WORD_SIZE);
        int expectedSum = src + dst;
        int expectedSub = src - dst;

        boolean shouldOverflowAdd = Math.abs(expectedSum) >= 32767;
        boolean shouldOverflowSub = Math.abs(expectedSub) >= 32767;

        List<Node> queue = new ArrayList<>();
        queue.addAll(List.of(sourceInput, destinationInput, controlInput));
        run(queue);

        assertArrayEquals(new boolean[]{shouldOverflowSub}, overflow.getData());
    }

    @Test
    void randomTest() {
        BitStream source = new BitStream(Node.WORD_SIZE);
        BitStream destination = new BitStream(Node.WORD_SIZE);
        BitStream output = new BitStream(Node.WORD_SIZE);
        BitStream control = new BitStream(1);
        BitStream overflow = new BitStream(1);

        Input sourceInput = new Input(new boolean[Node.WORD_SIZE], source);
        Input destinationInput = new Input(new boolean[Node.WORD_SIZE], destination);
        Input controlInput = new Input(new boolean[]{false}, control);

        Output outputObject = new Output(output);
        Output overflowOutput = new Output(overflow);

        AddSubtract addSubtract = new AddSubtract(source, destination, output, control, overflow, false, 1);

        for (int iteration = 0; iteration < 10000; iteration++) {
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

            boolean shouldOverflowAdd, shouldOverflowSub;
            if (expectedSum < 0) {
                shouldOverflowAdd = Math.abs(expectedSum) >= 32769;
            }
            else {
                shouldOverflowAdd = Math.abs(expectedSum) >= 32768;
            }
            if (expectedSub < 0) {
                shouldOverflowSub = Math.abs(expectedSub) >= 32769;
            }
            else {
                shouldOverflowSub = Math.abs(expectedSub) >= 32768;
            }

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

//            System.out.println(shouldOverflowAdd);
//            System.out.println(shouldOverflowSub);
//            System.out.println(src);
//            System.out.println(dst);
//            System.out.println(DataConverter.convertBoolToBin(randomSource));
//            System.out.println(DataConverter.convertBoolToBin(randomDestination));
//            System.out.println(expectedSub);
//            System.out.println(expectedSum);
//            System.out.println(DataConverter.convertBoolToSignedDec(outputObject.getData(), Node.WORD_SIZE));
        }
    }

}