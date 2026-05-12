package com.sporttracker.desktop.api.dto;

public class ExerciseItemDto {
    private String  name;
    private Integer sets;
    private Integer reps;
    private Double  weight;
    private Integer durationInSeconds;
    private String  notes;

    public ExerciseItemDto(String name, int sets, int reps, double weight) {
        this.name              = name;
        this.sets              = sets;
        this.reps              = reps;
        this.weight            = weight;
        this.durationInSeconds = 0;
        this.notes             = "";
    }

    public String  getName()              { return name; }
    public Integer getSets()              { return sets; }
    public Integer getReps()              { return reps; }
    public Double  getWeight()            { return weight; }
    public Integer getDurationInSeconds() { return durationInSeconds; }
    public String  getNotes()             { return notes; }

    public String toDisplayString() {
        StringBuilder sb = new StringBuilder(name != null ? name : "?");
        if (sets != null && reps != null)          sb.append("  ").append(sets).append("×").append(reps);
        if (weight != null && weight > 0)          sb.append("  @").append(weight.intValue()).append(" kg");
        return sb.toString();
    }
}
