package com.examcontrol.controller;

import com.examcontrol.dto.SubmitExamRequest;
import com.examcontrol.model.*;
import com.examcontrol.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/results")
public class ResultController {

    private final ResultRepository resultRepository;
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    public ResultController(ResultRepository resultRepository, ExamRepository examRepository,
                            QuestionRepository questionRepository, UserRepository userRepository) {
        this.resultRepository = resultRepository;
        this.examRepository = examRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<Result>> getAll(Authentication auth) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        if (user.getRole() == Role.STUDENT)
            return ResponseEntity.ok(resultRepository.findByStudentId(user.getId()));
        return ResponseEntity.ok(resultRepository.findAll());
    }

    @GetMapping("/exam/{examId}")
    public List<Result> getByExam(@PathVariable Integer examId) {
        return resultRepository.findByExamId(examId);
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submit(@RequestBody SubmitExamRequest req, Authentication auth) {
        User student = userRepository.findByUsername(auth.getName()).orElseThrow();
        Exam exam = examRepository.findById(req.getExamId()).orElse(null);
        if (exam == null) return ResponseEntity.badRequest().body("Exam not found");
        if (resultRepository.findByStudentIdAndExamId(student.getId(), req.getExamId()).isPresent())
            return ResponseEntity.badRequest().body("Exam already submitted");

        Result result = new Result();
        result.setStudent(student);
        result.setExam(exam);
        result.setTotalMarks(exam.getTotalMarks());

        int score = 0;
        for (Question question : exam.getQuestions()) {
            StudentAnswer sa = new StudentAnswer();
            sa.setResult(result);
            sa.setQuestion(question);
            String selected = req.getAnswers() != null ? req.getAnswers().get(question.getId()) : null;
            sa.setSelectedOption(selected);
            boolean correct = selected != null && selected.equals(question.getCorrectOption());
            sa.setCorrect(correct);
            if (correct) score += question.getMarks();
            result.getAnswers().add(sa);
        }

        result.setScore(score);
        double pct = exam.getTotalMarks() > 0 ? (score * 100.0 / exam.getTotalMarks()) : 0;
        result.setPercentage(pct);
        result.setPassed(score >= exam.getPassingMarks());
        return ResponseEntity.ok(resultRepository.save(result));
    }
}
