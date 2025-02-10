```java
package de.tum.cit.aet.thesis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import de.tum.cit.aet.thesis.constants.*;
import de.tum.cit.aet.thesis.controller.payload.RequestChangesPayload;
import de.tum.cit.aet.thesis.controller.payload.ThesisStatePayload;
import de.tum.cit.aet.thesis.entity.*;
import de.tum.cit.aet.thesis.entity.jsonb.ThesisMetadata;
import de.tum.cit.aet.thesis.entity.key.ThesisRoleId;
import de.tum.cit.aet.thesis.entity.key.ThesisStateChangeId;
import de.tum.cit.aet.thesis.exception.request.ResourceInvalidParametersException;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.repository.*;
import de.tum.cit.aet.thesis.utility.DataFormatter;
import de.tum.cit.aet.thesis.utility.PDFBuilder;
import de.tum.cit.aet.thesis.utility.RequestValidator;

import java.time.Instant;
import java.util.*;

@Service
public class ThesisService {
    private final ThesisRoleRepository thesisRoleRepository;
    private final ThesisRepository thesisRepository;
    private final ThesisStateChangeRepository thesisStateChangeRepository;
    private final UserRepository userRepository;
    private final UploadService uploadService;
    private final ThesisProposalRepository thesisProposalRepository;
    private final ThesisAssessmentRepository thesisAssessmentRepository;
    private final MailingService mailingService;
    private final AccessManagementService accessManagementService;
    private final ThesisPresentationService thesisPresentationService;
    private final ThesisFeedbackRepository thesisFeedbackRepository;
    private final ThesisFileRepository thesisFileRepository;
    private final PublishedThesisService publishedThesisService;
    private final GroupAccessService groupAccessService;

    @Autowired
    public ThesisService(
            ThesisRoleRepository thesisRoleRepository,
            ThesisRepository thesisRepository,
            ThesisStateChangeRepository thesisStateChangeRepository,
            UserRepository userRepository,
            ThesisProposalRepository thesisProposalRepository,
            ThesisAssessmentRepository thesisAssessmentRepository,
            UploadService uploadService,
            MailingService mailingService,
            AccessManagementService accessManagementService,
            ThesisPresentationService thesisPresentationService,
            ThesisFeedbackRepository thesisFeedbackRepository, 
            ThesisFileRepository thesisFileRepository,
            PublishedThesisService publishedThesisService,
            GroupAccessService groupAccessService) {
        this.thesisRoleRepository = thesisRoleRepository;
        this.thesisRepository = thesisRepository;
        this.thesisStateChangeRepository = thesisStateChangeRepository;
        this.userRepository = userRepository;
        this.uploadService = uploadService;
        this.thesisProposalRepository = thesisProposalRepository;
        this.thesisAssessmentRepository = thesisAssessmentRepository;
        this.mailingService = mailingService;
        this.accessManagementService = accessManagementService;
        this.thesisPresentationService = thesisPresentationService;
        this.thesisFeedbackRepository = thesisFeedbackRepository;
        this.thesisFileRepository = thesisFileRepository;
        this.publishedThesisService = publishedThesisService;
        this.groupAccessService = groupAccessService;
    }

    public Page<Thesis> getAll(
            UUID userId,
            Set<ThesisVisibility> visibilities,
            String searchQuery,
            ThesisState[] states,
            String[] types,
            int page,
            int limit,
            String sortBy,
            String sortOrder
    ) {
        Sort.Order order = new Sort.Order(sortOrder.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);

        String searchQueryFilter = searchQuery == null || searchQuery.isEmpty() ? null : searchQuery.toLowerCase();
        Set<ThesisState> statesFilter = states == null || states.length == 0 ? null : new HashSet<>(Arrays.asList(states));
        Set<String> typesFilter = types == null || types.length == 0 ? null : new HashSet<>(Arrays.asList(types));
        Set<String> userGroups = groupAccessService.getUserGroups(userId);

        return thesisRepository.searchTheses(
                userId,
                visibilities,
                searchQueryFilter,
                statesFilter,
                typesFilter,
                userGroups,
                PageRequest.of(page, limit, Sort.by(order))
        );
    }

    @Transactional
    public Thesis createThesis(
            User creator,
            String thesisTitle,
            String thesisType,
            String language,
            List<UUID> supervisorIds,
            List<UUID> advisorIds,
            List<UUID> studentIds,
            Application application,
            boolean notifyUser
    ) {
        Thesis thesis = new Thesis();

        thesis.setTitle(thesisTitle);
        thesis.setType(thesisType);
        thesis.setLanguage(language);
        thesis.setMetadata(ThesisMetadata.getEmptyMetadata());
        thesis.setVisibility(ThesisVisibility.INTERNAL);
        thesis.setKeywords(new HashSet<>());
        thesis.setInfo("");
        thesis.setAbstractField("");
        thesis.setState(ThesisState.PROPOSAL);
        thesis.setApplication(application);
        thesis.setCreatedAt(Instant.now());
        thesis.setGroupVisibility(groupAccessService.getDefaultVisibilityGroups());

        thesis = thesisRepository.save(thesis);

        assignThesisRoles(thesis, creator, supervisorIds, advisorIds, studentIds);
        saveStateChange(thesis, ThesisState.PROPOSAL, Instant.now());

        if (notifyUser) {
            mailingService.sendThesisCreatedEmail(creator, thesis);
        }

        for (User student : thesis.getStudents()) {
            accessManagementService.addStudentGroup(student);
        }

        return thesis;
    }

    @Transactional
    public Thesis updateThesisVisibility(Thesis thesis, ThesisVisibility visibility, Set<String> groupVisibility) {
        if (!groupAccessService.validateGroupVisibility(groupVisibility)) {
            throw new ResourceInvalidParametersException("Invalid group visibility settings");
        }
        
        thesis.setVisibility(visibility);
        thesis.setGroupVisibility(groupVisibility);
        
        if (visibility == ThesisVisibility.PUBLIC) {
            publishedThesisService.publishThesis(thesis);
        } else {
            publishedThesisService.unpublishThesis(thesis);
        }
        
        return thesisRepository.save(thesis);
    }

    public Thesis findById(UUID thesisId) {
        Thesis thesis = thesisRepository.findById(thesisId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Thesis with id %s not found.", thesisId)));
                
        if (!groupAccessService.canAccessThesis(thesis)) {
            throw new ResourceNotFoundException("Thesis not accessible");
        }
        
        return thesis;
    }

    // Rest of the existing methods remain unchanged
    // ... (keep all other existing methods as they were)
}
```