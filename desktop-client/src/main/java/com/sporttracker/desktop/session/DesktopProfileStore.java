package com.sporttracker.desktop.session;

import java.util.prefs.Preferences;

public final class DesktopProfileStore {

    public static final class ProfileData {
        public final String avatar;
        public final String displayName;
        public final double heightCm;
        public final double weightKg;
        public final int dailyGoal;

        public ProfileData(String avatar, String displayName, double heightCm, double weightKg, int dailyGoal) {
            this.avatar = avatar;
            this.displayName = displayName;
            this.heightCm = heightCm;
            this.weightKg = weightKg;
            this.dailyGoal = dailyGoal;
        }
    }

    private static final Preferences PREFS = Preferences.userRoot().node("sporttracker/desktop/profile");
    private static final String KEY_AVATAR = "avatar";
    private static final String KEY_DISPLAY_NAME = "displayName";
    private static final String KEY_HEIGHT_CM = "heightCm";
    private static final String KEY_WEIGHT_KG = "weightKg";
    private static final String KEY_DAILY_GOAL = "dailyGoal";

    private DesktopProfileStore() {}

    public static ProfileData load() {
        return new ProfileData(
                PREFS.get(KEY_AVATAR, "🏃"),
                PREFS.get(KEY_DISPLAY_NAME, ""),
                PREFS.getDouble(KEY_HEIGHT_CM, 175.0),
                PREFS.getDouble(KEY_WEIGHT_KG, 74.0),
                PREFS.getInt(KEY_DAILY_GOAL, 10000)
        );
    }

    public static void save(ProfileData data) {
        PREFS.put(KEY_AVATAR, data.avatar);
        PREFS.put(KEY_DISPLAY_NAME, data.displayName);
        PREFS.putDouble(KEY_HEIGHT_CM, data.heightCm);
        PREFS.putDouble(KEY_WEIGHT_KG, data.weightKg);
        PREFS.putInt(KEY_DAILY_GOAL, data.dailyGoal);
    }
}
