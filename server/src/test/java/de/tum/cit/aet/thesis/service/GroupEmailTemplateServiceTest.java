package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.GroupSettings;
import de.tum.cit.aet.thesis.mock.EntityMockFactory;
import de.tum.cit.aet.thesis.repository.GroupSettingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupEmailTemplateServiceTest {
    @Mock
    private GroupSettingsRepository groupSettingsRepository;

    @Mock
    private SpringTemplateEngine templateEngine;

    @InjectMocks
    private GroupEmailTemplateService groupEmailTemplateService;

    private Group testGroup;
    private GroupSettings testSettings;

    @BeforeEach
    void setUp() {
        testGroup = EntityMockFactory.createGroup("Test Group");
        testSettings = EntityMockFactory.createGroupSettings(testGroup);
    }

    @Test
    void processTemplate_WithGroupSettings_ShouldIncludeGroupVariables() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("testVar", "testValue");

        when(groupSettingsRepository.findById(testGroup.getId())).thenReturn(Optional.of(testSettings));
        when(templateEngine.process(eq("test-template.html"), any())).thenReturn("processed template");

        String result = groupEmailTemplateService.processTemplate(
                "test-template.html", variables, testGroup.getId());

        assertThat(result).isEqualTo("processed template");
    }

    @Test
    void getGroupEmailFooter_WithValidGroup_ShouldReturnFooter() {
        when(groupSettingsRepository.findById(testGroup.getId())).thenReturn(Optional.of(testSettings));

        String result = groupEmailTemplateService.getGroupEmailFooter(testGroup.getId());

        assertThat(result).isEqualTo(testSettings.getEmailFooter());
    }

    @Test
    void getAcceptanceEmailTemplate_WithValidGroup_ShouldReturnTemplate() {
        when(groupSettingsRepository.findById(testGroup.getId())).thenReturn(Optional.of(testSettings));

        String result = groupEmailTemplateService.getAcceptanceEmailTemplate(testGroup.getId());

        assertThat(result).isEqualTo(testSettings.getAcceptanceEmailTemplate());
    }

    @Test
    void getPostAcceptanceInstructions_WithValidGroup_ShouldReturnInstructions() {
        when(groupSettingsRepository.findById(testGroup.getId())).thenReturn(Optional.of(testSettings));

        String result = groupEmailTemplateService.getPostAcceptanceInstructions(testGroup.getId());

        assertThat(result).isEqualTo(testSettings.getPostAcceptanceInstructions());
    }
}