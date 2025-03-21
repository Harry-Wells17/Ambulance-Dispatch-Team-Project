package team.bham.service;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.bham.domain.UserPerms;
import team.bham.repository.UserPermsRepository;

/**
 * Service Implementation for managing {@link UserPerms}.
 */
@Service
@Transactional
public class UserPermsService {

    private final Logger log = LoggerFactory.getLogger(UserPermsService.class);

    private final UserPermsRepository userPermsRepository;

    public UserPermsService(UserPermsRepository userPermsRepository) {
        this.userPermsRepository = userPermsRepository;
    }

    /**
     * Save a userPerms.
     *
     * @param userPerms the entity to save.
     * @return the persisted entity.
     */
    public UserPerms save(UserPerms userPerms) {
        log.debug("Request to save UserPerms : {}", userPerms);
        return userPermsRepository.save(userPerms);
    }

    /**
     * Update a userPerms.
     *
     * @param userPerms the entity to save.
     * @return the persisted entity.
     */
    public UserPerms update(UserPerms userPerms) {
        log.debug("Request to update UserPerms : {}", userPerms);
        return userPermsRepository.save(userPerms);
    }

    /**
     * Partially update a userPerms.
     *
     * @param userPerms the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<UserPerms> partialUpdate(UserPerms userPerms) {
        log.debug("Request to partially update UserPerms : {}", userPerms);

        return userPermsRepository
            .findById(userPerms.getId())
            .map(existingUserPerms -> {
                if (userPerms.getPerms() != null) {
                    existingUserPerms.setPerms(userPerms.getPerms());
                }

                return existingUserPerms;
            })
            .map(userPermsRepository::save);
    }

    /**
     * Get all the userPerms.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<UserPerms> findAll() {
        log.debug("Request to get all UserPerms");
        return userPermsRepository.findAll();
    }

    /**
     * Get one userPerms by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<UserPerms> findOne(Long id) {
        log.debug("Request to get UserPerms : {}", id);
        return userPermsRepository.findById(id);
    }

    /**
     * Delete the userPerms by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete UserPerms : {}", id);
        userPermsRepository.deleteById(id);
    }
}
