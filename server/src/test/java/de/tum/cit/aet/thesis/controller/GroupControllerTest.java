package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.mock.BaseIntegrationTest;
import de.tum.cit.aet.thesis.service.GroupService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GroupController.class)
public class GroupControllerTest extends BaseIntegrationTest {

    @MockBean
    private GroupService groupService;

    @Test
    @WithMockUser
    void getAllGroups_ShouldReturnGroups() throws Exception {
        Group group = new Group();
        group.setId(UUID.randomUUID());
        group.setName("Test Group");
        group.setSlug("test-group");

        when(groupService.getAllGroups()).thenReturn(List.of(group));

        mockMvc.perform(get("/v2/groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Group"));
    }

    @Test
    @WithMockUser(roles = "admin")
    void createGroup_WithValidData_ShouldSucceed() throws Exception {
        Group group = new Group();
        group.setId(UUID.randomUUID());
        group.setName("New Group");
        group.setSlug("new-group");

        when(groupService.createGroup(any(Group.class))).thenReturn(group);

        mockMvc.perform(post("/v2/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"New Group\",\"slug\":\"new-group\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Group"));
    }

    @Test
    @WithMockUser
    void createGroup_WithoutAdminRole_ShouldFail() throws Exception {
        mockMvc.perform(post("/v2/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"New Group\",\"slug\":\"new-group\"}"))
                .andExpect(status().isForbidden());
    }
}