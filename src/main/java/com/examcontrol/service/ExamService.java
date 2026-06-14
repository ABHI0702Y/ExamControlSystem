package com.examcontrol.service;

import com.examcontrol.dao.ExamDAO;
import com.examcontrol.dao.QuestionDAO;
import com.examcontrol.model.Exam;
import com.examcontrol.model.Question;
import com.examcontrol.util.ValidationUtil;

import java.util.List;
import java.util.Optional;

public class ExamService {

    private final ExamDAO examDAO       = new ExamDAO();
    private final QuestionDAO questionDAO = new QuestionDAO();

    public List<Exam> getAllExams()                          { return examDAO.findAll(); }
    public List<Exam> getActiveExams()                      { return examDAO.findActive(); }
    public Optional<Exam> getExamById(int id)               { return examDAO.findById(id); }
    public List<Exam> getAvailableForStudent(int studentId) { return examDAO.findAvailableForStudent(studentId); }
    public List<Question> getQuestionsForExam(int examId)   { return questionDAO.findByExamId(examId); }
    public List<Question> getAllQuestions()                  { return questionDAO.findAll(); }
    public Optional<Question> getQuestionById(int id)       { return questionDAO.findById(id); }
    public List<Question> getQuestionsNotInExam(int examId) { return questionDAO.findNotInExam(examId); }

    public UserService.ServiceResult createExam(Exam exam) {
        if (ValidationUtil.isBlank(exam.getTitle()))        return UserService.ServiceResult.error("Title is required.");
        if (exam.getDurationMinutes() <= 0)                 return UserService.ServiceResult.error("Duration must be positive.");
        if (exam.getPassingMarks() < 0)                     return UserService.ServiceResult.error("Passing marks cannot be negative.");
        return examDAO.create(exam)
            ? UserService.ServiceResult.success("Exam created.")
            : UserService.ServiceResult.error("Database error creating exam.");
    }

    public UserService.ServiceResult updateExam(Exam exam) {
        if (ValidationUtil.isBlank(exam.getTitle()))        return UserService.ServiceResult.error("Title is required.");
        if (exam.getDurationMinutes() <= 0)                 return UserService.ServiceResult.error("Duration must be positive.");
        return examDAO.update(exam)
            ? UserService.ServiceResult.success("Exam updated.")
            : UserService.ServiceResult.error("Database error updating exam.");
    }

    public UserService.ServiceResult deleteExam(int id) {
        return examDAO.deleteById(id)
            ? UserService.ServiceResult.success("Exam deleted.")
            : UserService.ServiceResult.error("Database error deleting exam.");
    }

    public UserService.ServiceResult addQuestionToExam(int examId, int questionId) {
        List<Question> current = questionDAO.findByExamId(examId);
        int order = current.size() + 1;
        boolean ok = examDAO.addQuestion(examId, questionId, order);
        if (ok) examDAO.updateTotalMarks(examId);
        return ok ? UserService.ServiceResult.success("Question added.")
                  : UserService.ServiceResult.error("Could not add question (already present?).");
    }

    public UserService.ServiceResult removeQuestionFromExam(int examId, int questionId) {
        boolean ok = examDAO.removeQuestion(examId, questionId);
        if (ok) examDAO.updateTotalMarks(examId);
        return ok ? UserService.ServiceResult.success("Question removed.")
                  : UserService.ServiceResult.error("Database error removing question.");
    }

    public UserService.ServiceResult createQuestion(Question q) {
        if (ValidationUtil.isBlank(q.getQuestionText())) return UserService.ServiceResult.error("Question text required.");
        if (ValidationUtil.isBlank(q.getOptionA()))      return UserService.ServiceResult.error("Option A required.");
        if (ValidationUtil.isBlank(q.getOptionB()))      return UserService.ServiceResult.error("Option B required.");
        if (ValidationUtil.isBlank(q.getOptionC()))      return UserService.ServiceResult.error("Option C required.");
        if (ValidationUtil.isBlank(q.getOptionD()))      return UserService.ServiceResult.error("Option D required.");
        if (ValidationUtil.isBlank(q.getCorrectOption())) return UserService.ServiceResult.error("Correct option required.");
        return questionDAO.create(q)
            ? UserService.ServiceResult.success("Question created.")
            : UserService.ServiceResult.error("Database error creating question.");
    }

    public UserService.ServiceResult updateQuestion(Question q) {
        if (ValidationUtil.isBlank(q.getQuestionText())) return UserService.ServiceResult.error("Question text required.");
        return questionDAO.update(q)
            ? UserService.ServiceResult.success("Question updated.")
            : UserService.ServiceResult.error("Database error updating question.");
    }

    public UserService.ServiceResult deleteQuestion(int id) {
        return questionDAO.deleteById(id)
            ? UserService.ServiceResult.success("Question deleted.")
            : UserService.ServiceResult.error("Database error deleting question.");
    }
}
