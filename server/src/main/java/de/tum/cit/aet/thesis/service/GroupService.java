package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.dto.GroupDto;
import de.tum.cit.aet.thesis.dto.GroupSettingsDto;
import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.GroupSettings;
import de.tum.cit.aet.thesis.exception.request.ResourceAlreadyExistsException;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.repository.GroupRepository;
import de.tum.cit.aet.thesis.repository.GroupSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupSettingsRepository groupSettingsRepository;

    @Transactional(readOnly = true)
    public List<GroupDto> getAllGroups() {
        return groupRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public GroupDto getGroupById(UUID id) {
        return groupRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
    }

    @Transactional(readOnly = true)
    public GroupDto getGroupBySlug(String slug) {
        return groupRepository.findBySlug(slug)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
    }

    @Transactional
    public GroupDto createGroup(GroupDto groupDto) {
        if (groupRepository.existsBySlug(groupDto.getSlug())) {
            throw new ResourceAlreadyExistsException("Group with this slug already exists");
        }

        Group group = new Group();
        updateGroupFromDto(group, groupDto);

        GroupSettings settings = new GroupSettings();
        settings.setGroup(group);
        group.setSettings(settings);

        group = groupRepository.save(group);
        return mapToDto(group);
    }

    @Transactional
    public GroupDto updateGroup(UUID id, GroupDto groupDto) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        if (!group.getSlug().equals(groupDto.getSlug()) && 
            groupRepository.existsBySlug(groupDto.getSlug())) {
            throw new ResourceAlreadyExistsException("Group with this slug already exists");
        }

        updateGroupFromDto(group, groupDto);
        group = groupRepository.save(group);
        return mapToDto(group);
    }

    @Transactional
    public GroupSettingsDto updateGroupSettings(UUID groupId, GroupSettingsDto settingsDto) {
        GroupSettings settings = groupSettingsRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group settings not found"));

        settings.setAcceptanceEmailTemplate(settingsDto.getAcceptanceEmailTemplate());
        settings.setPostAcceptanceInstructions(settingsDto.getPostAcceptanceInstructions());
        settings.setEmailFooter(settingsDto.getEmailFooter());

        settings = groupSettingsRepository.save(settings);
        return mapSettingsToDto(settings);
    }

    private GroupDto mapToDto(Group group) {
        GroupDto dto = new GroupDto();
        dto.setId(group.getId());
        dto.setSlug(group.getSlug());
        dto.setName(group.getName());
        dto.setDescription(group.getDescription());
        dto.setLogoUrl(group.getLogoUrl());
        dto.setExternalLink(group.getExternalLink());
        dto.setCreatedAt(group.getCreatedAt());
        dto.setUpdatedAt(group.getUpdatedAt());
        return dto;
    }

    private GroupSettingsDto mapSettingsToDto(GroupSettings settings) {
        GroupSettingsDto dto = new GroupSettingsDto();
        dto.setAcceptanceEmailTemplate(settings.getAcceptanceEmailTemplate());
        dto.setPostAcceptanceInstructions(settings.getPostAcceptanceInstructions());
        dto.setEmailFooter(settings.getEmailFooter());
        return dto;
    }

    private void updateGroupFromDto(Group group, GroupDto dto) {
        group.setSlug(dto.getSlug());
        group.setName(dto.getName());
        group.setDescription(dto.getDescription());
        group.setLogoUrl(dto.getLogoUrl());
        group.setExternalLink(dto.getExternalLink());
    }
}