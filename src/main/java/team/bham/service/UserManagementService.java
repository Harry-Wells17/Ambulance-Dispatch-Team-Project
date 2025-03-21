package team.bham.service;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.bham.domain.UserManagement;
import team.bham.repository.UserManagementRepository;

/**
 * Service Implementation for managing {@link UserManagement}.
 */
@Service
@Transactional
public class UserManagementService {

    private final Logger log = LoggerFactory.getLogger(UserManagementService.class);

    private final UserManagementRepository userManagementRepository;

    public UserManagementService(UserManagementRepository userManagementRepository) {
        this.userManagementRepository = userManagementRepository;
    }

    /**
     * Save a userManagement.
     *
     * @param userManagement the entity to save.
     * @return the persisted entity.
     */
    public UserManagement save(UserManagement userManagement) {
        log.debug("Request to save UserManagement : {}", userManagement);
        return userManagementRepository.save(userManagement);
    }

    /**
     * Update a userManagement.
     *
     * @param userManagement the entity to save.
     * @return the persisted entity.
     */
    public UserManagement update(UserManagement userManagement) {
        log.debug("Request to update UserManagement : {}", userManagement);
        return userManagementRepository.save(userManagement);
    }

    /**
     * Partially update a userManagement.
     *
     * @param userManagement the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<UserManagement> partialUpdate(UserManagement userManagement) {
        log.debug("Request to partially update UserManagement : {}", userManagement);

        return userManagementRepository
            .findById(userManagement.getId())
            .map(existingUserManagement -> {
                return existingUserManagement;
            })
            .map(userManagementRepository::save);
    }

    /**
     * Get all the userManagements.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<UserManagement> findAll() {
        log.debug("Request to get all UserManagements");
        return userManagementRepository.findAll();
    }

    /**
     * Get one userManagement by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<UserManagement> findOne(Long id) {
        log.debug("Request to get UserManagement : {}", id);
        return userManagementRepository.findById(id);
    }

    /**
     * Delete the userManagement by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete UserManagement : {}", id);
        userManagementRepository.deleteById(id);
    }
}
