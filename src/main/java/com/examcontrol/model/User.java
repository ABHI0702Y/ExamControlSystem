package com.examcontrol.model;

import java.time.LocalDateTime;

public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private Role role;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User() {}

    public User(int id, String username, String fullName, String email, Role role, boolean active) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.active = active;
    }

    // Getters & Setters
    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }
    public String getUsername()                 { return username; }
    public void setUsername(String username)    { this.username = username; }
    public String getPasswordHash()             { return passwordHash; }
    public void setPasswordHash(String h)       { this.passwordHash = h; }
    public String getFullName()                 { return fullName; }
    public void setFullName(String fullName)    { this.fullName = fullName; }
    public String getEmail()                    { return email; }
    public void setEmail(String email)          { this.email = email; }
    public Role getRole()                       { return role; }
    public void setRole(Role role)              { this.role = role; }
    public boolean isActive()                   { return active; }
    public void setActive(boolean active)       { this.active = active; }
    public LocalDateTime getCreatedAt()         { return createdAt; }
    public void setCreatedAt(LocalDateTime t)   { this.createdAt = t; }
    public LocalDateTime getUpdatedAt()         { return updatedAt; }
    public void setUpdatedAt(LocalDateTime t)   { this.updatedAt = t; }

    @Override
    public String toString() { return fullName + " (" + username + ")"; }
}
