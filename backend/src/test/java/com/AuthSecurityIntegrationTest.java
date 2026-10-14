package com;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import com.entity.*;
import com.repository.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.assignment.storage-dir=${java.io.tmpdir}/campus-connect-test-uploads",
        "DEMO_ACCOUNTS_ENABLED=true",
        "DEMO_ADMIN_EMAIL=integration.admin@tsdcem.ac.in",
        "DEMO_ADMIN_PASSWORD=IntegrationDemo123",
        "DEMO_FACULTY_EMAIL=integration.faculty@tsdcem.ac.in",
        "DEMO_FACULTY_PASSWORD=IntegrationDemo123"
})
@AutoConfigureMockMvc
class AuthSecurityIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired StudentRepository studentRepository;
    @Autowired DepartmentRepository departmentRepository;
    @Autowired AcademicClassRepository academicClassRepository;
    @Autowired TeachingAssignmentRepository teachingAssignmentRepository;
    @Autowired AttendanceRecordRepository attendanceRecordRepository;
    @Autowired AttendanceChangeRepository attendanceChangeRepository;
    @Autowired AttendanceSessionRepository attendanceSessionRepository;
    @Autowired StudentEnrollmentRepository studentEnrollmentRepository;
    @Autowired FacultyProfileRepository facultyProfileRepository;
    @Autowired com.repository.AssignmentRepository assignments;
    @Autowired com.service.AssignmentFileStorage assignmentFileStorage;
    @Autowired ClubRepository clubRepository;
    @Autowired ClubMembershipRepository clubMembershipRepository;
    @Autowired TeachingAssignmentRepository assignmentRepository;

    @Test
    void registrationLoginAndProtectedApiWork() throws Exception {
        String email = "demo." + UUID.randomUUID() + "@tsdcem.ac.in";
        String registration = """
                {"name":"Demo Student","email":"%s","password":"CampusDemo123","department":"IT","year":"First Year","division":"A"}
                """.formatted(email);

        String registered = mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON).content(registration))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        JsonNode registrationJson = objectMapper.readTree(registered);
        String token = registrationJson.path("token").asText();
        assertFalse(token.isBlank());
        assertEquals("STUDENT", registrationJson.path("user").path("role").asText());

        String studentJson = mvc.perform(get("/api/students/" + registrationJson.path("user").path("id").asLong())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertFalse(objectMapper.readTree(studentJson).has("passwordHash"));

        mvc.perform(get("/api/posts")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/posts").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        String login = """
                {"email":"%s","password":"CampusDemo123"}
                """.formatted(email);
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(login))
                .andExpect(status().isOk());

        String invalidLogin = """
                {"email":"%s","password":"wrong-password"}
                """.formatted(email);
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(invalidLogin))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void studentCannotAccessAdminOrFacultyOperations() throws Exception {
        String email = "roles." + UUID.randomUUID() + "@tsdcem.ac.in";
        String registration = """
                {"name":"Role Test","email":"%s","password":"CampusDemo123"}
                """.formatted(email);
        String response = mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON).content(registration))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        String token = objectMapper.readTree(response).path("token").asText();
        mvc.perform(post("/api/assignments").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/students").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void provisionedFacultyAndAdminRolesAreEnforced() throws Exception {
        String facultyResponse = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"integration.faculty@tsdcem.ac.in\",\"password\":\"IntegrationDemo123\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String facultyToken = objectMapper.readTree(facultyResponse).path("token").asText();
        JsonNode facultyClass = objectMapper.readTree(mvc.perform(get("/api/faculty/attendance/options")
                        .header("Authorization", "Bearer " + facultyToken))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).get(0);
        mvc.perform(post("/api/assignments").header("Authorization", "Bearer " + facultyToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Faculty API test\",\"description\":\"Integration test assignment\",\"subjectId\":"
                                + facultyClass.path("subjectId").asLong() + ",\"classId\":" + facultyClass.path("classId").asLong()
                                + ",\"deadlineAt\":\"2099-09-30T23:59:00\",\"maximumMarks\":20,\"status\":\"PUBLISHED\"}"))
                .andExpect(status().isCreated());

        String adminResponse = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"integration.admin@tsdcem.ac.in\",\"password\":\"IntegrationDemo123\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String adminToken = objectMapper.readTree(adminResponse).path("token").asText();
        mvc.perform(get("/api/students").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void assignmentSubmissionReviewAndResubmissionWorkflowIsProtectedAndPersistent() throws Exception {
        String facultyLogin = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"integration.faculty@tsdcem.ac.in\",\"password\":\"IntegrationDemo123\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String facultyToken = objectMapper.readTree(facultyLogin).path("token").asText();
        JsonNode option = objectMapper.readTree(mvc.perform(get("/api/faculty/attendance/options")
                        .header("Authorization", "Bearer " + facultyToken))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).get(0);
        String createBody = "{\"title\":\"Workflow assignment\",\"description\":\"Upload a PDF\",\"subjectId\":"
                + option.path("subjectId").asLong() + ",\"classId\":" + option.path("classId").asLong()
                + ",\"deadlineAt\":\"2099-09-30T23:59:00\",\"maximumMarks\":20,\"status\":\"PUBLISHED\",\"allowResubmission\":true}";
        mvc.perform(post("/api/assignments").header("Authorization", "Bearer " + facultyToken)
                        .contentType(MediaType.APPLICATION_JSON).content(createBody.replace("2099-09-30T23:59:00", "2000-09-30T23:59:00")))
                .andExpect(status().isBadRequest());
        assertThrows(IllegalArgumentException.class, () -> assignmentFileStorage.validate(new MockMultipartFile("file", "large.pdf",
                "application/pdf", new byte[10 * 1024 * 1024 + 1])));
        long assignmentId = objectMapper.readTree(mvc.perform(post("/api/assignments").header("Authorization", "Bearer " + facultyToken)
                        .contentType(MediaType.APPLICATION_JSON).content(createBody))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString()).path("id").asLong();
        String otherFaculty = "other.faculty." + UUID.randomUUID() + "@tsdcem.ac.in";
        Student otherFacultyAccount = new Student("Other Faculty", otherFaculty, "IT", "FE", "A", "Test faculty");
        otherFacultyAccount.setRole("FACULTY");
        otherFacultyAccount.setPasswordHash(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("CampusDemo123"));
        studentRepository.save(otherFacultyAccount);
        facultyProfileRepository.save(new FacultyProfile(otherFaculty, "Other Faculty", departmentRepository.findById("IT").orElseThrow()));
        String otherToken = objectMapper.readTree(mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + otherFaculty + "\",\"password\":\"CampusDemo123\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).path("token").asText();
        mvc.perform(put("/api/assignments/" + assignmentId).header("Authorization", "Bearer " + otherToken)
                        .contentType(MediaType.APPLICATION_JSON).content(createBody)).andExpect(status().isNotFound());

        String email = "submit." + UUID.randomUUID() + "@tsdcem.ac.in";
        String studentJson = mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Submission Student\",\"email\":\"" + email + "\",\"password\":\"CampusDemo123\",\"department\":\"IT\",\"year\":\"FE\",\"division\":\"A\",\"batch\":\"A1\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        String studentToken = objectMapper.readTree(studentJson).path("token").asText();
        Student testStudent = studentRepository.findByEmailIgnoreCase(email).orElseThrow();
        testStudent.setDepartment(option.path("departmentCode").asText());
        testStudent.setYear(option.path("academicYear").asText());
        testStudent.setDivision(option.path("division").asText()); testStudent.setBatch(option.path("batch").asText());
        studentRepository.save(testStudent);
        mvc.perform(get("/api/assignments/" + assignmentId).header("Authorization", "Bearer " + studentToken)).andExpect(status().isOk());
        byte[] pdf = "%PDF-1.4\nCampus test PDF".getBytes(java.nio.charset.StandardCharsets.US_ASCII);
        mvc.perform(multipart("/api/student/assignments/" + assignmentId + "/submission")
                        .file(new MockMultipartFile("file", "notes.pdf", "application/pdf", pdf))
                        .header("Authorization", "Bearer " + studentToken)).andExpect(status().isCreated());
        mvc.perform(multipart("/api/student/assignments/" + assignmentId + "/submission")
                        .file(new MockMultipartFile("file", "notes.pdf", "application/pdf", pdf))
                        .header("Authorization", "Bearer " + studentToken)).andExpect(status().isConflict());
        mvc.perform(multipart("/api/student/assignments/" + assignmentId + "/submission")
                        .file(new MockMultipartFile("file", "notes.txt", "text/plain", "not pdf".getBytes()))
                        .header("Authorization", "Bearer " + studentToken)).andExpect(status().isBadRequest());
        JsonNode submissions = objectMapper.readTree(mvc.perform(get("/api/faculty/assignments/" + assignmentId + "/submissions")
                        .header("Authorization", "Bearer " + facultyToken))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        JsonNode sub = submissions.get(0); long submissionId = sub.path("id").asLong();
        String anotherJson = mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Private Attempt\",\"email\":\"private." + UUID.randomUUID() + "@tsdcem.ac.in\",\"password\":\"CampusDemo123\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        String anotherToken = objectMapper.readTree(anotherJson).path("token").asText();
        mvc.perform(get("/api/submissions/" + submissionId + "/versions/1/file").header("Authorization", "Bearer " + anotherToken)).andExpect(status().isForbidden());
        mvc.perform(post("/api/faculty/submissions/" + submissionId + "/review").header("Authorization", "Bearer " + facultyToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"action\":\"GRADE\",\"marks\":21,\"feedback\":\"Over max\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/submissions/" + submissionId + "/versions/1/file").header("Authorization", "Bearer " + facultyToken))
                .andExpect(status().isOk()).andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_PDF));
        mvc.perform(post("/api/faculty/submissions/" + submissionId + "/review").header("Authorization", "Bearer " + facultyToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"action\":\"REQUEST_RESUBMISSION\",\"feedback\":\"Please include the missing section.\"}"))
                .andExpect(status().isOk());
        mvc.perform(multipart("/api/student/assignments/" + assignmentId + "/submission")
                        .file(new MockMultipartFile("file", "revision.pdf", "application/pdf", pdf))
                        .header("Authorization", "Bearer " + studentToken)).andExpect(status().isCreated());
        mvc.perform(post("/api/faculty/submissions/" + submissionId + "/review").header("Authorization", "Bearer " + facultyToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"action\":\"GRADE\",\"marks\":18,\"feedback\":\"Good revision.\"}"))
                .andExpect(status().isOk());
        JsonNode ownAssignment = objectMapper.readTree(mvc.perform(get("/api/assignments/" + assignmentId).header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        assertEquals("GRADED", ownAssignment.path("submissionStatus").asText());
        assertEquals(18, ownAssignment.path("marks").asInt());
        JsonNode ownSubmission = objectMapper.readTree(mvc.perform(get("/api/student/assignments/" + assignmentId + "/submission").header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        assertEquals(2, ownSubmission.path("currentVersion").asInt());
        assertEquals(2, ownSubmission.path("versions").size());
        assertEquals("integration.faculty@tsdcem.ac.in", ownSubmission.path("reviewerEmail").asText());
    }

    @Test
    void clubApplicationsAreScopedReviewedAndBecomeMemberships() throws Exception {
        String leaderEmail="club.leader."+UUID.randomUUID()+"@tsdcem.ac.in";
        String leaderAuth=mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Club Leader\",\"email\":\""+leaderEmail+"\",\"password\":\"CampusDemo123\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        String leaderToken=objectMapper.readTree(leaderAuth).path("token").asText();
        assertEquals("STUDENT",objectMapper.readTree(leaderAuth).path("user").path("role").asText());
        String applicantEmail="club.applicant."+UUID.randomUUID()+"@tsdcem.ac.in";
        String applicantAuth=mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Club Applicant\",\"email\":\""+applicantEmail+"\",\"password\":\"CampusDemo123\",\"department\":\"IT\",\"year\":\"FE\",\"division\":\"A\",\"batch\":\"A1\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        String applicantToken=objectMapper.readTree(applicantAuth).path("token").asText();
        String adminAuth=mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"integration.admin@tsdcem.ac.in\",\"password\":\"IntegrationDemo123\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String adminToken=objectMapper.readTree(adminAuth).path("token").asText();
        String created=mvc.perform(post("/api/clubs").header("Authorization","Bearer "+adminToken).contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test Demo Club\",\"category\":\"Technical\",\"description\":\"Test description\",\"activities\":\"Peer learning\",\"eligibility\":\"Open to students\",\"department\":\"IT\",\"leaderEmail\":\""+leaderEmail+"\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long clubId=objectMapper.readTree(created).path("id").asLong();
        assertTrue(objectMapper.readTree(mvc.perform(get("/api/clubs").header("Authorization","Bearer "+applicantToken))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).size()>0);
        mvc.perform(post("/api/student/clubs/"+clubId+"/applications").header("Authorization","Bearer "+applicantToken)
                .contentType(MediaType.APPLICATION_JSON).content("{\"motivation\":\"I enjoy collaborative projects.\"}"))
                .andExpect(status().isOk());
        mvc.perform(post("/api/student/clubs/"+clubId+"/applications").header("Authorization","Bearer "+applicantToken)
                .contentType(MediaType.APPLICATION_JSON).content("{}" )).andExpect(status().isConflict());
        JsonNode ownApps=objectMapper.readTree(mvc.perform(get("/api/student/clubs/applications").header("Authorization","Bearer "+applicantToken))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        assertEquals(1,ownApps.size());long appId=ownApps.get(0).path("id").asLong();
        mvc.perform(put("/api/student/clubs/leader/applications/"+appId).header("Authorization","Bearer "+applicantToken)
                .contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"APPROVED\"}")).andExpect(status().isNotFound());
        String facultyAuth=mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"integration.faculty@tsdcem.ac.in\",\"password\":\"IntegrationDemo123\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        mvc.perform(put("/api/student/clubs/leader/applications/"+appId).header("Authorization","Bearer "+objectMapper.readTree(facultyAuth).path("token").asText())
                .contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"APPROVED\"}")).andExpect(status().isForbidden());
        long otherClubId=clubRepository.findByDemoKey("demo-coding").orElseThrow().getId();
        mvc.perform(get("/api/student/clubs/leader/"+otherClubId+"/applications").header("Authorization","Bearer "+leaderToken)).andExpect(status().isNotFound());
        JsonNode leaderClubs=objectMapper.readTree(mvc.perform(get("/api/student/clubs/leader").header("Authorization","Bearer "+leaderToken))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());assertEquals(1,leaderClubs.size());
        mvc.perform(get("/api/student/clubs/leader/"+clubId+"/applications").param("status","PENDING").header("Authorization","Bearer "+leaderToken))
                .andExpect(status().isOk());
        mvc.perform(put("/api/student/clubs/leader/applications/"+appId).header("Authorization","Bearer "+leaderToken)
                .contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"APPROVED\",\"decisionNote\":\"Welcome to the club.\"}"))
                .andExpect(status().isOk());
        Student applicant=studentRepository.findByEmailIgnoreCase(applicantEmail).orElseThrow();
        assertTrue(clubMembershipRepository.existsByClubIdAndStudentIdAndActiveTrue(clubId,applicant.getId()));
        JsonNode clubDetail=objectMapper.readTree(mvc.perform(get("/api/clubs/"+clubId).header("Authorization","Bearer "+applicantToken))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        assertTrue(clubDetail.path("isMember").asBoolean());assertEquals("APPROVED",clubDetail.path("latestApplicationStatus").asText());
        JsonNode notices=objectMapper.readTree(mvc.perform(get("/api/student/notifications").header("Authorization","Bearer "+applicantToken))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());assertEquals(1,notices.size());
        mvc.perform(put("/api/student/notifications/"+notices.get(0).path("id").asLong()+"/read").header("Authorization","Bearer "+applicantToken))
                .andExpect(status().isOk());
        String relogin=mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\""+applicantEmail+"\",\"password\":\"CampusDemo123\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        JsonNode afterLogin=objectMapper.readTree(mvc.perform(get("/api/student/clubs/applications").header("Authorization","Bearer "+objectMapper.readTree(relogin).path("token").asText()))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        assertEquals("APPROVED",afterLogin.get(0).path("status").asText());assertEquals("Club Leader",afterLogin.get(0).path("decidedByName").asText());

        String secondEmail="club.withdraw."+UUID.randomUUID()+"@tsdcem.ac.in";
        String secondAuth=mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Withdraw Student\",\"email\":\""+secondEmail+"\",\"password\":\"CampusDemo123\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();String secondToken=objectMapper.readTree(secondAuth).path("token").asText();
        assertEquals(0,objectMapper.readTree(mvc.perform(get("/api/student/clubs/applications").header("Authorization","Bearer "+secondToken))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).size());
        JsonNode withdrawn=objectMapper.readTree(mvc.perform(post("/api/student/clubs/"+clubId+"/applications").header("Authorization","Bearer "+secondToken)
                .contentType(MediaType.APPLICATION_JSON).content("{}" )).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        long withdrawnId=withdrawn.path("id").asLong();
        mvc.perform(delete("/api/student/clubs/applications/"+withdrawnId).header("Authorization","Bearer "+secondToken)).andExpect(status().isOk());
        mvc.perform(post("/api/student/clubs/"+clubId+"/applications").header("Authorization","Bearer "+secondToken)
                .contentType(MediaType.APPLICATION_JSON).content("{}" )).andExpect(status().isOk());
        JsonNode secondHistory=objectMapper.readTree(mvc.perform(get("/api/student/clubs/applications").header("Authorization","Bearer "+secondToken))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());long reapplicationId=secondHistory.get(0).path("id").asLong();
        mvc.perform(put("/api/student/clubs/leader/applications/"+reapplicationId).header("Authorization","Bearer "+leaderToken)
                .contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"REJECTED\",\"decisionNote\":\"This demo club has reached its review limit.\"}"))
                .andExpect(status().isOk());
        mvc.perform(post("/api/student/clubs/"+clubId+"/applications").header("Authorization","Bearer "+secondToken)
                .contentType(MediaType.APPLICATION_JSON).content("{}" )).andExpect(status().isConflict());
    }

    @Test
    void authenticatedPostCrudPersistsAcrossReads() throws Exception {
        String email = "crud." + UUID.randomUUID() + "@tsdcem.ac.in";
        String registration = """
                {"name":"CRUD Student","email":"%s","password":"CampusDemo123"}
                """.formatted(email);
        String authResponse = mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON).content(registration))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        String token = objectMapper.readTree(authResponse).path("token").asText();

        String otherEmail = "other." + UUID.randomUUID() + "@tsdcem.ac.in";
        String otherRegistration = """
                {"name":"Other Student","email":"%s","password":"CampusDemo123"}
                """.formatted(otherEmail);
        String otherResponse = mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON).content(otherRegistration))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        String otherToken = objectMapper.readTree(otherResponse).path("token").asText();

        String created = mvc.perform(post("/api/posts").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Persisted post\",\"content\":\"Saved in JPA\",\"category\":\"General\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(created).path("id").asLong();
        assertEquals("CRUD Student", objectMapper.readTree(created).path("authorName").asText());
        mvc.perform(put("/api/posts/" + id).header("Authorization", "Bearer " + otherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Hijacked\",\"content\":\"No\",\"category\":\"General\"}"))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/posts/" + id).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mvc.perform(put("/api/posts/" + id).header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated persisted post\",\"content\":\"Updated\",\"category\":\"Academics\"}"))
                .andExpect(status().isOk());
        String reread = mvc.perform(get("/api/posts/" + id).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertEquals("Updated persisted post", objectMapper.readTree(reread).path("title").asText());

        mvc.perform(delete("/api/posts/" + id).header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/posts/" + id).header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void eventRegistrationIsPersistedUniqueAndCancellable() throws Exception {
        String facultyResponse = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"integration.faculty@tsdcem.ac.in\",\"password\":\"IntegrationDemo123\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String facultyToken = objectMapper.readTree(facultyResponse).path("token").asText();
        String eventJson = mvc.perform(post("/api/events").header("Authorization", "Bearer " + facultyToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Registration API test\",\"date\":\"2099-10-01\",\"registrationDeadline\":\"2099-09-30\",\"capacity\":2,\"eligibility\":\"All\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long eventId = objectMapper.readTree(eventJson).path("id").asLong();

        String email = "event." + UUID.randomUUID() + "@tsdcem.ac.in";
        String authJson = mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Event Student\",\"email\":\"" + email + "\",\"password\":\"CampusDemo123\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        String token = objectMapper.readTree(authJson).path("token").asText();
        mvc.perform(post("/api/events/" + eventId + "/registrations").header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/events/" + eventId + "/registrations").header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
        String registrations = mvc.perform(get("/api/events/registrations/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertEquals(1, objectMapper.readTree(registrations).size());
        mvc.perform(delete("/api/events/" + eventId + "/registrations/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
        assertEquals(0, objectMapper.readTree(mvc.perform(get("/api/events/registrations/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).size());
    }

    @Test
    void connectedAcademicDemoAndAttendanceWorkflowEnforceOwnership() throws Exception {
        assertEquals(4, departmentRepository.count());
        assertEquals(8, academicClassRepository.count());
        assertEquals(16, teachingAssignmentRepository.count());

        String facultyLogin = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"integration.faculty@tsdcem.ac.in\",\"password\":\"IntegrationDemo123\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String facultyToken = objectMapper.readTree(facultyLogin).path("token").asText();
        JsonNode options = objectMapper.readTree(mvc.perform(get("/api/faculty/attendance/options")
                        .header("Authorization", "Bearer " + facultyToken))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        assertTrue(options.size() > 0);
        for (JsonNode option : options) assertEquals("IT", option.path("departmentCode").asText());
        JsonNode facultyTimetable = objectMapper.readTree(mvc.perform(get("/api/faculty/timetable")
                        .header("Authorization", "Bearer " + facultyToken))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        assertFalse(facultyTimetable.isEmpty());
        assertTrue(facultyTimetable.get(0).path("classroom").asText().contains("Demo"));
        long syllabusSubjectId = options.get(0).path("subjectId").asLong();
        mvc.perform(put("/api/faculty/subjects/" + syllabusSubjectId + "/syllabus")
                        .header("Authorization", "Bearer " + facultyToken).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"syllabus\":\"Integration syllabus\"}"))
                .andExpect(status().isOk());
        long scheduledAssignmentId = options.get(1).path("id").asLong();
        mvc.perform(put("/api/faculty/timetable/" + scheduledAssignmentId)
                        .header("Authorization", "Bearer " + facultyToken).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dayOfWeek\":\"TUESDAY\",\"startTime\":\"13:00\",\"endTime\":\"14:00\",\"classroom\":\"IT Lab 2\"}"))
                .andExpect(status().isOk());

        long unauthorizedAssignmentId = teachingAssignmentRepository.findAll().stream()
                .filter(a -> a.getAcademicClass().getDepartment().getCode().equals("CE"))
                .findFirst().orElseThrow().getId();
        LocalDate lectureDate = nextMonday();
        mvc.perform(get("/api/faculty/attendance/roster").param("assignmentId", String.valueOf(unauthorizedAssignmentId))
                        .param("date", lectureDate.toString()).header("Authorization", "Bearer " + facultyToken))
                .andExpect(status().isForbidden());

        JsonNode option = options.get(0);
        long assignmentId = option.path("id").asLong();
        String email = "attendance." + UUID.randomUUID() + "@tsdcem.ac.in";
        String studentAuth = mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Attendance Test Student\",\"email\":\"" + email + "\",\"password\":\"CampusDemo123\",\"department\":\"IT\",\"year\":\"FE\",\"division\":\"A\",\"batch\":\"A1\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        String studentToken = objectMapper.readTree(studentAuth).path("token").asText();
        Student testStudent = studentRepository.findByEmailIgnoreCase(email).orElseThrow();
        testStudent.setDepartment("IT"); testStudent.setYear("FE"); testStudent.setDivision("A"); testStudent.setBatch("A1");
        studentRepository.save(testStudent);
        assertTrue(studentEnrollmentRepository.existsByStudentIdAndAcademicClassId(
                testStudent.getId(), assignmentRepository.findById(assignmentId).orElseThrow().getAcademicClass().getId()));
        JsonNode studentTimetable = objectMapper.readTree(mvc.perform(get("/api/student/timetable")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        assertFalse(studentTimetable.isEmpty());
        assertTrue(java.util.stream.StreamSupport.stream(studentTimetable.spliterator(), false)
                .anyMatch(row -> row.path("syllabus").asText().equals("Integration syllabus")));
        assertTrue(java.util.stream.StreamSupport.stream(studentTimetable.spliterator(), false)
                .anyMatch(row -> row.path("assignmentId").asLong() == scheduledAssignmentId && row.path("dayOfWeek").asText().equals("TUESDAY")));

        mvc.perform(get("/api/faculty/attendance/roster").param("assignmentId", String.valueOf(assignmentId))
                        .param("date", lectureDate.toString()).header("Authorization", "Bearer " + facultyToken))
                .andExpect(status().isOk());
        mvc.perform(get("/api/faculty/attendance/roster").param("assignmentId", String.valueOf(assignmentId))
                        .param("date", lectureDate.plusDays(1).toString()).header("Authorization", "Bearer " + facultyToken))
                .andExpect(status().isBadRequest());
        JsonNode roster = objectMapper.readTree(mvc.perform(get("/api/faculty/attendance/roster")
                        .param("assignmentId", String.valueOf(assignmentId)).param("date", lectureDate.toString())
                        .header("Authorization", "Bearer " + facultyToken))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        assertTrue(roster.path("students").size() >= 3);

        mvc.perform(post("/api/faculty/attendance/sessions").header("Authorization", "Bearer " + facultyToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"assignmentId\":" + assignmentId + ",\"date\":\"" + lectureDate + "\"}"))
                .andExpect(status().isBadRequest());
        String incomplete = objectMapper.writeValueAsString(Map.of("assignmentId", assignmentId, "date", lectureDate.toString(),
                "attendance", List.of(Map.of("studentId", roster.path("students").get(0).path("id").asLong(), "present", true))));
        mvc.perform(post("/api/faculty/attendance/sessions").header("Authorization", "Bearer " + facultyToken)
                        .contentType(MediaType.APPLICATION_JSON).content(incomplete))
                .andExpect(status().isBadRequest());
        String outsideClass = objectMapper.writeValueAsString(Map.of("assignmentId", assignmentId, "date", lectureDate.toString(),
                "attendance", List.of(Map.of("studentId", 99999999L, "present", true))));
        mvc.perform(post("/api/faculty/attendance/sessions").header("Authorization", "Bearer " + facultyToken)
                        .contentType(MediaType.APPLICATION_JSON).content(outsideClass))
                .andExpect(status().isForbidden());
        JsonNode marks = attendanceMarks(roster, true);
        String request = objectMapper.writeValueAsString(Map.of("assignmentId", assignmentId, "date", lectureDate.toString(), "attendance", marks));
        String saved = mvc.perform(post("/api/faculty/attendance/sessions").header("Authorization", "Bearer " + facultyToken)
                        .contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long sessionId = objectMapper.readTree(saved).path("id").asLong();
        assertEquals(roster.path("students").size(), objectMapper.readTree(saved).path("totalCount").asInt());
        mvc.perform(post("/api/faculty/attendance/sessions").header("Authorization", "Bearer " + facultyToken)
                        .contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isConflict());

        JsonNode correctedMarks = attendanceMarks(roster, false);
        String correction = objectMapper.writeValueAsString(Map.of("assignmentId", assignmentId, "date", lectureDate.toString(), "attendance", correctedMarks));
        mvc.perform(put("/api/faculty/attendance/sessions/" + sessionId).header("Authorization", "Bearer " + facultyToken)
                        .contentType(MediaType.APPLICATION_JSON).content(correction))
                .andExpect(status().isOk());
        assertTrue(attendanceChangeRepository.count() > 0);

        JsonNode own = objectMapper.readTree(mvc.perform(get("/api/student/attendance").header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        assertEquals(1, own.path("classesHeld").asInt());
        assertEquals(0, own.path("classesAttended").asInt());
        assertEquals(1, own.path("classesMissed").asInt());
        assertEquals(0.0, own.path("overallPercentage").asDouble());
        assertEquals(1, own.path("sessions").size());
        JsonNode savedHistory = objectMapper.readTree(mvc.perform(get("/api/faculty/attendance/sessions")
                        .header("Authorization", "Bearer " + facultyToken))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        assertEquals(1, savedHistory.size());
        assertEquals(0, savedHistory.get(0).path("presentCount").asInt());
        assertEquals(1, attendanceSessionRepository.count());
        assertTrue(attendanceRecordRepository.count() >= 3);
        mvc.perform(get("/api/student/attendance").header("Authorization", "Bearer " + facultyToken))
                .andExpect(status().isForbidden());

        String anotherEmail = "attendance.other." + UUID.randomUUID() + "@tsdcem.ac.in";
        String anotherAuth = mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Another Student\",\"email\":\"" + anotherEmail + "\",\"password\":\"CampusDemo123\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        JsonNode another = objectMapper.readTree(mvc.perform(get("/api/student/attendance")
                        .header("Authorization", "Bearer " + objectMapper.readTree(anotherAuth).path("token").asText()))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        assertEquals(0, another.path("classesHeld").asInt());
    }

    private LocalDate nextMonday() {
        LocalDate today = LocalDate.now();
        return today.plusDays((8 - today.getDayOfWeek().getValue()) % 7);
    }

    private JsonNode attendanceMarks(JsonNode roster, boolean present) {
        var marks = new ArrayList<Map<String, Object>>();
        for (JsonNode student : roster.path("students")) {
            Map<String, Object> mark = new LinkedHashMap<>();
            mark.put("studentId", student.path("id").asLong());
            mark.put("present", present);
            marks.add(mark);
        }
        try { return objectMapper.valueToTree(marks); }
        catch (IllegalArgumentException exception) { throw new AssertionError(exception); }
    }
}
