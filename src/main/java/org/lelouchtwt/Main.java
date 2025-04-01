package org.lelouchtwt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        String gridPath = "/Users/joaopedroantoniazitonello/Documents/testes/grid-25x25-88W-400L-225B.txt";
        String wordsPath = "/Users/joaopedroantoniazitonello/Documents/lista_palavras.txt";
        List<String> gridLines = readLinesFromFile(gridPath);
        List<String> wordList = readLinesFromFile(wordsPath);

        int xSize = gridLines.getFirst().length();
        int ySize = gridLines.size();

        CrosswordsSolver crossword = new CrosswordsSolver(xSize, ySize, new ArrayList<>(gridLines), wordList);
        crossword.run();

        long stopTime = System.currentTimeMillis();
        System.out.printf("Execution time: %d ms", stopTime - startTime);
    }

    private static List<String> readLinesFromFile(String path) {
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
}