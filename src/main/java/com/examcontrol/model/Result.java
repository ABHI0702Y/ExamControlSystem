package com.examcontrol.model;

import java.time.LocalDateTime;

public class Result {
    private int id;
    private int studentId;
    private String studentName;
    private int examId;
    private String examTitle;
    private int score;
    private int totalMarks;
    private double percentage;
    private boolean passed;
    private LocalDateTime submittedAt;

    public Result() {}

    public int getId()                              { return id; }
    public void setId(int id)                       { this.id = id; }
    public int getStudentId()                       { return studentId; }
    public void setStudentId(int id)                { this.studentId = id; }
    public String getStudentName()                  { return studentName; }
    public void setStudentName(String n)            { this.studentName = n; }
    public int getExamId()                          { return examId; }
    public void setExamId(int id)                   { this.examId = id; }
    public String getExamTitle()                    { return examTitle; }
    public void setExamTitle(String t)              { this.examTitle = t; }
    public int getScore()                           { return score; }
    public void setScore(int score)                 { this.score = score; }
    public int getTotalMarks()                      { return totalMarks; }
    public void setTotalMarks(int t)                { this.totalMarks = t; }
    public double getPercentage()                   { return percentage; }
    public void setPercentage(double p)             { this.percentage = p; }
    public boolean isPassed()                       { return passed; }
    public void setPassed(boolean passed)           { this.passed = passed; }
    public LocalDateTime getSubmittedAt()           { return submittedAt; }
    public void setSubmittedAt(LocalDateTime t)     { this.submittedAt = t; }
}
