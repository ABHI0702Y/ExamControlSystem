package com.examcontrol.model;

public class StudentAnswer {
    private int id;
    private int resultId;
    private int questionId;
    private String selectedOption;
    private boolean correct;

    public StudentAnswer() {}

    public StudentAnswer(int resultId, int questionId, String selectedOption, boolean correct) {
        this.resultId = resultId;
        this.questionId = questionId;
        this.selectedOption = selectedOption;
        this.correct = correct;
    }

    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }
    public int getResultId()                    { return resultId; }
    public void setResultId(int id)             { this.resultId = id; }
    public int getQuestionId()                  { return questionId; }
    public void setQuestionId(int id)           { this.questionId = id; }
    public String getSelectedOption()           { return selectedOption; }
    public void setSelectedOption(String o)     { this.selectedOption = o; }
    public boolean isCorrect()                  { return correct; }
    public void setCorrect(boolean correct)     { this.correct = correct; }
}
