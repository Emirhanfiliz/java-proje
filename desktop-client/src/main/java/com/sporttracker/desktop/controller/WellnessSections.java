package com.sporttracker.desktop.controller;

import com.sporttracker.desktop.wellness.SmartReminderService;
import com.sporttracker.desktop.wellness.WellnessStore;
import com.sporttracker.shared.wellness.AdaptiveGoalEngine;
import com.sporttracker.shared.wellness.AdaptiveGoals;
import com.sporttracker.shared.wellness.Challenge;
import com.sporttracker.shared.wellness.ChallengeEngine;
import com.sporttracker.shared.wellness.Habit;
import com.sporttracker.shared.wellness.HabitEngine;
import com.sporttracker.shared.wellness.HeartRateZoneCalculator;
import com.sporttracker.shared.wellness.HrZoneSession;
import com.sporttracker.shared.wellness.MealAggregator;
import com.sporttracker.shared.wellness.MealEntry;
import com.sporttracker.shared.wellness.PlannedExercise;
import com.sporttracker.shared.wellness.PrRecord;
import com.sporttracker.shared.wellness.PrTracker;
import com.sporttracker.shared.wellness.RecoveryEntry;
import com.sporttracker.shared.wellness.RecoveryScoreCalculator;
import com.sporttracker.shared.wellness.Reminder;
import com.sporttracker.shared.wellness.TrendInsight;
import com.sporttracker.shared.wellness.TrendInsightsService;
import com.sporttracker.shared.wellness.WeeklyWorkoutPlan;
import com.sporttracker.shared.wellness.WellnessData;
import com.sporttracker.shared.wellness.WorkoutDayPlan;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Compact "section" cards rendered directly on the desktop dashboard.
 *
 * <p>Each {@code build*} method returns a self-contained {@link VBox} that wires
 * its own actions through {@link WellnessStore} and triggers the supplied
 * {@code refresh} callback whenever data changes so other cards can re-render.
 */
public final class WellnessSections {

    private WellnessSections() {}

    private static final String[] TR_DAYS = {"Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi", "Pazar"};

    // ====================================================================================== //
    // 1. WORKOUT PLAN
    // ====================================================================================== //

    public static VBox buildPlanCard(WellnessData data, Runnable refresh) {
        VBox card = newCard("📋  Bugünkü Antrenman Planı");

        WeeklyWorkoutPlan plan = data.getWeeklyPlan();
        if (plan == null) {
            data.setWeeklyPlan(WeeklyWorkoutPlan.buildPushPullLegs());
            plan = data.getWeeklyPlan();
            WellnessStore.save(data);
        }
        WorkoutDayPlan today = todayPlan(plan);

        Label dayLabel = new Label(today.getDayOfWeek() + " · " + today.getFocus());
        dayLabel.getStyleClass().add("section-title");

        Label tag = new Label(today.isRestDay() ? "REST" : today.getSplitType());
        tag.getStyleClass().add(today.isRestDay() ? "rest-tag" : "split-tag");
        Label template = new Label("Şablon: " + WeeklyWorkoutPlan.templateOptions().getOrDefault(plan.getTemplateName(), plan.getTemplateName()));
        template.getStyleClass().add("metric-key");
        HBox meta = new HBox(8, tag, template);
        meta.setAlignment(Pos.CENTER_LEFT);

        VBox exercises = new VBox(2);
        if (today.getExercises().isEmpty()) {
            Label empty = new Label(today.isRestDay() ? "Dinlenme günü 💤" : "Henüz egzersiz yok");
            empty.getStyleClass().add("metric-key");
            exercises.getChildren().add(empty);
        } else {
            int max = Math.min(4, today.getExercises().size());
            for (int i = 0; i < max; i++) {
                Label line = new Label("• " + today.getExercises().get(i).toString());
                line.getStyleClass().add("performance-value");
                exercises.getChildren().add(line);
            }
            if (today.getExercises().size() > max) {
                Label more = new Label("+" + (today.getExercises().size() - max) + " daha…");
                more.getStyleClass().add("metric-key");
                exercises.getChildren().add(more);
            }
        }

        Button edit = primaryButton("Planı Düzenle");
        edit.setOnAction(e -> openPlanDialog(data, refresh));

        card.getChildren().addAll(dayLabel, meta, exercises, edit);
        return card;
    }

    private static WorkoutDayPlan todayPlan(WeeklyWorkoutPlan plan) {
        int idx = LocalDate.now().getDayOfWeek().getValue() - 1;
        if (plan.getDays().isEmpty()) return new WorkoutDayPlan(TR_DAYS[idx], "REST", "Dinlenme", true);
        return plan.getDays().get(Math.min(idx, plan.getDays().size() - 1));
    }

