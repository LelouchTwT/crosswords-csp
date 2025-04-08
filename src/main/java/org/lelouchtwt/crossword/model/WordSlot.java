package org.lelouchtwt.crossword.model;

import java.util.Objects;

public class WordSlot {
    private final int x;
    private final int y;
    private final int length;
    private final boolean horizontal;
    private String word;

    public WordSlot(int x, int y, int length, boolean horizontal) {
        this.x = x;
        this.y = y;
        this.length = length;
        this.horizontal = horizontal;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getLength() { return length; }
    public boolean isHorizontal() { return horizontal; }
    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }

    @Override
    public String toString() {
        return String.format("(%d,%d,%d,%s)", x, y, length, horizontal ? "H" : "V");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordSlot that = (WordSlot) o;
        return x == that.x &&
                y == that.y &&
                length == that.length &&
                horizontal == that.horizontal;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, length, horizontal);
    }
}
