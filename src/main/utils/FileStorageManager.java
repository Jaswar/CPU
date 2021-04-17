package main.utils;

import main.exceptions.WordSizeMismatchException;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileStorageManager {

    private String filepath;

    public FileStorageManager(String filepath) {
        this.filepath = filepath;
    }

    public String getFilepath() {
        return filepath;
    }

    public boolean[][] readStringBinaryData() {
        List<boolean[]> readData = new ArrayList<>();
        try (Scanner fileScanner = new Scanner(new File(this.filepath))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();

                boolean[] data = DataConverter.convertBinToBool(line);
                readData.add(data);
            }
            return convertBooleanListTo2DArray(readData);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private boolean[][] convertBooleanListTo2DArray(List<boolean[]> data) {
        boolean[][] array = new boolean[data.size()][data.get(0).length];
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).length != data.get(0).length) {
                throw new WordSizeMismatchException(this);
            }

            array[i] = data.get(i);
        }
        return array;
    }
}
