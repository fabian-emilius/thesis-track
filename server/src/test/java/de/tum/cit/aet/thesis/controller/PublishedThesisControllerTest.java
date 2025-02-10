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
class PublishedThesisControllerTest extends BaseIntegrationTest {

    @DynamicPropertySource
    static void configureDynamicProperties(DynamicPropertyRegistry registry) {
        configureProperties(registry);
    }

    @Test
    void getVisibleTheses_Success() throws Exception {
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

        // Create a thesis
        UUID thesisId = createTestThesis("Test Thesis");

        // Update thesis visibility
        mockMvc.perform(MockMvcRequestBuilders.post("/v2/published-theses/" + thesisId + "/visibility")
                .header("Authorization", adminAuth)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{"
                    + "\"visibilityGroups\": [\"" + groupId + "\"]"
                    + "}")
                )
                .andExpect(status().isOk());

        // Get visible theses
        mockMvc.perform(MockMvcRequestBuilders.get("/v2/published-theses")
                .header("Authorization", adminAuth)
                .param("groupId", groupId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[*].thesis.id", hasItem(thesisId.toString())));
    }

    @Test
    void updateVisibility_Success() throws Exception {
        String adminAuth = createRandomAdminAuthentication();

        // Create a group
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

        // Create a thesis
        UUID thesisId = createTestThesis("Test Thesis");

        mockMvc.perform(MockMvcRequestBuilders.post("/v2/published-theses/" + thesisId + "/visibility")
                .header("Authorization", adminAuth)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{"
                    + "\"visibilityGroups\": [\"" + groupId + "\"]"
                    + "}")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.visibilityGroups", hasItem(groupId.toString())));
    }

    @Test
    void updateVisibility_AsStudent_Forbidden() throws Exception {
        String studentAuth = createRandomAuthentication("student");
        UUID thesisId = createTestThesis("Test Thesis");

        mockMvc.perform(MockMvcRequestBuilders.post("/v2/published-theses/" + thesisId + "/visibility")
                .header("Authorization", studentAuth)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{"
                    + "\"visibilityGroups\": [\"" + UUID.randomUUID() + "\"]"
                    + "}")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void unpublishThesis_Success() throws Exception {
        String adminAuth = createRandomAdminAuthentication();
        UUID thesisId = createTestThesis("Test Thesis");

        mockMvc.perform(MockMvcRequestBuilders.delete("/v2/published-theses/" + thesisId)
                .header("Authorization", adminAuth))
                .andExpect(status().isOk());
    }
}
