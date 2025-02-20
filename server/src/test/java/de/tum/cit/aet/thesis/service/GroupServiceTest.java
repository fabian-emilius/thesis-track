package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.constants.GroupRole;
import de.tum.cit.aet.thesis.dto.GroupDto;
import de.tum.cit.aet.thesis.dto.GroupSettingsDto;
import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.GroupMember;
import de.tum.cit.aet.thesis.entity.GroupSettings;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.exception.request.ResourceAlreadyExistsException;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.mock.EntityMockFactory;
import de.tum.cit.aet.thesis.repository.GroupMemberRepository;
import de.tum.cit.aet.thesis.repository.GroupRepository;
import de.tum.cit.aet.thesis.repository.GroupSettingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {
    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupSettingsRepository groupSettingsRepository;

    @InjectMocks
    private GroupService groupService;

    private Group testGroup;
    private GroupSettings testSettings;

    @BeforeEach
    void setUp() {
        testGroup = EntityMockFactory.createGroup("Test Group");
        testSettings = EntityMockFactory.createGroupSettings(testGroup);
        testGroup.setSettings(testSettings);
    }

    @Test
    void getAllGroups_ShouldReturnGroups() {
        when(groupRepository.findAll()).thenReturn(List.of(testGroup));

        List<GroupDto> result = groupService.getAllGroups();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo(testGroup.getName());
    }

    @Test
    void getGroupById_WithValidId_ShouldReturnGroup() {
        when(groupRepository.findById(testGroup.getId())).thenReturn(Optional.of(testGroup));

        GroupDto result = groupService.getGroupById(testGroup.getId());

        assertThat(result.getName()).isEqualTo(testGroup.getName());
    }

    @Test
    void getGroupById_WithInvalidId_ShouldThrowException() {
        when(groupRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> groupService.getGroupById(testGroup.getId()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createGroup_WithValidData_ShouldCreateGroup() {
        GroupDto dto = new GroupDto();
        dto.setName("New Group");
        dto.setSlug("new-group");

        when(groupRepository.existsBySlug(dto.getSlug())).thenReturn(false);
        when(groupRepository.save(any())).thenReturn(testGroup);

        GroupDto result = groupService.createGroup(dto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(testGroup.getName());
    }

    @Test
    void createGroup_WithExistingSlug_ShouldThrowException() {
        GroupDto dto = new GroupDto();
        dto.setName("New Group");
        dto.setSlug("existing-slug");

        when(groupRepository.existsBySlug(dto.getSlug())).thenReturn(true);

        assertThatThrownBy(() -> groupService.createGroup(dto))
                .isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test
    void updateGroupSettings_WithValidData_ShouldUpdateSettings() {
        GroupSettingsDto dto = new GroupSettingsDto();
        dto.setAcceptanceEmailTemplate("New template");
        dto.setPostAcceptanceInstructions("New instructions");
        dto.setEmailFooter("New footer");

        when(groupSettingsRepository.findById(testGroup.getId())).thenReturn(Optional.of(testSettings));
        when(groupSettingsRepository.save(any())).thenReturn(testSettings);

        GroupSettingsDto result = groupService.updateGroupSettings(testGroup.getId(), dto);

        assertThat(result).isNotNull();
        assertThat(result.getAcceptanceEmailTemplate()).isEqualTo(dto.getAcceptanceEmailTemplate());
    }
}