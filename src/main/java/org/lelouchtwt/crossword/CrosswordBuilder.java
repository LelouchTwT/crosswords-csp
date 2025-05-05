package org.lelouchtwt.crossword;

import me.tongfei.progressbar.ProgressBar;
import org.lelouchtwt.crossword.core.DomainManager;
import org.lelouchtwt.crossword.core.SlotIdentifier;
import org.lelouchtwt.crossword.model.WordSlot;
import org.lelouchtwt.crossword.util.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class CrosswordBuilder {
    private static final Logger logger = Logger.getLogger(CrosswordBuilder.class.getName());

    private final int width;
    private final int height;
    private final char[][] grid;
    private final List<WordSlot> slots;
    private final List<String> dictionary;
    private final Map<WordSlot, List<String>> domains;
    private final BitSet usedWords = new BitSet();
    private final Map<String, Integer> wordIndex = new HashMap<>();
    private final Map<String, List<WordSlot>> positionToSlots = new HashMap<>();
    private int steps = 0;
    private ProgressBar pb;

    public CrosswordBuilder(List<String> input, List<String> words) {
        this.height = input.size();
        this.width = input.get(0).length();
        this.grid = new char[height][width];
        this.dictionary = words;

        for (int i = 0; i < dictionary.size(); i++) {
            wordIndex.put(dictionary.get(i), i);
        }

        setGrid(input);
        SlotIdentifier slotIdentifier = new SlotIdentifier(grid, width, height, positionToSlots);
        this.slots = slotIdentifier.identifySlots();
        DomainManager domainManager = new DomainManager(dictionary);
        this.domains = domainManager.initializeDomains(slots);
        applyMRVHeuristic(slots);
    }

    public void build() {
        LoggingUtils.configureLogger(logger, Boolean.getBoolean("debug"));
        pb = new ProgressBar("Building Crossword", slots.size());
        TimerUtils.time("Crossword building", () -> {
            pb.setExtraMessage("Building...");
            boolean success = backtrack(0);
            if (success) {
                pb.close();
                logger.info("✅ Solution found!");
                GridPrinter.print(grid);
                FileUtils.saveGridToFile("./crossword.txt", grid);
            } else {
                logger.warning("❌ No solution found.");
            }
            logger.info("Recursive steps: " + steps);
        }, logger);
    }

    private void setGrid(List<String> input) {
        for (int i = 0; i < height; i++) {
            grid[i] = input.get(i).toCharArray();
        }
    }

    private boolean backtrack(int index) {
        steps++;

        pb.step();

        if (index == slots.size()) return true;

        WordSlot slot = slots.get(index);
        List<String> domain = new ArrayList<>(domains.get(slot));

        for (String word : domain) {
            int idx = wordIndex.get(word);
            if (!usedWords.get(idx) && isValidPlacement(slot, word)) {
                assignWord(slot, word);
                slot.setWord(word);
                usedWords.set(idx);

                Map<WordSlot, List<String>> modifiedDomains = new HashMap<>();
                boolean success = forwardCheck(index + 1, modifiedDomains) && backtrack(index + 1);
                if (success) return true;

                domains.putAll(modifiedDomains);
                unassignWord(slot);
                slot.setWord(null);
                usedWords.clear(idx);
            }
        }
        return false;
    }


    private boolean forwardCheck(int startIndex, Map<WordSlot, List<String>> modifiedDomains) {
        for (int i = startIndex; i < slots.size(); i++) {
            WordSlot slot = slots.get(i);
            List<String> currentDomain = domains.get(slot);

            List<String> filtered = currentDomain.parallelStream().filter(word -> isValidPlacement(slot, word)).toList();

            if (filtered.isEmpty()) {
                return false;
            } else if (filtered.size() < currentDomain.size()) {
                modifiedDomains.put(slot, currentDomain);
                domains.put(slot, filtered);
            }
        }
        applyMRVHeuristic(slots.subList(startIndex, slots.size()));
        return true;
    }

    private boolean isValidPlacement(WordSlot slot, String word) {
        int x = slot.getX();
        int y = slot.getY();
        for (int i = 0; i < slot.getLength(); i++) {
            char current = slot.isHorizontal() ? grid[y][x + i] : grid[y + i][x];
            if (current != Constants.EMPTY && current != word.charAt(i)) return false;
        }
        return true;
    }

    private void assignWord(WordSlot slot, String word) {
        int x = slot.getX();
        int y = slot.getY();
        for (int i = 0; i < slot.getLength(); i++) {
            int row = slot.isHorizontal() ? y : y + i;
            int col = slot.isHorizontal() ? x + i : x;
            grid[row][col] = word.charAt(i);
        }
    }

    private void unassignWord(WordSlot slot) {
        int x = slot.getX();
        int y = slot.getY();
        for (int i = 0; i < slot.getLength(); i++) {
            int row = slot.isHorizontal() ? y : y + i;
            int col = slot.isHorizontal() ? x + i : x;
            if (!conflictAt(row, col, slot)) {
                grid[row][col] = Constants.EMPTY;
            }
        }
    }

    private boolean conflictAt(int row, int col, WordSlot currentSlot) {
        String key = row + "," + col;
        List<WordSlot> occupyingSlots = positionToSlots.getOrDefault(key, new ArrayList<>());
        for (WordSlot slot : occupyingSlots) {
            if (slot != currentSlot && slot.getWord() != null) {
                return true;
            }
        }
        return false;
    }

    private void applyMRVHeuristic(List<WordSlot> slotsToSort) {
        slotsToSort.sort(Comparator.comparingInt(slot -> domains.get(slot).size()));
    }
}