package com.examcontrol.dto;

import java.util.Map;

public class SubmitExamRequest {
    private Integer examId;
    private Map<Integer, String> answers;

    public Integer getExamId() { return examId; }
    public void setExamId(Integer examId) { this.examId = examId; }
    public Map<Integer, String> getAnswers() { return answers; }
    public void setAnswers(Map<Integer, String> answers) { this.answers = answers; }
}
