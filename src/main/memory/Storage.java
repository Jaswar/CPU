package main.memory;

import java.io.*;

public class Storage implements Serializable {

    private boolean[][] data;
    private transient String filepath;

    public Storage(String filepath, int numRows, int wordSize) {
        this.data = new boolean[numRows][wordSize];
        this.filepath = filepath;
    }

    public boolean[][] getData() {
        return data;
    }

    public String getFilepath() {
        return filepath;
    }

    public int getSize() {
        return this.data.length;
    }

    public int getWordSize() {
        return this.data[0].length;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public void putData(int address, boolean[] data) {
        this.data[address] = data;
    }

    public static Storage read(String filepath) {
        try {
            FileInputStream fis = new FileInputStream(filepath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Storage newStorage = (Storage) ois.readObject();
            ois.close();
            newStorage.setFilepath(filepath);
            return newStorage;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void save() {
        try {
            FileOutputStream fos = new FileOutputStream(filepath);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Storage)) {
            return false;
        }
        Storage other = (Storage) o;
        if (other.getData().length != this.getData().length
                || other.getData()[0].length != this.getData()[0].length) {
            return false;
        }
        for (int i = 0; i < this.getData().length; i++) {
            for (int j = 0; j < this.getData()[i].length; j++) {
                if (this.getData()[i][j] != other.getData()[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "Storage<" + this.data.length + ", " + this.data[0].length +">";
    }
}
