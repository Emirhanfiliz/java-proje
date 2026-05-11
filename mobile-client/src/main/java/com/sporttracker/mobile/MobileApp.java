package com.sporttracker.mobile;

import com.gluonhq.charm.glisten.application.MobileApplication;
import com.sporttracker.mobile.view.AddWorkoutView;
import com.sporttracker.mobile.view.LoginView;
import com.sporttracker.mobile.view.WorkoutDetailView;
import com.sporttracker.mobile.view.WorkoutListView;
import com.sporttracker.mobile.view.RegisterView;

public class MobileApp extends MobileApplication {

    public static final String WORKOUT_VIEW        = "WorkoutListView";
    public static final String ADD_WORKOUT_VIEW    = "AddWorkoutView";
    public static final String WORKOUT_DETAIL_VIEW = "WorkoutDetailView";
    public static final String REGISTER_VIEW       = "RegisterView";

    @Override
    public void init() {
        addViewFactory(HOME_VIEW,            LoginView::new);
        addViewFactory(WORKOUT_VIEW,         WorkoutListView::new);
        addViewFactory(ADD_WORKOUT_VIEW,     AddWorkoutView::new);
        addViewFactory(WORKOUT_DETAIL_VIEW,  WorkoutDetailView::new);
        addViewFactory(REGISTER_VIEW,        RegisterView::new);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
