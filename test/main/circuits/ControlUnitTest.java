package main.circuits;

import main.BitStream;
import main.control.Input;
import main.utils.DataConverter;
import main.utils.ProcessRunner;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;

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

        ControlUnit controlUnit = new ControlUnit(input, clk, intermediate, RFIn, RFOut, RFAddrWrite, RFAddrRead,
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
        assertTrue(memRead.getData()[0]);
        assertTrue(memAddress.getData()[0]);

        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);

        assertFalse(PCIn.getData()[0]);
        assertTrue(ZOut.getData()[0]);
        assertTrue(memRead.getData()[0]);
        assertFalse(memAddress.getData()[0]);

        // common 5
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);
        clock.setData(new boolean[]{true});
        ProcessRunner.run(clock);

        assertTrue(memDataOut.getData()[0]);

        clock.setData(new boolean[]{false});
        ProcessRunner.run(clock);

        assertTrue(memDataOut.getData()[0]);

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


}