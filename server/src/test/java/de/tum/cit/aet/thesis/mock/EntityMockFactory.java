package de.tum.cit.aet.thesis.mock;

import de.tum.cit.aet.thesis.constants.GroupRole;
import de.tum.cit.aet.thesis.constants.ThesisPresentationState;
import de.tum.cit.aet.thesis.constants.ThesisState;
import de.tum.cit.aet.thesis.constants.ThesisVisibility;
import de.tum.cit.aet.thesis.entity.*;
import de.tum.cit.aet.thesis.entity.key.GroupMemberId;
import de.tum.cit.aet.thesis.entity.jsonb.ThesisMetadata;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.UUID;

public class EntityMockFactory {
    public static Group createGroup(String name) {
        Group group = new Group();
        group.setId(UUID.randomUUID());
        group.setName(name);
        group.setSlug(name.toLowerCase().replace(" ", "-"));
        group.setDescription("Test group description");
        group.setCreatedAt(LocalDateTime.now());
        group.setUpdatedAt(LocalDateTime.now());
        return group;
    }

    public static GroupSettings createGroupSettings(Group group) {
        GroupSettings settings = new GroupSettings();
        settings.setGroup(group);
        settings.setGroupId(group.getId());
        settings.setAcceptanceEmailTemplate("Test acceptance template");
        settings.setPostAcceptanceInstructions("Test instructions");
        settings.setEmailFooter("Test footer");
        return settings;
    }

    public static GroupMember createGroupMember(Group group, User user, GroupRole role) {
        GroupMember member = new GroupMember();
        member.setId(new GroupMemberId(group.getId(), user.getId()));
        member.setGroup(group);
        member.setUser(user);
        member.setRole(role);
        return member;
    }

    public static Thesis createThesis(Group group) {
        Thesis thesis = new Thesis();
        thesis.setId(UUID.randomUUID());
        thesis.setTitle("Test Thesis");
        thesis.setType("MASTER");
        thesis.setLanguage("en");
        thesis.setMetadata(new ThesisMetadata());
        thesis.setInfo("Test info");
        thesis.setAbstractField("Test abstract");
        thesis.setState(ThesisState.CREATED);
        thesis.setVisibility(ThesisVisibility.INTERNAL);
        thesis.setKeywords(new HashSet<>());
        thesis.setGroup(group);
        thesis.setCreatedAt(Instant.now());
        return thesis;
    }

    public static Application createApplication(Thesis thesis) {
        Application application = new Application();
        application.setId(UUID.randomUUID());
        application.setThesis(thesis);
        application.setUser(createUser());
        application.setMotivation("Test motivation");
        application.setCreatedAt(Instant.now());
        return application;
    }

    public static ThesisPresentation createPresentation(Thesis thesis) {
        ThesisPresentation presentation = new ThesisPresentation();
        presentation.setId(UUID.randomUUID());
        presentation.setThesis(thesis);
        presentation.setState(ThesisPresentationState.SCHEDULED);
        presentation.setScheduledAt(Instant.now().plus(7, ChronoUnit.DAYS));
        presentation.setLocation("Test Room");
        return presentation;
    }

    public static User createUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testuser");
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        return user;
    }
}