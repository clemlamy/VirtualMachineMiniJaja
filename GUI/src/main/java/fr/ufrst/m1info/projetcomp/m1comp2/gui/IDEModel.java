package fr.ufrst.m1info.projetcomp.m1comp2.gui;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class IDEModel {

    public void saveFile(String text, String path) {
        try {
            PrintWriter writer;
            writer = new PrintWriter(path);
            writer.print(text);
            writer.close();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public IOResult<TextFile> openFile(Path file) {
        try {
            List<String> lines = Files.readAllLines(file);
            return new IOResult<>(true, new TextFile(file, lines));
        } catch (IOException e) {
            e.printStackTrace();
            return new IOResult<>(false, null);
        }
    }

    public void closeIDE() {
        System.exit(0);
    }
}
