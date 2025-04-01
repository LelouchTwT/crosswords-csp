package org.lelouchtwt.crossword;

import org.lelouchtwt.crossword.model.WordSlot;
import org.lelouchtwt.crossword.util.FileUtils;
import org.lelouchtwt.crossword.util.ImageUtils;

import java.util.*;
import java.util.List;

public class CrosswordBuilder {
    private final boolean DEBUG = false;
    private final int xSize;
    private final int ySize;
    private final char[][] grid;
    private final List<WordSlot> slots = new ArrayList<>();
    private final List<String> dictionary;
    private final Map<WordSlot, List<String>> domains = new HashMap<>();
    private final Set<String> usedWords = new HashSet<>();
    private final Map<String, List<WordSlot>> positionToSlots = new HashMap<>();
    private int steps = 0;

    public CrosswordBuilder(List<String> input, List<String> words) {
        this.ySize = input.size();
        this.xSize = input.getFirst().length();
        this.grid = new char[ySize][xSize];
        this.dictionary = words;
        setGrid(input);
        identifyWordSlots();
    }

    public void build() {
        long startTime = System.currentTimeMillis();
        initDomains();
        boolean success = backtrack(0);
        if (success) {
            System.out.println("Solução encontrada!");
            printGrid();
            FileUtils.saveGridToFile("./crossword.txt", grid);
            long stopTime = System.currentTimeMillis();
            ImageUtils.saveGridAsImage("./crossword.jpg", grid, xSize, ySize, stopTime - startTime);
        } else {
            System.out.println("Nenhuma solução encontrada.");
        }
        System.out.println("Passos (recursão): " + steps);
    }

    private void setGrid(List<String> input) {
        for (int i = 0; i < ySize; i++) {
            grid[i] = input.get(i).toCharArray();
        }
    }

    private void identifyWordSlots() {
        setHorizontalSlots();
        setVerticalSlots();
    }

    private void setHorizontalSlots() {
        for (int y = 0; y < ySize; y++) {
            int x = 0;
            while (x < xSize) {
                if (grid[y][x] == '.') {
                    x++;
                    continue;
                }
                int start = x;
                while (x < xSize && grid[y][x] != '.') x++;
                int length = x - start;
                if (length > 1) {
                    WordSlot slot = new WordSlot(start, y, length, true);
                    slots.add(slot);
                    registerSlotPositions(slot);
                }
            }
        }
    }

    private void setVerticalSlots() {
        for (int x = 0; x < xSize; x++) {
            int y = 0;
            while (y < ySize) {
                if (grid[y][x] == '.') {
                    y++;
                    continue;
                }
                int start = y;
                while (y < ySize && grid[y][x] != '.') y++;
                int length = y - start;
                if (length > 1) {
                    WordSlot slot = new WordSlot(x, start, length, false);
                    slots.add(slot);
                    registerSlotPositions(slot);
                }
            }
        }
    }

    private void registerSlotPositions(WordSlot slot) {
        for (int i = 0; i < slot.getLength(); i++) {
            int x = slot.isHorizontal() ? slot.getX() + i : slot.getX();
            int y = slot.isHorizontal() ? slot.getY() : slot.getY() + i;
            String key = y + "," + x;
            positionToSlots.computeIfAbsent(key, k -> new ArrayList<>()).add(slot);
        }
    }

    private void initDomains() {
        Map<Integer, List<String>> domainCache = new HashMap<>();
        for (WordSlot slot : slots) {
            int length = slot.getLength();
            domainCache.computeIfAbsent(length, len ->
                    dictionary.stream()
                            .filter(word -> word.length() == len)
                            .toList()
            );
            domains.put(slot, new ArrayList<>(domainCache.get(length)));
        }
        slots.sort(Comparator.comparingInt(s -> domains.get(s).size()));
    }

    private boolean backtrack(int index) {
        steps++;
        double progress = (index * 100.0) / slots.size();
        System.out.printf("Progresso: %.1f%% (%d/%d slots preenchidos) %n", progress, index, slots.size());

        if (index == slots.size())
            return true;

        WordSlot slot = slots.get(index);
        List<String> originalDomain = domains.get(slot);

        for (String word : originalDomain) {
            if (!usedWords.contains(word) && isValidPlacement(slot, word)) {
                if (DEBUG) System.out.printf("Tentando colocar '%s' no slot %s %n", word, slot);
                assignWord(slot, word);
                slot.setWord(word);
                usedWords.add(word);

                Map<WordSlot, List<String>> modifiedDomains = new HashMap<>();
                if (forwardCheck(index + 1, modifiedDomains) && backtrack(index + 1))
                    return true;

                domains.putAll(modifiedDomains);
                unassignWord(slot);
                if (DEBUG) System.out.printf("Removendo '%s' do slot %s %n", word, slot);
                slot.setWord(null);
                usedWords.remove(word);
            }
        }
        return false;
    }

    private boolean forwardCheck(int startIndex, Map<WordSlot, List<String>> modifiedDomains) {
        for (int i = startIndex; i < slots.size(); i++) {
            WordSlot slot = slots.get(i);
            List<String> currentDomain = domains.get(slot);

            List<String> filtered = new ArrayList<>();
            for (String word : currentDomain) {
                if (isValidPlacement(slot, word)) {
                    filtered.add(word);
                }
            }

            if (filtered.isEmpty()) {
                if (DEBUG) System.out.printf("[FC] Slot %s ficou com domínio vazio! %n", slot);
                return false;
            } else if (filtered.size() < currentDomain.size()) {
                if (DEBUG) System.out.printf("[FC] Slot %s: domínio reduzido de %d para %d %n", slot, currentDomain.size(), filtered.size());
                modifiedDomains.put(slot, new ArrayList<>(currentDomain));
            }

            domains.put(slot, filtered);
        }
        slots.subList(startIndex, slots.size()).sort(
                Comparator.comparingInt(s -> domains.get(s).size())
        );
        return true;
    }

    private boolean isValidPlacement(WordSlot slot, String word) {
        int x = slot.getX();
        int y = slot.getY();
        for (int i = 0; i < slot.getLength(); i++) {
            char current = slot.isHorizontal() ? grid[y][x + i] : grid[y + i][x];
            if (current != '?' && current != word.charAt(i))
                return false;
        }
        return true;
    }

    private void assignWord(WordSlot slot, String word) {
        int x = slot.getX();
        int y = slot.getY();
        for (int i = 0; i < slot.getLength(); i++) {
            if (slot.isHorizontal()) {
                grid[y][x + i] = word.charAt(i);
            } else {
                grid[y + i][x] = word.charAt(i);
            }
        }
    }

    private void unassignWord(WordSlot slot) {
        int x = slot.getX();
        int y = slot.getY();
        for (int i = 0; i < slot.getLength(); i++) {
            int row = slot.isHorizontal() ? y : y + i;
            int col = slot.isHorizontal() ? x + i : x;
            if (!conflictAt(row, col, slot)) {
                grid[row][col] = '?';
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

    private void printGrid() {
        for (char[] linha : grid) {
            for (char c : linha) {
                System.out.print(c + " ");
            }
            System.out.println();
        }
    }
}
