package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.dto.GroupDto;
import de.tum.cit.aet.thesis.dto.GroupSettingsDto;
import de.tum.cit.aet.thesis.mock.BaseIntegrationTest;
import de.tum.cit.aet.thesis.service.GroupService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GroupControllerTest extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GroupService groupService;

    @Test
    void getAllGroups_ShouldReturnGroups() throws Exception {
        mockMvc.perform(get("/v2/groups")
                        .header("Authorization", "Bearer " + getAdminToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void createGroup_WithValidData_ShouldCreateGroup() throws Exception {
        GroupDto dto = new GroupDto();
        dto.setName("Test Group");
        dto.setSlug("test-group");
        dto.setDescription("Test Description");

        mockMvc.perform(post("/v2/groups")
                        .header("Authorization", "Bearer " + getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(dto.getName()));
    }

    @Test
    void updateGroupSettings_WithValidData_ShouldUpdateSettings() throws Exception {
        // First create a group
        GroupDto groupDto = new GroupDto();
        groupDto.setName("Test Group");
        groupDto.setSlug("test-group");
        GroupDto createdGroup = groupService.createGroup(groupDto);

        // Then update its settings
        GroupSettingsDto settingsDto = new GroupSettingsDto();
        settingsDto.setAcceptanceEmailTemplate("Test template");
        settingsDto.setPostAcceptanceInstructions("Test instructions");
        settingsDto.setEmailFooter("Test footer");

        mockMvc.perform(put("/v2/groups/" + createdGroup.getId() + "/settings")
                        .header("Authorization", "Bearer " + getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(settingsDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.acceptanceEmailTemplate").value(settingsDto.getAcceptanceEmailTemplate()));
    }

    @Test
    void createGroup_WithoutAdminRole_ShouldReturnForbidden() throws Exception {
        GroupDto dto = new GroupDto();
        dto.setName("Test Group");
        dto.setSlug("test-group");

        mockMvc.perform(post("/v2/groups")
                        .header("Authorization", "Bearer " + getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }
}