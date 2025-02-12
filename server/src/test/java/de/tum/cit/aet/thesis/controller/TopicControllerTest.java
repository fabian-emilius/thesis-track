```java
package de.tum.cit.aet.thesis.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Testcontainers;
import de.tum.cit.aet.thesis.constants.ApplicationRejectReason;
import de.tum.cit.aet.thesis.controller.payload.CloseTopicPayload;
import de.tum.cit.aet.thesis.controller.payload.ReplaceTopicPayload;
import de.tum.cit.aet.thesis.mock.BaseIntegrationTest;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
class TopicControllerTest extends BaseIntegrationTest {

    @DynamicPropertySource
    static void configureDynamicProperties(DynamicPropertyRegistry registry) {
        configureProperties(registry);
    }

    @Test
    void getTopics_Success() throws Exception {
        UUID groupId = createTestGroup("Test Group");
        createTestTopic("Test Topic", groupId);

        mockMvc.perform(MockMvcRequestBuilders.get("/v2/groups/{groupId}/topics", groupId)
                        .header("Authorization", createRandomAdminAuthentication()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", isA(List.class)))
                .andExpect(jsonPath("$.content", hasSize(equalTo(1))))
                .andExpect(jsonPath("$.totalElements", isA(Number.class)));
    }

    @Test
    void getTopic_Success() throws Exception {
        UUID groupId = createTestGroup("Test Group");
        UUID topicId = createTestTopic("Test Topic", groupId);

        mockMvc.perform(MockMvcRequestBuilders.get("/v2/groups/{groupId}/topics/{topicId}", groupId, topicId)
                        .header("Authorization", createRandomAdminAuthentication()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.topicId").value(topicId.toString()))
                .andExpect(jsonPath("$.title").value("Test Topic"))
                .andExpect(jsonPath("$.groupId").value(groupId.toString()));
    }

    @Test
    void getTopic_WrongGroup_NotFound() throws Exception {
        UUID correctGroupId = createTestGroup("Correct Group");
        UUID wrongGroupId = createTestGroup("Wrong Group");
        UUID topicId = createTestTopic("Test Topic", correctGroupId);

        mockMvc.perform(MockMvcRequestBuilders.get("/v2/groups/{groupId}/topics/{topicId}", wrongGroupId, topicId)
                        .header("Authorization", createRandomAdminAuthentication()))
                .andExpect(status().isNotFound());
    }

    @Test
    void createTopic_Success() throws Exception {
        UUID groupId = createTestGroup("Test Group");
        UUID advisorId = createTestUser("supervisor", List.of("supervisor", "advisor"));
        addUserToGroup(advisorId, groupId);

        ReplaceTopicPayload payload = new ReplaceTopicPayload(
                "Test Topic",
                Set.of("MASTER", "BACHELOR"),
                "Problem Statement",
                "Requirements",
                "Goals",
                "References",
                List.of(advisorId),
                List.of(advisorId)
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/v2/groups/{groupId}/topics", groupId)
                        .header("Authorization", createRandomAdminAuthentication())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Topic"))
                .andExpect(jsonPath("$.thesisTypes", containsInAnyOrder("MASTER", "BACHELOR")))
                .andExpect(jsonPath("$.groupId").value(groupId.toString()));
    }

    @Test
    void createTopic_UserNotInGroup_Forbidden() throws Exception {
        UUID groupId = createTestGroup("Test Group");
        UUID advisorId = createTestUser("supervisor", List.of("supervisor", "advisor"));

        ReplaceTopicPayload payload = new ReplaceTopicPayload(
                "Test Topic",
                Set.of("MASTER"),
                "Problem Statement",
                "Requirements",
                "Goals",
                "References",
                List.of(advisorId),
                List.of(advisorId)
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/v2/groups/{groupId}/topics", groupId)
                        .header("Authorization", createRandomAdminAuthentication())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createTopic_AsStudent_Forbidden() throws Exception {
        UUID groupId = createTestGroup("Test Group");
        ReplaceTopicPayload payload = new ReplaceTopicPayload(
                "Test Topic",
                Set.of("MASTER"),
                "Problem Statement",
                "Requirements",
                "Goals",
                "References",
                List.of(UUID.randomUUID()),
                List.of(UUID.randomUUID())
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/v2/groups/{groupId}/topics", groupId)
                        .header("Authorization", createRandomAuthentication("student"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateTopic_Success() throws Exception {
        UUID groupId = createTestGroup("Test Group");
        UUID topicId = createTestTopic("Test Topic", groupId);
        UUID advisorId = createTestUser("supervisor", List.of("supervisor", "advisor"));
        addUserToGroup(advisorId, groupId);

        ReplaceTopicPayload updatePayload = new ReplaceTopicPayload(
                "Updated Topic",
                Set.of("MASTER"),
                "Updated Problem Statement",
                "Updated Requirements",
                "Updated Goals",
                "Updated References",
                List.of(advisorId),
                List.of(advisorId)
        );

        mockMvc.perform(MockMvcRequestBuilders.put("/v2/groups/{groupId}/topics/{topicId}", groupId, topicId)
                        .header("Authorization", createRandomAdminAuthentication())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Topic"))
                .andExpect(jsonPath("$.problemStatement").value("Updated Problem Statement"))
                .andExpect(jsonPath("$.requirements").value("Updated Requirements"))
                .andExpect(jsonPath("$.goals").value("Updated Goals"))
                .andExpect(jsonPath("$.references").value("Updated References"))
                .andExpect(jsonPath("$.groupId").value(groupId.toString()));
    }

    @Test
    void updateTopic_WrongGroup_NotFound() throws Exception {
        UUID correctGroupId = createTestGroup("Correct Group");
        UUID wrongGroupId = createTestGroup("Wrong Group");
        UUID topicId = createTestTopic("Test Topic", correctGroupId);
        UUID advisorId = createTestUser("supervisor", List.of("supervisor", "advisor"));
        addUserToGroup(advisorId, correctGroupId);

        ReplaceTopicPayload updatePayload = new ReplaceTopicPayload(
                "Updated Topic",
                Set.of("MASTER"),
                "Updated Problem Statement",
                "Updated Requirements",
                "Updated Goals",
                "Updated References",
                List.of(advisorId),
                List.of(advisorId)
        );

        mockMvc.perform(MockMvcRequestBuilders.put("/v2/groups/{groupId}/topics/{topicId}", wrongGroupId, topicId)
                        .header("Authorization", createRandomAdminAuthentication())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isNotFound());
    }

    @Test
    void closeTopic_Success() throws Exception {
        UUID groupId = createTestGroup("Test Group");
        UUID topicId = createTestTopic("Test Topic", groupId);

        CloseTopicPayload closePayload = new CloseTopicPayload(
                ApplicationRejectReason.TOPIC_FILLED,
                true
        );

        mockMvc.perform(MockMvcRequestBuilders.delete("/v2/groups/{groupId}/topics/{topicId}", groupId, topicId)
                        .header("Authorization", createRandomAdminAuthentication())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(closePayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.closedAt").value(notNullValue(String.class)));
    }

    @Test
    void closeTopic_WrongGroup_NotFound() throws Exception {
        UUID correctGroupId = createTestGroup("Correct Group");
        UUID wrongGroupId = createTestGroup("Wrong Group");
        UUID topicId = createTestTopic("Test Topic", correctGroupId);

        CloseTopicPayload closePayload = new CloseTopicPayload(
                ApplicationRejectReason.TOPIC_FILLED,
                true
        );

        mockMvc.perform(MockMvcRequestBuilders.delete("/v2/groups/{groupId}/topics/{topicId}", wrongGroupId, topicId)
                        .header("Authorization", createRandomAdminAuthentication())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(closePayload)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTopics_WithPagination_Success() throws Exception {
        UUID groupId = createTestGroup("Test Group");
        createTestTopic("Topic 1", groupId);
        createTestTopic("Topic 2", groupId);
        createTestTopic("Topic 3", groupId);

        mockMvc.perform(MockMvcRequestBuilders.get("/v2/groups/{groupId}/topics", groupId)
                        .header("Authorization", createRandomAdminAuthentication())
                        .param("page", "0")
                        .param("limit", "2")
                        .param("sortBy", "createdAt")
                        .param("sortOrder", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @Test
    void getTopics_WithSearch_Success() throws Exception {
        UUID groupId = createTestGroup("Test Group");
        createTestTopic("Specific Topic Title", groupId);

        mockMvc.perform(MockMvcRequestBuilders.get("/v2/groups/{groupId}/topics", groupId)
                        .header("Authorization", createRandomAdminAuthentication())
                        .param("search", "Specific")
                        .param("page", "0")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(equalTo(1))))
                .andExpect(jsonPath("$.content[0].title", containsString("Specific")))
                .andExpect(jsonPath("$.content[0].groupId").value(groupId.toString()));
    }
}
```