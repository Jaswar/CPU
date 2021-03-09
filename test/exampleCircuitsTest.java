import main.BitStream;
import main.Node;
import main.control.Input;
import main.control.Output;
import main.exceptions.BitStreamInputSizeMismatch;
import main.exceptions.InconsistentBitStreamSources;
import main.gates.NOR;
import main.gates.OR;
import main.gates.XOR;
import main.utils.BitInformationConverter;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class exampleCircuitsTest {

    void run(List<Node> queue) {
        while (queue.size() > 0) {
            Node toEvaluate = queue.remove(0);
            toEvaluate.evaluate(queue);
        }
    }

    @Test
    void RSLatch() {
        BitStream streamIn1 = new BitStream(1);
        BitStream streamIn2 = new BitStream(1);
        BitStream streamOut1 = new BitStream(1);
        BitStream streamOut2 = new BitStream(1);

        Input r = new Input(new boolean[]{true}, streamIn1, "R", false);
        Input s = new Input(new boolean[]{false}, streamIn2, "S", false);

        NOR upperNor = new NOR(streamIn1, streamOut2, streamOut1, "upper NOR", false);
        NOR lowerNor = new NOR(streamIn2, streamOut1, streamOut2, "lower NOR", false);

        Output q = new Output(streamOut1, "Q", false);
        Output notQ = new Output(streamOut2, "-Q", false);

        List<Node> queue = new ArrayList<>();
        queue.addAll(List.of(r, s));

        run(queue);

        assertEquals("0", BitInformationConverter.convertBoolToBits(q.getData()));
        assertEquals("1", BitInformationConverter.convertBoolToBits(notQ.getData()));
    }

    //OR and XOR connected to the same stream with different values
    @Test
    void inconsitencyTest() {
        BitStream in1 = new BitStream(1);
        BitStream in2 = new BitStream(1);
        BitStream in3 = new BitStream(1);
        BitStream in4 = new BitStream(1);
        BitStream out = new BitStream(1);

        Input input1 = new Input(new boolean[]{true}, in1);
        Input input2 = new Input(new boolean[]{false}, in2);

        Input input3 = new Input(new boolean[]{true}, in3);
        Input input4 = new Input(new boolean[]{true}, in4);

        OR or = new OR(in1, in2, out);
        XOR xor = new XOR(in3, in4, out);

        List<Node> queue = new ArrayList<>();
        queue.addAll(List.of(input1, input2, input3, input4));

        assertThrows(InconsistentBitStreamSources.class, () -> {
           run(queue);
        });
    }

    @Test
    void sizeMismatchTest() {
        BitStream in1 = new BitStream(1);
        BitStream in2 = new BitStream(2);
        BitStream out = new BitStream(1);

        OR or = new OR(in1, in2, out);

        assertThrows(BitStreamInputSizeMismatch.class, () -> {
           or.evaluate(new ArrayList<Node>());
        });
    }

}
