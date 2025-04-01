package org.lelouchtwt;

import java.util.*;

public class CrosswordsSolver {
    private final int xSize;
    private final int ySize;
    private final char[][] grid;
    private final List<Slot> slots = new ArrayList<>();
    private final List<String> dictionary;
    private final Map<Slot, List<String>> domains = new HashMap<>();
    private final Set<String> usedWords = new HashSet<>();

    private final Map<String, List<Slot>> positionToSlots = new HashMap<>();


    private int steps = 0;

    public CrosswordsSolver(int xSize, int ySize, ArrayList<String> input, List<String> words) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.grid = new char[ySize][xSize];
        this.dictionary = words;
        setGrid(input);
        setSlots();
    }

    public void run() {
        initDomains();
        boolean success = backtrack(0);
        if (success) {
            System.out.println("Solução encontrada!");
            printGrid();
        } else {
            System.out.println("Nenhuma solução encontrada.");
        }
        System.out.println("Passos (recursão): " + steps);
    }

    private void setGrid(ArrayList<String> input) {
        for (int i = 0; i < ySize; i++) {
            grid[i] = input.get(i).toCharArray();
        }
    }

    private void setSlots() {
        setHorizontalSlots();
        setVerticalSlots();
    }

    private void setHorizontalSlots(){
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
                    Slot slot = new Slot(start, y, length, true);
                    slots.add(slot);
                    registerSlotPositions(slot);
                }
            }
        }
    }

    private void setVerticalSlots(){
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
                    Slot slot = new Slot(x, start, length, false);
                    slots.add(slot);
                    registerSlotPositions(slot);
                }
            }
        }
    }

    private void registerSlotPositions(Slot slot) {
        for (int i = 0; i < slot.getLength(); i++) {
            int x = slot.isHorizontal() ? slot.getX() + i : slot.getX();
            int y = slot.isHorizontal() ? slot.getY() : slot.getY() + i;
            String key = y + "," + x;

            positionToSlots.computeIfAbsent(key, k -> new ArrayList<>()).add(slot);
        }
    }


    private void initDomains() {
        for (Slot slot : slots) {
            List<String> domain = new ArrayList<>();
            for (String word : dictionary) {
                if (word.length() == slot.getLength()) {
                    domain.add(word.toUpperCase());
                }
            }
            domains.put(slot, domain);
        }
        // Ordenar os slots por tamanho do domínio (MRV)
        slots.sort(Comparator.comparingInt(s -> domains.get(s).size()));

    }

    private boolean backtrack(int index) {
        steps++;
        double progress = (index * 100.0) / slots.size();
        System.out.printf("Progresso: %.1f%% (%d/%d slots preenchidos)\n", progress, index, slots.size());

        if (index == slots.size())
            return true;

        Slot slot = slots.get(index);
        System.out.println(slot.getLength());
        List<String> originalDomain = domains.get(slot);

        for (String word : originalDomain) {
            if (!usedWords.contains(word) && canPlace(slot, word)) {
                placeWord(slot, word);
                slot.setWord(word);
                usedWords.add(word);

                Map<Slot, List<String>> backup = new HashMap<>();
                for (int i = index + 1; i < slots.size(); i++) {
                    Slot futureSlot = slots.get(i);
                    backup.put(futureSlot, new ArrayList<>(domains.get(futureSlot)));
                }

                if (forwardCheck(index + 1)) {
                    if (backtrack(index + 1))
                        return true;
                }

                for (int i = index + 1; i < slots.size(); i++) {
                    domains.put(slots.get(i), backup.get(slots.get(i)));
                }

                removeWord(slot);
                slot.setWord(null);
                usedWords.remove(word);
            }
        }
        return false;
    }


    private boolean forwardCheck(int startIndex) {
        for (int i = startIndex; i < slots.size(); i++) {
            Slot slot = slots.get(i);
            List<String> domain = domains.get(slot);
            List<String> newDomain = new ArrayList<>();

            for (String word : domain) {
                if (canPlace(slot, word)) {
                    newDomain.add(word);
                }
            }

            if (newDomain.isEmpty()) {
                return false;
            }

            domains.put(slot, newDomain);
        }
        return true;
    }

    private boolean canPlace(Slot slot, String word) {
        int x = slot.getX();
        int y = slot.getY();
        for (int i = 0; i < slot.getLength(); i++) {
            char current = slot.isHorizontal() ? grid[y][x + i] : grid[y + i][x];
            if (current != '?' && current != word.charAt(i))
                return false;
        }
        return true;
    }

    private void placeWord(Slot slot, String word) {
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

    private void removeWord(Slot slot) {
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

    private boolean conflictAt(int row, int col, Slot currentSlot) {
        String key = row + "," + col;
        List<Slot> occupyingSlots = positionToSlots.getOrDefault(key, new ArrayList<>());
        for (Slot slot : occupyingSlots) {
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
