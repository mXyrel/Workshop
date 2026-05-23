package com.workshop.bbs.model;

/**
 * Represents a student who can borrow books.
 */
public class Student {
    private int id;
    private String studentNumber;
    private String fullName;
    private String course;
    private String yearLevel;
    private String email;

    public Student() {}

    public Student(int id, String studentNumber, String fullName,
                   String course, String yearLevel, String email) {
        this.id = id;
        this.studentNumber = studentNumber;
        this.fullName = fullName;
        this.course = course;
        this.yearLevel = yearLevel;
        this.email = email;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public String getYearLevel() { return yearLevel; }
    public void setYearLevel(String yearLevel) { this.yearLevel = yearLevel; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() { return studentNumber + " — " + fullName; }
}
