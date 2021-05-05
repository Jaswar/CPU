package main.utils;

import main.circuits.Microprocessor;
import main.memory.Storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MappingROMLoader {

    /**Run the main method of this class to create a storage file with the mapping information.
     */
    public static void main(String[] args) {
        readMicroinstructionCSVFile("./documentation/microinstructions.csv", "./storage/main/microMapping.stg");
    }

    /**Method to load the mapping information for the microprocessor from the file specified.
     *
     * @param documentFilepath - filepath where the .csv file containing the mapping information is. This should be
     *                         the file generated from microinstructions.xlsx
     * @param storageFilepath - filepath where the resulting storage should be saved
     */
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
