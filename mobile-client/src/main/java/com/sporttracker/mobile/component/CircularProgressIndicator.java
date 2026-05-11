package com.sporttracker.mobile.component;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;

public class CircularProgressIndicator extends StackPane {

    private final Canvas canvas;
    private final DoubleProperty progress = new SimpleDoubleProperty(0);
    private final double radius = 40;
    private final double strokeWidth = 8;

    public CircularProgressIndicator() {
        canvas = new Canvas(radius * 2 + strokeWidth, radius * 2 + strokeWidth);
        getChildren().add(canvas);

        progress.addListener((obs, oldVal, newVal) -> draw());
        draw();
    }

    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double size = radius * 2 + strokeWidth;
        gc.clearRect(0, 0, size, size);

        // Draw background circle
        gc.setLineWidth(strokeWidth);
        gc.setStroke(Color.rgb(255, 255, 255, 0.2));
        gc.strokeOval(strokeWidth / 2, strokeWidth / 2, radius * 2, radius * 2);

        // Draw progress arc
        double p = Math.max(0, Math.min(1.0, progress.get()));
        if (p > 0) {
            double angle = p * 360;
            
            // Vibrant gradient
            LinearGradient gradient = new LinearGradient(
                    0, 0, 1, 1, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
                    new Stop(0, Color.web("#FFD700")), // Gold
                    new Stop(1, Color.web("#FFF")) // White
            );

            gc.setStroke(gradient);
            gc.setLineCap(StrokeLineCap.ROUND);
            gc.strokeArc(strokeWidth / 2, strokeWidth / 2, radius * 2, radius * 2, 90, -angle, ArcType.OPEN);
        }
    }

    public void setProgress(double value) {
        progress.set(value);
    }
    
    public DoubleProperty progressProperty() {
        return progress;
    }
}
