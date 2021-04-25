package main.utils;

import main.circuits.Microprocessor;
import main.memory.Storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MicroinstructionROMLoader {

    public static void main(String[] args) {
        readMicroinstructionCSVFile("./documentation/microinstructions.csv", "./storage/main/microinstructions.stg");
    }

    private static void readMicroinstructionCSVFile(String documentFilepath, String storageFilepath) {
        try (Scanner fileScanner = new Scanner(new File(documentFilepath))) {

            for (int i = 0; i < 15; i++) {
                fileScanner.nextLine();
            }

            Storage storage = new Storage(storageFilepath, 256, Microprocessor.NUM_ROM_MICROINSTRUCTIONS);

            int currentLineNumber = 0;
            while (fileScanner.hasNext()) {

                String line = fileScanner.nextLine();
                if (line.startsWith("0") || line.startsWith("1")) {
                    int currentBit = 0;

                    String[] splitData = line.split(";");
                    boolean[] booleanData = new boolean[Microprocessor.NUM_ROM_MICROINSTRUCTIONS];
                    for (int i = 0; i < splitData.length; i++) {
                        boolean[] converted = DataConverter.convertBinToBool(splitData[i]);

                        for (int j = 0; j < converted.length; j++) {
                            booleanData[currentBit++] = converted[j];
                        }
                    }
                    storage.putData(currentLineNumber++, booleanData);
                }
            }

            storage.save();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

}
