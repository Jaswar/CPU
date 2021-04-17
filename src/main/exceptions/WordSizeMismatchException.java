package main.exceptions;

import main.utils.FileStorageManager;

public class WordSizeMismatchException extends RuntimeException {

    public WordSizeMismatchException(FileStorageManager manager) {
        super("Word size mismatch detected when reading file " + manager.getFilepath());
    }

    public WordSizeMismatchException(String message) {
        super(message);
    }

}
