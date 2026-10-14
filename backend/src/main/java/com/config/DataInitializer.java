package com.config;

import com.entity.*;
import com.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final StudentRepository studentRepository;
    private final ClubRepository clubRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final NoticeRepository noticeRepository;
    private final ElectionRepository electionRepository;
    private final ComplaintRepository complaintRepository;
    private final PasswordEncoder passwordEncoder;
    private final Environment environment;

    public DataInitializer(StudentRepository studentRepository,
                           ClubRepository clubRepository,
                           PostRepository postRepository,
                           CommentRepository commentRepository,
                           EventRepository eventRepository,
                           NoticeRepository noticeRepository,
                           ElectionRepository electionRepository,
                           ComplaintRepository complaintRepository,
                           PasswordEncoder passwordEncoder,
                           Environment environment) {
        this.studentRepository = studentRepository;
        this.clubRepository = clubRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.eventRepository = eventRepository;
        this.noticeRepository = noticeRepository;
        this.electionRepository = electionRepository;
        this.complaintRepository = complaintRepository;
        this.passwordEncoder = passwordEncoder;
        this.environment = environment;
    }

    @Override
    public void run(String... args) {
        seedStudents();
        seedClubs();
        seedPosts();
        seedEvents();
        seedNotices();
        seedElections();
        seedComplaints();
        provisionRoleAccount("ADMIN");
        provisionRoleAccount("FACULTY");
    }

    private void provisionRoleAccount(String role) {
        if (!Boolean.parseBoolean(environment.getProperty("DEMO_ACCOUNTS_ENABLED", "false"))) return;
        String prefix = "DEMO_" + role + "_";
        String email = environment.getProperty(prefix + "EMAIL", "").trim().toLowerCase();
        String password = environment.getProperty(prefix + "PASSWORD", "");
        String name = environment.getProperty(prefix + "NAME", role + " Demo Account").trim();
        if (email.isBlank() || password.length() < 8 || !email.endsWith("@tsdcem.ac.in")) return;
        if (studentRepository.existsByEmailIgnoreCase(email)) return;
        Student account = new Student();
        account.setName(name);
        account.setEmail(email);
        account.setRole(role);
        account.setPasswordHash(passwordEncoder.encode(password));
        studentRepository.save(account);
    }

    private void seedStudents() {
        // Linked FE/SE demo student rows are seeded by AcademicDataInitializer.
    }

    private void seedClubs() {
        if (clubRepository.count() == 0) {
            clubRepository.saveAll(List.of(
                    new Club("Coding Club", "Technical", "code.png", "cover1.png", "Official competitive programming and open-source club", "IT", "Anurag Yadav", 150),
                    new Club("Robotics Society", "Technical", "robot.png", "cover2.png", "Hardware and robotics innovation club", "CE", "Atul Tiwari", 85),
                    new Club("Cultural Committee", "Cultural", "art.png", "cover3.png", "DRAMA, Music and Arts student committee", "All", "Priya Sharma", 200),
                    new Club("Sports Club", "Sports", "sports.png", "cover4.png", "Inter and intra college sports association", "All", "Hrishabh Soni", 120)
            ));
        }
    }

    private void seedPosts() {
        if (postRepository.count() == 0) {
            Post p1 = postRepository.save(new Post("Anurag Yadav", "Welcome to Campus Connect!", "Official platform for students & faculty.", "General", "2026-09-20 10:00", 15));
            Post p2 = postRepository.save(new Post("Prof. Sumeet Rathod", "Mid-Sem Exam Preparation Strategy - IT Department", "Detailed notes and syllabus outline for Java Programming.", "Academics", "2026-09-19 14:30", 28));
            Post p3 = postRepository.save(new Post("Atul Tiwari", "Annual Hackathon 2026 Registration Open!", "Form your teams of 4 and register before October 1st.", "Events", "2026-09-18 09:15", 42));

            if (commentRepository.count() == 0) {
                commentRepository.saveAll(List.of(
                        new Comment(p1.getId(), "Atul Tiwari", "Great platform! Looking forward to updates.", "2026-09-20 10:15"),
                        new Comment(p3.getId(), "Hrishabh Soni", "Is there any team size limit for the hackathon?", "2026-09-18 11:00")
                ));
            }
        }
    }

    private void seedEvents() {
        if (eventRepository.count() == 0) {
            eventRepository.saveAll(List.of(
                    new Event("Campus Hackathon 2026", "Annual inter-department coding contest", "Coding Club", "2026-10-15", "09:00 AM", "Auditorium 1", "Technical"),
                    new Event("Annual Cultural Fest", "Music, dance and drama competitions", "Cultural Committee", "2026-11-01", "10:00 AM", "Main Ground", "Cultural")
            ));
        }
    }

    private void seedNotices() {
        if (noticeRepository.count() == 0) {
            noticeRepository.saveAll(List.of(
                    new Notice("Experiment 05 Submission Deadline", "All students must submit Experiment 05 by Friday.", "Prof. Sumeet Rathod", "2026-09-20", "Academics"),
                    new Notice("Library Extended Hours for Mid-Sem Exams", "Library will remain open till 10 PM during exam week.", "Central Library", "2026-09-18", "General")
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
