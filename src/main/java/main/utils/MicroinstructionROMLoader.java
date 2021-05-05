package main.utils;

import main.circuits.Microprocessor;
import main.memory.Storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MicroinstructionROMLoader {


    /**Run the main method of this class to create a storage file with the microinstructions.
     */
    public static void main(String[] args) {
        readMicroinstructionCSVFile("./documentation/microinstructions.csv", "./storage/main/microinstructions.stg");
    }

    /**Method to load the microinstructions for the microprocessor from the file specified.
     *
     * @param documentFilepath - filepath where the .csv file containing the microinstructions is. This should be
     *                         the file generated from microinstructions.xlsx
     * @param storageFilepath - filepath where the resulting storage should be saved
     */
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
