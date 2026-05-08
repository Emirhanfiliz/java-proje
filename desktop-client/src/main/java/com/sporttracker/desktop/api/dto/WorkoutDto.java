package com.sporttracker.desktop.api.dto;

import java.util.List;

public class WorkoutDto {
    private String       id;
    private String       userId;
    private String       name;
    private String       description;
    private String       date;
    private List<Object> exercises;

    public String getId()          { return id; }
    public String getUserId()      { return userId; }
    public String getName()        { return name; }
    public String getDescription() { return description; }
    public String getDate()        { return date; }

    public String toDisplayString() {
        String dateShort = (date != null && date.length() >= 10) ? date.substring(0, 10) : "";
        return name + (dateShort.isEmpty() ? "" : "  •  " + dateShort);
    }
}
