package org.lelouchtwt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrosswordsFiller {
    int xSize, ySize;
    char[][] grid;
    List<Slot> slots = new ArrayList<>();
    List<String> dictionary;

    Map<Slot, List<String>> domains = new HashMap<>();

public CrosswordsFiller(int xSize, int ySize, ArrayList<String> input, List<String> words){
    this.xSize = xSize;
    this.ySize = ySize;
    this.grid = new char[xSize][ySize];
    this.dictionary = words;
    setGrid(input);
    setSlots();
}

    private void setSlots(){
        for (int i = 0; i < ySize; i++){
            int j = 0;
            while (j < xSize){
                if (grid[i][j] == '.'){
                    j++;
                    continue;
                }
                int length = 0;
                while (j < xSize && grid[i][j] != '.'){
                    length++;
                    j++;
                }
                if (length > 1){
                    slots.add(new Slot(j-length, i, length, true));
                }
            }
        }
        for (int i = 0; i < xSize; i++){
            int j = 0;
            while (j < ySize){
                if (grid[j][i] == '.'){
                    j++;
                    continue;
                }
                int length = 0;
                while (j < ySize && grid[j][i] != '.'){
                    length++;
                    j++;
                }
                if (length > 1){
                    slots.add(new Slot(i, j-length, length, false));
                }
            }
        }
    }

    private void setGrid(ArrayList<String> input){
        for (int i = 0; i < ySize; i++)
            grid[i] = input.get(i).toCharArray();
    }
}
