package org.lelouchtwt.crossword;
import org.lelouchtwt.crossword.util.FileUtils;

import java.util.List;

public class CrosswordRunner {
    public static void main(String[] args) {
        List<String> gridLines = FileUtils.readLines("/Users/joaopedroantoniazitonello/Documents/testes/grid-25x25-88W-400L-225B.txt");
        List<String> words = FileUtils.readLines("/Users/joaopedroantoniazitonello/Documents/lista_palavras.txt");

        CrosswordBuilder builder = new CrosswordBuilder(gridLines, words);
        builder.build();
    }
}