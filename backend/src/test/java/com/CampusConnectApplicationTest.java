package com;

import com.entity.*;
import com.repository.*;
import com.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CampusConnectApplicationTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private ClubRepository clubRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private NoticeRepository noticeRepository;

    @InjectMocks
    private StudentService studentService;

    @InjectMocks
    private ClubService clubService;

    @InjectMocks
    private PostService postService;

    @InjectMocks
    private EventService eventService;

    @InjectMocks
    private NoticeService noticeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testStudentCrudOperations() {
        Student student = new Student("Anurag Yadav", "anurag@campusconnect.edu", "IT", "Second Year", "C", "Lead Developer");
        student.setId(1L);

        when(studentRepository.findAll()).thenReturn(List.of(student));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(studentRepository.existsById(1L)).thenReturn(true);

        List<Student> students = studentService.getAllStudents();
        assertEquals(1, students.size());
        assertEquals("Anurag Yadav", students.get(0).getName());

        Student found = studentService.getStudentById(1L);
        assertNotNull(found);
        assertEquals("IT", found.getDepartment());

        Student updated = studentService.updateStudent(1L, student);
        assertNotNull(updated);

        boolean deleted = studentService.deleteStudent(1L);
        assertTrue(deleted);
        verify(studentRepository, times(1)).deleteById(1L);
    }

    @Test
    void testClubCrudOperations() {
        Club club = new Club("Coding Club", "Technical", "logo.png", "cover.png", "Coding club", "IT", "Anurag Yadav", 150);
        club.setId(10L);

        when(clubRepository.findAll()).thenReturn(List.of(club));
        when(clubRepository.findById(10L)).thenReturn(Optional.of(club));
        when(clubRepository.save(any(Club.class))).thenReturn(club);
        when(clubRepository.existsById(10L)).thenReturn(true);

        List<Club> clubs = clubService.getAllClubs();
        assertEquals(1, clubs.size());
        assertEquals("Coding Club", clubs.get(0).getName());

        boolean deleted = clubService.deleteClub(10L);
        assertTrue(deleted);
    }

    @Test
    void testPostCrudAndVoteOperations() {
        Post post = new Post("Anurag Yadav", "Test Post", "Content", "General", "2026-09-20 10:00", 10);
        post.setId(100L);

        when(postRepository.findById(100L)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Post voted = postService.votePost(100L, 1);
        assertNotNull(voted);
        assertEquals(11, voted.getVoteCount());
    }

    @Test
    void testEventAndNoticeOperations() {
        Event event = new Event("Hackathon", "Coding Event", "Coding Club", "2026-10-15", "09:00 AM", "Auditorium", "Technical");
        event.setId(5L);
        when(eventRepository.findAll()).thenReturn(List.of(event));
        assertEquals(1, eventService.getAllEvents().size());

        Notice notice = new Notice("Notice 1", "Content 1", "Admin", "2026-09-20", "General");
        notice.setId(8L);
        when(noticeRepository.findAll()).thenReturn(List.of(notice));
        assertEquals(1, noticeService.getAllNotices().size());
    }
}
