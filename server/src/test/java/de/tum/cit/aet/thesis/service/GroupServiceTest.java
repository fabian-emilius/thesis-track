package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.repository.GroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private GroupService groupService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllGroups_ShouldReturnListOfGroups() {
        // Arrange
        Group group1 = new Group();
        Group group2 = new Group();
        when(groupRepository.findAll()).thenReturn(Arrays.asList(group1, group2));

        // Act
        List<Group> result = groupService.getAllGroups();

        // Assert
        assertEquals(2, result.size());
        verify(groupRepository).findAll();
    }

    @Test
    void getGroupById_WithValidId_ShouldReturnGroup() {
        // Arrange
        UUID id = UUID.randomUUID();
        Group group = new Group();
        when(groupRepository.findById(id)).thenReturn(Optional.of(group));

        // Act
        Group result = groupService.getGroupById(id);

        // Assert
        assertNotNull(result);
        verify(groupRepository).findById(id);
    }

    @Test
    void getGroupById_WithInvalidId_ShouldThrowException() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(groupRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> groupService.getGroupById(id));
        verify(groupRepository).findById(id);
    }

    @Test
    void createGroup_ShouldReturnCreatedGroup() {
        // Arrange
        Group group = new Group();
        when(groupRepository.save(any(Group.class))).thenReturn(group);

        // Act
        Group result = groupService.createGroup(group);

        // Assert
        assertNotNull(result);
        verify(groupRepository).save(group);
    }

    @Test
    void updateGroup_WithValidId_ShouldReturnUpdatedGroup() {
        // Arrange
        UUID id = UUID.randomUUID();
        Group existingGroup = new Group();
        Group updatedGroup = new Group();
        updatedGroup.setName("Updated Name");
        updatedGroup.setDescription("Updated Description");

        when(groupRepository.findById(id)).thenReturn(Optional.of(existingGroup));
        when(groupRepository.save(any(Group.class))).thenReturn(updatedGroup);

        // Act
        Group result = groupService.updateGroup(id, updatedGroup);

        // Assert
        assertEquals("Updated Name", result.getName());
        assertEquals("Updated Description", result.getDescription());
        verify(groupRepository).findById(id);
        verify(groupRepository).save(any(Group.class));
    }

    @Test
    void deleteGroup_WithValidId_ShouldDeleteGroup() {
        // Arrange
        UUID id = UUID.randomUUID();
        Group group = new Group();
        when(groupRepository.findById(id)).thenReturn(Optional.of(group));

        // Act
        groupService.deleteGroup(id);

        // Assert
        verify(groupRepository).findById(id);
        verify(groupRepository).delete(group);
    }
}