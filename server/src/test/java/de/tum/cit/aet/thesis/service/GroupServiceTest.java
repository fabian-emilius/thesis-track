package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.exception.request.ResourceAlreadyExistsException;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.repository.GroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private GroupService groupService;

    private Group testGroup;

    @BeforeEach
    void setUp() {
        testGroup = new Group();
        testGroup.setId(UUID.randomUUID());
        testGroup.setName("Test Group");
        testGroup.setSlug("test-group");
    }

    @Test
    void getAllGroups_ShouldReturnListOfGroups() {
        when(groupRepository.findAll()).thenReturn(List.of(testGroup));

        List<Group> groups = groupService.getAllGroups();

        assertFalse(groups.isEmpty());
        assertEquals(1, groups.size());
        assertEquals(testGroup.getName(), groups.get(0).getName());
    }

    @Test
    void getGroupById_WithValidId_ShouldReturnGroup() {
        when(groupRepository.findById(testGroup.getId())).thenReturn(Optional.of(testGroup));

        Group found = groupService.getGroupById(testGroup.getId());

        assertNotNull(found);
        assertEquals(testGroup.getName(), found.getName());
    }

    @Test
    void getGroupById_WithInvalidId_ShouldThrowException() {
        UUID invalidId = UUID.randomUUID();
        when(groupRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            groupService.getGroupById(invalidId)
        );
    }

    @Test
    void createGroup_WithUniqueSlug_ShouldSucceed() {
        when(groupRepository.existsBySlug(testGroup.getSlug())).thenReturn(false);
        when(groupRepository.save(any(Group.class))).thenReturn(testGroup);

        Group created = groupService.createGroup(testGroup);

        assertNotNull(created);
        assertEquals(testGroup.getName(), created.getName());
    }

    @Test
    void createGroup_WithDuplicateSlug_ShouldThrowException() {
        when(groupRepository.existsBySlug(testGroup.getSlug())).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () ->
            groupService.createGroup(testGroup)
        );
    }
}