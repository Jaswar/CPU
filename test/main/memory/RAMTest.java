package main.memory;

import main.BitStream;
import main.utils.ProcessRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RAMTest {

    BitStream address, dataIn, dataOut, write, read;
    RAM ram;

    @BeforeEach
    void setup() {
        address = new BitStream(3);
        dataIn = new BitStream(4);
        dataOut = new BitStream(4);
        write = new BitStream(1);
        read = new BitStream(1);

        ram = new RAM(address, dataIn, dataOut, write, read);
    }

    @Test
    void testSimple() {
        dataIn.setData(new boolean[]{true, true, false, false});
        address.setData(new boolean[]{false, false, false});
        write.setData(new boolean[]{true});
        read.setData(new boolean[]{true});
        ProcessRunner.run(ram);

        write.setData(new boolean[]{false});
        ProcessRunner.run(ram);

        assertArrayEquals(new boolean[]{true, true, false, false}, dataOut.getData());
    }

    @Test
    void testMultipleWrites() {
        dataIn.setData(new boolean[]{false, false, false, true});
        address.setData(new boolean[]{false, false, false});
        write.setData(new boolean[]{true});
        ProcessRunner.run(ram);

        write.setData(new boolean[]{false});
        ProcessRunner.run(ram);

        dataIn.setData(new boolean[]{false, true, true, false});
        address.setData(new boolean[]{false, true, false});
        write.setData(new boolean[]{true});
        ProcessRunner.run(ram);

        write.setData(new boolean[]{false});
        ProcessRunner.run(ram);

        dataIn.setData(new boolean[]{true, true, false, false});
        address.setData(new boolean[]{true, true, true});
        write.setData(new boolean[]{true});
        ProcessRunner.run(ram);

        write.setData(new boolean[]{false});
        ProcessRunner.run(ram);

        dataIn.setData(new boolean[]{false, false, true, false});
        address.setData(new boolean[]{true, true, false});
        write.setData(new boolean[]{true});
        ProcessRunner.run(ram);

        write.setData(new boolean[]{false});
        ProcessRunner.run(ram);

        address.setData(new boolean[]{true, true, true});
        read.setData(new boolean[]{true});
        ProcessRunner.run(ram);

        assertArrayEquals(new boolean[]{true, true, false, false}, dataOut.getData());

        read.setData(new boolean[]{false});
        ProcessRunner.run(ram);

        address.setData(new boolean[]{false, false, false});
        dataIn.setData(new boolean[]{true, true, false, true});
        ProcessRunner.run(ram);
        assertArrayEquals(new boolean[]{true, true, false, false}, dataOut.getData());

        read.setData(new boolean[]{true});
        ProcessRunner.run(ram);

        assertArrayEquals(new boolean[]{false, false, false, true}, dataOut.getData());
    }

}