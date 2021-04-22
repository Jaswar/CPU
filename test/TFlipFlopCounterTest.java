import main.BitStream;
import main.circuits.memory.TFlipFlop;
import main.control.Input;
import static org.junit.jupiter.api.Assertions.*;

import main.utils.DataConverter;
import main.utils.ProcessRunner;
import org.junit.jupiter.api.Test;

public class TFlipFlopCounterTest {

    @Test
    void test3Bit() {
        BitStream constant = new BitStream(1);
        constant.setData(new boolean[]{true});
        BitStream enable = new BitStream(1);
        enable.setData(new boolean[]{true});
        BitStream clk = new BitStream(1);
        BitStream preset = new BitStream(1);
        BitStream clear = new BitStream(1);

        Input clock = new Input(new boolean[]{false}, clk);

        BitStream q1 = new BitStream(1);
        BitStream notQ1 = new BitStream(1);
        TFlipFlop t1 = new TFlipFlop(constant, clk, enable, preset, clear, q1, notQ1, true);

        BitStream q2 = new BitStream(1);
        BitStream notQ2 = new BitStream(1);
        TFlipFlop t2 = new TFlipFlop(constant, notQ1, enable, preset, clear, q2, notQ2, true);

        BitStream q3 = new BitStream(1);
        BitStream notQ3 = new BitStream(1);
        TFlipFlop t3 = new TFlipFlop(constant, notQ2, enable, preset, clear, q3, notQ3, true);

        for (int i = 0; i < 8; i++) {
            boolean[] expected = DataConverter.convertSignedDecToBool(i, 3);

            assertArrayEquals(new boolean[]{expected[2]}, q1.getData());
            assertArrayEquals(new boolean[]{expected[1]}, q2.getData());
            assertArrayEquals(new boolean[]{expected[0]}, q3.getData());

            clock.setData(new boolean[]{true});
            ProcessRunner.run(clock);
            clock.setData(new boolean[]{false});
            ProcessRunner.run(clock);
        }

        assertArrayEquals(new boolean[]{false}, q1.getData());
        assertArrayEquals(new boolean[]{false}, q2.getData());
        assertArrayEquals(new boolean[]{false}, q3.getData());
    }

    @Test
    void test5Bit() {
        BitStream constant = new BitStream(1);
        constant.setData(new boolean[]{true});
        BitStream enable = new BitStream(1);
        enable.setData(new boolean[]{true});
        BitStream clk = new BitStream(1);
        BitStream preset = new BitStream(1);
        BitStream clear = new BitStream(1);

        Input clock = new Input(new boolean[]{false}, clk);

        BitStream q1 = new BitStream(1);
        BitStream notQ1 = new BitStream(1);
        TFlipFlop t1 = new TFlipFlop(constant, clk, enable, preset, clear, q1, notQ1, true);

        BitStream q2 = new BitStream(1);
        BitStream notQ2 = new BitStream(1);
        TFlipFlop t2 = new TFlipFlop(constant, notQ1, enable, preset, clear, q2, notQ2, true);

        BitStream q3 = new BitStream(1);
        BitStream notQ3 = new BitStream(1);
        TFlipFlop t3 = new TFlipFlop(constant, notQ2, enable, preset, clear, q3, notQ3, true);

        BitStream q4 = new BitStream(1);
        BitStream notQ4 = new BitStream(1);
        TFlipFlop t4 = new TFlipFlop(constant, notQ3, enable, preset, clear, q4, notQ4, true);

        BitStream q5 = new BitStream(1);
        BitStream notQ5 = new BitStream(1);
        TFlipFlop t5 = new TFlipFlop(constant, notQ4, enable, preset, clear, q5, notQ5, true);

        for (int i = 0; i < 32; i++) {
            boolean[] expected = DataConverter.convertSignedDecToBool(i, 5);

            assertArrayEquals(new boolean[]{expected[4]}, q1.getData());
            assertArrayEquals(new boolean[]{expected[3]}, q2.getData());
            assertArrayEquals(new boolean[]{expected[2]}, q3.getData());
            assertArrayEquals(new boolean[]{expected[1]}, q4.getData());
            assertArrayEquals(new boolean[]{expected[0]}, q5.getData());

            clock.setData(new boolean[]{true});
            ProcessRunner.run(clock);
            clock.setData(new boolean[]{false});
            ProcessRunner.run(clock);
        }

        assertArrayEquals(new boolean[]{false}, q1.getData());
        assertArrayEquals(new boolean[]{false}, q2.getData());
        assertArrayEquals(new boolean[]{false}, q3.getData());
        assertArrayEquals(new boolean[]{false}, q4.getData());
        assertArrayEquals(new boolean[]{false}, q5.getData());
    }

}
