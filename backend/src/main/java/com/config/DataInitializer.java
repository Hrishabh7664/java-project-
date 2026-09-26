package com.config;

import com.entity.*;
import com.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final StudentRepository studentRepository;
    private final ClubRepository clubRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final NoticeRepository noticeRepository;
    private final AssignmentRepository assignmentRepository;
    private final ElectionRepository electionRepository;
    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(StudentRepository studentRepository,
                           ClubRepository clubRepository,
                           PostRepository postRepository,
                           CommentRepository commentRepository,
                           EventRepository eventRepository,
                           NoticeRepository noticeRepository,
                           AssignmentRepository assignmentRepository,
                           ElectionRepository electionRepository,
                           ComplaintRepository complaintRepository,
                           UserRepository userRepository,
                           AccountRepository accountRepository,
                           TransactionRepository transactionRepository,
                           PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.clubRepository = clubRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.eventRepository = eventRepository;
        this.noticeRepository = noticeRepository;
        this.assignmentRepository = assignmentRepository;
        this.electionRepository = electionRepository;
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedUsers();
        seedAccountsAndTransactions();
        seedStudents();
        seedClubs();
        seedPosts();
        seedEvents();
        seedNotices();
        seedAssignments();
        seedElections();
        seedComplaints();
    }

    private void seedUsers() {
        if (userRepository.count() == 0) {
            userRepository.saveAll(List.of(
                    new User("student", "student@campusconnect.edu", passwordEncoder.encode("student123"), Role.ROLE_USER),
                    new User("admin", "admin@campusconnect.edu", passwordEncoder.encode("admin123"), Role.ROLE_ADMIN),
                    new User("faculty", "faculty@campusconnect.edu", passwordEncoder.encode("faculty123"), Role.ROLE_FACULTY),
                    new User("hrishabh", "hrishabh@campusconnect.edu", passwordEncoder.encode("password123"), Role.ROLE_USER)
            ));
        }
    }

    private void seedAccountsAndTransactions() {
        if (accountRepository.count() == 0) {
            Account acc1 = new Account("AC100001", "Anurag Yadav", "Savings", new BigDecimal("25480.00"));
            Account acc2 = new Account("AC100002", "Hrishabh Soni", "Checking", new BigDecimal("18250.50"));
            Account acc3 = new Account("AC100003", "Campus Student Council", "Business", new BigDecimal("150000.00"));

            accountRepository.saveAll(List.of(acc1, acc2, acc3));

            if (transactionRepository.count() == 0) {
                transactionRepository.saveAll(List.of(
                        new Transaction("TXN-INIT001", "Opening Initial Deposit", new BigDecimal("25000.00"), "CREDIT", "COMPLETED", acc1),
                        new Transaction("TXN-TRF002", "Semester Project Grant", new BigDecimal("1500.00"), "CREDIT", "COMPLETED", acc1),
                        new Transaction("TXN-FEE003", "Library Caution Fee", new BigDecimal("1020.00"), "DEBIT", "COMPLETED", acc1),
                        new Transaction("TXN-DEP004", "Opening Initial Balance", new BigDecimal("20000.00"), "CREDIT", "COMPLETED", acc2),
                        new Transaction("TXN-TRF005", "Campus Cafeteria Recharge", new BigDecimal("1749.50"), "DEBIT", "COMPLETED", acc2)
                ));
            }
        }
    }

    private void seedStudents() {
        if (studentRepository.count() == 0) {
            studentRepository.saveAll(List.of(
                    new Student("Anurag Yadav", "anurag.yadav@campusconnect.edu", "IT", "Second Year", "C", "IT Student & Lead Developer"),
                    new Student("Atul Tiwari", "atul.tiwari@campusconnect.edu", "CE", "Second Year", "A", "Computer Engineering Student"),
                    new Student("Hrishabh Soni", "hrishabh.soni@campusconnect.edu", "AIDS", "Second Year", "B", "AI & Data Science Enthusiast"),
                    new Student("Adarsh Singh", "adarsh.singh@campusconnect.edu", "EXTC", "Second Year", "B", "Electronics & IoT Lead")
            ));
        }
    }

    private void seedClubs() {
        if (clubRepository.count() == 0) {
            clubRepository.saveAll(List.of(
                    new Club("Coding Club", "Technology", "/assets/clubs/coding.png", "/assets/covers/coding.jpg",
                            "Official programming and competitive coding club.", "IT", "Anurag Yadav", 142),
                    new Club("Robotics Club", "Engineering", "/assets/clubs/robotics.png", "/assets/covers/robotics.jpg",
                            "Innovating hardware, IoT, and autonomous bots.", "EXTC", "Adarsh Singh", 88),
                    new Club("Cultural Committee", "Arts & Culture", "/assets/clubs/cultural.png", "/assets/covers/cultural.jpg",
                            "Organizing college annual festival and stage arts.", "Central", "Priya Nair", 210)
            ));
        }
    }

    private void seedPosts() {
        if (postRepository.count() == 0) {
            Post p1 = new Post("Anurag Yadav", "Tips for preparing for Annual College Hackathon 2026",
                    "Focus on problem statement clarity, API documentation, and presentation polish.", "General", 24);
            Post p2 = new Post("Hrishabh Soni", "Best resources to learn Spring Boot & React for Mini Project",
                    "Check out official Spring Guides and modern React documentation with hooks.", "Technology", 42);
            postRepository.saveAll(List.of(p1, p2));

            commentRepository.saveAll(List.of(
                    new Comment(p1.getId(), "Atul Tiwari", "Great tips! Looking forward to the hackathon."),
                    new Comment(p2.getId(), "Anurag Yadav", "Very helpful list of resources, thanks!")
            ));
        }
    }

    private void seedEvents() {
        if (eventRepository.count() == 0) {
            eventRepository.saveAll(List.of(
                    new Event("Hackatron 2026", "24-hour inter-college hackathon with cash prizes.", "Coding Club", "2026-10-15", "09:00 AM", "Auditorium", "Technology"),
                    new Event("IoT Workshop", "Hands-on microcontrollers and sensor integration.", "Robotics Club", "2026-10-22", "11:00 AM", "Lab 4", "Workshop")
            ));
        }
    }

    private void seedNotices() {
        if (noticeRepository.count() == 0) {
            noticeRepository.saveAll(List.of(
                    new Notice("Experiment 05 & 06 Submission Deadline", "All students must submit Experiment 05 and 06 by Friday.", "Prof. Sumeet Rathod", "2026-09-20", "Academics"),
                    new Notice("Library Extended Hours for Mid-Sem Exams", "Library will remain open till 10 PM during exam week.", "Central Library", "2026-09-18", "General")
            ));
        }
    }

    private void seedAssignments() {
        if (assignmentRepository.count() == 0) {
            assignmentRepository.saveAll(List.of(
                    new Assignment("Experiment 05 - Spring Boot & MySQL Integration", "Perform CRUD operations with Spring Data JPA and MySQL database integration.", "Full Stack Java", "Prof. Sumeet Rathod", "2026-09-25", "PENDING"),
                    new Assignment("Experiment 06 - JWT Authentication", "Implement stateless JWT authentication with Spring Security and React Axios interceptors.", "Full Stack Java", "Prof. Sumeet Rathod", "2026-10-02", "PENDING")
            ));
        }
    }

    private void seedElections() {
        if (electionRepository.count() == 0) {
            electionRepository.saveAll(List.of(
                    new Election("Student Council President 2026", "President", "2026-10-01", "2026-10-05", "UPCOMING")
            ));
        }
    }

    private void seedComplaints() {
        if (complaintRepository.count() == 0) {
            complaintRepository.saveAll(List.of(
                    new Complaint("Lab 3 WiFi connectivity issues", "Frequent disconnection on local network in Lab 3.", "Infrastructure", "IN_PROGRESS", "Anurag Yadav", false)
            ));
        }
    }
}
