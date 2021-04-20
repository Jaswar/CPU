package main;

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
        microinstructions.putData(3, new boolean[]{true, true, false, false, false, false, false, false,
                false, false, false, false, false, true, false, false,
                false, false, false, false, false, false, false, false});
        microinstructions.save();

        Storage mapping = new Storage("./storage/main/microMapping.stg", 256, 8);
        mapping.putData(0, new boolean[]{false, false, false, false, false, false, false, false});
        mapping.putData(1, new boolean[]{false, false, false, false, false, false, false, true});
        mapping.save();
    }

}
