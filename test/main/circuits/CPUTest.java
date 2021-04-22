package main.circuits;

import main.BitStream;
import main.control.Input;
import main.memory.RAM;
import main.utils.DataConverter;
import main.utils.ProcessRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CPUTest {

    Input clock;
    BitStream bus;
    CPU cpu;

    @BeforeEach
    void setup() {
        BitStream clk = new BitStream(1);
        BitStream memoryDataOut = new BitStream(Microprocessor.WORD_SIZE);
        BitStream memoryDataIn = new BitStream(Microprocessor.WORD_SIZE);
        BitStream memoryAddress = new BitStream(Microprocessor.WORD_SIZE);
        BitStream memRead = new BitStream(1);
        BitStream memWrite = new BitStream(1);

        clock = new Input(new boolean[]{false}, clk);
        cpu = new CPU(clk, memRead, memWrite, memoryDataOut, memoryDataIn, memoryAddress);
        bus = cpu.getBus();

        RAM ram = new RAM(memoryAddress, memoryDataIn, memoryDataOut, memWrite, memRead);

        ram.putData(0, new boolean[]{false, false, false, true, false, false, false, false,
                false, false, false, false, false, false, false, true});
        ram.putData(1, new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, true, false, true});
        ram.putData(2, new boolean[]{false, false, false, false, true, false, true, false,
                false, false, false, false, false, false, false, false});
        ram.putData(4, new boolean[]{false, false, false, false, true, false, true, false,
                false, false, false, false, false, false, true, false});
        ram.putData(6, new boolean[]{false, false, false, false, true, false, false, true,
                false, false, false, false, false, false, true, false});
    }

    @Test
    void testMovingIntermediateToRegister() {
        //to get to the microinstruction state
        for (int i = 0; i < 7; i++) {
            clock.setData(new boolean[]{true});
            ProcessRunner.run(clock);
            clock.setData(new boolean[]{false});
            ProcessRunner.run(clock);
            clock.setData(new boolean[]{true});
            ProcessRunner.run(clock);
            clock.setData(new boolean[]{false});
            ProcessRunner.run(clock);
            System.out.println(cpu.requestStatus());
            System.out.println();
        }
        assertArrayEquals(new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, true, false, true}, bus.getData());
        for (int i = 0; i < 8; i++) {
            clock.setData(new boolean[]{true});
            ProcessRunner.run(clock);
            clock.setData(new boolean[]{false});
            ProcessRunner.run(clock);
            clock.setData(new boolean[]{true});
            ProcessRunner.run(clock);
            clock.setData(new boolean[]{false});
            ProcessRunner.run(clock);
            System.out.println(cpu.requestStatus());
            System.out.println();
        }

        for (int i = 0; i < 20; i++) {
            clock.setData(new boolean[]{true});
            ProcessRunner.run(clock);
            clock.setData(new boolean[]{false});
            ProcessRunner.run(clock);
            clock.setData(new boolean[]{true});
            ProcessRunner.run(clock);
            clock.setData(new boolean[]{false});
            ProcessRunner.run(clock);
            System.out.println(cpu.requestStatus());
            System.out.println();
        }
    }

}