    private static void openPlanDialog(WellnessData data, Runnable refresh) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Haftalık Plan");
        dialog.getDialogPane().getStylesheets().add(WellnessSections.class.getResource("/css/dark-theme.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("dashboard-root");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        ComboBox<Map.Entry<String, String>> templates = new ComboBox<>(
                FXCollections.observableArrayList(WeeklyWorkoutPlan.templateOptions().entrySet())
        );
        templates.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Map.Entry<String, String> entry) { return entry == null ? "" : entry.getValue(); }
            @Override public Map.Entry<String, String> fromString(String s) { return null; }
        });
        if (data.getWeeklyPlan() != null) {
            for (Map.Entry<String, String> e : templates.getItems()) {
                if (e.getKey().equals(data.getWeeklyPlan().getTemplateName())) {
                    templates.getSelectionModel().select(e);
                    break;
                }
            }
        }
        if (templates.getSelectionModel().getSelectedItem() == null) templates.getSelectionModel().selectFirst();

        VBox daysBox = new VBox(6);
        renderPlanDays(daysBox, data, refresh);

        Button apply = new Button("Şablonu Uygula");
        apply.getStyleClass().add("button-success");
        apply.setOnAction(e -> {
            Map.Entry<String, String> selected = templates.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            WeeklyWorkoutPlan plan;
            switch (selected.getKey()) {
                case WeeklyWorkoutPlan.TEMPLATE_PPL:         plan = WeeklyWorkoutPlan.buildPushPullLegs(); break;
                case WeeklyWorkoutPlan.TEMPLATE_UPPER_LOWER: plan = WeeklyWorkoutPlan.buildUpperLower();   break;
                case WeeklyWorkoutPlan.TEMPLATE_FULL_BODY:   plan = WeeklyWorkoutPlan.buildFullBody();     break;
                default:                                     plan = WeeklyWorkoutPlan.emptyPlan();
            }
            plan.setCreatedAt(LocalDate.now().toString());
            data.setWeeklyPlan(plan);
            WellnessStore.save(data);
            renderPlanDays(daysBox, data, refresh);
            refresh.run();
        });

        HBox header = new HBox(8, new Label("Şablon:"), templates, apply);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox content = new VBox(10, header, daysBox);
        content.setPadding(new Insets(10));
        content.setPrefWidth(640);
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }

    private static void renderPlanDays(VBox box, WellnessData data, Runnable refresh) {
        box.getChildren().clear();
        WeeklyWorkoutPlan plan = data.getWeeklyPlan();
        if (plan == null) return;
        for (WorkoutDayPlan day : plan.getDays()) {
            VBox dayCard = new VBox(4);
            dayCard.getStyleClass().add("wellness-day-card");
            Label title = new Label(day.getDayOfWeek() + " · " + day.getFocus());
            title.getStyleClass().add("section-title");
            Label tag = new Label(day.isRestDay() ? "REST" : day.getSplitType());
            tag.getStyleClass().add(day.isRestDay() ? "rest-tag" : "split-tag");

            HBox actions = new HBox(8, tag);
            actions.setAlignment(Pos.CENTER_LEFT);

            Button addExercise = new Button("+ Egzersiz");
            addExercise.getStyleClass().add("button-link");
            addExercise.setOnAction(ev -> {
                javafx.scene.control.TextInputDialog input = new javafx.scene.control.TextInputDialog("Bench Press, 4x8, 60");
                input.setHeaderText("Egzersiz ekle (isim, set x rep, kg)");
                input.showAndWait().ifPresent(text -> {
                    PlannedExercise ex = parsePlannedExercise(text);
                    if (ex != null) {
                        day.getExercises().add(ex);
                        WellnessStore.save(data);
                        renderPlanDays(box, data, refresh);
                        refresh.run();
                    }
                });
            });
            Button toggle = new Button(day.isRestDay() ? "Aktif Yap" : "Dinlenme");
            toggle.getStyleClass().add("button-link");
            toggle.setOnAction(ev -> {
                day.setRestDay(!day.isRestDay());
                if (day.isRestDay()) {
                    day.setSplitType("REST");
                    day.setFocus("Dinlenme & Mobilite");
                } else if (day.getSplitType() == null || "REST".equals(day.getSplitType())) {
                    day.setSplitType("CUSTOM");
                    day.setFocus("Özel Antrenman");
                }
                WellnessStore.save(data);
                renderPlanDays(box, data, refresh);
                refresh.run();
            });
            actions.getChildren().addAll(addExercise, toggle);

            VBox exercises = new VBox(2);
            if (day.getExercises().isEmpty()) {
                Label empty = new Label(day.isRestDay() ? "Dinlenme 💤" : "Egzersiz yok");
                empty.getStyleClass().add("metric-key");
                exercises.getChildren().add(empty);
            } else {
                for (PlannedExercise ex : day.getExercises()) {
                    HBox row = new HBox(8);
                    row.setAlignment(Pos.CENTER_LEFT);
                    Label l = new Label("• " + ex.toString());
                    l.getStyleClass().add("performance-value");
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    Button x = new Button("X");
                    x.getStyleClass().add("button-link");
                    x.setOnAction(ev -> {
                        day.getExercises().remove(ex);
                        WellnessStore.save(data);
                        renderPlanDays(box, data, refresh);
                        refresh.run();
                    });
                    row.getChildren().addAll(l, spacer, x);
                    exercises.getChildren().add(row);
                }
            }
            dayCard.getChildren().addAll(title, actions, exercises);
            box.getChildren().add(dayCard);
        }
    }

    private static PlannedExercise parsePlannedExercise(String input) {
        if (input == null || input.isBlank()) return null;
        try {
            String[] parts = input.split(",");
            String name = parts[0].trim();
            int sets = 3, reps = 10;
            double weight = 0;
            if (parts.length > 1) {
                String[] sxr = parts[1].trim().toLowerCase().split("x");
                if (sxr.length == 2) {
                    sets = Integer.parseInt(sxr[0].trim());
                    reps = Integer.parseInt(sxr[1].trim());
                }
            }
            if (parts.length > 2) {
                weight = Double.parseDouble(parts[2].trim());
            }
            return new PlannedExercise(name, sets, reps, weight, "Genel");
        } catch (Exception ex) {
            new Alert(AlertType.WARNING, "Format hatalı. Örnek: 'Bench Press, 4x8, 60'").showAndWait();
            return null;
        }
    }

    // ====================================================================================== //
    // 2. ADAPTIVE GOALS
    // ====================================================================================== //

    public static VBox buildAdaptiveGoalsCard(WellnessData data, Runnable refresh) {
        VBox card = newCard("🎯  Adaptive Goals");

        AdaptiveGoals goals = data.getAdaptiveGoals();
        Label workouts = new Label("Antrenman: " + goals.getWeeklyWorkoutGoal() + "/hafta");
        Label minutes = new Label("Dakika: " + goals.getWeeklyMinutesGoal() + "/hafta");
        Label steps = new Label("Adım: " + goals.getDailyStepsGoal() + "/gün");
        Label water = new Label("Su: " + goals.getDailyWaterGoal() + " bardak/gün");
        for (Label l : new Label[]{workouts, minutes, steps, water}) l.getStyleClass().add("performance-value");

        Label note = new Label(goals.getLastAdjustmentNote() == null
                ? "Otomasyon: hazır"
                : "💡 " + goals.getLastAdjustmentNote());
        note.setWrapText(true);
        note.getStyleClass().add("metric-key");

        Button edit = primaryButton("Hedefleri Ayarla");
        edit.setOnAction(e -> openAdaptiveGoalsDialog(data, refresh));

        card.getChildren().addAll(workouts, minutes, steps, water, note, edit);
        return card;
    }

    private static void openAdaptiveGoalsDialog(WellnessData data, Runnable refresh) {
        Dialog<Void> dialog = baseDialog("Adaptive Goals");
        AdaptiveGoals goals = data.getAdaptiveGoals();

        Spinner<Integer> workouts = new Spinner<>(1, 7, goals.getWeeklyWorkoutGoal());
        Spinner<Integer> minutes = new Spinner<>(60, 1000, goals.getWeeklyMinutesGoal(), 10);
        Spinner<Integer> steps = new Spinner<>(2000, 25000, goals.getDailyStepsGoal(), 500);
        Spinner<Integer> water = new Spinner<>(2, 14, goals.getDailyWaterGoal());
        Spinner<Integer> calories = new Spinner<>(100, 2000, goals.getDailyCaloriesGoal(), 50);
        CheckBox autoAdjust = new CheckBox("Otomatik ayarlama açık");
        autoAdjust.setSelected(goals.isAutoAdjustEnabled());

        Spinner<Integer> doneWorkouts = new Spinner<>(0, 20, 3);
        Spinner<Integer> trainedMin = new Spinner<>(0, 2000, 180, 10);
        Spinner<Integer> avgSteps = new Spinner<>(0, 30000, 8000, 500);
        Spinner<Integer> avgWater = new Spinner<>(0, 20, 7);

        GridPane g1 = grid(
                row("Haftalık antrenman", workouts),
                row("Haftalık dakika", minutes),
                row("Günlük adım", steps),
                row("Günlük su", water),
                row("Günlük kalori", calories)
        );
        g1.add(autoAdjust, 1, 5);
        GridPane g2 = grid(
                row("Bu hafta antrenman", doneWorkouts),
                row("Bu hafta dakika", trainedMin),
                row("Ort. günlük adım", avgSteps),
                row("Ort. günlük su", avgWater)
        );

        Button save = new Button("Kaydet");
        save.getStyleClass().add("button-success");
        save.setOnAction(e -> {
            goals.setWeeklyWorkoutGoal(workouts.getValue());
            goals.setWeeklyMinutesGoal(minutes.getValue());
            goals.setDailyStepsGoal(steps.getValue());
            goals.setDailyWaterGoal(water.getValue());
            goals.setDailyCaloriesGoal(calories.getValue());
            goals.setAutoAdjustEnabled(autoAdjust.isSelected());
            WellnessStore.save(data);
            refresh.run();
            dialog.close();
        });

        Button auto = new Button("Otomatik Ayarla");
        auto.getStyleClass().add("button-success");
        auto.setOnAction(e -> {
            AdaptiveGoalEngine.adjust(data, doneWorkouts.getValue(), trainedMin.getValue(), avgSteps.getValue(), avgWater.getValue());
            workouts.getValueFactory().setValue(goals.getWeeklyWorkoutGoal());
            minutes.getValueFactory().setValue(goals.getWeeklyMinutesGoal());
            steps.getValueFactory().setValue(goals.getDailyStepsGoal());
            water.getValueFactory().setValue(goals.getDailyWaterGoal());
            WellnessStore.save(data);
            refresh.run();
        });

        VBox content = new VBox(10, sectionTitle("Hedefler"), g1, sectionTitle("Bu Haftaki Performans"), g2, new HBox(10, save, auto));
        content.setPadding(new Insets(10));
        content.setPrefWidth(540);
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }

    // ====================================================================================== //
    // 3. RECOVERY SCORE
    // ====================================================================================== //

    public static VBox buildRecoveryCard(WellnessData data, Runnable refresh) {
        VBox card = newCard("💤  Recovery Score");

        RecoveryEntry last = data.getRecoveryHistory().isEmpty() ? null
                : data.getRecoveryHistory().get(data.getRecoveryHistory().size() - 1);

        Label score = new Label(last == null ? "Skor: -" : "Skor: " + last.getScore());
        score.getStyleClass().add("dashboard-welcome");
        Label label = new Label(last == null ? "Verini gir, skoru hesapla" : (last.getLabel() == null ? "" : last.getLabel()));
        label.getStyleClass().add("metric-key");
        label.setWrapText(true);

        ProgressBar bar = new ProgressBar((last == null ? 0 : last.getScore()) / 100.0);
        bar.setPrefWidth(220);
        bar.getStyleClass().add("weekly-progress");

        Label history = new Label("Son 5 gün: " + lastRecoveryLine(data));
        history.getStyleClass().add("metric-key");
        history.setWrapText(true);

        Button log = primaryButton(last == null ? "Recovery Hesapla" : "Bugünü Güncelle");
        log.setOnAction(e -> openRecoveryDialog(data, refresh));

        card.getChildren().addAll(score, bar, label, history, log);
        return card;
    }

    private static String lastRecoveryLine(WellnessData data) {
        List<RecoveryEntry> list = data.getRecoveryHistory();
        if (list.isEmpty()) return "-";
        int from = Math.max(0, list.size() - 5);
        StringBuilder sb = new StringBuilder();
        for (int i = from; i < list.size(); i++) {
            if (sb.length() > 0) sb.append(" → ");
            sb.append(list.get(i).getScore());
        }
        return sb.toString();
    }

    private static void openRecoveryDialog(WellnessData data, Runnable refresh) {
        Dialog<Void> dialog = baseDialog("Recovery Score");

        Spinner<Double> sleep = new Spinner<>();
        sleep.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 12, 7.0, 0.5));
        Spinner<Integer> rhr = new Spinner<>(40, 120, 60);
        Spinner<Integer> load = new Spinner<>(0, 1500, 250, 25);

        Label preview = new Label("Skor: -");
        preview.getStyleClass().add("dashboard-welcome");
        Runnable preview2 = () -> {
            int s = RecoveryScoreCalculator.compute(sleep.getValue(), rhr.getValue(), load.getValue());
            preview.setText("Skor: " + s + " — " + RecoveryScoreCalculator.labelFor(s));
        };
        sleep.valueProperty().addListener((o, a, b) -> preview2.run());
        rhr.valueProperty().addListener((o, a, b) -> preview2.run());
        load.valueProperty().addListener((o, a, b) -> preview2.run());
        preview2.run();

        Button save = new Button("Kaydet");
        save.getStyleClass().add("button-success");
        save.setOnAction(e -> {
            RecoveryEntry entry = new RecoveryEntry(LocalDate.now().toString(), sleep.getValue(), rhr.getValue(), load.getValue());
            RecoveryScoreCalculator.compute(entry);
            data.getRecoveryHistory().add(entry);
            WellnessStore.save(data);
            refresh.run();
            dialog.close();
        });

        VBox content = new VBox(10,
                sectionTitle("Bugünün Verileri"),
                grid(row("Uyku (saat)", sleep), row("Dinlenme nabzı", rhr), row("Antrenman yükü", load)),
                preview, save);
        content.setPadding(new Insets(10));
        content.setPrefWidth(420);
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }

    // ====================================================================================== //
    // 4. HR ZONE
    // ====================================================================================== //

    public static VBox buildHrZoneCard(WellnessData data, Runnable refresh) {
        VBox card = newCard("❤️  HR Zone Takibi");

        HrZoneSession last = data.getHrZoneSessions().isEmpty() ? null
                : data.getHrZoneSessions().get(data.getHrZoneSessions().size() - 1);

        if (last == null) {
            Label empty = new Label("Henüz oturum yok. Bir kardiyo seansı kaydet.");
            empty.getStyleClass().add("metric-key");
            empty.setWrapText(true);
            card.getChildren().add(empty);
        } else {
            Label header = new Label(last.getWorkoutName() + " · " + last.getDate());
            header.getStyleClass().add("performance-value");
            Map<String, Integer> sum = HeartRateZoneCalculator.summary(last);
            int total = sum.values().stream().mapToInt(Integer::intValue).sum();
            VBox zones = new VBox(2);
            int z = 1;
            for (Map.Entry<String, Integer> e : sum.entrySet()) {
                double ratio = total == 0 ? 0 : e.getValue() / (double) total;
                ProgressBar bar = new ProgressBar(ratio);
                bar.setPrefWidth(150);
                bar.setStyle("-fx-accent: " + HeartRateZoneCalculator.zoneColor(z) + ";");
                Label l = new Label("Z" + z + " — " + e.getValue() + " dk");
                l.getStyleClass().add("metric-key");
                HBox row = new HBox(8, bar, l);
                row.setAlignment(Pos.CENTER_LEFT);
                zones.getChildren().add(row);
                z++;
            }
            card.getChildren().addAll(header, zones);
        }

        Button log = primaryButton("Kardiyo Oturumu Ekle");
        log.setOnAction(e -> openHrZoneDialog(data, refresh));
        card.getChildren().add(log);
        return card;
    }

    private static void openHrZoneDialog(WellnessData data, Runnable refresh) {
        Dialog<Void> dialog = baseDialog("HR Zone Oturumu");

        Spinner<Integer> age = new Spinner<>(12, 90, 25);
        Spinner<Integer> avgHr = new Spinner<>(60, 220, 130);
        Spinner<Integer> duration = new Spinner<>(5, 240, 45, 5);
        TextField name = new TextField();
        name.setPromptText("Antrenman adı");

        Button save = new Button("Kaydet");
        save.getStyleClass().add("button-success");
        save.setOnAction(e -> {
            HrZoneSession s = new HrZoneSession(
                    LocalDate.now().toString(),
                    name.getText().isBlank() ? "Kardiyo" : name.getText(),
                    avgHr.getValue(), duration.getValue());
            HeartRateZoneCalculator.distribute(s, age.getValue());
            data.getHrZoneSessions().add(s);
            WellnessStore.save(data);
            refresh.run();
            dialog.close();
        });

        VBox content = new VBox(10,
                grid(row("Yaş", age), row("Ortalama nabız", avgHr), row("Süre (dk)", duration), row("İsim", name)),
                save);
        content.setPadding(new Insets(10));
        content.setPrefWidth(420);
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }

    // ====================================================================================== //
    // 5. PR TRACKER
    // ====================================================================================== //

    public static VBox buildPrCard(WellnessData data, Runnable refresh) {
        VBox card = newCard("💪  Kişisel Rekorlar (PR)");

        Map<String, PrRecord> bests = PrTracker.bestByLift(data);
        if (bests.isEmpty()) {
            Label empty = new Label("Henüz PR yok. Bench / Squat / Deadlift ekle.");
            empty.getStyleClass().add("metric-key");
            card.getChildren().add(empty);
        } else {
            int count = 0;
            for (Map.Entry<String, PrRecord> e : bests.entrySet()) {
                if (count++ >= 3) break;
                PrRecord r = e.getValue();
                Label line = new Label(String.format("🏋️ %s · %.1fkg x %d (1RM ≈ %.1f)",
                        e.getKey(), r.getWeightKg(), r.getReps(), r.estimatedOneRepMax()));
                line.getStyleClass().add("performance-value");
                card.getChildren().add(line);
            }
            if (bests.size() > 3) {
                Label more = new Label("+" + (bests.size() - 3) + " lift daha");
                more.getStyleClass().add("metric-key");
                card.getChildren().add(more);
            }
        }

        Button add = primaryButton("Yeni PR Ekle");
        add.setOnAction(e -> openPrDialog(data, refresh));
        card.getChildren().add(add);
        return card;
    }

    private static void openPrDialog(WellnessData data, Runnable refresh) {
        Dialog<Void> dialog = baseDialog("Kişisel Rekor");

        ComboBox<String> exercise = new ComboBox<>(FXCollections.observableArrayList(
                "Bench Press", "Squat", "Deadlift", "Overhead Press", "Pull-up", "Barbell Row", "Front Squat", "Hip Thrust"));
        exercise.setEditable(true);
        exercise.getSelectionModel().selectFirst();

        Spinner<Double> w = new Spinner<>();
        w.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(20, 400, 80, 2.5));
        w.setEditable(true);
        Spinner<Integer> r = new Spinner<>(1, 30, 5);
        TextField note = new TextField();
        note.setPromptText("Not");

        Button save = new Button("Kaydet");
        save.getStyleClass().add("button-success");
        save.setOnAction(e -> {
            String ex = exercise.getSelectionModel().getSelectedItem();
            if (ex == null || ex.isBlank()) return;
            PrRecord rec = new PrRecord(ex, w.getValue(), r.getValue(), LocalDate.now().toString(), note.getText());
            PrTracker.addRecord(data, rec);
            WellnessStore.save(data);
            refresh.run();
            dialog.close();
        });

        VBox content = new VBox(10,
                grid(row("Egzersiz", exercise), row("Kg", w), row("Tekrar", r), row("Not", note)),
                save);
        content.setPadding(new Insets(10));
        content.setPrefWidth(420);
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }

    // ====================================================================================== //
    // 6. HABIT STACKING
    // ====================================================================================== //

    public static VBox buildHabitCard(WellnessData data, Runnable refresh) {
        VBox card = newCard("🔗  Alışkanlık Zinciri");

        HabitEngine.ensureDefaults(data);
        LocalDate today = LocalDate.now();
        FlowPane chips = new FlowPane(6, 6);
        chips.setPrefWrapLength(280);

        for (Habit habit : data.getHabits()) {
            boolean done = habit.getCompletedDates().contains(today.toString());
            Button chip = new Button((habit.getIcon() == null ? "✨" : habit.getIcon()) + " " +
                    habit.getName() + "  🔥" + habit.getStreak());
            chip.getStyleClass().add(done ? "habit-chip-done" : "habit-chip");
            chip.setOnAction(e -> {
                if (done) HabitEngine.resetCompletion(habit, today);
                else HabitEngine.markCompleted(habit, today);
                WellnessStore.save(data);
                refresh.run();
            });
            chips.getChildren().add(chip);
        }

        Button manage = primaryButton("Alışkanlık Yönet");
        manage.setOnAction(e -> openHabitDialog(data, refresh));

        card.getChildren().addAll(chips, manage);
        return card;
    }

    private static void openHabitDialog(WellnessData data, Runnable refresh) {
        Dialog<Void> dialog = baseDialog("Alışkanlıklar");

        VBox list = new VBox(6);
        Runnable render = () -> {
            list.getChildren().clear();
            LocalDate today = LocalDate.now();
            for (Habit habit : data.getHabits()) {
                HBox row = new HBox(8);
                row.getStyleClass().add("metric-chip");
                row.setAlignment(Pos.CENTER_LEFT);
                Label icon = new Label(habit.getIcon() == null ? "✨" : habit.getIcon());
                icon.setStyle("-fx-font-size: 22px;");
                VBox info = new VBox(2);
                Label name = new Label(habit.getName());
                name.getStyleClass().add("section-title");
                Label streak = new Label("🔥 " + habit.getStreak() + " gün");
                streak.getStyleClass().add("metric-key");
                info.getChildren().addAll(name, streak);
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                boolean done = habit.getCompletedDates().contains(today.toString());
                Button toggle = new Button(done ? "✓" : "Yap");
                toggle.getStyleClass().add(done ? "button-success" : "button-link");
                toggle.setOnAction(ev -> {
                    if (done) HabitEngine.resetCompletion(habit, today);
                    else HabitEngine.markCompleted(habit, today);
                    WellnessStore.save(data);
                    refresh.run();
                });
                Button rm = new Button("Sil");
                rm.getStyleClass().add("button-danger");
                rm.setOnAction(ev -> {
                    data.getHabits().remove(habit);
                    WellnessStore.save(data);
                    refresh.run();
                });
                row.getChildren().addAll(icon, info, spacer, toggle, rm);
                list.getChildren().add(row);
            }
        };
        render.run();
        // wire re-render when dialog mutates
        Runnable wrapped = () -> { render.run(); refresh.run(); };

        TextField name = new TextField();
        name.setPromptText("Yeni alışkanlık");
        TextField icon = new TextField();
        icon.setPromptText("Emoji");
        icon.setPrefWidth(80);
        Button add = new Button("+ Ekle");
        add.getStyleClass().add("button-success");
        add.setOnAction(e -> {
            if (name.getText().isBlank()) return;
            Habit habit = new Habit("habit-" + UUID.randomUUID(),
                    name.getText().trim(), icon.getText().isBlank() ? "✨" : icon.getText().trim(),
                    "Kullanıcı tarafından eklendi");
            data.getHabits().add(habit);
            WellnessStore.save(data);
            name.clear(); icon.clear();
            wrapped.run();
        });

        VBox content = new VBox(10, list, new HBox(8, icon, name, add));
        content.setPadding(new Insets(10));
        content.setPrefWidth(480);
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }

    // ====================================================================================== //
    // 7. SMART REMINDERS
    // ====================================================================================== //

    public static VBox buildReminderCard(WellnessData data, Runnable refresh) {
        VBox card = newCard("⏰  Akıllı Hatırlatıcılar");

        if (data.getReminders().isEmpty()) {
            Label empty = new Label("Su, uyku ya da antrenman için bir hatırlatıcı kur.");
            empty.getStyleClass().add("metric-key");
            empty.setWrapText(true);
            card.getChildren().add(empty);
        } else {
            int count = 0;
            for (Reminder reminder : data.getReminders()) {
                if (count++ >= 3) break;
                Label line = new Label((reminder.isEnabled() ? "🟢 " : "⚪ ") +
                        reminder.formattedTime() + " · " + reminder.getLabel());
                line.getStyleClass().add("performance-value");
                card.getChildren().add(line);
            }
            if (data.getReminders().size() > 3) {
                Label more = new Label("+" + (data.getReminders().size() - 3) + " hatırlatıcı daha");
                more.getStyleClass().add("metric-key");
                card.getChildren().add(more);
            }
        }

        Button manage = primaryButton("Hatırlatıcı Yönet");
        manage.setOnAction(e -> openReminderDialog(data, refresh));
        card.getChildren().add(manage);
        return card;
    }

    private static void openReminderDialog(WellnessData data, Runnable refresh) {
        Dialog<Void> dialog = baseDialog("Hatırlatıcılar");

        VBox list = new VBox(6);
        Runnable render = () -> {
            list.getChildren().clear();
            if (data.getReminders().isEmpty()) {
                Label empty = new Label("Henüz hatırlatıcı yok.");
                empty.getStyleClass().add("metric-key");
                list.getChildren().add(empty);
                return;
            }
            for (Reminder reminder : data.getReminders()) {
                HBox row = new HBox(10);
                row.getStyleClass().add("metric-chip");
                row.setAlignment(Pos.CENTER_LEFT);
                VBox info = new VBox(2);
                Label header = new Label(reminder.formattedTime() + " · " + reminder.getLabel());
                header.getStyleClass().add("section-title");
                Label body = new Label("[" + reminder.getType() + "] " + reminder.getMessage());
                body.getStyleClass().add("metric-key");
                body.setWrapText(true);
                info.getChildren().addAll(header, body);
                CheckBox enabled = new CheckBox("Aktif");
                enabled.setSelected(reminder.isEnabled());
                enabled.setOnAction(ev -> {
                    reminder.setEnabled(enabled.isSelected());
                    WellnessStore.save(data);
                    refresh.run();
                });
                Button rm = new Button("Sil");
                rm.getStyleClass().add("button-danger");
                rm.setOnAction(ev -> {
                    data.getReminders().remove(reminder);
                    WellnessStore.save(data);
                    refresh.run();
                });
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                row.getChildren().addAll(info, spacer, enabled, rm);
                list.getChildren().add(row);
            }
        };
        render.run();
        Runnable wrapped = () -> { render.run(); refresh.run(); };

        TextField label = new TextField();
        label.setPromptText("Başlık");
        TextField msg = new TextField();
        msg.setPromptText("Mesaj");
        ChoiceBox<String> type = new ChoiceBox<>(FXCollections.observableArrayList(
                Reminder.TYPE_WATER, Reminder.TYPE_WORKOUT, Reminder.TYPE_SLEEP,
                Reminder.TYPE_STRETCH, Reminder.TYPE_MEAL, Reminder.TYPE_HABIT));
        type.getSelectionModel().selectFirst();
        Spinner<Integer> hour = new Spinner<>(0, 23, 9);
        Spinner<Integer> minute = new Spinner<>(0, 59, 0, 5);
        Button add = new Button("+ Hatırlatıcı");
        add.getStyleClass().add("button-success");
        add.setOnAction(e -> {
            if (label.getText().isBlank()) return;
            Reminder reminder = new Reminder(
                    "rem-" + UUID.randomUUID(),
                    label.getText().trim(),
                    msg.getText().isBlank() ? label.getText() : msg.getText(),
                    type.getValue(), hour.getValue(), minute.getValue());
            data.getReminders().add(reminder);
            WellnessStore.save(data);
            SmartReminderService.getInstance().notifyDataChanged();
            label.clear(); msg.clear();
            wrapped.run();
        });

        VBox content = new VBox(10, list, grid(
                row("Başlık", label), row("Mesaj", msg), row("Tür", type), row("Saat", hour), row("Dakika", minute)
        ), add);
        content.setPadding(new Insets(10));
        content.setPrefWidth(500);
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }

    // ====================================================================================== //
    // 8. MEAL SNAPSHOT
    // ====================================================================================== //

    public static VBox buildMealCard(WellnessData data, Runnable refresh) {
        VBox card = newCard("🥗  Bugünün Öğünleri");

        String today = LocalDate.now().toString();
        MealAggregator.MacroTotals totals = MealAggregator.totalsForDate(data.getMeals(), today);

        Label total = new Label(totals.calories + " kcal");
        total.getStyleClass().add("dashboard-welcome");
        Label macros = new Label(String.format("Protein %.0fg · Karb %.0fg · Yağ %.0fg · %d öğün",
                totals.protein, totals.carbs, totals.fat, totals.mealCount));
        macros.getStyleClass().add("metric-key");

        int goal = data.getAdaptiveGoals().getDailyCaloriesGoal() * 4;
        ProgressBar bar = new ProgressBar(goal == 0 ? 0 : Math.min(1.0, totals.calories / (double) goal));
        bar.setPrefWidth(220);
        bar.getStyleClass().add("weekly-progress");

        Label items = new Label(meals3Line(data, today));
        items.setWrapText(true);
        items.getStyleClass().add("performance-value");

        Button add = primaryButton("Öğün Ekle");
        add.setOnAction(e -> openMealDialog(data, refresh));

        card.getChildren().addAll(total, macros, bar, items, add);
        return card;
    }

    private static String meals3Line(WellnessData data, String today) {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (MealEntry meal : data.getMeals()) {
            if (!today.equals(meal.getDate())) continue;
            if (count > 0) sb.append("\n");
            sb.append("• ").append(meal.getName()).append(" — ").append(meal.getCalories()).append(" kcal");
            count++;
            if (count >= 3) break;
        }
        return sb.length() == 0 ? "Bugün henüz öğün yok." : sb.toString();
    }

    private static void openMealDialog(WellnessData data, Runnable refresh) {
        Dialog<Void> dialog = baseDialog("Öğün Ekle");

        TextField name = new TextField();
        name.setPromptText("Öğün adı");
        ChoiceBox<String> slot = new ChoiceBox<>(FXCollections.observableArrayList(
                MealEntry.SLOT_BREAKFAST, MealEntry.SLOT_LUNCH, MealEntry.SLOT_DINNER, MealEntry.SLOT_SNACK));
        slot.getSelectionModel().select(MealEntry.SLOT_LUNCH);
        Spinner<Integer> kcal = new Spinner<>(0, 3000, 450, 10);
        Spinner<Integer> protein = new Spinner<>(0, 200, 30);
        Spinner<Integer> carbs = new Spinner<>(0, 400, 55);
        Spinner<Integer> fat = new Spinner<>(0, 200, 12);
        TextField note = new TextField();
        note.setPromptText("Not");

        Button save = new Button("Kaydet");
        save.getStyleClass().add("button-success");
        save.setOnAction(e -> {
            if (name.getText().isBlank()) return;
            MealEntry meal = new MealEntry(
                    "meal-" + UUID.randomUUID(), LocalDate.now().toString(),
                    slot.getValue(), name.getText().trim(),
                    kcal.getValue(), protein.getValue(), carbs.getValue(), fat.getValue());
            meal.setNote(note.getText());
            data.getMeals().add(meal);
            WellnessStore.save(data);
            refresh.run();
            dialog.close();
        });

        VBox content = new VBox(10, grid(
                row("Öğün", name), row("Slot", slot), row("Kalori", kcal),
                row("Protein (g)", protein), row("Karb (g)", carbs),
                row("Yağ (g)", fat), row("Not", note)
        ), save);
        content.setPadding(new Insets(10));
        content.setPrefWidth(440);
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }

    // ====================================================================================== //
    // 9. TREND INSIGHTS
    // ====================================================================================== //

    public static VBox buildTrendCard(WellnessData data, Runnable refresh) {
        VBox card = newCard("📈  Trend Insights");

        TrendInsight[] holder = new TrendInsight[]{TrendInsightsService.compute(data, TrendInsightsService.PERIOD_7)};

        Label totals = new Label();
        Label advice = new Label();
        Label motivation = new Label();
        totals.getStyleClass().add("performance-value");
        totals.setWrapText(true);
        advice.getStyleClass().add("metric-key");
        advice.setWrapText(true);
        motivation.getStyleClass().add("dashboard-motivation");
        motivation.setWrapText(true);

        ProgressBar trendBar = new ProgressBar();
        trendBar.setPrefWidth(220);
        trendBar.getStyleClass().add("weekly-progress");

        Runnable render = () -> {
            TrendInsight insight = holder[0];
            totals.setText(String.format("Antrenman %d · Dakika %d · Kalori %d · Recovery %.1f · Trend %.1f%%",
                    insight.getTotalWorkouts(), insight.getTotalMinutes(), insight.getTotalCalories(),
                    insight.getAverageRecoveryScore(), insight.getTrendPercentage()));
            advice.setText("💡 " + insight.getAdvice());
            motivation.setText("🔥 " + insight.getMotivation());
            double normalized = Math.max(0, Math.min(1, (insight.getTrendPercentage() + 50) / 100.0));
            trendBar.setProgress(normalized);
        };
        render.run();

        Button b7 = new Button("7g");
        Button b30 = new Button("30g");
        Button b90 = new Button("90g");
        for (Button b : new Button[]{b7, b30, b90}) b.getStyleClass().add("button-link");
        b7.setOnAction(e -> { holder[0] = TrendInsightsService.compute(data, TrendInsightsService.PERIOD_7); render.run(); });
        b30.setOnAction(e -> { holder[0] = TrendInsightsService.compute(data, TrendInsightsService.PERIOD_30); render.run(); });
        b90.setOnAction(e -> { holder[0] = TrendInsightsService.compute(data, TrendInsightsService.PERIOD_90); render.run(); });

        HBox tabs = new HBox(6, b7, b30, b90);
        card.getChildren().addAll(tabs, totals, trendBar, advice, motivation);
        return card;
    }

    // ====================================================================================== //
    // 10. CHALLENGES & BADGES
    // ====================================================================================== //

    public static VBox buildChallengeCard(WellnessData data, Runnable refresh) {
        VBox card = newCard("🏆  Haftalık Görevler");

        ChallengeEngine.ensureWeeklyChallenges(data);

        int shown = 0;
        for (Challenge challenge : data.getChallenges()) {
            if (shown++ >= 3) break;
            VBox box = new VBox(3);
            box.getStyleClass().add("challenge-card");
            Label title = new Label(challenge.getBadgeIcon() + " " + challenge.getTitle());
            title.getStyleClass().add("section-title");
            ProgressBar bar = new ProgressBar(challenge.progressRatio());
            bar.setPrefWidth(220);
            bar.getStyleClass().add("weekly-progress");
            Label progress = new Label(challenge.getCurrentValue() + " / " + challenge.getTargetValue() + " " +
                    challenge.getUnit() + " · " + (challenge.isCompleted() ? "✅" : "⏳"));
            progress.getStyleClass().add("metric-key");
            box.getChildren().addAll(title, bar, progress);
            card.getChildren().add(box);
        }
        if (data.getChallenges().size() > 3) {
            Label more = new Label("+" + (data.getChallenges().size() - 3) + " görev daha");
            more.getStyleClass().add("metric-key");
            card.getChildren().add(more);
        }

        Button manage = primaryButton("Performansı Güncelle");
        manage.setOnAction(e -> openChallengeDialog(data, refresh));
        card.getChildren().add(manage);
        return card;
    }

    private static void openChallengeDialog(WellnessData data, Runnable refresh) {
        Dialog<Void> dialog = baseDialog("Haftalık Görevler");

        Spinner<Integer> ws = new Spinner<>(0, 14, 0);
        Spinner<Integer> steps = new Spinner<>(0, 200000, 0, 1000);
        Spinner<Integer> waterDays = new Spinner<>(0, 7, 0);
        Spinner<Integer> rec = new Spinner<>(0, 100, 0);
        Spinner<Integer> prs = new Spinner<>(0, 10, 0);

        Button update = new Button("Güncelle");
        update.getStyleClass().add("button-success");
        update.setOnAction(e -> {
            ChallengeEngine.evaluate(data, ws.getValue(), steps.getValue(), waterDays.getValue(), rec.getValue(), prs.getValue());
            WellnessStore.save(data);
            refresh.run();
        });
        Button reset = new Button("Yeni Hafta Başlat");
        reset.getStyleClass().add("button-link");
        reset.setOnAction(e -> {
            data.getChallenges().removeIf(c -> Challenge.STATUS_ACTIVE.equals(c.getStatus()));
            ChallengeEngine.ensureWeeklyChallenges(data);
            WellnessStore.save(data);
            refresh.run();
        });

        FlowPane badges = new FlowPane(8, 8);
        if (data.getEarnedBadges().isEmpty()) {
            Label empty = new Label("Henüz rozet kazanmadın.");
            empty.getStyleClass().add("metric-key");
            badges.getChildren().add(empty);
        } else {
            for (String b : data.getEarnedBadges()) {
                Label chip = new Label(b);
                chip.getStyleClass().add("badge-chip");
                badges.getChildren().add(chip);
            }
        }

        VBox content = new VBox(10,
                sectionTitle("Performansı Gir"),
                grid(row("Antrenman", ws), row("Toplam adım", steps), row("Su zinciri", waterDays),
                        row("Recovery Ort.", rec), row("Yeni PR", prs)),
                new HBox(10, update, reset),
                sectionTitle("Rozetler"),
                badges);
        content.setPadding(new Insets(10));
        content.setPrefWidth(500);
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }

    public static HBox buildBadgeStrip(WellnessData data) {
        HBox strip = new HBox(8);
        strip.setAlignment(Pos.CENTER_LEFT);
        if (data.getEarnedBadges().isEmpty()) {
            Label empty = new Label("🏅 Henüz rozet yok");
            empty.getStyleClass().add("metric-key");
            strip.getChildren().add(empty);
            return strip;
        }
        int max = Math.min(5, data.getEarnedBadges().size());
        for (int i = 0; i < max; i++) {
            Label chip = new Label(data.getEarnedBadges().get(i));
            chip.getStyleClass().add("badge-chip");
            strip.getChildren().add(chip);
        }
        if (data.getEarnedBadges().size() > max) {
            Label more = new Label("+" + (data.getEarnedBadges().size() - max));
            more.getStyleClass().add("metric-key");
            strip.getChildren().add(more);
        }
        return strip;
    }

    // ====================================================================================== //
    // HELPERS
    // ====================================================================================== //

    private static VBox newCard(String titleText) {
        VBox card = new VBox(8);
        card.getStyleClass().add("glass-card");
        card.setPadding(new Insets(14));
        Label title = new Label(titleText);
        title.getStyleClass().add("section-title");
        card.getChildren().add(title);
        VBox.setVgrow(card, Priority.NEVER);
        card.setMinWidth(260);
        return card;
    }

    private static Label sectionTitle(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("section-title");
        return l;
    }

    private static Button primaryButton(String text) {
        Button b = new Button(text);
        b.getStyleClass().add("button-success");
        b.setMaxWidth(Double.MAX_VALUE);
        return b;
    }

    private static Dialog<Void> baseDialog(String title) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.getDialogPane().getStylesheets().add(WellnessSections.class.getResource("/css/dark-theme.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("dashboard-root");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        return dialog;
    }

    private static class Row {
        final Label label;
        final Node node;
        Row(String l, Node n) { this.label = new Label(l); this.node = n; }
    }

    private static Row row(String label, Node node) { return new Row(label, node); }

    private static GridPane grid(Row... rows) {
        GridPane g = new GridPane();
        g.setHgap(10);
        g.setVgap(6);
        for (int i = 0; i < rows.length; i++) {
            rows[i].label.getStyleClass().add("metric-key");
            g.addRow(i, rows[i].label, rows[i].node);
        }
        return g;
    }

    @SuppressWarnings("unused")
    private static Map<String, String> templateMap() {
        return new LinkedHashMap<>(WeeklyWorkoutPlan.templateOptions());
    }

    @SuppressWarnings("unused")
    private static DayOfWeek today() { return LocalDate.now().getDayOfWeek(); }
}
