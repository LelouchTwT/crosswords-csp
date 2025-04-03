package org.lelouchtwt.crossword.core;

import org.lelouchtwt.crossword.model.WordSlot;
import org.lelouchtwt.crossword.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SlotIdentifier {
    private final char[][] grid;
    private final int width;
    private final int height;
    private final List<WordSlot> slots = new ArrayList<>();
    private final Map<String, List<WordSlot>> positionToSlots;

    public SlotIdentifier(char[][] grid, int width, int height, Map<String, List<WordSlot>> positionToSlots) {
        this.grid = grid;
        this.width = width;
        this.height = height;
        this.positionToSlots = positionToSlots;
    }

    public List<WordSlot> identifySlots() {
        setHorizontalSlots();
        setVerticalSlots();
        return slots;
    }

    private void setHorizontalSlots() {
        for (int y = 0; y < height; y++) {
            int x = 0;
            while (x < width) {
                if (grid[y][x] == Constants.BLOCK) {
                    x++;
                    continue;
                }
                int start = x;
                while (x < width && grid[y][x] != Constants.BLOCK) x++;
                int len = x - start;
                if (len > 1) {
                    WordSlot slot = new WordSlot(start, y, len, true);
                    slots.add(slot);
                    registerSlotPositions(slot);
                }
            }
        }
    }

    private void setVerticalSlots() {
        for (int x = 0; x < width; x++) {
            int y = 0;
            while (y < height) {
                if (grid[y][x] == Constants.BLOCK) {
                    y++;
                    continue;
                }
                int start = y;
                while (y < height && grid[y][x] != Constants.BLOCK) y++;
                int len = y - start;
                if (len > 1) {
                    WordSlot slot = new WordSlot(x, start, len, false);
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
}