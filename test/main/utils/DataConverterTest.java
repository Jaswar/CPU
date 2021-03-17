package main.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataConverterTest {

    @Test
    void convertBoolToBitsTest() {
        boolean[] data = new boolean[]{true, false, false, true, true};
        assertEquals("10011", DataConverter.convertBoolToBin(data));
    }

    @Test
    void convertBitsToBoolTest() {
        assertArrayEquals(new boolean[]{true, false, false, true, true}, DataConverter.convertBinToBool("10011"));
    }

    @Test
    void convertBinToDecTest() {
        String binary = "110011";
        int decimal = 51;
        assertEquals(decimal, DataConverter.convertBinToUnsignedDec(binary));
    }

    @Test
    void convertDecToBinTest() {
        String binary = "1100010";
        int decimal = 98;
        assertEquals(binary, DataConverter.convertUnsignedDecToBin(decimal));
    }

    @Test
    void convertDecToBinTestZero() {
        String binary = "0";
        int decimal = 0;
        assertEquals(binary, DataConverter.convertUnsignedDecToBin(decimal));
    }

    @Test
    void convertBinToSignedDec1() {
        String bin = "1011";
        assertEquals(-5, DataConverter.convertBinToSignedDec(bin, 4));
    }

    @Test
    void convertBinToSignedDec2() {
        String bin = "1011";
        assertEquals(11, DataConverter.convertBinToSignedDec(bin, 5));
    }

    @Test
    void convertBinToSignedDec3() {
        String bin = "1000";
        assertEquals(-8, DataConverter.convertBinToSignedDec(bin, 4));
    }

    @Test
    void convertSignedDecToBin1() {
        int decimal = -13;
        assertEquals("111110011", DataConverter.convertSignedDecToBin(decimal, 9));
    }

    @Test
    void convertSignedDecToBin2() {
        int decimal = -28;
        assertEquals("100100", DataConverter.convertSignedDecToBin(decimal, 6));
    }

    @Test
    void convertSignedDecToBin3() {
        int decimal = 9;
        assertEquals("01001", DataConverter.convertSignedDecToBin(decimal, 5));
    }

    @Test
    void convertBoolToSignedDec1() {
        boolean[] bool = new boolean[]{false, true, true, true};
        assertEquals(7, DataConverter.convertBoolToSignedDec(bool, 4));
    }

    @Test
    void convertBoolToSignedDec2() {
        boolean[] bool = new boolean[]{true, true, false, true};
        assertEquals(-3, DataConverter.convertBoolToSignedDec(bool, 4));
    }

    @Test
    void convertSignedDecToBool1() {
        int decimal = -14;
        assertArrayEquals(new boolean[]{true, false, false, true, false},
                DataConverter.convertSignedDecToBool(decimal, 5));
    }

    @Test
    void convertSignedDecToBool2() {
        int decimal = 36;
        assertArrayEquals(new boolean[]{false, true, false, false, true, false, false},
                DataConverter.convertSignedDecToBool(decimal, 7));
    }

    @Test
    void convertSignedDecToBool3() {
        int decimal = -14;
        assertArrayEquals(new boolean[]{true, true, true, false, false, true, false},
                DataConverter.convertSignedDecToBool(decimal, 7));
    }

    @Test
    void convertSignedDecToBool4() {
        int decimal = 0;
        assertArrayEquals(new boolean[]{false},
                DataConverter.convertSignedDecToBool(decimal, 1));
    }

    @Test
    void convertSignedDecToBool5() {
        int decimal = 36;
        assertArrayEquals(new boolean[]{false, false, true, false, false, true, false, false},
                DataConverter.convertSignedDecToBool(decimal, 8));
    }

}