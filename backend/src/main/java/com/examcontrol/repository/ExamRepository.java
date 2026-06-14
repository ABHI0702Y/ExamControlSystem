package com.examcontrol.repository;

import com.examcontrol.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Integer> {
    List<Exam> findByActiveTrue();
    List<Exam> findByCreatedBy(Integer userId);
}
