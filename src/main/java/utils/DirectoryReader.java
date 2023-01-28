package utils;

import com.google.common.collect.Maps;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class DirectoryReader {

    private final File directory;

    public DirectoryReader(File directory) {
        this.directory = directory;
    }

    private final LinkedHashMap<String, Object> contents = Maps.newLinkedHashMap();

    public synchronized void read() {
        if (directory == null)
            try {
                throw new FileNotFoundException();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        if (!directory.isDirectory())
            throw new RuntimeException("File is not directory");

    }

    public static String removeFileExtension(String file) {
        return file != null && file.lastIndexOf(".") > 0 ? file.substring(0, file.lastIndexOf(".")) : file;
    }
}
