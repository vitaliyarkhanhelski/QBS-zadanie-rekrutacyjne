package com.example.qbs.exceptons;

public class FileDoesntContainThisByteCode extends Exception{
    public FileDoesntContainThisByteCode(String message) {
        super(message);
    }
}
