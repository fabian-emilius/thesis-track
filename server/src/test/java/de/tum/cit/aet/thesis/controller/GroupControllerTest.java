package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.dto.GroupDto;
import de.tum.cit.aet.thesis.mock.BaseIntegrationTest;
import de.tum.cit.aet.thesis.service.GroupService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class GroupControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroupService groupService;

    @Test
    @WithMockUser(roles = "admin")
    void createGroup_Success() throws Exception {
        GroupDto groupDto = new GroupDto();
        groupDto.setId(UUID.randomUUID());
        groupDto.setName("Test Group");
        groupDto.setSlug("test-group");

        when(groupService.createGroup(
            eq("Test Group"),
            eq("test-group"),
            any(),
            any(),
            any()
        )).thenReturn(groupDto);

        mockMvc.perform(post("/v2/groups")
                .param("name", "Test Group")
                .param("slug", "test-group")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test Group"))
                .andExpect(jsonPath("$.slug").value("test-group"));
    }

    @Test
    @WithMockUser(roles = "user")
    void createGroup_Unauthorized() throws Exception {
        mockMvc.perform(post("/v2/groups")
                .param("name", "Test Group")
                .param("slug", "test-group")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllGroups_Success() throws Exception {
        GroupDto groupDto = new GroupDto();
        groupDto.setId(UUID.randomUUID());
        groupDto.setName("Test Group");
        groupDto.setSlug("test-group");

        when(groupService.getAllGroups()).thenReturn(List.of(groupDto));

        mockMvc.perform(get("/v2/groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").value("Test Group"))
                .andExpect(jsonPath("$[0].slug").value("test-group"));
    }

    @Test
    @WithMockUser(roles = "admin")
    void updateGroup_Success() throws Exception {
        UUID groupId = UUID.randomUUID();
        GroupDto groupDto = new GroupDto();
        groupDto.setId(groupId);
        groupDto.setName("Updated Group");

        when(groupService.updateGroup(
            eq(groupId),
            eq("Updated Group"),
            any(),
            any(),
            any()
        )).thenReturn(groupDto);

        mockMvc.perform(put("/v2/groups/" + groupId)
                .param("name", "Updated Group")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(groupId.toString()))
                .andExpect(jsonPath("$.name").value("Updated Group"));
    }

    @Test
    @WithMockUser(roles = "admin")
    void deleteGroup_Success() throws Exception {
        UUID groupId = UUID.randomUUID();

        mockMvc.perform(delete("/v2/groups/" + groupId))
                .andExpect(status().isOk());
    }
}
