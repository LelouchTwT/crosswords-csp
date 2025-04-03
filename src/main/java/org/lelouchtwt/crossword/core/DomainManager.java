package org.lelouchtwt.crossword.core;

import org.lelouchtwt.crossword.model.WordSlot;

import java.util.*;
import java.util.stream.Collectors;

public class DomainManager {
    private final List<String> dictionary;
    private final Map<WordSlot, List<String>> domains = new HashMap<>();

    public DomainManager(List<String> dictionary) {
        this.dictionary = dictionary;
    }

    public Map<WordSlot, List<String>> initializeDomains(List<WordSlot> slots) {
        Map<Integer, List<String>> domainCache = new HashMap<>();
        for (WordSlot slot : slots) {
            int length = slot.getLength();
            List<String> filtered = domainCache.computeIfAbsent(length, len ->
                    dictionary.parallelStream().filter(word -> word.length() == len).collect(Collectors.toList())
            );
            domains.put(slot, new ArrayList<>(filtered));
        }
        return domains;
    }

    public Map<WordSlot, List<String>> getDomains() {
        return domains;
    }
} 