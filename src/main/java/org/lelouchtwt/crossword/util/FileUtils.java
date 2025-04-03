package org.lelouchtwt.crossword.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FileUtils {
    private static final Logger logger = Logger.getLogger(FileUtils.class.getName());

    private FileUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static List<String> readLines(String path) {
        List<String> lines = new ArrayList<>();
        TimerUtils.time("Reading lines from: " + path, () -> {
            try (BufferedReader br = new BufferedReader(new FileReader(path))) {
                String line;
                while ((line = br.readLine()) != null) {
                    lines.add(line.trim());
                }
            } catch (IOException e) {
                logger.severe("Error reading file: " + e.getMessage());
            }
        }, logger);
        return lines;
    }

    public static void saveGridToFile(String path, char[][] grid) {
        TimerUtils.time("Saving grid to file: " + path, () -> {
            try (PrintWriter writer = new PrintWriter(path)) {
                for (char[] row : grid) {
                    writer.println(new String(row));
                }
            } catch (IOException e) {
                logger.severe("Error saving grid: " + e.getMessage());
            }
        }, logger);
    }
}
