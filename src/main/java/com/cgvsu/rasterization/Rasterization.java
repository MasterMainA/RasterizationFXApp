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

    /**
     * Рисует отрезок с алгоритмом Ву с интерполяцией цвета
     * @param graphicsContext графический контекст
     * @param x1 координата X первой точки
     * @param y1 координата Y первой точки
     * @param color1 цвет первой точки
     * @param x2 координата X второй точки
     * @param y2 координата Y второй точки
     * @param color2 цвет второй точки
     */
    public static void drawWuLine(
            final GraphicsContext graphicsContext,
            final int x1, final int y1, final Color color1,
            final int x2, final int y2, final Color color2)
    {
        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();

        // Если точки совпадают, рисуем одну точку
        if (x1 == x2 && y1 == y2) {
            pixelWriter.setColor(x1, y1, color1);
            return;
        }

        // Определяем направление отрезка
        boolean steep = Math.abs(y2 - y1) > Math.abs(x2 - x1);

        int _x1 = x1, _y1 = y1, _x2 = x2, _y2 = y2;
        Color _color1 = color1, _color2 = color2;

        // Если отрезок крутой, меняем координаты
        if (steep) {
            _x1 = y1; _y1 = x1;
            _x2 = y2; _y2 = x2;
        }

        // Упорядочиваем точки по X
        if (_x1 > _x2) {
            int temp = _x1; _x1 = _x2; _x2 = temp;
            temp = _y1; _y1 = _y2; _y2 = temp;
            Color tempColor = _color1; _color1 = _color2; _color2 = tempColor;
        }

        int dx = _x2 - _x1;
        int dy = _y2 - _y1;
        float gradient = dx == 0 ? 1 : (float) dy / dx;

        // Первая точка
        float intery = _y1 + gradient;

        // Рисуем первую точку
        drawWuPixel(pixelWriter, steep, _x1, _y1, _color1, 1.0f);

        // Основной цикл
        for (int x = _x1 + 1; x < _x2; x++) {
            Color currentColor = interpolateColor(_color1, _color2, x - _x1, dx);

            drawWuPixel(pixelWriter, steep, x, (int) intery, currentColor, 1 - fractionalPart(intery));
            drawWuPixel(pixelWriter, steep, x, (int) intery + 1, currentColor, fractionalPart(intery));

            intery += gradient;
        }

        // Рисуем последнюю точку
        drawWuPixel(pixelWriter, steep, _x2, _y2, _color2, 1.0f);
    }

    /**
     * Рисует пиксель с антиалиасингом
     */
    private static void drawWuPixel(PixelWriter pixelWriter, boolean steep,
                                    int x, int y, Color color, float brightness) {
        if (brightness <= 0) return;

        if (steep) {
            // Меняем координаты местами для крутых отрезков
            plot(pixelWriter, y, x, color, brightness);
        } else {
            plot(pixelWriter, x, y, color, brightness);
        }
    }

    /**
     * Рисует отрезок с алгоритмом Ву с увеличенным размером пикселей
     */
    public static void drawWuLineScaled(
            final GraphicsContext graphicsContext,
            final int x1, final int y1, final Color color1,
            final int x2, final int y2, final Color color2,
            final int pixelSize)
    {
        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();

        // Если точки совпадают, рисуем одну точку
        if (x1 == x2 && y1 == y2) {
            drawScaledPixel(pixelWriter, x1, y1, color1, pixelSize);
            return;
        }

        // Определяем направление отрезка
        boolean steep = Math.abs(y2 - y1) > Math.abs(x2 - x1);

        int _x1 = x1, _y1 = y1, _x2 = x2, _y2 = y2;
        Color _color1 = color1, _color2 = color2;

        // Если отрезок крутой, меняем координаты
        if (steep) {
            _x1 = y1; _y1 = x1;
            _x2 = y2; _y2 = x2;
        }

        // Упорядочиваем точки по X
        if (_x1 > _x2) {
            int temp = _x1; _x1 = _x2; _x2 = temp;
            temp = _y1; _y1 = _y2; _y2 = temp;
            Color tempColor = _color1; _color1 = _color2; _color2 = tempColor;
        }

        int dx = _x2 - _x1;
        int dy = _y2 - _y1;
        float gradient = dx == 0 ? 1 : (float) dy / dx;

        // Первая точка
        float intery = _y1 + gradient;

        // Рисуем первую точку
        drawWuPixelScaled(pixelWriter, steep, _x1, _y1, _color1, 1.0f, pixelSize);

        // Основной цикл
        for (int x = _x1 + 1; x < _x2; x++) {
            Color currentColor = interpolateColor(_color1, _color2, x - _x1, dx);

            drawWuPixelScaled(pixelWriter, steep, x, (int) intery, currentColor, 1 - fractionalPart(intery), pixelSize);
            drawWuPixelScaled(pixelWriter, steep, x, (int) intery + 1, currentColor, fractionalPart(intery), pixelSize);

            intery += gradient;
        }

        // Рисуем последнюю точку
        drawWuPixelScaled(pixelWriter, steep, _x2, _y2, _color2, 1.0f, pixelSize);
    }

    /**
     * Рисует увеличенный пиксель с антиалиасингом
     */
    private static void drawWuPixelScaled(PixelWriter pixelWriter, boolean steep,
                                          int x, int y, Color color, float brightness,
                                          int pixelSize) {
        if (brightness <= 0) return;

        if (steep) {
            // Меняем координаты местами для крутых отрезков
            drawScaledPixel(pixelWriter, y, x, color, brightness, pixelSize);
        } else {
            drawScaledPixel(pixelWriter, x, y, color, brightness, pixelSize);
        }
    }

    /**
     * Рисует увеличенный пиксель
     */
    private static void drawScaledPixel(PixelWriter pixelWriter, int x, int y,
                                        Color color, float brightness, int pixelSize) {
        double r = color.getRed() * brightness;
        double g = color.getGreen() * brightness;
        double b = color.getBlue() * brightness;

        Color scaledColor = Color.color(
                Math.max(0, Math.min(1, r)),
                Math.max(0, Math.min(1, g)),
                Math.max(0, Math.min(1, b))
        );

        // Рисуем квадрат размером pixelSize x pixelSize
        for (int dx = 0; dx < pixelSize; dx++) {
            for (int dy = 0; dy < pixelSize; dy++) {
                pixelWriter.setColor(x * pixelSize + dx, y * pixelSize + dy, scaledColor);
            }
        }
    }

    /**
     * Рисует обычный пиксель (без масштабирования)
     */
    private static void drawScaledPixel(PixelWriter pixelWriter, int x, int y, Color color, int pixelSize) {
        // Рисуем квадрат размером pixelSize x pixelSize
        for (int dx = 0; dx < pixelSize; dx++) {
            for (int dy = 0; dy < pixelSize; dy++) {
                pixelWriter.setColor(x * pixelSize + dx, y * pixelSize + dy, color);
            }
        }
    }

    /**
     * Рисует пиксель с учетом прозрачности
     */
    private static void plot(PixelWriter pixelWriter, int x, int y, Color color, float brightness) {
        // Получаем текущий цвет пикселя (предполагаем прозрачный/черный фон)
        // Для простоты будем смешивать с черным цветом
        double r = color.getRed() * brightness;
        double g = color.getGreen() * brightness;
        double b = color.getBlue() * brightness;

        pixelWriter.setColor(x, y, Color.color(
                Math.max(0, Math.min(1, r)),
                Math.max(0, Math.min(1, g)),
                Math.max(0, Math.min(1, b))
        ));
    }

    /**
     * Вычисляет дробную часть числа
     */
    private static float fractionalPart(float x) {
        return x - (float) Math.floor(x);
    }

    /**
     * Интерполирует цвет между двумя точками
     */
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