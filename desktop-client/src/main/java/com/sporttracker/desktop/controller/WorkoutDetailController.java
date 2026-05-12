package com.sporttracker.desktop.controller;

import com.sporttracker.desktop.api.dto.ExerciseItemDto;
import com.sporttracker.desktop.api.dto.WorkoutDto;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.stream.Collectors;

public class WorkoutDetailController extends BaseController {

    @FXML private VBox             detailCard;
    @FXML private Label            workoutNameLabel;
    @FXML private Label            dateLabel;
    @FXML private Label            durationLabel;
    @FXML private Label            descriptionLabel;
    @FXML private ListView<String> exercisesListView;

    @FXML
    public void initialize() {
        if (detailCard != null) {
            detailCard.setScaleX(0.85);
            detailCard.setScaleY(0.85);
            detailCard.setOpacity(0);
            ScaleTransition scale = new ScaleTransition(Duration.millis(280), detailCard);
            scale.setFromX(0.85); scale.setToX(1.0);
            scale.setFromY(0.85); scale.setToY(1.0);
            FadeTransition fade = new FadeTransition(Duration.millis(280), detailCard);
            fade.setFromValue(0); fade.setToValue(1);
            new ParallelTransition(scale, fade).play();
        }
    }

    public void setWorkout(WorkoutDto workout) {
        workoutNameLabel.setText(workout.getName() != null ? workout.getName() : "—");

        String dateStr = workout.getDate() != null && workout.getDate().length() >= 10
                ? workout.getDate().substring(0, 10) : "Tarih yok";
        dateLabel.setText("📅  " + dateStr);

        int dur = workout.getDurationInMinutes();
        durationLabel.setText("⏱  " + (dur > 0 ? dur + " dk" : "—"));

        String desc = workout.getDescription();
        descriptionLabel.setText(desc != null && !desc.isEmpty() ? desc : "—");

        List<ExerciseItemDto> exercises = workout.getExercises();
        if (exercises.isEmpty()) {
            exercisesListView.setItems(FXCollections.observableArrayList("Egzersiz kaydı yok"));
        } else {
            exercisesListView.setItems(FXCollections.observableArrayList(
                    exercises.stream().map(ExerciseItemDto::toDisplayString).collect(Collectors.toList())
            ));
        }
    }

    @FXML
    public void handleClose() {
        if (detailCard != null) {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), detailCard);
            scale.setToX(0.85); scale.setToY(0.85);
            FadeTransition fade = new FadeTransition(Duration.millis(200), detailCard);
            fade.setToValue(0);
            ParallelTransition anim = new ParallelTransition(scale, fade);
            anim.setOnFinished(e -> ((Stage) workoutNameLabel.getScene().getWindow()).close());
            anim.play();
        } else {
            ((Stage) workoutNameLabel.getScene().getWindow()).close();
        }
    }
}
