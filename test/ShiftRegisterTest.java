import main.BitStream;
import main.circuits.memory.DFlipFlop;
import main.control.Input;
import main.gates.unary.NOT;
import main.utils.ProcessRunner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class ShiftRegisterTest {

    @Test
    void test1() {
        BitStream in = new BitStream(1);
        BitStream q1 = new BitStream(1);
        BitStream q2 = new BitStream(1);
        BitStream q3 = new BitStream(1);
        BitStream q4 = new BitStream(1);

        BitStream clk = new BitStream(1);
        BitStream enable = new BitStream(1);
        enable.setData(new boolean[]{true});

        DFlipFlop flipFlop1 = new DFlipFlop(in, clk, enable, new BitStream(1),
                new BitStream(1), q1, new BitStream(1), true);
        DFlipFlop flipFlop2 = new DFlipFlop(q1, clk, enable, new BitStream(1),
                new BitStream(1), q2, new BitStream(1), true);
        DFlipFlop flipFlop3 = new DFlipFlop(q2, clk, enable, new BitStream(1),
                new BitStream(1), q3, new BitStream(1), true);
        DFlipFlop flipFlop4 = new DFlipFlop(q3, clk, enable, new BitStream(1),
                new BitStream(1), q4, new BitStream(1), true);

        Input clock = new Input(new boolean[]{false}, clk);

        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);

        Input input = new Input(new boolean[]{false}, in);
        input.setData(new boolean[]{true});
        clock.setData(new boolean[]{true});
        ProcessRunner.run(input);
        ProcessRunner.run(clock);

        assertArrayEquals(new boolean[]{true}, q1.getData());
        assertArrayEquals(new boolean[]{false}, q2.getData());
        assertArrayEquals(new boolean[]{false}, q3.getData());
        assertArrayEquals(new boolean[]{false}, q4.getData());

        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);

        assertArrayEquals(new boolean[]{true}, q1.getData());
        assertArrayEquals(new boolean[]{true}, q2.getData());
        assertArrayEquals(new boolean[]{false}, q3.getData());
        assertArrayEquals(new boolean[]{false}, q4.getData());

        clock.setData(new boolean[]{false});
        input.setData(new boolean[]{false});
        ProcessRunner.run(clock, input);
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);

        assertArrayEquals(new boolean[]{false}, q1.getData());
        assertArrayEquals(new boolean[]{true}, q2.getData());
        assertArrayEquals(new boolean[]{true}, q3.getData());
        assertArrayEquals(new boolean[]{false}, q4.getData());

        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);

        assertArrayEquals(new boolean[]{false}, q1.getData());
        assertArrayEquals(new boolean[]{false}, q2.getData());
        assertArrayEquals(new boolean[]{true}, q3.getData());
        assertArrayEquals(new boolean[]{true}, q4.getData());
    }
}
