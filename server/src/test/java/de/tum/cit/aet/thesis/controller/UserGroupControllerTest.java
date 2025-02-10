package de.tum.cit.aet.thesis.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Testcontainers;
import de.tum.cit.aet.thesis.mock.BaseIntegrationTest;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
class UserGroupControllerTest extends BaseIntegrationTest {

    @DynamicPropertySource
    static void configureDynamicProperties(DynamicPropertyRegistry registry) {
        configureProperties(registry);
    }

    @Test
    void createGroup_Success() throws Exception {
        String adminAuth = createRandomAdminAuthentication();

        mockMvc.perform(MockMvcRequestBuilders.post("/v2/groups")
                .header("Authorization", adminAuth)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{"
                    + "\"name\": \"Test Group\","
                    + "\"description\": \"Test Description\""
                    + "}")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Group"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    void createGroup_AsStudent_Forbidden() throws Exception {
        String studentAuth = createRandomAuthentication("student");

        mockMvc.perform(MockMvcRequestBuilders.post("/v2/groups")
                .header("Authorization", studentAuth)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{"
                    + "\"name\": \"Test Group\","
                    + "\"description\": \"Test Description\""
                    + "}")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void getGroups_Success() throws Exception {
        String adminAuth = createRandomAdminAuthentication();

        // Create a group first
        String groupResponse = mockMvc.perform(MockMvcRequestBuilders.post("/v2/groups")
                .header("Authorization", adminAuth)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{"
                    + "\"name\": \"Test Group\","
                    + "\"description\": \"Test Description\""
                    + "}")
                )
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID groupId = objectMapper.readTree(groupResponse).get("id").asText().transform(UUID::fromString);

        mockMvc.perform(MockMvcRequestBuilders.get("/v2/groups")
                .header("Authorization", adminAuth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].id").value(groupId.toString()));
    }

    @Test
    void updateGroup_Success() throws Exception {
        String adminAuth = createRandomAdminAuthentication();

        // Create a group first
        String groupResponse = mockMvc.perform(MockMvcRequestBuilders.post("/v2/groups")
                .header("Authorization", adminAuth)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{"
                    + "\"name\": \"Test Group\","
                    + "\"description\": \"Test Description\""
                    + "}")
                )
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID groupId = objectMapper.readTree(groupResponse).get("id").asText().transform(UUID::fromString);

        mockMvc.perform(MockMvcRequestBuilders.put("/v2/groups/" + groupId)
                .header("Authorization", adminAuth)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{"
                    + "\"name\": \"Updated Group\","
                    + "\"description\": \"Updated Description\""
                    + "}")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Group"))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }

    @Test
    void getGroupTopics_Success() throws Exception {
        String adminAuth = createRandomAdminAuthentication();

        // Create a group first
        String groupResponse = mockMvc.perform(MockMvcRequestBuilders.post("/v2/groups")
                .header("Authorization", adminAuth)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{"
                    + "\"name\": \"Test Group\","
                    + "\"description\": \"Test Description\""
                    + "}")
                )
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID groupId = objectMapper.readTree(groupResponse).get("id").asText().transform(UUID::fromString);

        mockMvc.perform(MockMvcRequestBuilders.get("/v2/groups/" + groupId + "/topics")
                .header("Authorization", adminAuth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getGroupTopics_InvalidGroup_NotFound() throws Exception {
        String adminAuth = createRandomAdminAuthentication();

        mockMvc.perform(MockMvcRequestBuilders.get("/v2/groups/" + UUID.randomUUID() + "/topics")
                .header("Authorization", adminAuth))
                .andExpect(status().isNotFound());
    }
}
