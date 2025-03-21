package team.bham.service;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.bham.domain.ResourceBreaks;
import team.bham.repository.ResourceBreaksRepository;

/**
 * Service Implementation for managing {@link ResourceBreaks}.
 */
@Service
@Transactional
public class ResourceBreaksService {

    private final Logger log = LoggerFactory.getLogger(ResourceBreaksService.class);

    private final ResourceBreaksRepository resourceBreaksRepository;

    public ResourceBreaksService(ResourceBreaksRepository resourceBreaksRepository) {
        this.resourceBreaksRepository = resourceBreaksRepository;
    }

    /**
     * Save a resourceBreaks.
     *
     * @param resourceBreaks the entity to save.
     * @return the persisted entity.
     */
    public ResourceBreaks save(ResourceBreaks resourceBreaks) {
        log.debug("Request to save ResourceBreaks : {}", resourceBreaks);
        return resourceBreaksRepository.save(resourceBreaks);
    }

    /**
     * Update a resourceBreaks.
     *
     * @param resourceBreaks the entity to save.
     * @return the persisted entity.
     */
    public ResourceBreaks update(ResourceBreaks resourceBreaks) {
        log.debug("Request to update ResourceBreaks : {}", resourceBreaks);
        return resourceBreaksRepository.save(resourceBreaks);
    }

    /**
     * Partially update a resourceBreaks.
     *
     * @param resourceBreaks the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ResourceBreaks> partialUpdate(ResourceBreaks resourceBreaks) {
        log.debug("Request to partially update ResourceBreaks : {}", resourceBreaks);

        return resourceBreaksRepository
            .findById(resourceBreaks.getId())
            .map(existingResourceBreaks -> {
                if (resourceBreaks.getLastBreak() != null) {
                    existingResourceBreaks.setLastBreak(resourceBreaks.getLastBreak());
                }
                if (resourceBreaks.getBreakRequested() != null) {
                    existingResourceBreaks.setBreakRequested(resourceBreaks.getBreakRequested());
                }
                if (resourceBreaks.getStartedBreak() != null) {
                    existingResourceBreaks.setStartedBreak(resourceBreaks.getStartedBreak());
                }
                if (resourceBreaks.getOnBreak() != null) {
                    existingResourceBreaks.setOnBreak(resourceBreaks.getOnBreak());
                }

                return existingResourceBreaks;
            })
            .map(resourceBreaksRepository::save);
    }

    /**
     * Get all the resourceBreaks.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ResourceBreaks> findAll() {
        log.debug("Request to get all ResourceBreaks");
        return resourceBreaksRepository.findAll();
    }

    /**
     * Get one resourceBreaks by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ResourceBreaks> findOne(Long id) {
        log.debug("Request to get ResourceBreaks : {}", id);
        return resourceBreaksRepository.findById(id);
    }

    /**
     * Delete the resourceBreaks by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete ResourceBreaks : {}", id);
        resourceBreaksRepository.deleteById(id);
    }
}
