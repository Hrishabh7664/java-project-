package com.service;

import com.entity.Student;
import com.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    public Student updateStudent(Long id, Student studentDetails) {
        Student existing = studentRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }

        existing.setName(studentDetails.getName());
        existing.setEmail(studentDetails.getEmail());
        existing.setDepartment(studentDetails.getDepartment());
        existing.setYear(studentDetails.getYear());
        existing.setDivision(studentDetails.getDivision());
        existing.setBio(studentDetails.getBio());

        return studentRepository.save(existing);
    }

    public boolean deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            return false;
        }
        studentRepository.deleteById(id);
        return true;
    }
}
