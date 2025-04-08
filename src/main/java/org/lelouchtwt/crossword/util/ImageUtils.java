package org.lelouchtwt.crossword.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class ImageUtils {
    private static final Logger logger = Logger.getLogger(ImageUtils.class.getName());

    private ImageUtils(){
        throw new IllegalStateException("Utility class");
    }

    public static void saveGridAsImage(String path, char[][] grid, int width, int height, long elapsedMillis) {
        TimerUtils.time("Rendering grid image", () -> {
            int cellSize = 40;
            int imgWidth = width * cellSize;
            int imgHeight = height * cellSize;
            int titleHeight = 60;
            int footerHeight = 40;
            int totalHeight = imgHeight + titleHeight + footerHeight;

            BufferedImage image = new BufferedImage(imgWidth, totalHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, imgWidth, totalHeight);

            g.setColor(Color.BLACK);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
            String title = "Crossword " + width + "x" + height;
            FontMetrics titleMetrics = g.getFontMetrics();
            int titleX = (imgWidth - titleMetrics.stringWidth(title)) / 2;
            int titleY = titleMetrics.getAscent() + 15;
            g.drawString(title, titleX, titleY);

            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
            FontMetrics metrics = g.getFontMetrics();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
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

            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
            String timeStr = "Execution time: " + elapsedMillis + " ms";
            int footerX = (imgWidth - g.getFontMetrics().stringWidth(timeStr)) / 2;
            int footerY = totalHeight - 10;
            g.drawString(timeStr, footerX, footerY);

            g.dispose();

            try {
                ImageIO.write(image, "jpg", new File(path));
                logger.info("Image saved to: " + path);
            } catch (IOException e) {
                logger.severe("Error saving image: " + e.getMessage());
            }
        }, logger);
    }
}
