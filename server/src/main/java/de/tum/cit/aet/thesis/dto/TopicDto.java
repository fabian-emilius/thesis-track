package de.tum.cit.aet.thesis.dto;

import de.tum.cit.aet.thesis.entity.Topic;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class TopicDto {
    private UUID id;
    private String title;
    private Set<String> thesisTypes;
    private String problemStatement;
    private String requirements;
    private String goals;
    private String references;
    private Instant closedAt;
    private Instant updatedAt;
    private Instant createdAt;
    private UUID groupId;
    private List<TopicRoleDto> roles;

    public static TopicDto from(Topic topic) {
        TopicDto dto = new TopicDto();
        dto.setId(topic.getId());
        dto.setTitle(topic.getTitle());
        dto.setThesisTypes(topic.getThesisTypes());
        dto.setProblemStatement(topic.getProblemStatement());
        dto.setRequirements(topic.getRequirements());
        dto.setGoals(topic.getGoals());
        dto.setReferences(topic.getReferences());
        dto.setClosedAt(topic.getClosedAt());
        dto.setUpdatedAt(topic.getUpdatedAt());
        dto.setCreatedAt(topic.getCreatedAt());
        if (topic.getGroup() != null) {
            dto.setGroupId(topic.getGroup().getId());
        }
        dto.setRoles(topic.getRoles().stream()
                .map(TopicRoleDto::from)
                .collect(Collectors.toList()));
        return dto;
    }
}
