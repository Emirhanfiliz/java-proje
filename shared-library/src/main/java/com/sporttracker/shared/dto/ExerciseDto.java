package com.sporttracker.shared.dto;

public class ExerciseDto {
    private String  name;
    private Integer sets;
    private Integer reps;
    private Double  weight;
    private Integer durationInSeconds;
    private String  notes;

    public ExerciseDto() {}

    public String  getName()              { return name; }
    public Integer getSets()              { return sets; }
    public Integer getReps()              { return reps; }
    public Double  getWeight()            { return weight; }
    public Integer getDurationInSeconds() { return durationInSeconds; }
    public String  getNotes()             { return notes; }

    public void setName(String name)                        { this.name = name; }
    public void setSets(Integer sets)                       { this.sets = sets; }
    public void setReps(Integer reps)                       { this.reps = reps; }
    public void setWeight(Double weight)                    { this.weight = weight; }
    public void setDurationInSeconds(Integer d)             { this.durationInSeconds = d; }
    public void setNotes(String notes)                      { this.notes = notes; }
}
