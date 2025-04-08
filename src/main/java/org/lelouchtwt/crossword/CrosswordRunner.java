package org.lelouchtwt.crossword;
import org.lelouchtwt.crossword.util.FileUtils;

import java.util.List;

public class CrosswordRunner {
    public static void main(String[] args) {
        if(args.length != 2){
            System.out.println("Argumentos invalidos");
            return;
        }

        List<String> gridLines = FileUtils.readLines(args[0]);
        List<String> words = FileUtils.readLines(args[1]);

        CrosswordBuilder builder = new CrosswordBuilder(gridLines, words);
        builder.build();
    }
}