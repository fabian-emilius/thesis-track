package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.constants.GroupRole;
import de.tum.cit.aet.thesis.dto.GroupDto;
import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.GroupMember;
import de.tum.cit.aet.thesis.mock.BaseIntegrationTest;
import de.tum.cit.aet.thesis.repository.GroupMemberRepository;
import de.tum.cit.aet.thesis.repository.GroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GroupControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    private Group testGroup;
    private GroupMember testGroupMember;
    private GroupMember testSupervisorMember;

    @BeforeEach
    void setUp() {
        groupRepository.deleteAll();
        groupMemberRepository.deleteAll();

        testGroup = new Group();
        testGroup.setName("Test Group");
        testGroup.setSlug("test-group");
        testGroup = groupRepository.save(testGroup);

        testGroupMember = new GroupMember();
        testGroupMember.setGroupId(testGroup.getId());
        testGroupMember.setUserId(UUID.randomUUID());
        testGroupMember.setRole(GroupRole.MEMBER);
        testGroupMember.setIsAdmin(true);
        testGroupMember = groupMemberRepository.save(testGroupMember);

        testSupervisorMember = new GroupMember();
        testSupervisorMember.setGroupId(testGroup.getId());
        testSupervisorMember.setUserId(UUID.randomUUID());
        testSupervisorMember.setRole(GroupRole.SUPERVISOR);
        testSupervisorMember.setIsAdmin(true);
        testSupervisorMember = groupMemberRepository.save(testSupervisorMember);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllGroups() throws Exception {
        mockMvc.perform(get("/v2/groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testGroup.getId().toString()))
                .andExpect(jsonPath("$[0].name").value(testGroup.getName()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getGroup() throws Exception {
        mockMvc.perform(get("/v2/groups/{groupId}", testGroup.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testGroup.getId().toString()))
                .andExpect(jsonPath("$.name").value(testGroup.getName()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createGroup() throws Exception {
        GroupDto groupDto = new GroupDto();
        groupDto.setName("New Group");
        groupDto.setSlug("new-group");

        mockMvc.perform(post("/v2/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(groupDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(groupDto.getName()))
                .andExpect(jsonPath("$.slug").value(groupDto.getSlug()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateGroup() throws Exception {
        GroupDto groupDto = new GroupDto();
        groupDto.setName("Updated Group");
        groupDto.setSlug(testGroup.getSlug());

        mockMvc.perform(put("/v2/groups/{groupId}", testGroup.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(groupDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(groupDto.getName()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void uploadGroupLogo() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test-logo.png",
            MediaType.IMAGE_PNG_VALUE,
            "test image content".getBytes()
        );

        mockMvc.perform(multipart("/v2/groups/{groupId}/logo", testGroup.getId())
                .file(file))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getGroupMembers() throws Exception {
        mockMvc.perform(get("/v2/groups/{groupId}/members", testGroup.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].groupId").value(testGroupMember.getGroupId().toString()))
                .andExpect(jsonPath("$[0].userId").value(testGroupMember.getUserId().toString()))
                .andExpect(jsonPath("$[0].role").value(testGroupMember.getRole().toString()))
                .andExpect(jsonPath("$[0].isAdmin").value(true))
                .andExpect(jsonPath("$[1].groupId").value(testSupervisorMember.getGroupId().toString()))
                .andExpect(jsonPath("$[1].userId").value(testSupervisorMember.getUserId().toString()))
                .andExpect(jsonPath("$[1].role").value(testSupervisorMember.getRole().toString()))
                .andExpect(jsonPath("$[1].isAdmin").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void supervisorShouldAutomaticallyBeAdmin() throws Exception {
        GroupMember newSupervisor = new GroupMember();
        newSupervisor.setGroupId(testGroup.getId());
        newSupervisor.setUserId(UUID.randomUUID());
        newSupervisor.setRole(GroupRole.SUPERVISOR);
        
        mockMvc.perform(post("/v2/groups/{groupId}/members", testGroup.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newSupervisor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value(GroupRole.SUPERVISOR.toString()))
                .andExpect(jsonPath("$.isAdmin").value(true));
    }
}
