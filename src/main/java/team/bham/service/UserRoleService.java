package team.bham.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.bham.domain.UserRole;
import team.bham.repository.UserRoleRepository;

/**
 * Service Implementation for managing {@link UserRole}.
 */
@Service
@Transactional
public class UserRoleService {

    private final Logger log = LoggerFactory.getLogger(UserRoleService.class);

    private final UserRoleRepository userRoleRepository;

    public UserRoleService(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    /**
     * Save a userRole.
     *
     * @param userRole the entity to save.
     * @return the persisted entity.
     */
    public UserRole save(UserRole userRole) {
        log.debug("Request to save UserRole : {}", userRole);
        return userRoleRepository.save(userRole);
    }

    /**
     * Update a userRole.
     *
     * @param userRole the entity to save.
     * @return the persisted entity.
     */
    public UserRole update(UserRole userRole) {
        log.debug("Request to update UserRole : {}", userRole);
        return userRoleRepository.save(userRole);
    }

    /**
     * Partially update a userRole.
     *
     * @param userRole the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<UserRole> partialUpdate(UserRole userRole) {
        log.debug("Request to partially update UserRole : {}", userRole);

        return userRoleRepository
            .findById(userRole.getId())
            .map(existingUserRole -> {
                if (userRole.getRole() != null) {
                    existingUserRole.setRole(userRole.getRole());
                }

                return existingUserRole;
            })
            .map(userRoleRepository::save);
    }

    /**
     * Get all the userRoles.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<UserRole> findAll() {
        log.debug("Request to get all UserRoles");
        return userRoleRepository.findAll();
    }

    /**
     *  Get all the userRoles where UserManagement is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<UserRole> findAllWhereUserManagementIsNull() {
        log.debug("Request to get all userRoles where UserManagement is null");
        return StreamSupport
            .stream(userRoleRepository.findAll().spliterator(), false)
            .filter(userRole -> userRole.getUserManagement() == null)
            .collect(Collectors.toList());
    }

    /**
     * Get one userRole by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<UserRole> findOne(Long id) {
        log.debug("Request to get UserRole : {}", id);
        return userRoleRepository.findById(id);
    }

    /**
     * Delete the userRole by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete UserRole : {}", id);
        userRoleRepository.deleteById(id);
    }
}
