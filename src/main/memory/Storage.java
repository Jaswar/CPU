package main.memory;

import java.io.*;

public class Storage implements Serializable {

    private boolean[][] data;
    private transient String filepath;

    /**Constructor for the Storage class. This class can be saved and written
     * to a file. Used to store data in files.
     *
     * @param filepath - the path to the file this class is connected to
     * @param numRows - the number of entries in data
     * @param wordSize - the width of each data entry
     */
    public Storage(String filepath, int numRows, int wordSize) {
        this.data = new boolean[numRows][wordSize];
        this.filepath = filepath;
    }

    /**Getters for some attributes and additional data.
     */
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

    /**Setter for the filepath.
     */
    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    /**Put data at given address. Does not save the data automatically to the file.
     *
     * @param address - the address where to put data
     * @param data - the data to put
     */
    public void putData(int address, boolean[] data) {
        this.data[address] = data;
    }

    /**Static method to read the storage from a file.
     *
     * @param filepath - filepath to read the Storage from
     * @return - the read Storage
     */
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

    /**Save the Storage to its filepath.
     */
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

    /**Define the equals method to check if two storages are equal.
     *
     * @param o - the other object to check if equal to "this"
     * @return - true if o.equals(this), false otherwise
     */
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

    /**Define the toString method.
     *
     * @return - a String representation of this
     */
    @Override
    public String toString() {
        return "Storage<" + this.data.length + ", " + this.data[0].length +">";
    }
}
