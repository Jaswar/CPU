package main.circuits;

import main.BitStream;
import main.control.Input;
import main.memory.RAM;
import main.utils.DataConverter;
import main.utils.ProcessRunner;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.lifecycle.BeforeProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CPUTest {

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

        cpu = new CPU(memRead, memWrite, memoryDataOut, memoryDataIn, memoryAddress);

        ram = new RAM(memoryAddress, memoryDataIn, memoryDataOut, memWrite, memRead);
    }

    @Test
    void testMovingBetweenRegisters() {

        ram.putData(2, new boolean[]{false, false, false, false, true, false, true, false,
                false, false, false, false, false, false, false, true});

        testMovingIntermediateToRegister();

        cpu.run(false, 1);

        assertArrayEquals(new boolean[]{false, false, false, false, false, false, false, false,
                        false, false, false, false, false, true, false, true},
                cpu.getRegisterFile().getRegisters().get(2).getDataBitStream().getData());
        assertArrayEquals(new boolean[]{false, false, false, false, false, false, false, false,
                        false, false, false, false, false, true, false, true},
                cpu.getRegisterFile().getRegisters().get(1).getDataBitStream().getData());
        assertArrayEquals(new boolean[]{false, false, false, false, true, false, true, false,
                        false, false, false, false, false, false, false, true},
                cpu.getControlUnit().getMicroprocessor().getIR1().getDataBitStream().getData());
    }

    @Test
    void testMovingIntermediateToRegister() {

        ram.putData(0, new boolean[]{false, false, false, true, false, false, false, false,
                false, false, false, false, false, false, true, false});
        ram.putData(1, new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, true, false, true});

        cpu.run(false, 1);

        assertArrayEquals(new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, true, false, true},
                cpu.getRegisterFile().getRegisters().get(2).getDataBitStream().getData());
        assertArrayEquals(new boolean[]{false, false, false, true, false, false, false, false,
                false, false, false, false, false, false, true, false},
                cpu.getControlUnit().getMicroprocessor().getIR1().getDataBitStream().getData());
        assertArrayEquals(new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, true, false, true},
                cpu.getControlUnit().getMicroprocessor().getIR2().getDataBitStream().getData());
    }

    @Test
    void testMovingFromIndirectMemory() {
        //mv 0x10, ax
        ram.putData(0, new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, true, false});
        ram.putData(1, new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, true, false, false, false, false});

        //mv (ax), bx
        ram.putData(2, new boolean[]{false, false, false, false, true, false, false, false,
                false, false, false, false, false, false, true, true});
        ram.putData(16, new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, true, false, true});

        cpu.run(false, 2);

        assertArrayEquals(new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, true, false, true},
                cpu.getRegisterFile().getRegisters().get(1).getDataBitStream().getData());
    }

    @Test
    void testMovingFromIntermediateValueMemory() {
        ram.putData(16, new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, true, false, true});

        //mv (0x10), dx
        ram.putData(0, new boolean[]{false, false, false, true, true, false, false, false,
                false, false, false, false, false, true, false, false});
        ram.putData(1, new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, true, false, false, false, false});

        cpu.run(false, 1);

        assertArrayEquals(new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, true, false, true},
                cpu.getRegisterFile().getRegisters().get(3).getDataBitStream().getData());
    }

    @Test
    void testIndirectMemoryWriting() {
        //mv -1, si
        ram.putData(0, new boolean[]{false, false, true, false, true, false, false, false,
                false, false, false, false, false, false, true, false});
        ram.putData(1, new boolean[]{true, true, true, true, true, true, true, true,
                true, true, true, true, true, true, true, true});

        //mv 0x20, di
        ram.putData(2, new boolean[]{false, false, true, false, false, false, false, false,
                false, false, false, false, false, false, true, false});
        ram.putData(3, new boolean[]{false, false, false, false, false, false, false, false,
                false, false, true, false, false, false, false, false});

        //mv si, (di)
        ram.putData(4, new boolean[]{false, false, true, false, false, true, false, true,
                false, false, false, false, false, true, false, true});

        cpu.run(false, 3);

        assertArrayEquals(new boolean[]{true, true, true, true, true, true, true, true,
                true, true, true, true, true, true, true, true}, ram.getData()[32]);
    }

    @Test
    void testIndirectValueMemoryWrite() {
        //mv 0x20, bp
        ram.putData(0, new boolean[]{false, false, true, true, false, false, false, false,
                false, false, false, false, false, false, true, false});
        ram.putData(1, new boolean[]{false, false, false, false, false, false, false, false,
                false, false, true, false, false, false, false, false});

        //mv 15, (bp)
        ram.putData(2, new boolean[]{false, false, true, true, false, false, false, false,
                false, false, false, false, false, true, true, true});
        ram.putData(3, DataConverter.convertSignedDecToBool(15, 16));

        cpu.run(false, 2);

        assertEquals(15, DataConverter.convertBoolToUnsignedDec(ram.getData()[0x20]));
    }

    @Property(tries = 10)
    void testRegisterAddition(@ForAll @IntRange(min = -16000, max = 16000) int a,
                              @ForAll @IntRange(min = -16000, max = 16000) int b) {
        setup();

        //mv a, ax
        ram.putData(0, new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, true, false});
        ram.putData(1, DataConverter.convertSignedDecToBool(a, 16));

        //mv b, bx
        ram.putData(2, new boolean[]{false, false, false, false, true, false, false, false,
                false, false, false, false, false, false, true, false});
        ram.putData(3, DataConverter.convertSignedDecToBool(b, 16));

        //add ax, bx
        ram.putData(4, new boolean[]{false, false, false, false, true, false, false, false,
                false, false, false, false, true, false, false, false});

        cpu.run(false, 3);

        assertArrayEquals(DataConverter.convertSignedDecToBool(a, 16),
                cpu.getRegisterFile().getRegisters().get(0).getDataBitStream().getData());
        assertArrayEquals(DataConverter.convertSignedDecToBool(a + b, 16),
                cpu.getRegisterFile().getRegisters().get(1).getDataBitStream().getData());
        assertArrayEquals(new boolean[]{false, false, false, false, true, false, false, false,
                        false, false, false, false, true, false, false, false},
                cpu.getControlUnit().getMicroprocessor().getIR1().getDataBitStream().getData());
    }

    @Test
    void testRegisterIntermediateAddition() {
        //mv 64, ax
        ram.putData(0, new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, true, false});
        ram.putData(1, DataConverter.convertSignedDecToBool(64, 16));

        //add 74,
    }

    @Test
    void testUnconditionalJumps() {

        testRegisterAddition(10, 5);

        ram.putData(6, new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, true, true, false, true, false});
        ram.putData(7, new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, true, true});

        cpu.run(false, 1);

        assertArrayEquals(new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, true, false, true, true}, cpu.getIag().getPC().getDataBitStream().getData());
        assertArrayEquals(new boolean[]{false, false, false, false, false, false, false, false,
                        false, false, false, false, true, true, true, true},
                cpu.getRegisterFile().getRegisters().get(1).getDataBitStream().getData());
        assertArrayEquals(new boolean[]{false, false, false, false, false, false, false, false,
                        false, false, false, false, true, false, true, false},
                cpu.getRegisterFile().getRegisters().get(0).getDataBitStream().getData());

    }
    
    @Test
    void testFibonacci() {
        //mov 1, ax
        ram.putData(0, new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, true, false});
        ram.putData(1, new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, true});
        //loop:
        //mov ax, cx
        ram.putData(2, new boolean[]{false, false, false, true, false, false, false, false,
                false, false, false, false, false, false, false, true});

        //add bx, ax
        ram.putData(4, new boolean[]{false, false, false, false, false, false, false, true,
                false, false, false, false, true, false, false, false});

        //mov cx, bx
        ram.putData(6, new boolean[]{false, false, false, false, true, false, true, false,
                false, false, false, false, false, false, false, true});

        //jno loop
        ram.putData(8, new boolean[]{false, false, false, false, false, false, false, false,
                false, false, true, false, false, false, true, false});
        ram.putData(9, DataConverter.convertSignedDecToBool(-8, 16));

        //jmp $
        ram.putData(10, new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, true, true, false, true, false});
        ram.putData(11, DataConverter.convertSignedDecToBool(-2, 16));


        cpu.run(false, 1);
        int b = 0; int a = 1;
        for (int j = 0; j < 30; j++) {
            cpu.run(false, 4);

            assertEquals(a + b, DataConverter.convertBoolToUnsignedDec(
                    cpu.getRegisterFile().getRegisters().get(0).getDataBitStream().getData()));
            assertEquals(a, DataConverter.convertBoolToUnsignedDec(
                    cpu.getRegisterFile().getRegisters().get(1).getDataBitStream().getData()));
            if (j < 22) {
                int temp = a;
                a = a + b;
                b = temp;
            }
        }
    }

}