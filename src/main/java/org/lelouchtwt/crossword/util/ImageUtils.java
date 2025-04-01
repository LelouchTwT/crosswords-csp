package org.lelouchtwt.crossword.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageUtils {
    public static void saveGridAsImage(String path, char[][] grid, int xSize, int ySize, long elapsedMillis) {
        int cellSize = 40;
        int width = xSize * cellSize;
        int height = ySize * cellSize;
        int titleHeight = 60;
        int footerHeight = 40;
        int totalHeight = height + titleHeight + footerHeight;

        BufferedImage image = new BufferedImage(width, totalHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, totalHeight);

        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 24));
        String title = "Crossword " + xSize + "x" + ySize;
        FontMetrics titleMetrics = g.getFontMetrics();
        int titleX = (width - titleMetrics.stringWidth(title)) / 2;
        int titleY = titleMetrics.getAscent() + 15;
        g.drawString(title, titleX, titleY);

        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        FontMetrics metrics = g.getFontMetrics();

        for (int y = 0; y < ySize; y++) {
            for (int x = 0; x < xSize; x++) {
                int posX = x * cellSize;
                int posY = titleHeight + y * cellSize;

                g.setColor(Color.BLACK);
                g.drawRect(posX, posY, cellSize, cellSize);

                char c = grid[y][x];
                if (c == '.') {
                    g.setColor(Color.BLACK);
                    g.fillRect(posX, posY, cellSize, cellSize);
                } else if (c != '?') {
                    g.setColor(Color.BLACK);
                    int textX = posX + (cellSize - metrics.charWidth(c)) / 2;
                    int textY = posY + ((cellSize - metrics.getHeight()) / 2) + metrics.getAscent();
                    g.drawString(String.valueOf(c), textX, textY);
                }
            }
        }

        g.setFont(new Font("SansSerif", Font.PLAIN, 16));
        String timeStr = "Execution time: " + elapsedMillis + " ms";
        FontMetrics footerMetrics = g.getFontMetrics();
        int footerX = (width - footerMetrics.stringWidth(timeStr)) / 2;
        int footerY = totalHeight - 10;
        g.drawString(timeStr, footerX, footerY);

        g.dispose();

        try {
            ImageIO.write(image, "jpg", new File(path));
            System.out.println("Imagem salva em: " + path);
        } catch (IOException e) {
            System.err.println("Erro ao salvar imagem: " + path);
            e.printStackTrace();
        }
    }
}
