package main.circuits;

import main.BitStream;
import main.control.Input;
import main.utils.DataConverter;
import main.utils.ProcessRunner;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ControlUnitTest {

    Input mainInput, clock;
    BitStream intermediate, RFIn, RFOut, RFAddrWrite, RFAddrRead,
            XIn, MUXConst, ALUOpcode, ZIn, ZOut, PCIn, PCOut, memRead, memWrite, memAddress,
            memDataIn, memDataOut;

    @BeforeEach
    void setup() {
        BitStream input = new BitStream(16);
        BitStream clk = new BitStream(1);

        intermediate = new BitStream(16);
        RFIn = new BitStream(1);
        RFOut = new BitStream(1);
        RFAddrWrite = new BitStream(3);
        RFAddrRead = new BitStream(3);
        XIn = new BitStream(1);
        MUXConst = new BitStream(1);
        ALUOpcode = new BitStream(5);
        ZIn = new BitStream(1);
        ZOut = new BitStream(1);
        PCIn = new BitStream(1);
        PCOut = new BitStream(1);
        memRead = new BitStream(1);
        memWrite = new BitStream(1);
        memAddress = new BitStream(1);
        memDataIn = new BitStream(1);
        memDataOut = new BitStream(1);
        BitStream status = new BitStream(4);

        ControlUnit controlUnit = new ControlUnit(input, clk, status, intermediate, RFIn, RFOut, RFAddrWrite, RFAddrRead,
                XIn, MUXConst, ALUOpcode, ZIn, ZOut, PCIn, PCOut, memRead, memWrite, memAddress, memDataIn, memDataOut,
                false, 1);

        mainInput = new Input(new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false}, input);
        clock = new Input(new boolean[]{false}, clk);
        ProcessRunner.run(mainInput, clock);

    }

    @Test
    void testCommonMicroinstructions() {
        // common 2
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);

        assertTrue(memRead.getData()[0]);
        assertTrue(memDataOut.getData()[0]);

        // common 3
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);

        assertTrue(MUXConst.getData()[0]);
        assertEquals("00001", DataConverter.convertBoolToBin(ALUOpcode.getData()));
        assertTrue(ZIn.getData()[0]);
        assertTrue(PCOut.getData()[0]);
        assertFalse(memDataOut.getData()[0]);

        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);

        assertTrue(MUXConst.getData()[0]);
        assertEquals("00001", DataConverter.convertBoolToBin(ALUOpcode.getData()));
        assertFalse(ZIn.getData()[0]);
        assertTrue(PCOut.getData()[0]);
        assertFalse(memDataOut.getData()[0]);

        // common 4
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);

        assertTrue(PCIn.getData()[0]);
        assertTrue(ZOut.getData()[0]);
        assertFalse(memRead.getData()[0]);
        assertTrue(memAddress.getData()[0]);

        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);

        assertFalse(PCIn.getData()[0]);
        assertTrue(ZOut.getData()[0]);
        assertFalse(memRead.getData()[0]);
        assertFalse(memAddress.getData()[0]);

        // common 5
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);

        assertTrue(memRead.getData()[0]);
        assertTrue(memDataOut.getData()[0]);

        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);

        assertTrue(memDataOut.getData()[0]);
        assertTrue(memRead.getData()[0]);

        // common 6
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);

        assertTrue(MUXConst.getData()[0]);
        assertEquals("00001", DataConverter.convertBoolToBin(ALUOpcode.getData()));
        assertTrue(ZIn.getData()[0]);
        assertTrue(PCOut.getData()[0]);
        assertFalse(memDataOut.getData()[0]);

        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);

        assertTrue(MUXConst.getData()[0]);
        assertEquals("00001", DataConverter.convertBoolToBin(ALUOpcode.getData()));
        assertFalse(ZIn.getData()[0]);
        assertTrue(PCOut.getData()[0]);
        assertFalse(memDataOut.getData()[0]);

        // common 7
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);

        assertTrue(ZOut.getData()[0]);
        assertTrue(PCIn.getData()[0]);

        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);

        assertTrue(ZOut.getData()[0]);
        assertFalse(PCIn.getData()[0]);
    }

    @Test
    void testRegisterMoving() {
        mainInput.setData(new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, true});
        ProcessRunner.run(mainInput);

        testCommonMicroinstructions();

        //microinstruction
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);

        assertTrue(RFIn.getData()[0]);
        assertTrue(RFOut.getData()[0]);
        assertEquals("000", DataConverter.convertBoolToBin(RFAddrRead.getData()));
        assertEquals("000", DataConverter.convertBoolToBin(RFAddrWrite.getData()));

        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);

        assertFalse(RFIn.getData()[0]);
        assertTrue(RFOut.getData()[0]);
        assertEquals("000", DataConverter.convertBoolToBin(RFAddrRead.getData()));
        assertEquals("000", DataConverter.convertBoolToBin(RFAddrWrite.getData()));

        //common 1
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);

        assertTrue(PCOut.getData()[0]);
        assertFalse(memRead.getData()[0]);
        assertTrue(memAddress.getData()[0]);

        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);

        assertTrue(PCOut.getData()[0]);
        assertFalse(memRead.getData()[0]);
        assertFalse(memAddress.getData()[0]);

        mainInput.setData(new boolean[]{false, false, false, true, true, false, false, true,
                false, false, false, false, false, false, false, true});
        ProcessRunner.run(mainInput);

        for (int i = 2; i <= 7; i++) {
            clock.setData(new boolean[]{true});
            ProcessRunner.run(clock);
            clock.setData(new boolean[]{false});
            ProcessRunner.run(clock);
            clock.setData(new boolean[]{true});
            ProcessRunner.run(clock);
            clock.setData(new boolean[]{false});
            ProcessRunner.run(clock);
        }

        //microinstruction
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);

        assertTrue(RFIn.getData()[0]);
        assertTrue(RFOut.getData()[0]);
        assertEquals("001", DataConverter.convertBoolToBin(RFAddrRead.getData()));
        assertEquals("011", DataConverter.convertBoolToBin(RFAddrWrite.getData()));

        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);

        assertFalse(RFIn.getData()[0]);
        assertTrue(RFOut.getData()[0]);
        assertEquals("001", DataConverter.convertBoolToBin(RFAddrRead.getData()));
        assertEquals("011", DataConverter.convertBoolToBin(RFAddrWrite.getData()));
    }

    @Test
    void testMovingIntermediate() {
        testCommonMicroinstructions();

        //microinstruction
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);

        //common 1
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);

        mainInput.setData(new boolean[]{false, false, true, false, false, false, false, false,
                false, false, false, false, false, false, true, false});
        ProcessRunner.run(mainInput);

        // common 2
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);

        // common 3
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);

        // common 4
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);

        mainInput.setData(new boolean[]{true, false, false, false, false, false, false, false,
                false, false, true, false, false, false, true, true});
        ProcessRunner.run(mainInput);

        for (int i = 5; i <= 7; i++) {
            clock.setData(new boolean[]{true});
            ProcessRunner.run(clock);
            clock.setData(new boolean[]{false});
            ProcessRunner.run(clock);
            clock.setData(new boolean[]{true});
            ProcessRunner.run(clock);
            clock.setData(new boolean[]{false});
            ProcessRunner.run(clock);
        }

        //microinstruction
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);

        assertEquals("100", DataConverter.convertBoolToBin(RFAddrWrite.getData()));
        assertArrayEquals(new boolean[]{true, false, false, false, false, false, false, false,
                false, false, true, false, false, false, true, true}, intermediate.getData());

    }

    @Test
    void testThirdInstruction() {
        testCommonMicroinstructions();

        //microinstruction
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);

        //common 1
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);

        mainInput.setData(new boolean[]{false, false, false, true, true, true, false, true,
                false, false, false, false, true, false, false, false});
        ProcessRunner.run(mainInput);

        for (int i = 2; i <= 7; i++) {
            clock.setData(new boolean[]{true});
            ProcessRunner.run(clock);
            clock.setData(new boolean[]{false});
            ProcessRunner.run(clock);
            clock.setData(new boolean[]{true});
            ProcessRunner.run(clock);
            clock.setData(new boolean[]{false});
            ProcessRunner.run(clock);
        }

        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);


        assertTrue(RFOut.getData()[0]);
        assertEquals("011", DataConverter.convertBoolToBin(RFAddrRead.getData()));
        assertTrue(XIn.getData()[0]);

        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);

        assertTrue(RFOut.getData()[0]);
        assertEquals("011", DataConverter.convertBoolToBin(RFAddrRead.getData()));
        assertFalse(XIn.getData()[0]);

        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);

        assertTrue(RFOut.getData()[0]);
        assertTrue(ZIn.getData()[0]);
        assertEquals("101", DataConverter.convertBoolToBin(RFAddrRead.getData()));
        assertEquals("00001", DataConverter.convertBoolToBin(ALUOpcode.getData()));

        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);

        assertTrue(RFOut.getData()[0]);
        assertFalse(ZIn.getData()[0]);
        assertEquals("101", DataConverter.convertBoolToBin(RFAddrRead.getData()));
        assertEquals("00001", DataConverter.convertBoolToBin(ALUOpcode.getData()));

        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);

        assertTrue(RFIn.getData()[0]);
        assertEquals("011", DataConverter.convertBoolToBin(RFAddrWrite.getData()));
        assertTrue(ZOut.getData()[0]);

        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);

        assertFalse(RFIn.getData()[0]);
        assertEquals("011", DataConverter.convertBoolToBin(RFAddrWrite.getData()));
        assertTrue(ZOut.getData()[0]);

        //common 1
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);

        assertTrue(PCOut.getData()[0]);
        assertFalse(memRead.getData()[0]);
        assertTrue(memAddress.getData()[0]);

        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);

        assertTrue(PCOut.getData()[0]);
        assertFalse(memRead.getData()[0]);
        assertFalse(memAddress.getData()[0]);
    }
}