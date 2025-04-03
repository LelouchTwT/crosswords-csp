package org.lelouchtwt.crossword.util;

public class GridPrinter {
    private GridPrinter() {}

    public static void print(char[][] grid) {
        for (char[] row : grid) {
            for (char c : row) System.out.print(c + " ");
            System.out.println();
        }
    }
}