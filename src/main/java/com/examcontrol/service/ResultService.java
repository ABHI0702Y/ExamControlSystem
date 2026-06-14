package com.examcontrol.service;

import com.examcontrol.dao.ResultDAO;
import com.examcontrol.model.Question;
import com.examcontrol.model.Result;
import com.examcontrol.model.StudentAnswer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ResultService {

    private final ResultDAO resultDAO = new ResultDAO();

    public List<Result> getAllResults()                  { return resultDAO.findAll(); }
    public List<Result> getResultsByStudent(int id)     { return resultDAO.findByStudent(id); }
    public List<Result> getResultsByExam(int id)        { return resultDAO.findByExam(id); }
    public Optional<Result> getResultById(int id)       { return resultDAO.findById(id); }
    public boolean hasAttempted(int studentId, int examId) { return resultDAO.hasAttempted(studentId, examId); }
    public int countTotalResults()                       { return resultDAO.countTotalResults(); }
    public List<StudentAnswer> getAnswersByResult(int resultId) { return resultDAO.findAnswersByResult(resultId); }

    /**
     * Grades and persists a student's exam submission.
     * @param studentId  student's user id
     * @param examId     exam id
     * @param questions  ordered list of exam questions
     * @param answers    map of questionId → selected option (A/B/C/D), null if skipped
     * @param totalMarks exam's total mark value
     * @param passingMarks exam's passing threshold
     */
    public UserService.ServiceResult submitExam(int studentId, int examId,
                                                List<Question> questions,
                                                Map<Integer, String> answers,
                                                int totalMarks, int passingMarks) {
        if (resultDAO.hasAttempted(studentId, examId))
            return UserService.ServiceResult.error("You have already submitted this exam.");

        int score = 0;
        List<StudentAnswer> studentAnswers = new ArrayList<>();

        for (Question q : questions) {
            String selected = answers.get(q.getId());
            boolean correct = selected != null && selected.equalsIgnoreCase(q.getCorrectOption());
            if (correct) score += q.getMarks();
            studentAnswers.add(new StudentAnswer(0, q.getId(), selected, correct));
        }

        double percentage = totalMarks > 0 ? (score * 100.0 / totalMarks) : 0;
        boolean passed = score >= passingMarks;

        Result result = new Result();
        result.setStudentId(studentId);
        result.setExamId(examId);
        result.setScore(score);
        result.setTotalMarks(totalMarks);
        result.setPercentage(Math.round(percentage * 100.0) / 100.0);
        result.setPassed(passed);

        return resultDAO.saveResultWithAnswers(result, studentAnswers)
            ? UserService.ServiceResult.success(
                String.format("Submitted! Score: %d/%d (%.1f%%) — %s",
                    score, totalMarks, percentage, passed ? "PASSED" : "FAILED"))
            : UserService.ServiceResult.error("Database error saving result.");
    }
}
