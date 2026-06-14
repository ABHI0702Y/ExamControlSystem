package com.examcontrol.controller;

import com.examcontrol.model.*;
import com.examcontrol.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/exams")
public class ExamController {

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    public ExamController(ExamRepository examRepository, QuestionRepository questionRepository, UserRepository userRepository) {
        this.examRepository = examRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Exam> getAll(Authentication auth) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        if (user.getRole() == Role.STUDENT) return examRepository.findByActiveTrue();
        if (user.getRole() == Role.TEACHER) return examRepository.findByCreatedBy(user.getId());
        return examRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Exam> getById(@PathVariable Integer id) {
        return examRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ExamRequest req, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        Exam exam = new Exam();
        exam.setTitle(req.title);
        exam.setDescription(req.description);
        exam.setDurationMinutes(req.durationMinutes);
        exam.setPassingMarks(req.passingMarks);
        exam.setActive(req.active);
        exam.setCreatedBy(user.getId());
        if (req.startTime != null && !req.startTime.isEmpty()) exam.setStartTime(LocalDateTime.parse(req.startTime));
        if (req.endTime != null && !req.endTime.isEmpty()) exam.setEndTime(LocalDateTime.parse(req.endTime));
        if (req.questionIds != null) {
            List<Question> questions = questionRepository.findAllById(req.questionIds);
            exam.setQuestions(questions);
            exam.setTotalMarks(questions.stream().mapToInt(Question::getMarks).sum());
        }
        return ResponseEntity.ok(examRepository.save(exam));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody ExamRequest req) {
        return examRepository.findById(id).map(exam -> {
            exam.setTitle(req.title);
            exam.setDescription(req.description);
            exam.setDurationMinutes(req.durationMinutes);
            exam.setPassingMarks(req.passingMarks);
            exam.setActive(req.active);
            if (req.startTime != null && !req.startTime.isEmpty()) exam.setStartTime(LocalDateTime.parse(req.startTime));
            if (req.endTime != null && !req.endTime.isEmpty()) exam.setEndTime(LocalDateTime.parse(req.endTime));
            if (req.questionIds != null) {
                List<Question> questions = questionRepository.findAllById(req.questionIds);
                exam.setQuestions(questions);
                exam.setTotalMarks(questions.stream().mapToInt(Question::getMarks).sum());
            }
            return ResponseEntity.ok(examRepository.save(exam));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        examRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    static class ExamRequest {
        public String title;
        public String description;
        public int durationMinutes;
        public int passingMarks;
        public boolean active = true;
        public String startTime;
        public String endTime;
        public List<Integer> questionIds;
    }
}
