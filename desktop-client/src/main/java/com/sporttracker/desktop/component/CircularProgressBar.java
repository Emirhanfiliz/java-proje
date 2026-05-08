package com.sporttracker.desktop.component;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class CircularProgressBar extends StackPane {

    private final Arc progressArc;
    private final Text progressText;

    public CircularProgressBar(double radius, double strokeWidth) {
        Circle backgroundCircle = new Circle(radius);
        backgroundCircle.setFill(Color.TRANSPARENT);
        backgroundCircle.setStroke(Color.LIGHTGRAY);
        backgroundCircle.setStrokeWidth(strokeWidth);

        progressArc = new Arc(0, 0, radius, radius, 90, 0);
        progressArc.setType(ArcType.OPEN);
        progressArc.setFill(Color.TRANSPARENT);
        progressArc.setStroke(Color.web("#007bff"));
        progressArc.setStrokeWidth(strokeWidth);

        progressText = new Text("0%");
        progressText.setFont(Font.font("System", FontWeight.BOLD, radius * 0.4));

        getChildren().addAll(backgroundCircle, progressArc, progressText);
    }

    public void setProgress(double percentage) {
        if (percentage < 0) percentage = 0;
        if (percentage > 100) percentage = 100;

        double angle = percentage * 3.6;
        progressArc.setLength(-angle);

        progressText.setText(String.format("%.0f%%", percentage));

        if (percentage < 40) {
            progressArc.setStroke(Color.GREEN);
        } else if (percentage < 75) {
            progressArc.setStroke(Color.ORANGE);
        } else {
            progressArc.setStroke(Color.RED);
        }
    }
}
