package com;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
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
        mvc.perform(post("/api/assignments").header("Authorization", "Bearer " + facultyToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Faculty API test\",\"status\":\"PENDING\"}"))
                .andExpect(status().isCreated());

        String adminResponse = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"integration.admin@tsdcem.ac.in\",\"password\":\"IntegrationDemo123\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String adminToken = objectMapper.readTree(adminResponse).path("token").asText();
        mvc.perform(get("/api/students").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
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
}
