package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.constants.GroupRole;
import de.tum.cit.aet.thesis.dto.GroupDto;
import de.tum.cit.aet.thesis.mock.BaseIntegrationTest;
import de.tum.cit.aet.thesis.service.GroupService;
import org.junit.jupiter.api.BeforeEach;
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
class GroupMemberControllerTest extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GroupService groupService;

    private GroupDto testGroup;

    @BeforeEach
    void setUp() {
        GroupDto dto = new GroupDto();
        dto.setName("Test Group");
        dto.setSlug("test-group");
        testGroup = groupService.createGroup(dto);
    }

    @Test
    void getGroupMembers_ShouldReturnMembers() throws Exception {
        mockMvc.perform(get("/v2/groups/" + testGroup.getId() + "/members")
                        .header("Authorization", "Bearer " + getAdminToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void addGroupMember_WithValidData_ShouldAddMember() throws Exception {
        mockMvc.perform(post("/v2/groups/" + testGroup.getId() + "/members/" + getTestUserId())
                        .header("Authorization", "Bearer " + getAdminToken())
                        .param("role", GroupRole.ADVISOR.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(getTestUserId().toString()))
                .andExpect(jsonPath("$.role").value(GroupRole.ADVISOR.name()));
    }

    @Test
    void updateMemberRole_WithValidData_ShouldUpdateRole() throws Exception {
        // First add a member
        mockMvc.perform(post("/v2/groups/" + testGroup.getId() + "/members/" + getTestUserId())
                .header("Authorization", "Bearer " + getAdminToken())
                .param("role", GroupRole.ADVISOR.name()));

        // Then update their role
        mockMvc.perform(put("/v2/groups/" + testGroup.getId() + "/members/" + getTestUserId() + "/role")
                        .header("Authorization", "Bearer " + getAdminToken())
                        .param("role", GroupRole.SUPERVISOR.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value(GroupRole.SUPERVISOR.name()));
    }

    @Test
    void removeGroupMember_ShouldRemoveMember() throws Exception {
        // First add a member
        mockMvc.perform(post("/v2/groups/" + testGroup.getId() + "/members/" + getTestUserId())
                .header("Authorization", "Bearer " + getAdminToken())
                .param("role", GroupRole.ADVISOR.name()));

        // Then remove them
        mockMvc.perform(delete("/v2/groups/" + testGroup.getId() + "/members/" + getTestUserId())
                        .header("Authorization", "Bearer " + getAdminToken()))
                .andExpect(status().isOk());
    }

    @Test
    void addGroupMember_WithoutAdminRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/v2/groups/" + testGroup.getId() + "/members/" + getTestUserId())
                        .header("Authorization", "Bearer " + getUserToken())
                        .param("role", GroupRole.ADVISOR.name()))
                .andExpect(status().isForbidden());
    }
}