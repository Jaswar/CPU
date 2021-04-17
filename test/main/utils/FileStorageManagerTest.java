package main.utils;

import main.exceptions.WordSizeMismatchException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageManagerTest {

    @Test
    void test1() {
        FileStorageManager manager = new FileStorageManager("./storage/test/fsmStringTest1.txt");

        boolean[][] expected = new boolean[][] {new boolean[]{true, true, false, false, true},
                                                new boolean[]{true, false, false, true, false},
                                                new boolean[]{false, true, true, false, false}};
        assertArrayEquals(expected, manager.readStringBinaryData());
    }

    @Test
    void testIllegalData() {
        FileStorageManager manager = new FileStorageManager("./storage/test/fsmStringTestIllegal.txt");

        assertThrows(WordSizeMismatchException.class, () -> {
           manager.readStringBinaryData();
        });
    }
}