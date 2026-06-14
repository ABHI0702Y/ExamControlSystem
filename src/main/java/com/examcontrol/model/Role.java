package com.examcontrol.model;

public enum Role {
    ADMIN, TEACHER, STUDENT;

    public static Role fromString(String s) {
        return Role.valueOf(s.toUpperCase());
    }
}
