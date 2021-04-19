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
        Storage microMapping = new Storage("./storage/main/microinstructions.stg", 256, 17);
        microMapping.putData(0, new boolean[]{false, false, false, false, false, false, false});
        microMapping.putData(1, new boolean[]{false, false, false, false, false, false, true});
        microMapping.save();
    }
}
