package org.lelouchtwt.crossword.heuristic;

import org.lelouchtwt.crossword.model.WordSlot;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MRVHeuristicImpl implements Heuristic<WordSlot> {
    private final Map<WordSlot, List<String>> domains;

    public MRVHeuristicImpl(Map<WordSlot, List<String>> domains) {
        this.domains = domains;
    }

    @Override
    public void apply(List<WordSlot> slots) {
        slots.sort(Comparator.comparingInt(s -> domains.get(s).size()));
    }
}