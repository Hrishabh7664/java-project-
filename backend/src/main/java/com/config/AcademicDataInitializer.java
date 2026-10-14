package com.config;

import com.entity.*;
import com.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class AcademicDataInitializer implements CommandLineRunner {
    private final DepartmentRepository departments;
    private final AcademicClassRepository classes;
    private final SubjectRepository subjects;
    private final FacultyProfileRepository faculty;
    private final TeachingAssignmentRepository assignments;
    private final StudentRepository students;
    private final StudentEnrollmentRepository enrollments;
    private final Environment environment;
    public AcademicDataInitializer(DepartmentRepository departments, AcademicClassRepository classes,
                                   SubjectRepository subjects, FacultyProfileRepository faculty,
                                   TeachingAssignmentRepository assignments, StudentRepository students,
                                   StudentEnrollmentRepository enrollments, Environment environment) {
        this.departments = departments; this.classes = classes; this.subjects = subjects;
        this.faculty = faculty; this.assignments = assignments; this.students = students;
        this.enrollments = enrollments; this.environment = environment;
    }
    @Override public void run(String... args) {
        seedDepartment("IT", "Information Technology (Demo)");
        seedDepartment("CE", "Computer Engineering (Demo)");
        seedDepartment("ECS", "Electronics and Computer Science (Demo)");
        seedDepartment("ME", "Mechanical Engineering (Demo)");
        for (String code : List.of("IT", "CE", "ECS", "ME")) seedAcademicDepartment(code);
    }
    private void seedDepartment(String code, String fallback) {
        String name = environment.getProperty("campus.demo.department." + code, fallback);
        departments.findById(code).orElseGet(() -> departments.save(new Department(code, name)));
    }
    private void seedAcademicDepartment(String code) {
        Department department = departments.findById(code).orElseThrow();
        String configuredFacultyEmail = environment.getProperty("DEMO_FACULTY_EMAIL", "").trim().toLowerCase();
        String facultyEmail = code.equals("IT") && !configuredFacultyEmail.isBlank()
                ? configuredFacultyEmail : "demo.faculty." + code.toLowerCase() + "@tsdcem.ac.in";
        String facultyName = code.equals("IT") ? environment.getProperty("DEMO_FACULTY_NAME", "Demo Faculty IT") : "Demo Faculty " + code;
        FacultyProfile profile = faculty.findById(facultyEmail)
                .orElseGet(() -> faculty.save(new FacultyProfile(facultyEmail, facultyName, department)));
        for (String year : List.of("FE", "SE")) {
            AcademicClass cls = classes.findByDepartmentCodeAndAcademicYearAndDivisionAndBatch(code, year, "A", "A1")
                    .orElseGet(() -> classes.save(new AcademicClass(department, year, "A", "A1")));
            seedStudents(code, year, cls);
            int semester = year.equals("FE") ? 1 : 3;
            for (int part = 1; part <= 2; part++) {
                int partNumber = part;
                int subjectSemester = semester + partNumber - 1;
                String subjectCode = code + year + (partNumber == 1 ? "101" : "102");
                Subject subject = subjects.findBySubjectCodeAndAcademicYear(subjectCode, year)
                        .orElseGet(() -> subjects.save(new Subject(department, year, subjectSemester, subjectCode, subjectName(code, year, partNumber))));
                if (!assignments.existsBySubjectIdAndAcademicClassIdAndLectureNumber(subject.getId(), cls.getId(), partNumber))
                    assignments.save(new TeachingAssignment(subject, cls, profile, partNumber, "MONDAY",
                            partNumber == 1 ? "09:00" : "10:00", partNumber == 1 ? "10:00" : "11:00"));
            }
        }
    }
    private void seedStudents(String department, String year, AcademicClass academicClass) {
        String prefix = "demo." + department.toLowerCase() + "." + year.toLowerCase();
        String[] names = {"Aarav Sharma", "Diya Patil", "Kabir Deshmukh", "Ananya Kulkarni",
                "Ishaan Joshi", "Meera Nair", "Rohan Mehta", "Sana Khan",
                "Arjun Rao", "Kavya Iyer", "Vivaan Gupta", "Aditi Menon",
                "Reyansh Verma", "Ira Shah", "Aditya Kulkarni", "Nisha Reddy"};
        int nameIndex = (List.of("IT", "CE", "ECS", "ME").indexOf(department) * 4)
                + (year.equals("SE") ? 2 : 0);
        ensureEnrollment(addStudent(prefix + ".a@tsdcem.ac.in", names[nameIndex], department, year), academicClass);
        ensureEnrollment(addStudent(prefix + ".b@tsdcem.ac.in", names[nameIndex + 1], department, year), academicClass);
    }
    private Student addStudent(String email, String name, String department, String academicYear) {
        return students.findByEmailIgnoreCase(email).orElseGet(() -> {
            Student student = new Student(name, email, department, academicYear, "A", "Demo student record");
            student.setBatch("A1");
            return students.save(student);
        });
    }
    private void ensureEnrollment(Student student, AcademicClass academicClass) {
        if (!enrollments.existsByStudentIdAndAcademicClassId(student.getId(), academicClass.getId()))
            enrollments.save(new StudentEnrollment(student, academicClass));
    }
    private String subjectName(String dept, String year, int part) {
        String[][] names = {
                {"Programming Fundamentals", "Digital Systems", "Data Structures", "Database Management Systems"},
                {"Engineering Mechanics", "Engineering Drawing", "Computer Aided Design", "Data Structures"},
                {"Electrical and Electronics Fundamentals", "Programming Fundamentals", "Signals and Systems", "Embedded Systems"},
                {"Engineering Mechanics", "Workshop Practice", "Thermodynamics", "Manufacturing Processes"}
        };
        int row = List.of("IT", "CE", "ECS", "ME").indexOf(dept);
        return names[row][(year.equals("FE") ? 0 : 2) + part - 1];
    }
}
