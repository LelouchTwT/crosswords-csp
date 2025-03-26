package org.lelouchtwt;

public class Slot {
    int x, y, length;
    boolean horizontal;
    String word;

    public Slot(int x, int y, int length, boolean horizontal) {
        this.x = x;
        this.y = y;
        this.length = length;
        this.horizontal = horizontal;
    }
}
