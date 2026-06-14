package com.examcontrol.dao;

import com.examcontrol.config.DatabaseConfig;
import com.examcontrol.model.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuestionDAO {

    private static final Logger log = LoggerFactory.getLogger(QuestionDAO.class);

    private Connection conn() throws SQLException {
        return DatabaseConfig.getInstance().getConnection();
    }

    public Optional<Question> findById(int id) {
        String sql = """
            SELECT q.*, u.full_name AS creator_name
            FROM questions q LEFT JOIN users u ON q.created_by = u.id
            WHERE q.id=?
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

    public List<Question> findAll() {
        List<Question> list = new ArrayList<>();
        String sql = """
            SELECT q.*, u.full_name AS creator_name
            FROM questions q LEFT JOIN users u ON q.created_by = u.id
            ORDER BY q.subject, q.id
            """;
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            log.error("findAll failed", e);
        }
        return list;
    }

    public List<Question> findBySubject(String subject) {
        List<Question> list = new ArrayList<>();
        String sql = """
            SELECT q.*, u.full_name AS creator_name
            FROM questions q LEFT JOIN users u ON q.created_by = u.id
            WHERE q.subject LIKE ? ORDER BY q.id
            """;
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + subject + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            log.error("findBySubject failed", e);
        }
        return list;
    }

    public List<Question> findByExamId(int examId) {
        List<Question> list = new ArrayList<>();
        String sql = """
            SELECT q.*, u.full_name AS creator_name
            FROM questions q
            JOIN exam_questions eq ON q.id = eq.question_id
            LEFT JOIN users u ON q.created_by = u.id
            WHERE eq.exam_id=?
            ORDER BY eq.question_order
            """;
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            log.error("findByExamId failed", e);
        }
        return list;
    }

    public List<Question> findNotInExam(int examId) {
        List<Question> list = new ArrayList<>();
        String sql = """
            SELECT q.*, u.full_name AS creator_name
            FROM questions q LEFT JOIN users u ON q.created_by = u.id
            WHERE q.id NOT IN (SELECT question_id FROM exam_questions WHERE exam_id=?)
            ORDER BY q.subject, q.id
            """;
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            log.error("findNotInExam failed", e);
        }
        return list;
    }

    public boolean create(Question q) {
        String sql = """
            INSERT INTO questions (question_text, option_a, option_b, option_c, option_d,
                                   correct_option, marks, difficulty, subject, created_by)
            VALUES (?,?,?,?,?,?,?,?,?,?)
            """;
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, q.getQuestionText());
            ps.setString(2, q.getOptionA());
            ps.setString(3, q.getOptionB());
            ps.setString(4, q.getOptionC());
            ps.setString(5, q.getOptionD());
            ps.setString(6, q.getCorrectOption());
            ps.setInt(7, q.getMarks());
            ps.setString(8, q.getDifficulty());
            ps.setString(9, q.getSubject());
            ps.setInt(10, q.getCreatedBy());
            if (ps.executeUpdate() > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) q.setId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            log.error("create question failed", e);
        }
        return false;
    }

    public boolean update(Question q) {
        String sql = """
            UPDATE questions SET question_text=?, option_a=?, option_b=?, option_c=?, option_d=?,
                                 correct_option=?, marks=?, difficulty=?, subject=?
            WHERE id=?
            """;
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, q.getQuestionText());
            ps.setString(2, q.getOptionA());
            ps.setString(3, q.getOptionB());
            ps.setString(4, q.getOptionC());
            ps.setString(5, q.getOptionD());
            ps.setString(6, q.getCorrectOption());
            ps.setInt(7, q.getMarks());
            ps.setString(8, q.getDifficulty());
            ps.setString(9, q.getSubject());
            ps.setInt(10, q.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("update question failed", e);
        }
        return false;
    }

    public boolean deleteById(int id) {
        String sql = "DELETE FROM questions WHERE id=?";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("deleteById failed", e);
        }
        return false;
    }

    private Question map(ResultSet rs) throws SQLException {
        Question q = new Question();
        q.setId(rs.getInt("id"));
        q.setQuestionText(rs.getString("question_text"));
        q.setOptionA(rs.getString("option_a"));
        q.setOptionB(rs.getString("option_b"));
        q.setOptionC(rs.getString("option_c"));
        q.setOptionD(rs.getString("option_d"));
        q.setCorrectOption(rs.getString("correct_option"));
        q.setMarks(rs.getInt("marks"));
        q.setDifficulty(rs.getString("difficulty"));
        q.setSubject(rs.getString("subject"));
        q.setCreatedBy(rs.getInt("created_by"));
        q.setCreatedByName(rs.getString("creator_name"));
        Timestamp ca = rs.getTimestamp("created_at");
        if (ca != null) q.setCreatedAt(ca.toLocalDateTime());
        return q;
    }
}
