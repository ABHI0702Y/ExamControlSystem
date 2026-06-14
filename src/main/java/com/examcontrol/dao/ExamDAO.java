package com.examcontrol.dao;

import com.examcontrol.config.DatabaseConfig;
import com.examcontrol.model.Exam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExamDAO {

    private static final Logger log = LoggerFactory.getLogger(ExamDAO.class);

    private Connection conn() throws SQLException {
        return DatabaseConfig.getInstance().getConnection();
    }

    public Optional<Exam> findById(int id) {
        String sql = """
            SELECT e.*, u.full_name AS creator_name,
                   (SELECT COUNT(*) FROM exam_questions eq WHERE eq.exam_id = e.id) AS q_count
            FROM exams e LEFT JOIN users u ON e.created_by = u.id
            WHERE e.id=?
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

    public List<Exam> findAll() {
        List<Exam> list = new ArrayList<>();
        String sql = """
            SELECT e.*, u.full_name AS creator_name,
                   (SELECT COUNT(*) FROM exam_questions eq WHERE eq.exam_id = e.id) AS q_count
            FROM exams e LEFT JOIN users u ON e.created_by = u.id
            ORDER BY e.created_at DESC
            """;
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            log.error("findAll failed", e);
        }
        return list;
    }

    public List<Exam> findActive() {
        List<Exam> list = new ArrayList<>();
        String sql = """
            SELECT e.*, u.full_name AS creator_name,
                   (SELECT COUNT(*) FROM exam_questions eq WHERE eq.exam_id = e.id) AS q_count
            FROM exams e LEFT JOIN users u ON e.created_by = u.id
            WHERE e.is_active=1
            ORDER BY e.title
            """;
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            log.error("findActive failed", e);
        }
        return list;
    }

    /** Active exams the student hasn't taken yet */
    public List<Exam> findAvailableForStudent(int studentId) {
        List<Exam> list = new ArrayList<>();
        String sql = """
            SELECT e.*, u.full_name AS creator_name,
                   (SELECT COUNT(*) FROM exam_questions eq WHERE eq.exam_id = e.id) AS q_count
            FROM exams e LEFT JOIN users u ON e.created_by = u.id
            WHERE e.is_active = 1
              AND e.id NOT IN (SELECT exam_id FROM results WHERE student_id = ?)
            ORDER BY e.title
            """;
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            log.error("findAvailableForStudent failed", e);
        }
        return list;
    }

    public boolean create(Exam exam) {
        String sql = """
            INSERT INTO exams (title, description, duration_minutes, total_marks,
                               passing_marks, start_time, end_time, is_active, created_by)
            VALUES (?,?,?,?,?,?,?,?,?)
            """;
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, exam.getTitle());
            ps.setString(2, exam.getDescription());
            ps.setInt(3, exam.getDurationMinutes());
            ps.setInt(4, exam.getTotalMarks());
            ps.setInt(5, exam.getPassingMarks());
            ps.setObject(6, exam.getStartTime() != null ? Timestamp.valueOf(exam.getStartTime()) : null);
            ps.setObject(7, exam.getEndTime() != null ? Timestamp.valueOf(exam.getEndTime()) : null);
            ps.setBoolean(8, exam.isActive());
            ps.setInt(9, exam.getCreatedBy());
            if (ps.executeUpdate() > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) exam.setId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            log.error("create exam failed", e);
        }
        return false;
    }

    public boolean update(Exam exam) {
        String sql = """
            UPDATE exams SET title=?, description=?, duration_minutes=?, total_marks=?,
                             passing_marks=?, start_time=?, end_time=?, is_active=?
            WHERE id=?
            """;
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, exam.getTitle());
            ps.setString(2, exam.getDescription());
            ps.setInt(3, exam.getDurationMinutes());
            ps.setInt(4, exam.getTotalMarks());
            ps.setInt(5, exam.getPassingMarks());
            ps.setObject(6, exam.getStartTime() != null ? Timestamp.valueOf(exam.getStartTime()) : null);
            ps.setObject(7, exam.getEndTime() != null ? Timestamp.valueOf(exam.getEndTime()) : null);
            ps.setBoolean(8, exam.isActive());
            ps.setInt(9, exam.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("update exam failed", e);
        }
        return false;
    }

    public boolean deleteById(int id) {
        String sql = "DELETE FROM exams WHERE id=?";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("deleteById failed", e);
        }
        return false;
    }

    public boolean addQuestion(int examId, int questionId, int order) {
        String sql = "INSERT IGNORE INTO exam_questions (exam_id, question_id, question_order) VALUES (?,?,?)";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, examId);
            ps.setInt(2, questionId);
            ps.setInt(3, order);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("addQuestion failed", e);
        }
        return false;
    }

    public boolean removeQuestion(int examId, int questionId) {
        String sql = "DELETE FROM exam_questions WHERE exam_id=? AND question_id=?";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, examId);
            ps.setInt(2, questionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("removeQuestion failed", e);
        }
        return false;
    }

    public boolean updateTotalMarks(int examId) {
        String sql = """
            UPDATE exams SET total_marks =
                (SELECT COALESCE(SUM(q.marks),0) FROM exam_questions eq
                 JOIN questions q ON eq.question_id = q.id WHERE eq.exam_id = ?)
            WHERE id = ?
            """;
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, examId);
            ps.setInt(2, examId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("updateTotalMarks failed", e);
        }
        return false;
    }

    private Exam map(ResultSet rs) throws SQLException {
        Exam e = new Exam();
        e.setId(rs.getInt("id"));
        e.setTitle(rs.getString("title"));
        e.setDescription(rs.getString("description"));
        e.setDurationMinutes(rs.getInt("duration_minutes"));
        e.setTotalMarks(rs.getInt("total_marks"));
        e.setPassingMarks(rs.getInt("passing_marks"));
        Timestamp st = rs.getTimestamp("start_time");
        if (st != null) e.setStartTime(st.toLocalDateTime());
        Timestamp et = rs.getTimestamp("end_time");
        if (et != null) e.setEndTime(et.toLocalDateTime());
        e.setActive(rs.getBoolean("is_active"));
        e.setCreatedBy(rs.getInt("created_by"));
        e.setCreatedByName(rs.getString("creator_name"));
        Timestamp ca = rs.getTimestamp("created_at");
        if (ca != null) e.setCreatedAt(ca.toLocalDateTime());
        e.setQuestionCount(rs.getInt("q_count"));
        return e;
    }
}
