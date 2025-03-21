package team.bham.service;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.bham.domain.UserExist;
import team.bham.repository.UserExistRepository;

/**
 * Service Implementation for managing {@link UserExist}.
 */
@Service
@Transactional
public class UserExistService {

    private final Logger log = LoggerFactory.getLogger(UserExistService.class);

    private final UserExistRepository userExistRepository;

    public UserExistService(UserExistRepository userExistRepository) {
        this.userExistRepository = userExistRepository;
    }

    /**
     * Save a userExist.
     *
     * @param userExist the entity to save.
     * @return the persisted entity.
     */
    public UserExist save(UserExist userExist) {
        log.debug("Request to save UserExist : {}", userExist);
        return userExistRepository.save(userExist);
    }

    /**
     * Update a userExist.
     *
     * @param userExist the entity to save.
     * @return the persisted entity.
     */
    public UserExist update(UserExist userExist) {
        log.debug("Request to update UserExist : {}", userExist);
        return userExistRepository.save(userExist);
    }

    /**
     * Partially update a userExist.
     *
     * @param userExist the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<UserExist> partialUpdate(UserExist userExist) {
        log.debug("Request to partially update UserExist : {}", userExist);

        return userExistRepository
            .findById(userExist.getId())
            .map(existingUserExist -> {
                if (userExist.getExist() != null) {
                    existingUserExist.setExist(userExist.getExist());
                }

                return existingUserExist;
            })
            .map(userExistRepository::save);
    }

    /**
     * Get all the userExists.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<UserExist> findAll() {
        log.debug("Request to get all UserExists");
        return userExistRepository.findAll();
    }

    /**
     * Get all the userExists with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<UserExist> findAllWithEagerRelationships(Pageable pageable) {
        return userExistRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one userExist by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<UserExist> findOne(Long id) {
        log.debug("Request to get UserExist : {}", id);
        return userExistRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the userExist by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete UserExist : {}", id);
        userExistRepository.deleteById(id);
    }
}
