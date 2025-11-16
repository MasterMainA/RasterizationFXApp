package com.cgvsu.rasterizationfxapp;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;

import com.cgvsu.rasterization.*;
import javafx.scene.paint.Color;

public class RasterizationController {

    @FXML
    AnchorPane anchorPane;
    @FXML
    private Canvas canvas;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> {
            canvas.setWidth(newValue.doubleValue());
            drawShapes();
        });
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> {
            canvas.setHeight(newValue.doubleValue());
            drawShapes();
        });

        drawShapes();
    }

    private void drawShapes() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        Rasterization.drawWuLine(gc, 50, 50, Color.RED, 300, 150, Color.BLUE);
        Rasterization.drawWuLine(gc, 100, 400, Color.GREEN, 400, 350, Color.YELLOW);
        Rasterization.drawWuLine(gc, 500, 100, Color.PURPLE, 600, 400, Color.ORANGE);

        Rasterization.drawWuLine(gc, 450, 50, Color.CYAN, 450, 200, Color.MAGENTA);
        Rasterization.drawWuLine(gc, 50, 450, Color.PINK, 200, 450, Color.BROWN);
    }
}