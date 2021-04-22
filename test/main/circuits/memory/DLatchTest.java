package main.circuits.memory;

import main.BitStream;
import main.circuits.memory.DLatch;
import main.control.Input;
import main.utils.ProcessRunner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DLatchTest {

    @Test
    void testSize1() {
        BitStream D = new BitStream(1);
        BitStream enable = new BitStream(1);
        BitStream Q = new BitStream(1);
        BitStream notQ = new BitStream(1);

        Input dInput = new Input(new boolean[]{true}, D);
        Input enableInput = new Input(new boolean[]{true}, enable);

        DLatch dLatch = new DLatch(D, enable, Q, notQ);

        ProcessRunner.run(dInput, enableInput);

        assertArrayEquals(new boolean[]{true}, Q.getData());
        assertArrayEquals(new boolean[]{false}, notQ.getData());

        dInput.setData(new boolean[]{false});

        ProcessRunner.run(dInput, enableInput);

        assertArrayEquals(new boolean[]{false}, Q.getData());
        assertArrayEquals(new boolean[]{true}, notQ.getData());

        enableInput.setData(new boolean[]{false});

        ProcessRunner.run(dInput, enableInput);

        assertArrayEquals(new boolean[]{false}, Q.getData());
        assertArrayEquals(new boolean[]{true}, notQ.getData());

        dInput.setData(new boolean[]{true});

        ProcessRunner.run(dInput, enableInput);

        assertArrayEquals(new boolean[]{false}, Q.getData());
        assertArrayEquals(new boolean[]{true}, notQ.getData());

        enableInput.setData(new boolean[]{false});

        ProcessRunner.run(enableInput);

        assertArrayEquals(new boolean[]{false}, Q.getData());
        assertArrayEquals(new boolean[]{true}, notQ.getData());

        ProcessRunner.run(dInput);

        assertArrayEquals(new boolean[]{false}, Q.getData());
        assertArrayEquals(new boolean[]{true}, notQ.getData());

        dInput.setData(new boolean[]{true});
        enableInput.setData(new boolean[]{true});

        ProcessRunner.run(enableInput, dInput);

        assertArrayEquals(new boolean[]{true}, Q.getData());
        assertArrayEquals(new boolean[]{false}, notQ.getData());
    }

    @Test
    void testSize4() {
        BitStream D = new BitStream(4);
        BitStream enable = new BitStream(1);
        BitStream Q = new BitStream(4);
        BitStream notQ = new BitStream(4);

        Input dInput = new Input(new boolean[]{true, false, false, false}, D);
        Input enableInput = new Input(new boolean[]{true}, enable);

        DLatch dLatch = new DLatch(D, enable, Q, notQ);

        ProcessRunner.run(dInput, enableInput);

        assertArrayEquals(new boolean[]{true, false, false, false}, Q.getData());
        assertArrayEquals(new boolean[]{false, true, true, true}, notQ.getData());

        dInput.setData(new boolean[]{true, false, true, true});

        ProcessRunner.run(dInput, enableInput);

        assertArrayEquals(new boolean[]{true, false, true, true}, Q.getData());
        assertArrayEquals(new boolean[]{false, true, false, false}, notQ.getData());

        enableInput.setData(new boolean[]{false});

        //It is important to note here that enable must be evaluated before trying to change data.
        //Otherwise it will overwrite what was saved anyway.
        ProcessRunner.run(enableInput);

        dInput.setData(new boolean[]{false, false, true, false});

        ProcessRunner.run(dInput);

        assertArrayEquals(new boolean[]{true, false, true, true}, Q.getData());
        assertArrayEquals(new boolean[]{false, true, false, false}, notQ.getData());

        dInput.setData(new boolean[]{false, false, false, false});
        enableInput.setData(new boolean[]{true});

        ProcessRunner.run(dInput, enableInput);

        assertArrayEquals(new boolean[]{false, false, false, false}, Q.getData());
        assertArrayEquals(new boolean[]{true, true, true, true}, notQ.getData());
    }

}