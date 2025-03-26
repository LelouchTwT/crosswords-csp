package org.lelouchtwt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ArrayList<String> map = new ArrayList<>();
        List<String> words = new ArrayList<>();
        int xSize = 0;
        int ySize = 0;
        try {

            String path = "/Users/joaopedroantoniazitonello/Documents/testes/grid-11x11-20W-83L-38B.txt";
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null){
                map.add(line);
                //System.out.println(line);
                line = br.readLine();
            }
            xSize =map.getFirst().length();
            ySize = map.size();
            System.out.println(map.getFirst().length());
            System.out.println(map.size());
        } catch (IOException e) {
            e.printStackTrace();
        }

        CrosswordsFiller crossword = new CrosswordsFiller(xSize,ySize,map, words);
        try {

            String path = "/Users/joaopedroantoniazitonello/Documents/lista_palavras.txt";
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null){
                words.add(line);
                //System.out.println(line);
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("aaa");
    }
}