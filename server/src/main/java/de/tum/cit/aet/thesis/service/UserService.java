package de.tum.cit.aet.thesis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.Instant;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.repository.UserRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UploadService uploadService;

    @Autowired
    public UserService(UserRepository userRepository, UploadService uploadService) {
        this.userRepository = userRepository;
        this.uploadService = uploadService;
    }

    public void updateLastActivity(User user) {
        user.setLastActivityAt(Instant.now());
        userRepository.save(user);
    }

    public boolean isUserEligibleForDeletion(User user) {
        return user.isEligibleForDeletion();
    }

    public Page<User> getAll(String searchQuery, String[] groups, Integer page, Integer limit, String sortBy, String sortOrder) {
        Sort.Order order = new Sort.Order(sortOrder.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);

        String searchQueryFilter = searchQuery == null || searchQuery.isEmpty() ? null : searchQuery.toLowerCase();
        Set<String> groupsFilter = groups == null || groups.length == 0 ? null : new HashSet<>(Arrays.asList(groups));

        Page<User> users = userRepository
                .searchUsers(searchQueryFilter, groupsFilter, PageRequest.of(page, limit, Sort.by(order)));
        
        users.forEach(this::updateLastActivity);
        return users;
    }

    public Resource getExaminationReport(User user) {
        return uploadService.load(user.getExaminationFilename());
    }

    public Resource getCV(User user) {
        return uploadService.load(user.getCvFilename());
    }

    public Resource getDegreeReport(User user) {
        return uploadService.load(user.getDegreeFilename());
    }

    public User findById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id %s not found.", userId)));
        updateLastActivity(user);
        return user;
    }
}
