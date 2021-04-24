package main.circuits;

import main.BitStream;
import main.control.Input;
import main.memory.RAM;
import main.utils.DataConverter;
import main.utils.ProcessRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.naming.ldap.Control;

import static org.junit.jupiter.api.Assertions.*;

class CPUTest {

    Input clock;
    BitStream bus;
    CPU cpu;
    RAM ram;

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

        ram = new RAM(memoryAddress, memoryDataIn, memoryDataOut, memWrite, memRead);
    }

    @Test
    void testMovingIntermediateToRegister() {
        ram.putData(0, new boolean[]{false, false, false, true, false, false, false, false,
                false, false, false, false, false, false, false, true});
        ram.putData(1, new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, true, false, true});

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
        }
        assertArrayEquals(new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, true, false, true},
                cpu.getRegisterFile().getRegisters().get(2).getDataBitStream().getData());
        assertArrayEquals(new boolean[]{false, false, false, true, false, false, false, false,
                false, false, false, false, false, false, false, true},
                cpu.getControlUnit().getMicroprocessor().getIR1().getDataBitStream().getData());
        assertArrayEquals(new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, true, false, true},
                cpu.getControlUnit().getMicroprocessor().getIR2().getDataBitStream().getData());
    }

    @Test
    void testMovingBetweenRegisters() {
        ram.putData(2, new boolean[]{false, false, false, false, true, false, true, false,
                false, false, false, false, false, false, false, false});

        testMovingIntermediateToRegister();

        for (int i = 0; i < 8; i++) {
            clock.setData(new boolean[]{true});
            ProcessRunner.run(clock);
            clock.setData(new boolean[]{false});
            ProcessRunner.run(clock);
            clock.setData(new boolean[]{true});
            ProcessRunner.run(clock);
            clock.setData(new boolean[]{false});
            ProcessRunner.run(clock);
        }
        assertArrayEquals(new boolean[]{false, false, false, false, false, false, false, false,
                        false, false, false, false, false, true, false, true},
                cpu.getRegisterFile().getRegisters().get(2).getDataBitStream().getData());
        assertArrayEquals(new boolean[]{false, false, false, false, false, false, false, false,
                        false, false, false, false, false, true, false, true},
                cpu.getRegisterFile().getRegisters().get(1).getDataBitStream().getData());
        assertArrayEquals(new boolean[]{false, false, false, false, true, false, true, false,
                        false, false, false, false, false, false, false, false},
                cpu.getControlUnit().getMicroprocessor().getIR1().getDataBitStream().getData());
    }

    @Test
    void testRegisterAddition() {
        ram.putData(4, new boolean[]{false, false, false, false, true, false, true, false,
                false, false, false, false, false, false, true, false});

        testMovingBetweenRegisters();

        for (int i = 0; i < 10; i++) {
            clock.setData(new boolean[]{true});
            ProcessRunner.run(clock);
            clock.setData(new boolean[]{false});
            ProcessRunner.run(clock);
            clock.setData(new boolean[]{true});
            ProcessRunner.run(clock);
            clock.setData(new boolean[]{false});
            ProcessRunner.run(clock);

        }

        assertArrayEquals(new boolean[]{false, false, false, false, false, false, false, false,
                        false, false, false, false, false, true, false, true},
                cpu.getRegisterFile().getRegisters().get(2).getDataBitStream().getData());
        assertArrayEquals(new boolean[]{false, false, false, false, false, false, false, false,
                        false, false, false, false, true, false, true, false},
                cpu.getRegisterFile().getRegisters().get(1).getDataBitStream().getData());
        assertArrayEquals(new boolean[]{false, false, false, false, true, false, true, false,
                        false, false, false, false, false, false, true, false},
                cpu.getControlUnit().getMicroprocessor().getIR1().getDataBitStream().getData());
    }

    @Test
    void testUnconditionalJumps() {
        ram.putData(6, new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, true, true});
        ram.putData(7, new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, true, true});

        testRegisterAddition();

        for (int i = 0; i < 10; i++) {
            clock.setData(new boolean[]{true});
            ProcessRunner.run(clock);
            clock.setData(new boolean[]{false});
            ProcessRunner.run(clock);
            clock.setData(new boolean[]{true});
            ProcessRunner.run(clock);
            clock.setData(new boolean[]{false});
            ProcessRunner.run(clock);

        }

        assertArrayEquals(new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, true, false, true, true}, cpu.getIag().getPC().getDataBitStream().getData());
        assertArrayEquals(new boolean[]{false, false, false, false, false, false, false, false,
                        false, false, false, false, false, true, false, true},
                cpu.getRegisterFile().getRegisters().get(2).getDataBitStream().getData());
        assertArrayEquals(new boolean[]{false, false, false, false, false, false, false, false,
                        false, false, false, false, true, false, true, false},
                cpu.getRegisterFile().getRegisters().get(1).getDataBitStream().getData());
    }

    @Test
    void testFibonacci() {
        //mov 1, ax
        ram.putData(0, new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, true});
        ram.putData(1, new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, true});
        //loop:
        //mov ax, cx
        ram.putData(2, new boolean[]{false, false, false, true, false, false, false, false,
                false, false, false, false, false, false, false, false});

        //add bx, ax
        ram.putData(4, new boolean[]{false, false, false, false, false, false, false, true,
                false, false, false, false, false, false, true, false});

        //mov cx, bx
        ram.putData(6, new boolean[]{false, false, false, false, true, false, true, false,
                false, false, false, false, false, false, false, false});

        //jmp loop
        ram.putData(8, new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, true, true});
        ram.putData(9, DataConverter.convertSignedDecToBool(-8, 16));

        for (int i = 0; i < 8; i++) {
            clock.setData(new boolean[]{true});
            ProcessRunner.run(clock);
            clock.setData(new boolean[]{false});
            ProcessRunner.run(clock);
            clock.setData(new boolean[]{true});
            ProcessRunner.run(clock);
            clock.setData(new boolean[]{false});
            ProcessRunner.run(clock);
        }
        int b = 0; int a = 1;
        for (int j = 0; j < 23; j++) {
            for (int i = 0; i < 36; i++) {
                clock.setData(new boolean[]{true});
                ProcessRunner.run(clock);
                clock.setData(new boolean[]{false});
                ProcessRunner.run(clock);
                clock.setData(new boolean[]{true});
                ProcessRunner.run(clock);
                clock.setData(new boolean[]{false});
                ProcessRunner.run(clock);
            }

            assertEquals(a + b, DataConverter.convertBoolToUnsignedDec(
                    cpu.getRegisterFile().getRegisters().get(0).getDataBitStream().getData()));
            assertEquals(a, DataConverter.convertBoolToUnsignedDec(
                    cpu.getRegisterFile().getRegisters().get(1).getDataBitStream().getData()));

            int temp = a;
            a = a + b;
            b = temp;
        }
    }

}