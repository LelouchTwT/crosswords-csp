package org.lelouchtwt.crossword.heuristic;

import java.util.List;

public interface Heuristic<T> {
    void apply(List<T> list);
}