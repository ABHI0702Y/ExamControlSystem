package com.examcontrol.model;

import java.time.LocalDateTime;

public class Question {
    private int id;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctOption; // A/B/C/D
    private int marks;
    private String difficulty;   // EASY/MEDIUM/HARD
    private String subject;
    private int createdBy;
    private String createdByName;
    private LocalDateTime createdAt;

    public Question() { this.marks = 1; this.difficulty = "MEDIUM"; }

    // Getters & Setters
    public int getId()                             { return id; }
    public void setId(int id)                      { this.id = id; }
    public String getQuestionText()                { return questionText; }
    public void setQuestionText(String t)          { this.questionText = t; }
    public String getOptionA()                     { return optionA; }
    public void setOptionA(String o)               { this.optionA = o; }
    public String getOptionB()                     { return optionB; }
    public void setOptionB(String o)               { this.optionB = o; }
    public String getOptionC()                     { return optionC; }
    public void setOptionC(String o)               { this.optionC = o; }
    public String getOptionD()                     { return optionD; }
    public void setOptionD(String o)               { this.optionD = o; }
    public String getCorrectOption()               { return correctOption; }
    public void setCorrectOption(String c)         { this.correctOption = c; }
    public int getMarks()                          { return marks; }
    public void setMarks(int marks)                { this.marks = marks; }
    public String getDifficulty()                  { return difficulty; }
    public void setDifficulty(String d)            { this.difficulty = d; }
    public String getSubject()                     { return subject; }
    public void setSubject(String s)               { this.subject = s; }
    public int getCreatedBy()                      { return createdBy; }
    public void setCreatedBy(int id)               { this.createdBy = id; }
    public String getCreatedByName()               { return createdByName; }
    public void setCreatedByName(String n)         { this.createdByName = n; }
    public LocalDateTime getCreatedAt()            { return createdAt; }
    public void setCreatedAt(LocalDateTime t)      { this.createdAt = t; }

    public String getOptionByLetter(String letter) {
        return switch (letter.toUpperCase()) {
            case "A" -> optionA;
            case "B" -> optionB;
            case "C" -> optionC;
            case "D" -> optionD;
            default  -> "";
        };
    }
}
