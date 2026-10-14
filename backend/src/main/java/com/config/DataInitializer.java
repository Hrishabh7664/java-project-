package com.config;

import com.entity.*;
import com.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.core.annotation.Order;

import java.util.List;

@Component
@Order(2)
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
        seedClub("demo-coding", "Coding Club", "Anurag Yadav", "Demo Coding & Open Source Club", "Technical", "demo.it.fe.a@tsdcem.ac.in", "IT", "Practice programming, peer learning and small open-source projects. This is illustrative demo content.", "Coding sessions, beginner workshops and project showcases.", "Open to enrolled students; beginner friendly.");
        seedClub("demo-robotics", "Robotics Society", "Atul Tiwari", "Demo Robotics & Making Club", "Technical", "demo.ce.fe.a@tsdcem.ac.in", "CE", "Illustrative demo group for robotics, electronics and hands-on making.", "Prototype sessions, electronics introductions and team challenges.", "Open to enrolled students interested in making and engineering.");
        seedClub("demo-culture", "Cultural Committee", "Priya Sharma", "Demo Arts & Culture Club", "Cultural", "demo.ecs.se.a@tsdcem.ac.in", "ECS", "Illustrative demo group for music, performance and campus arts.", "Open mic events, music circles and cultural showcases.", "Open to enrolled students; no prior experience required.");
        seedClub("demo-sports", "Sports Club", "Hrishabh Soni", "Demo Sports & Recreation Club", "Sports", "demo.me.fe.a@tsdcem.ac.in", "ME", "Illustrative demo group for recreational sports and wellbeing.", "Friendly matches, fitness sessions and inter-class activities.", "Open to enrolled students; activities are recreational demo content.");
        seedClub("demo-literary", null, null, "Demo Literary & Service Club", "Literary / Social Service", "demo.it.se.a@tsdcem.ac.in", "IT", "Illustrative demo group combining reading, writing and community service.", "Reading circles, student writing and local service activities.", "Open to enrolled students interested in reading or volunteering.");
    }

    private void seedClub(String key,String legacyName,String legacyLeader,String name,String category,String leaderEmail,String department,String description,String activities,String eligibility) {
        Student leader=studentRepository.findByEmailIgnoreCase(leaderEmail).orElseThrow(()->new IllegalStateException("Demo academic leader account is missing: "+leaderEmail));
        Club club=clubRepository.findByDemoKey(key).orElseGet(()->clubRepository.findByNameIgnoreCase(legacyName==null?"":legacyName)
                .filter(old->legacyLeader!=null&&legacyLeader.equals(old.getPresident())).orElseGet(Club::new));club.setDemoKey(key);
        club.setName(name);club.setCategory(category);club.setDepartment(department);club.setDescription(description);club.setActivities(activities);club.setEligibility(eligibility);club.setLeader(leader);club.setPresident(leader.getName());
        clubRepository.save(club);
        String configuredPassword=environment.getProperty("DEMO_CLUB_LEADER_PASSWORD","");
        if(configuredPassword.length()>=8&&(leader.getPasswordHash()==null||leader.getPasswordHash().isBlank())){leader.setPasswordHash(passwordEncoder.encode(configuredPassword));studentRepository.save(leader);}
    }

    private void seedPosts() {
        Post welcome=seedPost("demo-welcome","Welcome to Campus Connect!","Share questions and ideas with fellow engineering students.","General","2026-09-20 10:00","demo.it.fe.a@tsdcem.ac.in","Anurag Yadav");
        Post java=seedPost("demo-java","Java revision: collections and generics","What examples helped you understand the Collections Framework before exams?","Academics","2026-09-19 14:30","demo.ecs.se.a@tsdcem.ac.in","Priya Sharma");
        Post dsa=seedPost("demo-dsa","DSA practice group for graph algorithms","Looking for peers to solve BFS, DFS and shortest path problems together.","Technology","2026-09-18 09:15","demo.ce.fe.a@tsdcem.ac.in","Atul Tiwari");
        seedPost("demo-gate","GATE preparation study plan","How are you balancing core subjects and mock tests this semester?","Academics","2026-09-17 11:00","demo.me.fe.a@tsdcem.ac.in","Hrishabh Soni");
        seedPost("demo-internship","Internship preparation: project portfolio","Which project write-ups best explain practical engineering work?","Placements","2026-09-16 16:00","demo.it.se.a@tsdcem.ac.in","Anurag Yadav");
        seedComment("demo-comment-welcome",welcome,"Atul Tiwari","Demo discussion: what topics should our next DSA session cover?","2026-09-20 10:15");
        seedComment("demo-comment-dsa",dsa,"Hrishabh Soni","Demo discussion: begin with team matching and project ideas.","2026-09-18 11:00");
        seedComment("demo-comment-java",java,"Priya Sharma","Demo discussion: small code examples made generics easier to follow.","2026-09-19 15:00");
    }

    private Post seedPost(String key,String title,String content,String category,String date,String email,String author){
        Student student=studentRepository.findByEmailIgnoreCase(email).orElse(null);
        Post post=postRepository.findAll().stream().filter(p->key.equals(p.getDemoKey())).findFirst().orElseGet(Post::new);
        post.setDemoKey(key);post.setTitle(title);post.setContent(content);post.setCategory(category);post.setCreatedAt(date);
        post.setAuthorEmail(email);post.setAuthorName(student==null?author:student.getName());post.setVoteCount(0);post.setHidden(false);
        return postRepository.save(post);
    }
    private void seedComment(String key,Post post,String author,String content,String date){
        if(commentRepository.findAll().stream().anyMatch(c->key.equals(c.getDemoKey())))return;
        Comment comment=new Comment(post.getId(),author,content,date);comment.setDemoKey(key);commentRepository.save(comment);
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
