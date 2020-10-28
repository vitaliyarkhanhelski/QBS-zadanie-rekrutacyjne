package com.example.qbs;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class FileService {
    /**
     * jedyna publiczna metoda, od której zaczyna się logika
     * sprawdzamy czy ciągi bajtów w systemie dziesiątkowym są poprawne i nie są takie same,
     * jeśli tak, przekazujemy dane do zmiany plików
     * zwraca Komunikat dla użytkownika
     */
    public String changeByteArrayInFiles(String path, String extension, String toRemove, String toAdd) {
        if (!checkIfStringIsByteArray(toRemove) || !checkIfStringIsByteArray(toAdd)) {
            return "Format kodu dziesiątkowego jest nieprawidłowy";
        }
        if (toRemove.equals(toAdd)) return "Ciągi bajtów są takie same";
        List<Byte> bytesToRemove = convertStringToByteArray(toRemove);
        List<Byte> bytesToAdd = convertStringToByteArray(toAdd);
        return changeBytesInFiles(path, extension, bytesToRemove, bytesToAdd);
    }


    /**
     * jeśli pliki o podanym rozszerzeniu istnieją tworzymy listę tych plików
     * i próbujemy zmienić ciąg bajtów w każdym pliku
     * zwraca odpowiedni Komunikat do użytkownika
     */
    private static String changeBytesInFiles(String path, String extension, List<Byte> bytesToRemove, List<Byte> bytesToAdd) {
        boolean fileWasChanged = false;
        int counter = 0;

        List<File> emptyList = new ArrayList<>();
        List<File> files = getMatchingFilesList(path, extension, emptyList);

        if (files.size() == 0)
            return "Żaden plik z rozszerzeniem '" + extension + "' nie istnieje w katalogu " + "'" + path + "'";
        else {
            for (File file : files) {
                boolean result = changeBytesInOneFile(file.getAbsolutePath(), bytesToRemove, bytesToAdd);
                if (result) {
                    fileWasChanged = true;
                    counter++;
                }
            }
        }
        return fileWasChanged ? userMessage(counter) : "Żaden plik nie zawiera podany ciąg bajtów";
    }


    /**
     * dokonuje zmiany bajtów w jednym pliku o podanych parametrach
     * zwraca false w przypadku gdy ciąg bytów nie został odnalziony i zamieniony
     */
    private static boolean changeBytesInOneFile(String absolutePath, List<Byte> bytesToRemove, List<Byte> bytesToAdd) {
        boolean flag = false;

        try {
            byte[] bytes = Files.readAllBytes(Paths.get(absolutePath));
            List<Byte> byteContent = new ArrayList<>();
            for (Byte i : bytes) {
                byteContent.add(i);
            }
            while (Collections.indexOfSubList(byteContent, bytesToRemove) != -1) {
                flag = true;
                int start = Collections.indexOfSubList(byteContent, bytesToRemove);
                byteContent.subList(start, start + bytesToRemove.size()).clear();
                byteContent.addAll(start, bytesToAdd);
            }

            byte[] byteArray = new byte[byteContent.size()];
            for (int i = 0; i < byteContent.size(); i++) {
                byteArray[i] = byteContent.get(i);
            }

            File file = new File(absolutePath);
            OutputStream os = new FileOutputStream(file);
            os.write(byteArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }


    /**
     * Konwertuje String w tablicę bytów
     */
    private static List<Byte> convertStringToByteArray(String stringToConvert) {
        String[] strings = stringToConvert.split(", ");
        List<Byte> bytes = new ArrayList<>();

        for (String i : strings)
            bytes.add(Byte.valueOf(i));
        return bytes;
    }


    /**
     * Sprawdzamy czy String zawiera poprawny ciąg bajtów w systemie dziesiątkowym
     */
    private static boolean checkIfStringIsByteArray(String stringToCheck) {
        String[] arrayToRemove = stringToCheck.split(", ");

        for (String i : arrayToRemove) {
            try {
                byte d = Byte.parseByte(i);
            } catch (NumberFormatException nfe) {
                return false;
            }
        }
        return true;
    }


    /**
     * szuka za pomocą rekursji czy w ogóle mamy pliki o podanym rozszerzeniu
     * w podanym katalogu oraz podkatalogu
     */
    private static List<File> getMatchingFilesList(String path, String extension, List<File> filesList) {
        File directoryPath = new File(path);
        File[] filesArray = directoryPath.listFiles();

        if (filesArray != null)
            for (File file : filesArray) {
                if (file.isFile() && file.getName().endsWith("." + extension)) {
                    filesList.add(file);
                } else if (file.isDirectory()) {
                    getMatchingFilesList(file.getAbsolutePath(), extension, filesList);
                }
            }
        return filesList;
    }


    /**
     * zwraca odpowiedni komunikat dla użytkownika wraz z ilością zmodyfikowanych plików
     */
    private static String userMessage(int n) {
        if (n % 10 == 1) return n + " Plik został pomyślnie zmodyfikowany";
        if (n % 10 == 2 || n % 10 == 3 || n % 10 == 4) return n + " Pliki zostały pomyślnie zmodyfikowane";
        else return n + " Plików zostały pomyślnie zmodyfikowane";
    }

}
