package org.lelouchtwt.crossword.heuristic;

import org.lelouchtwt.crossword.model.WordSlot;

import java.util.Comparator;
import java.util.List;
import java.util.function.ToIntBiFunction;

public class LCVHeuristicImpl implements Heuristic<String> {
    private final WordSlot slot;
    private final ToIntBiFunction<WordSlot, String> countFunc;

    public LCVHeuristicImpl(WordSlot slot, ToIntBiFunction<WordSlot, String> countFunc) {
        this.slot = slot;
        this.countFunc = countFunc;
    }

    @Override
    public void apply(List<String> domain) {
        domain.sort(Comparator.comparingInt(word -> countFunc.applyAsInt(slot, word)));
    }
}