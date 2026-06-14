package com.examcontrol.model;

import java.time.LocalDateTime;

public class Exam {
    private int id;
    private String title;
    private String description;
    private int durationMinutes;
    private int totalMarks;
    private int passingMarks;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean active;
    private int createdBy;
    private String createdByName;
    private LocalDateTime createdAt;
    private int questionCount;

    public Exam() {}

    // Getters & Setters
    public int getId()                              { return id; }
    public void setId(int id)                       { this.id = id; }
    public String getTitle()                        { return title; }
    public void setTitle(String title)              { this.title = title; }
    public String getDescription()                  { return description; }
    public void setDescription(String d)            { this.description = d; }
    public int getDurationMinutes()                 { return durationMinutes; }
    public void setDurationMinutes(int d)           { this.durationMinutes = d; }
    public int getTotalMarks()                      { return totalMarks; }
    public void setTotalMarks(int t)                { this.totalMarks = t; }
    public int getPassingMarks()                    { return passingMarks; }
    public void setPassingMarks(int p)              { this.passingMarks = p; }
    public LocalDateTime getStartTime()             { return startTime; }
    public void setStartTime(LocalDateTime s)       { this.startTime = s; }
    public LocalDateTime getEndTime()               { return endTime; }
    public void setEndTime(LocalDateTime e)         { this.endTime = e; }
    public boolean isActive()                       { return active; }
    public void setActive(boolean active)           { this.active = active; }
    public int getCreatedBy()                       { return createdBy; }
    public void setCreatedBy(int id)                { this.createdBy = id; }
    public String getCreatedByName()                { return createdByName; }
    public void setCreatedByName(String n)          { this.createdByName = n; }
    public LocalDateTime getCreatedAt()             { return createdAt; }
    public void setCreatedAt(LocalDateTime t)       { this.createdAt = t; }
    public int getQuestionCount()                   { return questionCount; }
    public void setQuestionCount(int c)             { this.questionCount = c; }

    @Override
    public String toString() { return title; }
}
