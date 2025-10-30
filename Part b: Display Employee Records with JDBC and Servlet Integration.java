package com.example.hibernatecrud;

import jakarta.persistence.*;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;

// ========== Entity Class ==========
@Entity
@Table(name = "students")
class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "course")
    private String course;

    @Column(name = "age")
    private int age;

    public Student() {}

    public Student(String name, String course, int age) {
        this.name = name;
        this.course = course;
        this.age = age;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
}

// ========== DAO Class ==========
class StudentDAO {
    private static SessionFactory factory = new Configuration()
            .configure("hibernate.cfg.xml")
            .addAnnotatedClass(Student.class)
            .buildSessionFactory();

    public void create(Student student) {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.save(student);
            tx.commit();
        }
    }

    public Student read(int id) {
        try (Session session = factory.openSession()) {
            return session.get(Student.class, id);
        }
    }

    public void update(Student student) {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.update(student);
            tx.commit();
        }
    }

    public void delete(int id) {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            Student s = session.get(Student.class, id);
            if (s != null) session.delete(s);
            tx.commit();
        }
    }
}

// ========== Main Class ==========
public class MainApp {
    public static void main(String[] args) {
        StudentDAO dao = new StudentDAO();

        // Create
        dao.create(new Student("Abhishek", "Spring Boot", 22));

        // Read
        Student s = dao.read(1);
        if (s != null)
            System.out.println("Fetched: " + s.getName());
        else
            System.out.println("Student not found!");

        // Update
        if (s != null) {
            s.setCourse("Spring + Hibernate");
            dao.update(s);
        }

        // Delete
        dao.delete(2);
    }
}
