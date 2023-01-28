package utils.files;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.reflections.vfs.Vfs;
import utils.DirectoryReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class Directory extends File {


    private final File file;

    public Directory(String parent, @NotNull String child) {
        super(parent, child);
        this.file = this;
        indexChildren();
    }

    public Directory(String parent, @NotNull File child) {
        this(parent, child.getName());
    }

    @SneakyThrows
    public Directory(File file) {
        this(file.getAbsolutePath(), file.getName());
    }

    private final LinkedHashMap<String, File> children = new LinkedHashMap<>();
    private final LinkedHashMap<String, File> childrenWithExtension = new LinkedHashMap<>();


    void indexChildren() {
        if (this.file.isDirectory()) {
            File[] files = file.listFiles();

            for (File content : files) {
                if (!content.exists() || content == null) continue;
                children.put(DirectoryReader.removeFileExtension(content.getName()), content);
                childrenWithExtension.put(content.getName(), content);
            }
        }
    }

    @SneakyThrows
    public File getChild(String name) {
        if (!children.containsKey(name))
            throw new FileNotFoundException();


        return children.get(name);
    }

    @SneakyThrows
    public File getSpecificChild(String name) {
        if (!childrenWithExtension.containsKey(name))
            throw new FileNotFoundException();


        return childrenWithExtension.get(name);
    }

    public boolean hasSpecificChild(String name) {

        return childrenWithExtension.containsKey(name);
    }

    public boolean hasChild(String name) {

        return children.containsKey(name);
    }

    static class InvalidDirectoryException extends Exception {

        public InvalidDirectoryException() {
            super("file is not a directory");
        }

        public InvalidDirectoryException(String msg) {
            super(msg);
        }

    }

}
