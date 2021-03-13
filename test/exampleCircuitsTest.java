import main.BitStream;
import main.Node;
import main.control.Input;
import main.control.Output;
import main.exceptions.BitStreamInputSizeMismatch;
import main.exceptions.InconsistentBitStreamSources;
import main.gates.binary.AND;
import main.gates.binary.NOR;
import main.gates.binary.OR;
import main.gates.binary.XOR;
import main.gates.unary.NOT;
import main.utils.DataConverter;
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

        NOR upperNor = new NOR(streamIn1, streamIn2, streamOut1, "upper NOR", false);

        NOR lowerNor = new NOR(streamIn2, streamOut1, streamOut2, "lower NOR", false);

        Output q = new Output(streamOut1, "Q", false);
        Output notQ = new Output(streamOut2, "-Q", false);

        List<Node> queue = new ArrayList<>();
        queue.addAll(List.of(r, s));

        run(queue);

        assertEquals("0", DataConverter.convertBoolToBin(q.getData()));
        assertEquals("1", DataConverter.convertBoolToBin(notQ.getData()));
    }

    @Test
    void DLatch() {
        BitStream dIn = new BitStream(1);
        BitStream clkIn = new BitStream(1);

        BitStream notOut = new BitStream(1);

        BitStream mid1 = new BitStream(1);
        BitStream mid2 = new BitStream(1);

        BitStream out1 = new BitStream(1);
        BitStream out2 = new BitStream(1);

        Input d = new Input(new boolean[]{true}, dIn, "d", false);
        Input clk = new Input(new boolean[]{true}, clkIn, "clk", false);

        NOT not = new NOT(dIn, notOut, false);

        AND upperAnd = new AND(notOut, clkIn, mid1, "upper and", false);

        AND lowerAnd = new AND(dIn, clkIn, mid2, "lower and", false);

        NOR upperNor = new NOR(mid1, out2, out1, "upper nor", false);

        NOR lowerNor = new NOR(mid2, out1, out2, "lower nor", false);

        Output q = new Output(out1);

        Output notQ = new Output(out2);

        List<Node> queue = new ArrayList<>();
        queue.addAll(List.of(d, clk));
        run(queue);

        assertEquals("1", DataConverter.convertBoolToBin(q.getData()));
        assertEquals("0", DataConverter.convertBoolToBin(notQ.getData()));

        d.setData(new boolean[]{false});
        clk.setData(new boolean[]{false});

        queue = new ArrayList<>();
        queue.addAll(List.of(d, clk));
        run(queue);

        assertEquals("1", DataConverter.convertBoolToBin(q.getData()));
        assertEquals("0", DataConverter.convertBoolToBin(notQ.getData()));

        d.setData(new boolean[]{false});
        clk.setData(new boolean[]{true});

        queue = new ArrayList<>();
        queue.addAll(List.of(d, clk));
        run(queue);

        assertEquals("0", DataConverter.convertBoolToBin(q.getData()));
        assertEquals("1", DataConverter.convertBoolToBin(notQ.getData()));

        d.setData(new boolean[]{true});
        clk.setData(new boolean[]{false});

        queue = new ArrayList<>();
        queue.addAll(List.of(d, clk));
        run(queue);

        assertEquals("0", DataConverter.convertBoolToBin(q.getData()));
        assertEquals("1", DataConverter.convertBoolToBin(notQ.getData()));
    }

    //OR and XOR connected to the same stream with different values
    @Test
    void inconsistencyTest() {
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

        assertThrows(InconsistentBitStreamSources.class, () -> {
            XOR xor = new XOR(in3, in4, out);
        });
    }

    @Test
    void sizeMismatchTest() {
        BitStream in1 = new BitStream(1);
        BitStream in2 = new BitStream(2);
        BitStream out = new BitStream(1);

        assertThrows(BitStreamInputSizeMismatch.class, () -> {
            OR or = new OR(in1, in2, out);
        });
    }

}
