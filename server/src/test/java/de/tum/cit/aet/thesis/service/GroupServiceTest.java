package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.repository.GroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class GroupServiceTest {
    @Mock
    private GroupRepository groupRepository;

    private GroupService groupService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        groupService = new GroupService(groupRepository);
    }

    @Test
    void getAllGroups_ShouldReturnAllGroups() {
        Group group1 = new Group();
        Group group2 = new Group();
        when(groupRepository.findAll()).thenReturn(Arrays.asList(group1, group2));

        List<Group> result = groupService.getAllGroups();

        assertEquals(2, result.size());
    }

    @Test
    void getGroupById_WithValidId_ShouldReturnGroup() {
        UUID id = UUID.randomUUID();
        Group group = new Group();
        when(groupRepository.findById(id)).thenReturn(Optional.of(group));

        Group result = groupService.getGroupById(id);

        assertNotNull(result);
    }

    @Test
    void getGroupById_WithInvalidId_ShouldThrowException() {
        UUID id = UUID.randomUUID();
        when(groupRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> groupService.getGroupById(id));
    }

    @Test
    void createGroup_ShouldReturnSavedGroup() {
        Group group = new Group();
        when(groupRepository.save(any(Group.class))).thenReturn(group);

        Group result = groupService.createGroup(group);

        assertNotNull(result);
    }

    @Test
    void updateGroup_WithValidId_ShouldReturnUpdatedGroup() {
        UUID id = UUID.randomUUID();
        Group existingGroup = new Group();
        Group updatedGroup = new Group();
        updatedGroup.setName("Updated Name");

        when(groupRepository.findById(id)).thenReturn(Optional.of(existingGroup));
        when(groupRepository.save(any(Group.class))).thenReturn(updatedGroup);

        Group result = groupService.updateGroup(id, updatedGroup);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
    }
}
