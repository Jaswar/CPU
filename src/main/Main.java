package main;

import main.control.Input;
import main.control.Output;
import main.gates.binary.AND;
import main.gates.binary.NOR;
import main.gates.binary.OR;
import main.memory.Storage;
import main.utils.ProcessRunner;

public class Main {


    public static void main(String[] args) {
        Storage microinstructions = new Storage("./storage/main/microinstructions.stg", 256, 24);
        microinstructions.putData(0, new boolean[]{true, true, true, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false});
        microinstructions.putData(1, new boolean[]{false, false, true, false, true, true, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false});
        microinstructions.putData(2, new boolean[]{false, false, true, false, false, false, false, false,
                false, false, false, true, true, false, false, false,
                false, false, false, false, false, false, false, false});
        microinstructions.putData(2, new boolean[]{true, true, false, false, false, false, false, false,
                false, false, false, false, false, true, false, false,
                false, false, false, false, false, false, false, false});
        microinstructions.save();

    }
}
