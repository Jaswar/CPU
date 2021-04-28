package main.memory;

import main.BitStream;
import main.utils.ProcessRunner;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ROMTest {

    static ROM rom;
    static BitStream address, output;

    @BeforeAll
    static void setup() {
        Storage storage = new Storage("./storage/test/romTest.stg", 8, 4);
        storage.putData(0, new boolean[]{false, false, false, false});
        storage.putData(1, new boolean[]{true, true, true, true});
        storage.putData(2, new boolean[]{true, false, false, false});
        storage.putData(3, new boolean[]{false, true, true, false});
        storage.putData(4, new boolean[]{true, true, false, true});
        storage.putData(5, new boolean[]{false, true, false, false});
        storage.putData(6, new boolean[]{true, false, false, false});
        storage.putData(7, new boolean[]{true, false, false, false});
        storage.save();

        address = new BitStream(3);
        output = new BitStream(4);

        rom = new ROM("./storage/test/romTest.stg", address, output);
    }

    @Test
    void test1() {
        address.setData(new boolean[]{false, false, false});
        ProcessRunner.run(rom);

        assertArrayEquals(new boolean[]{false, false, false, false}, output.getData());

        address.setData(new boolean[]{false, false, true});
        ProcessRunner.run(rom);

        assertArrayEquals(new boolean[]{true, true, true, true}, output.getData());

        address.setData(new boolean[]{false, true, false});
        ProcessRunner.run(rom);

        assertArrayEquals(new boolean[]{true, false, false, false}, output.getData());

        address.setData(new boolean[]{true, false, true});
        ProcessRunner.run(rom);

        assertArrayEquals(new boolean[]{false, true, false, false}, output.getData());

        address.setData(new boolean[]{true, true, true});
        ProcessRunner.run(rom);

        assertArrayEquals(new boolean[]{true, false, false, false}, output.getData());
    }

}