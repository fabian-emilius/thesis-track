package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.constants.GroupRole;
import de.tum.cit.aet.thesis.dto.GroupDto;
import de.tum.cit.aet.thesis.dto.GroupSettingsDto;
import de.tum.cit.aet.thesis.entity.*;
import de.tum.cit.aet.thesis.mock.BaseIntegrationTest;
import de.tum.cit.aet.thesis.mock.EntityMockFactory;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
class MailingServiceIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private MailingService mailingService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private JavaMailSender mailSender;

    private Group testGroup;
    private Application testApplication;
    private Thesis testThesis;

    @BeforeEach
    void setUp() {
        // Create test group with custom templates
        GroupDto groupDto = new GroupDto();
        groupDto.setName("Test Group");
        groupDto.setSlug("test-group");
        groupDto.setDescription("Test Description");
        GroupDto createdGroup = groupService.createGroup(groupDto);

        GroupSettingsDto settingsDto = new GroupSettingsDto();
        settingsDto.setAcceptanceEmailTemplate("Custom acceptance template");
        settingsDto.setPostAcceptanceInstructions("Custom instructions");
        settingsDto.setEmailFooter("Custom footer");
        groupService.updateGroupSettings(createdGroup.getId(), settingsDto);

        testGroup = groupService.findById(createdGroup.getId());

        // Create test thesis and application
        testThesis = EntityMockFactory.createThesis(testGroup);
        testApplication = EntityMockFactory.createApplication(testThesis);
    }

    @Test
    void sendApplicationAcceptedMail_ShouldUseGroupTemplate() throws MessagingException {
        mailingService.sendApplicationAcceptedMail(testApplication);

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void sendThesisCreatedMail_ShouldIncludeGroupInfo() throws MessagingException {
        mailingService.sendThesisCreatedMail(testThesis);

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void sendThesisPresentationScheduledMail_ShouldIncludeGroupInfo() throws MessagingException {
        ThesisPresentation presentation = EntityMockFactory.createPresentation(testThesis);
        mailingService.sendPresentationScheduledMail(presentation);

        verify(mailSender).send(any(MimeMessage.class));
    }
}