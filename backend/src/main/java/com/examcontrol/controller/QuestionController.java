package com.examcontrol.controller;

import com.examcontrol.model.Question;
import com.examcontrol.repository.QuestionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionRepository questionRepository;

    public QuestionController(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @GetMapping
    public List<Question> getAll() { return questionRepository.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Question> getById(@PathVariable Integer id) {
        return questionRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Question create(@RequestBody Question question) { return questionRepository.save(question); }

    @PutMapping("/{id}")
    public ResponseEntity<Question> update(@PathVariable Integer id, @RequestBody Question updated) {
        return questionRepository.findById(id).map(q -> {
            q.setQuestionText(updated.getQuestionText());
            q.setOptionA(updated.getOptionA());
            q.setOptionB(updated.getOptionB());
            q.setOptionC(updated.getOptionC());
            q.setOptionD(updated.getOptionD());
            q.setCorrectOption(updated.getCorrectOption());
            q.setMarks(updated.getMarks());
            q.setDifficulty(updated.getDifficulty());
            q.setSubject(updated.getSubject());
            return ResponseEntity.ok(questionRepository.save(q));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        questionRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
