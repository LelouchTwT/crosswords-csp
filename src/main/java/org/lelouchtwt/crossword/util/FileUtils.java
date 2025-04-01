package org.lelouchtwt.crossword.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    public static List<String> readLines(String path) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line.trim());
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo: " + path);
            e.printStackTrace();
        }
        return lines;
    }

    public static void saveGridToFile(String path, char[][] grid) {
        try (PrintWriter writer = new PrintWriter(path)) {
            for (char[] row : grid) {
                for (char c : row) {
                    writer.print(c);
                }
                writer.println();
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar grid no arquivo: " + path);
            e.printStackTrace();
        }
    }
}
