package main.memory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StorageTest {

    private final String filepath = "./storage/test/storageTest.stg";

    @Test
    void testSimpleReadWrite() {
        Storage storage = new Storage(filepath,5, 4);
        storage.putData(0, new boolean[]{true, true, false, true});
        storage.save();

        Storage read = Storage.read(filepath);
        assertEquals(storage, read);
    }

    @Test
    void testAdvancedReadWrite() {
        Storage storage = new Storage(filepath, 5, 4);
        storage.putData(0, new boolean[]{true, true, false, true});
        storage.save();

        Storage read = Storage.read(filepath);
        assertEquals(storage, read);

        read.putData(1, new boolean[]{false, false, true, false});
        read.putData(2, new boolean[]{true, false, false, false});
        read.putData(3, new boolean[]{true, false, false, false});
        read.save();

        read.putData(4, new boolean[]{true, true, true, true});
        read.save();

        Storage read2 = Storage.read(filepath);
        assertEquals(read, read2);
        assertArrayEquals(new boolean[]{true, false, false, false}, read2.getData()[2]);
    }

}