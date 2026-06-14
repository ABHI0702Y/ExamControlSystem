package com.examcontrol.repository;

import com.examcontrol.model.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ResultRepository extends JpaRepository<Result, Integer> {
    List<Result> findByStudentId(Integer studentId);
    List<Result> findByExamId(Integer examId);
    Optional<Result> findByStudentIdAndExamId(Integer studentId, Integer examId);
}
