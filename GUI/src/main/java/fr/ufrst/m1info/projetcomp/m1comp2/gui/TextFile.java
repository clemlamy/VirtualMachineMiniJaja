package fr.ufrst.m1info.projetcomp.m1comp2.gui;

import java.nio.file.Path;
import java.util.List;

public class TextFile {

    private final Path file;

    private final List<String> content;

    public TextFile(Path file, List<String> content) {
        this.file = file;
        this.content = content;
    }

    public Path getFile() {
        return file;
    }

    public List<String> getContent() {
        return content;
    }
}
