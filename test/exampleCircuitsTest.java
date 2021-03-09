import main.BitStream;
import main.Node;
import main.control.Input;
import main.control.Output;
import main.exceptions.BitStreamInputSizeMismatch;
import main.exceptions.InconsistentBitStreamSources;
import main.gates.*;
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

        Input r = new Input(new boolean[]{true}, "R", false);
        r.setOut(streamIn1);
        Input s = new Input(new boolean[]{false}, "S", false);
        s.setOut(streamIn2);

        NOR upperNor = new NOR("upper NOR", false);
        upperNor.setIn1(streamIn1);
        upperNor.setIn2(streamIn2);
        upperNor.setOut(streamOut1);

        NOR lowerNor = new NOR("lower NOR", false);
        lowerNor.setIn1(streamIn2);
        lowerNor.setIn2(streamOut1);
        lowerNor.setOut(streamOut2);

        Output q = new Output("Q", false);
        q.setIn(streamOut1);
        Output notQ = new Output("-Q", false);
        notQ.setIn(streamOut2);

        List<Node> queue = new ArrayList<>();
        queue.addAll(List.of(r, s));

        run(queue);

        assertEquals("0", BitInformationConverter.convertBoolToBits(q.getData()));
        assertEquals("1", BitInformationConverter.convertBoolToBits(notQ.getData()));
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

        Input d = new Input(new boolean[]{true}, "d", true);
        d.setOut(dIn);
        Input clk = new Input(new boolean[]{true}, "clk", true);
        clk.setOut(clkIn);

        NOT not = new NOT(true);
        not.setIn(dIn);
        not.setOut(notOut);

        AND upperAnd = new AND("upper and", true);
        upperAnd.setIn1(notOut);
        upperAnd.setIn2(clkIn);
        upperAnd.setOut(mid1);

        AND lowerAnd = new AND("lower and", true);
        lowerAnd.setIn1(dIn);
        lowerAnd.setIn2(clkIn);
        lowerAnd.setOut(mid2);

        NOR upperNor = new NOR("upper nor", true);
        upperNor.setIn1(mid1);
        upperNor.setIn2(out2);
        upperNor.setOut(out1);

        NOR lowerNor = new NOR("lower nor", true);
        lowerNor.setIn1(mid2);
        lowerNor.setIn2(out1);
        lowerNor.setOut(out2);

        Output q = new Output();
        q.setIn(out1);

        Output notQ = new Output();
        notQ.setIn(out2);

        List<Node> queue = new ArrayList<>();
        queue.addAll(List.of(d, clk));
        run(queue);

        assertEquals("1", BitInformationConverter.convertBoolToBits(q.getData()));
        assertEquals("0", BitInformationConverter.convertBoolToBits(notQ.getData()));
    }

    //OR and XOR connected to the same stream with different values
    @Test
    void inconsistencyTest() {
        BitStream in1 = new BitStream(1);
        BitStream in2 = new BitStream(1);
        BitStream in3 = new BitStream(1);
        BitStream in4 = new BitStream(1);
        BitStream out = new BitStream(1);

        Input input1 = new Input(new boolean[]{true});
        input1.setOut(in1);
        Input input2 = new Input(new boolean[]{false});
        input2.setOut(in2);
        Input input3 = new Input(new boolean[]{true});
        input3.setOut(in3);
        Input input4 = new Input(new boolean[]{true});
        input4.setOut(in4);

        OR or = new OR();
        or.setIn1(in1);
        or.setIn2(in2);
        or.setOut(out);

        XOR xor = new XOR();
        xor.setIn1(in3);
        xor.setIn2(in4);

        List<Node> queue = new ArrayList<>();
        queue.addAll(List.of(input1, input2, input3, input4));

        assertThrows(InconsistentBitStreamSources.class, () -> {
            xor.setOut(out);
        });
    }

    @Test
    void sizeMismatchTest() {
        BitStream in1 = new BitStream(1);
        BitStream in2 = new BitStream(2);
        BitStream out = new BitStream(1);

        OR or = new OR();
        or.setIn1(in1);

        assertThrows(BitStreamInputSizeMismatch.class, () -> {
            or.setIn2(in2);
        });
    }

}
