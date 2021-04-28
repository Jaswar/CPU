package main;

import main.circuits.Microprocessor;
import main.memory.Storage;
import main.utils.DataConverter;

public class Main {


    public static void main(String[] args) {
        Storage microinstructions = new Storage("./storage/main/microinstructions.stg",
                256, Microprocessor.NUM_ROM_MICROINSTRUCTIONS);
//        microinstructions.putData(0, new boolean[]{true, true, true, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false});
//        microinstructions.putData(1, new boolean[]{true, true, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, true,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false});
//        microinstructions.putData(2, new boolean[]{false, false, true, false, true, true, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false});
//        microinstructions.putData(3, new boolean[]{false, false, true, false, false, false, false, false,
//                false, false, false, true, true, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, true, true, true, true});
//        microinstructions.putData(4, new boolean[]{true, true, false, false, false, false, false, false,
//                false, false, false, false, false, true, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false});
        microinstructions.putData(5, DataConverter.convertBinToBool("00000100000000000000000110000000000000"));
        microinstructions.putData(6, DataConverter.convertBinToBool("00000000000110010000000010000000000000"));
        microinstructions.putData(7, DataConverter.convertBinToBool("10000000000001100000000010000000000000"));
        microinstructions.save();

        Storage mapping = new Storage("./storage/main/microMapping.stg", 256, 8);
        mapping.putData(0, new boolean[]{false, false, false, false, false, false, false, false});
        mapping.putData(1, new boolean[]{false, false, false, false, false, false, false, true});
        mapping.putData(2, new boolean[]{false, false, false, false, false, false, true, false});
        mapping.putData(3, new boolean[]{false, false, false, false, false, true, false, true});
        mapping.save();
    }

}
