package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.dto.GroupDto;
import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.mock.BaseIntegrationTest;
import de.tum.cit.aet.thesis.repository.GroupRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GroupControllerTest extends BaseIntegrationTest {

    @Autowired
    private GroupRepository groupRepository;

    @Test
    @WithMockUser(roles = {"USER"})
    void getAllGroups_ShouldReturnGroups() throws Exception {
        // Given
        Group group = new Group();
        group.setName("Test Group");
        group.setDescription("Test Description");
        groupRepository.save(group);

        // When
        ResultActions result = mockMvc.perform(get("/v2/groups"));

        // Then
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$[0].name").value("Test Group"))
              .andExpect(jsonPath("$[0].description").value("Test Description"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createGroup_WithValidData_ShouldCreateGroup() throws Exception {
        // Given
        GroupDto groupDto = new GroupDto();
        groupDto.setName("New Group");
        groupDto.setDescription("New Description");

        // When
        ResultActions result = mockMvc.perform(post("/v2/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(groupDto)));

        // Then
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.name").value("New Group"))
              .andExpect(jsonPath("$.description").value("New Description"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void createGroup_WithoutAdminRole_ShouldReturnForbidden() throws Exception {
        // Given
        GroupDto groupDto = new GroupDto();
        groupDto.setName("New Group");

        // When
        ResultActions result = mockMvc.perform(post("/v2/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(groupDto)));

        // Then
        result.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updateGroup_WithValidData_ShouldUpdateGroup() throws Exception {
        // Given
        Group group = new Group();
        group.setName("Original Name");
        group = groupRepository.save(group);

        GroupDto updateDto = new GroupDto();
        updateDto.setName("Updated Name");
        updateDto.setDescription("Updated Description");

        // When
        ResultActions result = mockMvc.perform(put("/v2/groups/" + group.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)));

        // Then
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.name").value("Updated Name"))
              .andExpect(jsonPath("$.description").value("Updated Description"));
    }
}
