package com.example.qbs.exceptons;

public class FileWithThisExtensionDoesntExist extends Exception{
    public FileWithThisExtensionDoesntExist(String message) {
        super(message);
    }
}
