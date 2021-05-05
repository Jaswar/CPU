package main.circuits;

import main.BitStream;
import main.control.Input;
import main.memory.RAM;
import main.utils.DataConverter;
import main.utils.ProcessRunner;
import main.warnings.InconsistentBitStreamSourcesWarning;
import net.jqwik.api.Data;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.lifecycle.AfterTry;
import net.jqwik.api.lifecycle.BeforeProperty;
import net.jqwik.api.lifecycle.BeforeTry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CPUTest {

    CPU cpu;
    RAM ram;

    public final int NUM_TRIES = 10;

    @BeforeEach
    @BeforeTry
    void setup() {
        InconsistentBitStreamSourcesWarning.setSuppress(true);

        BitStream clk = new BitStream(1);
        BitStream memoryDataOut = new BitStream(Microprocessor.WORD_SIZE);
        BitStream memoryDataIn = new BitStream(Microprocessor.WORD_SIZE);
        BitStream memoryAddress = new BitStream(Microprocessor.WORD_SIZE);
        BitStream memRead = new BitStream(1);
        BitStream memWrite = new BitStream(1);

        cpu = new CPU(memRead, memWrite, memoryDataOut, memoryDataIn, memoryAddress);

        ram = new RAM(memoryAddress, memoryDataIn, memoryDataOut, memWrite, memRead);
    }

    @AfterEach
    @AfterTry
    void teardown() {
        InconsistentBitStreamSourcesWarning.setSuppress(false);
    }

    private void loadForLogicalRegistersOperation(int a, int b) {
        //mv a, ax
        ram.putData(0, new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, true, false});
        ram.putData(1, DataConverter.convertSignedDecToBool(a, Microprocessor.WORD_SIZE));

        //mv b, bx
        ram.putData(2, new boolean[]{false, false, false, false, true, false, false, false,
                false, false, false, false, false, false, true, false});
        ram.putData(3, DataConverter.convertSignedDecToBool(b, Microprocessor.WORD_SIZE));
    }

    private void testJumps(int opCode, int a, int b, boolean shouldPass) {
        testRegisterAddition(a, b);

        ram.putData(6, DataConverter.convertSignedDecToBool(opCode, Microprocessor.WORD_SIZE));
        ram.putData(7, new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, true, false, true, false});

        cpu.run(false, 1);

        if (shouldPass) {
            assertArrayEquals(new boolean[]{false, false, false, false, false, false, false, false,
                    false, false, false, true, false, false, true, false}, cpu.getIag().getPC().getDataBitStream().getData());
        } else {
            assertArrayEquals(new boolean[]{false, false, false, false, false, false, false, false,
                    false, false, false, false, true, false, false, false}, cpu.getIag().getPC().getDataBitStream().getData());
        }
    }

    @Test
    void testMovingBetweenRegisters() {
        //mv cx, bx
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
        //mv 5, cx
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
        ram.putData(1, DataConverter.convertSignedDecToBool(0x20, Microprocessor.WORD_SIZE));

        //mv 15, (bp)
        ram.putData(2, new boolean[]{false, false, true, true, false, false, false, false,
                false, false, false, false, false, true, true, true});
        ram.putData(3, DataConverter.convertSignedDecToBool(15, Microprocessor.WORD_SIZE));

        cpu.run(false, 2);

        assertEquals(15, DataConverter.convertBoolToSignedDec(ram.getData()[0x20], 16));
    }

    @Property(tries = NUM_TRIES)
    void testRegisterAddition(@ForAll @IntRange(min = -32000, max = 32000) int a,
                              @ForAll @IntRange(min = -32000, max = 32000) int b) {
        //mv a, ax
        ram.putData(0, new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, true, false});
        ram.putData(1, DataConverter.convertSignedDecToBool(a, Microprocessor.WORD_SIZE));

        //mv b, bx
        ram.putData(2, new boolean[]{false, false, false, false, true, false, false, false,
                false, false, false, false, false, false, true, false});
        ram.putData(3, DataConverter.convertSignedDecToBool(b, Microprocessor.WORD_SIZE));

        //add ax, bx
        ram.putData(4, new boolean[]{false, false, false, false, true, false, false, false,
                false, false, false, false, true, false, false, false});

        cpu.run(false, 3);

        assertArrayEquals(DataConverter.convertSignedDecToBool(a, Microprocessor.WORD_SIZE),
                cpu.getRegisterFile().getRegisters().get(0).getDataBitStream().getData());
        assertArrayEquals(DataConverter.convertSignedDecToBool(a + b, Microprocessor.WORD_SIZE),
                cpu.getRegisterFile().getRegisters().get(1).getDataBitStream().getData());
        assertArrayEquals(new boolean[]{false, false, false, false, true, false, false, false,
                        false, false, false, false, true, false, false, false},
                cpu.getControlUnit().getMicroprocessor().getIR1().getDataBitStream().getData());
    }

    @Property(tries = NUM_TRIES)
    void testRegisterIntermediateAddition(@ForAll @IntRange(min = -32000, max = 32000) int reg,
                                          @ForAll @IntRange(min = -32000, max = 32000) int inter) {
        //mv reg, cx
        ram.putData(0, new boolean[]{false, false, false, true, false, false, false, false,
                false, false, false, false, false, false, true, false});
        ram.putData(1, DataConverter.convertSignedDecToBool(reg, Microprocessor.WORD_SIZE));

        //add inter, cx
        ram.putData(2, new boolean[]{false, false, false, true, false, false, false, false,
                false, false, false, false, true, false, false, true});
        ram.putData(3, DataConverter.convertSignedDecToBool(inter, Microprocessor.WORD_SIZE));

        cpu.run(false, 2);

        assertArrayEquals(DataConverter.convertSignedDecToBool(reg + inter, Microprocessor.WORD_SIZE),
                cpu.getRegisterFile().getRegisters().get(2).getDataBitStream().getData());
    }

    @Property(tries = NUM_TRIES)
    void testRegisterIncrement(@ForAll @IntRange(min = 0, max = 32) int count,
                               @ForAll @IntRange(min = -1000, max = 1000) int initialValue) {
        //mv initialValue, ax
        ram.putData(0, new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, true, false});
        ram.putData(1, DataConverter.convertSignedDecToBool(initialValue, Microprocessor.WORD_SIZE));

        for (int i = 1; i <= count ; i++) {
            //inc ax
            ram.putData(2 * i, new boolean[]{false, false, false, false, false, false, false, false,
                    false, false, false, false, true, false, true, false});
        }
        cpu.run(false, count + 1);

        assertArrayEquals(DataConverter.convertSignedDecToBool(count + initialValue, Microprocessor.WORD_SIZE),
                cpu.getRegisterFile().getRegisters().get(0).getDataBitStream().getData());
    }

    @Property(tries = NUM_TRIES)
    void testRegisterSubtraction(@ForAll @IntRange(min = -32000, max = 32000) int s,
                                 @ForAll @IntRange(min = -32000, max = 32000) int b) {
        //mv s, sp
        ram.putData(0, new boolean[]{false, false, true, true, true, false, false, false,
                false, false, false, false, false, false, true, false});
        ram.putData(1, DataConverter.convertSignedDecToBool(s, Microprocessor.WORD_SIZE));

        //mv b, bp
        ram.putData(2, new boolean[]{false, false, true, true, false, false, false, false,
                false, false, false, false, false, false, true, false});
        ram.putData(3, DataConverter.convertSignedDecToBool(b, Microprocessor.WORD_SIZE));

        //sub sp, bp
        ram.putData(4, new boolean[]{false, false, true, true, false, true, true, true,
                false, false, false, false, true, false, true, true});

        cpu.run(false, 3);

        assertArrayEquals(DataConverter.convertSignedDecToBool(s - b, Microprocessor.WORD_SIZE),
                cpu.getRegisterFile().getRegisters().get(6).getDataBitStream().getData());
    }

    @Property(tries = NUM_TRIES)
    void testRegisterMinusIntermediate(@ForAll @IntRange(min = -32000, max = 32000) int inter,
                                       @ForAll @IntRange(min = -32000, max = 32000) int s) {
        //mv s, si
        ram.putData(0, new boolean[]{false, false, true, false, true, false, false, false,
                false, false, false, false, false, false, true, false});
        ram.putData(1, DataConverter.convertSignedDecToBool(s, Microprocessor.WORD_SIZE));

        //sub sp, inter
        ram.putData(2, new boolean[]{false, false, true, false, true, false, false, false,
                false, false, false, false, true, true, false, false});
        ram.putData(3, DataConverter.convertSignedDecToBool(inter, Microprocessor.WORD_SIZE));

        cpu.run(false, 2);

        assertArrayEquals(DataConverter.convertSignedDecToBool(s - inter, Microprocessor.WORD_SIZE),
                cpu.getRegisterFile().getRegisters().get(5).getDataBitStream().getData());
    }

    @Property(tries = NUM_TRIES)
    void testIntermediateMinusRegister(@ForAll @IntRange(min = -32000, max = 32000) int inter,
                                       @ForAll @IntRange(min = -32000, max = 32000) int s) {
        //mv s, di
        ram.putData(0, new boolean[]{false, false, true, false, false, false, false, false,
                false, false, false, false, false, false, true, false});
        ram.putData(1, DataConverter.convertSignedDecToBool(s, Microprocessor.WORD_SIZE));

        //sub inter, di
        ram.putData(2, new boolean[]{false, false, true, false, false, false, false, false,
                false, false, false, false, true, true, false, true});
        ram.putData(3, DataConverter.convertSignedDecToBool(inter, Microprocessor.WORD_SIZE));

        cpu.run(false, 2);

        assertArrayEquals(DataConverter.convertSignedDecToBool(inter - s, Microprocessor.WORD_SIZE),
                cpu.getRegisterFile().getRegisters().get(4).getDataBitStream().getData());
    }

    @Property(tries = NUM_TRIES)
    void testRegisterDecrement(@ForAll @IntRange(min = 0, max = 32) int count,
                               @ForAll @IntRange(min = -1000, max = 1000) int initialValue) {
        //mv initialValue, bx
        ram.putData(0, new boolean[]{false, false, false, false, true, false, false, false,
                false, false, false, false, false, false, true, false});
        ram.putData(1, DataConverter.convertSignedDecToBool(initialValue, Microprocessor.WORD_SIZE));

        for (int i = 1; i <= count ; i++) {
            //dec ax
            ram.putData(2 * i, new boolean[]{false, false, false, false, true, false, false, false,
                    false, false, false, false, true, true, true, false});
        }
        cpu.run(false, count + 1);

        assertArrayEquals(DataConverter.convertSignedDecToBool(initialValue - count, Microprocessor.WORD_SIZE),
                cpu.getRegisterFile().getRegisters().get(1).getDataBitStream().getData());
    }

    @Property(tries = NUM_TRIES)
    void testNot(@ForAll @IntRange(min = -32000, max = 32000) int value) {
        //mv value, dx
        ram.putData(0, new boolean[]{false, false, false, true, true, false, false, false,
                false, false, false, false, false, false, true, false});
        ram.putData(1, DataConverter.convertSignedDecToBool(value, Microprocessor.WORD_SIZE));

        //not dx
        ram.putData(2, new boolean[]{false, false, false, true, true, false, false, false,
                false, false, false, false, true, true, true, true});

        cpu.run(false, 2);

        assertArrayEquals(DataConverter.convertSignedDecToBool(~value, Microprocessor.WORD_SIZE),
                cpu.getRegisterFile().getRegisters().get(3).getDataBitStream().getData());
    }

    @Property(tries = NUM_TRIES)
    void testOrRegisters(@ForAll @IntRange(min = -32000, max = 32000) int a,
                         @ForAll @IntRange(min = -32000, max = 32000) int b) {
        loadForLogicalRegistersOperation(a, b);

        //or ax, bx
        ram.putData(4, new boolean[]{false, false, false, false, true, false, false, false,
                false, false, false, true, false, false, false, false});

        cpu.run(false, 3);

        assertArrayEquals(DataConverter.convertSignedDecToBool(a | b, Microprocessor.WORD_SIZE),
                cpu.getRegisterFile().getRegisters().get(1).getDataBitStream().getData());
    }

    @Property(tries = NUM_TRIES)
    void testIntermediateOrRegister(@ForAll @IntRange(min = -32000, max = 32000) int b,
                         @ForAll @IntRange(min = -32000, max = 32000) int inter) {
        //mv b, bx
        ram.putData(0, new boolean[]{false, false, false, false, true, false, false, false,
                false, false, false, false, false, false, true, false});
        ram.putData(1, DataConverter.convertSignedDecToBool(b, Microprocessor.WORD_SIZE));

        //or inter, cx
        ram.putData(2, new boolean[]{false, false, false, false, true, false, false, false,
                false, false, false, true, false, false, false, true});
        ram.putData(3, DataConverter.convertSignedDecToBool(inter, Microprocessor.WORD_SIZE));

        cpu.run(false, 2);

        assertArrayEquals(DataConverter.convertSignedDecToBool(b | inter, Microprocessor.WORD_SIZE),
                cpu.getRegisterFile().getRegisters().get(1).getDataBitStream().getData());
    }

    @Property(tries = NUM_TRIES)
    void testAndRegisters(@ForAll @IntRange(min = -32000, max = 32000) int a,
                         @ForAll @IntRange(min = -32000, max = 32000) int b) {
        loadForLogicalRegistersOperation(a, b);

        //or ax, bx
        ram.putData(4, new boolean[]{false, false, false, false, true, false, false, false,
                false, false, false, true, false, false, true, false});

        cpu.run(false, 3);

        assertArrayEquals(DataConverter.convertSignedDecToBool(a & b, Microprocessor.WORD_SIZE),
                cpu.getRegisterFile().getRegisters().get(1).getDataBitStream().getData());
    }

    @Property(tries = NUM_TRIES)
    void testIntermediateAndRegister(@ForAll @IntRange(min = -32000, max = 32000) int d,
                                    @ForAll @IntRange(min = -32000, max = 32000) int inter) {
        //mv d, di
        ram.putData(0, new boolean[]{false, false, true, false, false, false, false, false,
                false, false, false, false, false, false, true, false});
        ram.putData(1, DataConverter.convertSignedDecToBool(d, Microprocessor.WORD_SIZE));

        //and inter, di
        ram.putData(2, new boolean[]{false, false, true, false, false, false, false, false,
                false, false, false, true, false, false, true, true});
        ram.putData(3, DataConverter.convertSignedDecToBool(inter, Microprocessor.WORD_SIZE));

        cpu.run(false, 2);

        assertArrayEquals(DataConverter.convertSignedDecToBool(d & inter, Microprocessor.WORD_SIZE),
                cpu.getRegisterFile().getRegisters().get(4).getDataBitStream().getData());
    }

    @Property(tries = NUM_TRIES)
    void testXorRegisters(@ForAll @IntRange(min = -32000, max = 32000) int a,
                          @ForAll @IntRange(min = -32000, max = 32000) int b) {
        loadForLogicalRegistersOperation(a, b);

        //xor ax, bx
        ram.putData(4, new boolean[]{false, false, false, false, true, false, false, false,
                false, false, false, true, false, true, false, false});

        cpu.run(false, 3);

        assertArrayEquals(DataConverter.convertSignedDecToBool(a ^ b, Microprocessor.WORD_SIZE),
                cpu.getRegisterFile().getRegisters().get(1).getDataBitStream().getData());
    }

    @Property(tries = NUM_TRIES)
    void testIntermediateXorRegister(@ForAll @IntRange(min = -32000, max = 32000) int d,
                                     @ForAll @IntRange(min = -32000, max = 32000) int inter) {
        //mv d, di
        ram.putData(0, new boolean[]{false, false, true, false, false, false, false, false,
                false, false, false, false, false, false, true, false});
        ram.putData(1, DataConverter.convertSignedDecToBool(d, Microprocessor.WORD_SIZE));

        //xor inter, di
        ram.putData(2, new boolean[]{false, false, true, false, false, false, false, false,
                false, false, false, true, false, true, false, true});
        ram.putData(3, DataConverter.convertSignedDecToBool(inter, Microprocessor.WORD_SIZE));

        cpu.run(false, 2);

        assertArrayEquals(DataConverter.convertSignedDecToBool(d ^ inter, Microprocessor.WORD_SIZE),
                cpu.getRegisterFile().getRegisters().get(4).getDataBitStream().getData());
    }

    @Property(tries = NUM_TRIES)
    void testNandRegisters(@ForAll @IntRange(min = -32000, max = 32000) int a,
                          @ForAll @IntRange(min = -32000, max = 32000) int b) {
        loadForLogicalRegistersOperation(a, b);

        //nand ax, bx
        ram.putData(4, new boolean[]{false, false, false, false, true, false, false, false,
                false, false, false, true, false, true, true, false});

        cpu.run(false, 3);

        assertArrayEquals(DataConverter.convertSignedDecToBool(~(a & b), Microprocessor.WORD_SIZE),
                cpu.getRegisterFile().getRegisters().get(1).getDataBitStream().getData());
    }

    @Property(tries = NUM_TRIES)
    void testIntermediateNandRegister(@ForAll @IntRange(min = -32000, max = 32000) int b,
                                     @ForAll @IntRange(min = -32000, max = 32000) int inter) {
        //mv b, bp
        ram.putData(0, new boolean[]{false, false, true, true, false, false, false, false,
                false, false, false, false, false, false, true, false});
        ram.putData(1, DataConverter.convertSignedDecToBool(b, Microprocessor.WORD_SIZE));

        //nand inter, bp
        ram.putData(2, new boolean[]{false, false, true, true, false, false, false, false,
                false, false, false, true, false, true, true, true});
        ram.putData(3, DataConverter.convertSignedDecToBool(inter, Microprocessor.WORD_SIZE));

        cpu.run(false, 2);

        assertArrayEquals(DataConverter.convertSignedDecToBool(~(b & inter), Microprocessor.WORD_SIZE),
                cpu.getRegisterFile().getRegisters().get(6).getDataBitStream().getData());
    }

    @Property(tries = NUM_TRIES)
    void testNorRegisters(@ForAll @IntRange(min = -32000, max = 32000) int a,
                           @ForAll @IntRange(min = -32000, max = 32000) int b) {
        loadForLogicalRegistersOperation(a, b);

        //nor ax, bx
        ram.putData(4, new boolean[]{false, false, false, false, true, false, false, false,
                false, false, false, true, true, false, false, false});

        cpu.run(false, 3);

        assertArrayEquals(DataConverter.convertSignedDecToBool(~(a | b), Microprocessor.WORD_SIZE),
                cpu.getRegisterFile().getRegisters().get(1).getDataBitStream().getData());
    }

    @Property(tries = NUM_TRIES)
    void testIntermediateNorRegister(@ForAll @IntRange(min = -32000, max = 32000) int b,
                                      @ForAll @IntRange(min = -32000, max = 32000) int inter) {
        //mv b, sp
        ram.putData(0, new boolean[]{false, false, true, true, true, false, false, false,
                false, false, false, false, false, false, true, false});
        ram.putData(1, DataConverter.convertSignedDecToBool(b, Microprocessor.WORD_SIZE));

        //nor inter, sp
        ram.putData(2, new boolean[]{false, false, true, true, true, false, false, false,
                false, false, false, true, true, false, false, true});
        ram.putData(3, DataConverter.convertSignedDecToBool(inter, Microprocessor.WORD_SIZE));

        cpu.run(false, 2);

        assertArrayEquals(DataConverter.convertSignedDecToBool(~(b | inter), Microprocessor.WORD_SIZE),
                cpu.getRegisterFile().getRegisters().get(7).getDataBitStream().getData());
    }



    @Property(tries = NUM_TRIES)
    void testUnconditionalJumps(@ForAll @IntRange(min = -16000, max = 16000) int a,
                                @ForAll @IntRange(min = -16000, max = 16000) int b) {
        testJumps(26, a, b, true);
    }

    @Property(tries = NUM_TRIES)
    void testJumpIfNegative(@ForAll @IntRange(min = -16000, max = 16000) int a,
                            @ForAll @IntRange(min = -16000, max = 16000) int b) {
        testJumps(27, a, b, a + b < 0);
    }

    @Property(tries = NUM_TRIES)
    void testJumpIfNonNegative(@ForAll @IntRange(min = -16000, max = 16000) int a,
                            @ForAll @IntRange(min = -16000, max = 16000) int b) {
        testJumps(28, a, b, a + b >= 0);
    }

    @Property(tries = NUM_TRIES)
    void testJumpIfZero(@ForAll @IntRange(min = -16000, max = 16000) int a,
                            @ForAll @IntRange(min = -16000, max = 16000) int b) {
        testJumps(29, a, b, a + b == 0);
    }

    @Property(tries = NUM_TRIES)
    void testJumpIfNonZero(@ForAll @IntRange(min = -16000, max = 16000) int a,
                            @ForAll @IntRange(min = -16000, max = 16000) int b) {
        testJumps(30, a, b, a + b != 0);
    }

    @Property(tries = NUM_TRIES)
    void testJumpIfPositive(@ForAll @IntRange(min = -16000, max = 16000) int a,
                               @ForAll @IntRange(min = -16000, max = 16000) int b) {
        testJumps(31, a, b, a + b > 0);
    }

    @Property(tries = NUM_TRIES)
    void testJumpIfNonPositive(@ForAll @IntRange(min = -16000, max = 16000) int a,
                            @ForAll @IntRange(min = -16000, max = 16000) int b) {
        testJumps(32, a, b, a + b <= 0);
    }

    @Property(tries = NUM_TRIES)
    void testJumpIfOverflow(@ForAll @IntRange(min = -32000, max = 32000) int a,
                               @ForAll @IntRange(min = -32000, max = 32000) int b) {
        boolean shouldPass = Math.abs(a + b) > 32767;
        if (a + b == -32768) {
            shouldPass = false;
        }
        testJumps(33, a, b, shouldPass);
    }

    @Property(tries = NUM_TRIES)
    void testJumpIfNoOverflow(@ForAll @IntRange(min = -32000, max = 32000) int a,
                            @ForAll @IntRange(min = -32000, max = 32000) int b) {
        boolean shouldPass = Math.abs(a + b) <= 32767;
        if (a + b == -32768) {
            shouldPass = true;
        }
        testJumps(34, a, b, shouldPass);
    }

    @Property(tries = NUM_TRIES)
    void testClearFlags(@ForAll @IntRange(min = -32000, max = 32000) int a,
                        @ForAll @IntRange(min = -32000, max = 32000) int b) {
        testRegisterAddition(a, b);

        ram.putData(6, DataConverter.convertSignedDecToBool(35, Microprocessor.WORD_SIZE));

        cpu.run(false, 1);

        assertEquals("0000",
                DataConverter.convertBoolToBin(cpu.getControlUnit().getMicroprocessor().getStatus().getData()));
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
        ram.putData(9, DataConverter.convertSignedDecToBool(-8, Microprocessor.WORD_SIZE));

        //jmp $
        ram.putData(10, new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, true, true, false, true, false});
        ram.putData(11, DataConverter.convertSignedDecToBool(-2, Microprocessor.WORD_SIZE));


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