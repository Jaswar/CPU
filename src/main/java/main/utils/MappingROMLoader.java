package main.utils;

import main.circuits.Microprocessor;
import main.memory.Storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MappingROMLoader {

    public static void main(String[] args) {
        readMicroinstructionCSVFile("./documentation/microinstructions.csv", "./storage/main/microMapping.stg");
    }

    private static void readMicroinstructionCSVFile(String documentFilepath, String storageFilepath) {
        try (Scanner fileScanner = new Scanner(new File(documentFilepath))) {

            for (int i = 0; i < 15; i++) {
                fileScanner.nextLine();
            }

            Storage storage = new Storage(storageFilepath, 256, Microprocessor.MAPPING_OUT_SIZE);

            while (fileScanner.hasNext()) {

                String line = fileScanner.nextLine();
                if (!line.startsWith("0") && !line.startsWith("1")) {
                    String[] splitData = line.split(";");
                    int opCode = Integer.parseInt(splitData[2]);
                    int mappingAddress = Integer.parseInt(splitData[3]);

                    storage.putData(opCode, DataConverter.convertSignedDecToBool(mappingAddress, Microprocessor.MAPPING_OUT_SIZE));
                }
            }

            storage.save();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

}
