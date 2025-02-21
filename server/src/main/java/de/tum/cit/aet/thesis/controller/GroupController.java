package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.GroupRole;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.service.GroupService;
import de.tum.cit.aet.thesis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v2/groups")
public class GroupController {

    private final GroupService groupService;
    private final UserService userService;

    @Autowired
    public GroupController(GroupService groupService, UserService userService) {
        this.groupService = groupService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Group>> listAllGroups() {
        List<Group> groups = groupService.findAllGroups();
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/{groupSlug}")
    public ResponseEntity<Group> getGroupDetails(@PathVariable String groupSlug) {
        Group group = groupService.findGroupBySlug(groupSlug);
        return ResponseEntity.ok(group);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Group> createGroup(
            @RequestBody GroupCreationRequest request,
            @RequestHeader("X-User-Id") UUID userId) {
        User creator = userService.findById(userId);
        Group group = groupService.createGroup(
                creator,
                request.getSlug(),
                request.getName(),
                request.getDescription(),
                request.getLogoUrl(),
                request.getWebsiteUrl(),
                request.getMailFooter(),
                request.getAcceptanceText(),
                request.getAcceptanceInstructions()
        );
        return ResponseEntity.ok(group);
    }

    @PreAuthorize("hasRole('ADMIN') or @groupService.checkUserPermission(#groupSlug, #userId, 'ADMIN')")
    @PutMapping("/{groupSlug}")
    public ResponseEntity<Group> updateGroup(
            @PathVariable String groupSlug,
            @RequestBody GroupUpdateRequest request,
            @RequestHeader("X-User-Id") UUID userId) {
        Group group = groupService.findGroupBySlug(groupSlug);
        Group updatedGroup = groupService.updateGroup(
                group.getId(),
                request.getName(),
                request.getDescription(),
                request.getLogoUrl(),
                request.getWebsiteUrl(),
                request.getMailFooter(),
                request.getAcceptanceText(),
                request.getAcceptanceInstructions()
        );
        return ResponseEntity.ok(updatedGroup);
    }

    @GetMapping("/{groupSlug}/members")
    public ResponseEntity<List<GroupRole>> getGroupMembers(@PathVariable String groupSlug) {
        Group group = groupService.findGroupBySlug(groupSlug);
        List<GroupRole> members = groupService.findGroupRolesByGroupId(group.getId());
        return ResponseEntity.ok(members);
    }

    @PreAuthorize("hasRole('ADMIN') or @groupService.checkUserPermission(#groupSlug, #userId, 'ADMIN')")
    @PutMapping("/{groupSlug}/members")
    public ResponseEntity<Void> updateGroupMembers(
            @PathVariable String groupSlug,
            @RequestBody GroupMembersUpdateRequest request,
            @RequestHeader("X-User-Id") UUID userId) {
        Group group = groupService.findGroupBySlug(groupSlug);

        // Add new roles
        for (GroupMemberUpdate member : request.getAddMembers()) {
            groupService.addGroupRole(group.getId(), member.getUserId(), member.getRole());
        }

        // Remove roles
        for (UUID userIdToRemove : request.getRemoveMembers()) {
            groupService.removeGroupRole(group.getId(), userIdToRemove);
        }

        return ResponseEntity.noContent().build();
    }

    public static class GroupCreationRequest {
        private String slug;
        private String name;
        private String description;
        private String logoUrl;
        private String websiteUrl;
        private String mailFooter;
        private String acceptanceText;
        private String acceptanceInstructions;

        // Getters and setters
        public String getSlug() {
            return slug;
        }

        public void setSlug(String slug) {
            this.slug = slug;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLogoUrl() {
            return logoUrl;
        }

        public void setLogoUrl(String logoUrl) {
            this.logoUrl = logoUrl;
        }

        public String getWebsiteUrl() {
            return websiteUrl;
        }

        public void setWebsiteUrl(String websiteUrl) {
            this.websiteUrl = websiteUrl;
        }

        public String getMailFooter() {
            return mailFooter;
        }

        public void setMailFooter(String mailFooter) {
            this.mailFooter = mailFooter;
        }

        public String getAcceptanceText() {
            return acceptanceText;
        }

        public void setAcceptanceText(String acceptanceText) {
            this.acceptanceText = acceptanceText;
        }

        public String getAcceptanceInstructions() {
            return acceptanceInstructions;
        }

        public void setAcceptanceInstructions(String acceptanceInstructions) {
            this.acceptanceInstructions = acceptanceInstructions;
        }
    }

    public static class GroupUpdateRequest {
        private String name;
        private String description;
        private String logoUrl;
        private String websiteUrl;
        private String mailFooter;
        private String acceptanceText;
        private String acceptanceInstructions;

        // Getters and setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLogoUrl() {
            return logoUrl;
        }

        public void setLogoUrl(String logoUrl) {
            this.logoUrl = logoUrl;
        }

        public String getWebsiteUrl() {
            return websiteUrl;
        }

        public void setWebsiteUrl(String websiteUrl) {
            this.websiteUrl = websiteUrl;
        }

        public String getMailFooter() {
            return mailFooter;
        }

        public void setMailFooter(String mailFooter) {
            this.mailFooter = mailFooter;
        }

        public String getAcceptanceText() {
            return acceptanceText;
        }

        public void setAcceptanceText(String acceptanceText) {
            this.acceptanceText = acceptanceText;
        }

        public String getAcceptanceInstructions() {
            return acceptanceInstructions;
        }

        public void setAcceptanceInstructions(String acceptanceInstructions) {
            this.acceptanceInstructions = acceptanceInstructions;
        }
    }

    public static class GroupMembersUpdateRequest {
        private List<GroupMemberUpdate> addMembers;
        private List<UUID> removeMembers;

        // Getters and setters
        public List<GroupMemberUpdate> getAddMembers() {
            return addMembers;
        }

        public void setAddMembers(List<GroupMemberUpdate> addMembers) {
            this.addMembers = addMembers;
        }

        public List<UUID> getRemoveMembers() {
            return removeMembers;
        }

        public void setRemoveMembers(List<UUID> removeMembers) {
            this.removeMembers = removeMembers;
        }
    }

    public static class GroupMemberUpdate {
        private UUID userId;
        private String role;

        // Getters and setters
        public UUID getUserId() {
            return userId;
        }

        public void setUserId(UUID userId) {
            this.userId = userId;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }
}
