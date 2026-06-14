package com.examcontrol.dao;

import com.examcontrol.config.DatabaseConfig;
import com.examcontrol.model.Result;
import com.examcontrol.model.StudentAnswer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ResultDAO {

    private static final Logger log = LoggerFactory.getLogger(ResultDAO.class);

    private Connection conn() throws SQLException {
        return DatabaseConfig.getInstance().getConnection();
    }

    public Optional<Result> findById(int id) {
        String sql = """
            SELECT r.*, u.full_name AS student_name, e.title AS exam_title
            FROM results r
            JOIN users u ON r.student_id = u.id
            JOIN exams e ON r.exam_id = e.id
            WHERE r.id=?
            """;
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            log.error("findById failed", e);
        }
        return Optional.empty();
    }

    public List<Result> findByStudent(int studentId) {
        List<Result> list = new ArrayList<>();
        String sql = """
            SELECT r.*, u.full_name AS student_name, e.title AS exam_title
            FROM results r
            JOIN users u ON r.student_id = u.id
            JOIN exams e ON r.exam_id = e.id
            WHERE r.student_id=?
            ORDER BY r.submitted_at DESC
            """;
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            log.error("findByStudent failed", e);
        }
        return list;
    }

    public List<Result> findByExam(int examId) {
        List<Result> list = new ArrayList<>();
        String sql = """
            SELECT r.*, u.full_name AS student_name, e.title AS exam_title
            FROM results r
            JOIN users u ON r.student_id = u.id
            JOIN exams e ON r.exam_id = e.id
            WHERE r.exam_id=?
            ORDER BY r.percentage DESC
            """;
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            log.error("findByExam failed", e);
        }
        return list;
    }

    public List<Result> findAll() {
        List<Result> list = new ArrayList<>();
        String sql = """
            SELECT r.*, u.full_name AS student_name, e.title AS exam_title
            FROM results r
            JOIN users u ON r.student_id = u.id
            JOIN exams e ON r.exam_id = e.id
            ORDER BY r.submitted_at DESC
            """;
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            log.error("findAll failed", e);
        }
        return list;
    }

    public boolean hasAttempted(int studentId, int examId) {
        String sql = "SELECT 1 FROM results WHERE student_id=? AND exam_id=?";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, examId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            log.error("hasAttempted failed", e);
        }
        return false;
    }

    /** Saves result + all student answers in one transaction */
    public boolean saveResultWithAnswers(Result result, List<StudentAnswer> answers) {
        String rSql = """
            INSERT INTO results (student_id, exam_id, score, total_marks, percentage, passed)
            VALUES (?,?,?,?,?,?)
            """;
        String aSql = """
            INSERT INTO student_answers (result_id, question_id, selected_option, is_correct)
            VALUES (?,?,?,?)
            """;
        try (Connection c = conn()) {
            c.setAutoCommit(false);
            try (PreparedStatement rps = c.prepareStatement(rSql, Statement.RETURN_GENERATED_KEYS)) {
                rps.setInt(1, result.getStudentId());
                rps.setInt(2, result.getExamId());
                rps.setInt(3, result.getScore());
                rps.setInt(4, result.getTotalMarks());
                rps.setDouble(5, result.getPercentage());
                rps.setBoolean(6, result.isPassed());
                rps.executeUpdate();
                try (ResultSet keys = rps.getGeneratedKeys()) {
                    if (keys.next()) result.setId(keys.getInt(1));
                }
            }
            try (PreparedStatement aps = c.prepareStatement(aSql)) {
                for (StudentAnswer sa : answers) {
                    aps.setInt(1, result.getId());
                    aps.setInt(2, sa.getQuestionId());
                    aps.setString(3, sa.getSelectedOption());
                    aps.setBoolean(4, sa.isCorrect());
                    aps.addBatch();
                }
                aps.executeBatch();
            }
            c.commit();
            return true;
        } catch (SQLException e) {
            log.error("saveResultWithAnswers failed", e);
        }
        return false;
    }

    public List<StudentAnswer> findAnswersByResult(int resultId) {
        List<StudentAnswer> list = new ArrayList<>();
        String sql = "SELECT * FROM student_answers WHERE result_id=?";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, resultId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StudentAnswer sa = new StudentAnswer();
                    sa.setId(rs.getInt("id"));
                    sa.setResultId(rs.getInt("result_id"));
                    sa.setQuestionId(rs.getInt("question_id"));
                    sa.setSelectedOption(rs.getString("selected_option"));
                    sa.setCorrect(rs.getBoolean("is_correct"));
                    list.add(sa);
                }
            }
        } catch (SQLException e) {
            log.error("findAnswersByResult failed", e);
        }
        return list;
    }

    public int countTotalResults() {
        String sql = "SELECT COUNT(*) FROM results";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            log.error("countTotalResults failed", e);
        }
        return 0;
    }

    private Result map(ResultSet rs) throws SQLException {
        Result r = new Result();
        r.setId(rs.getInt("id"));
        r.setStudentId(rs.getInt("student_id"));
        r.setStudentName(rs.getString("student_name"));
        r.setExamId(rs.getInt("exam_id"));
        r.setExamTitle(rs.getString("exam_title"));
        r.setScore(rs.getInt("score"));
        r.setTotalMarks(rs.getInt("total_marks"));
        r.setPercentage(rs.getDouble("percentage"));
        r.setPassed(rs.getBoolean("passed"));
        Timestamp sa = rs.getTimestamp("submitted_at");
        if (sa != null) r.setSubmittedAt(sa.toLocalDateTime());
        return r;
    }
}
