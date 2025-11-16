package com.cgvsu.rasterization;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

public class Rasterization {

    public static void drawRectangle(
            final GraphicsContext graphicsContext,
            final int x, final int y,
            final int width, final int height,
            final Color color)
    {
        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();

        for (int row = y; row < y + height; ++row)
            for (int col = x; col < x + width; ++col)
                pixelWriter.setColor(col, row, color);
    }

    public static void drawWuLine(
            final GraphicsContext graphicsContext,
            final int x1, final int y1, final Color color1,
            final int x2, final int y2, final Color color2)
    {
        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();

        if (x1 == x2 && y1 == y2) {
            pixelWriter.setColor(x1, y1, color1);
            return;
        }

        boolean steep = Math.abs(y2 - y1) > Math.abs(x2 - x1);

        int _x1 = x1, _y1 = y1, _x2 = x2, _y2 = y2;
        Color _color1 = color1, _color2 = color2;

        if (steep) {
            _x1 = y1; _y1 = x1;
            _x2 = y2; _y2 = x2;
        }

        if (_x1 > _x2) {
            int temp = _x1; _x1 = _x2; _x2 = temp;
            temp = _y1; _y1 = _y2; _y2 = temp;
            Color tempColor = _color1; _color1 = _color2; _color2 = tempColor;
        }

        int dx = _x2 - _x1;
        int dy = _y2 - _y1;
        float gradient = dx == 0 ? 1 : (float) dy / dx;

        float intery = _y1 + gradient;

        drawWuPixel(pixelWriter, steep, _x1, _y1, _color1, 1.0f);

        for (int x = _x1 + 1; x < _x2; x++) {
            Color currentColor = interpolateColor(_color1, _color2, x - _x1, dx);

            drawWuPixel(pixelWriter, steep, x, (int) intery, currentColor, 1 - fractionalPart(intery));
            drawWuPixel(pixelWriter, steep, x, (int) intery + 1, currentColor, fractionalPart(intery));

            intery += gradient;
        }

        drawWuPixel(pixelWriter, steep, _x2, _y2, _color2, 1.0f);
    }

    private static void drawWuPixel(PixelWriter pixelWriter, boolean steep,
                                    int x, int y, Color color, float brightness) {
        if (brightness <= 0) return;

        if (steep) {
            plot(pixelWriter, y, x, color, brightness);
        } else {
            plot(pixelWriter, x, y, color, brightness);
        }
    }

    private static void plot(PixelWriter pixelWriter, int x, int y, Color color, float brightness) {
        double r = color.getRed() * brightness;
        double g = color.getGreen() * brightness;
        double b = color.getBlue() * brightness;

        pixelWriter.setColor(x, y, Color.color(
                Math.max(0, Math.min(1, r)),
                Math.max(0, Math.min(1, g)),
                Math.max(0, Math.min(1, b))
        ));
    }

    private static float fractionalPart(float x) {
        return x - (float) Math.floor(x);
    }

    private static Color interpolateColor(Color color1, Color color2, int position, int total) {
        if (total == 0) return color1;

        float t = (float) position / total;

        double r = color1.getRed() * (1 - t) + color2.getRed() * t;
        double g = color1.getGreen() * (1 - t) + color2.getGreen() * t;
        double b = color1.getBlue() * (1 - t) + color2.getBlue() * t;

        return Color.color(
                Math.max(0, Math.min(1, r)),
                Math.max(0, Math.min(1, g)),
                Math.max(0, Math.min(1, b))
        );
    }
}