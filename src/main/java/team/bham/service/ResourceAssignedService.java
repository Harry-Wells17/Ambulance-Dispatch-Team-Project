package team.bham.service;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.bham.domain.ResourceAssigned;
import team.bham.repository.ResourceAssignedRepository;

/**
 * Service Implementation for managing {@link ResourceAssigned}.
 */
@Service
@Transactional
public class ResourceAssignedService {

    private final Logger log = LoggerFactory.getLogger(ResourceAssignedService.class);

    private final ResourceAssignedRepository resourceAssignedRepository;

    public ResourceAssignedService(ResourceAssignedRepository resourceAssignedRepository) {
        this.resourceAssignedRepository = resourceAssignedRepository;
    }

    /**
     * Save a resourceAssigned.
     *
     * @param resourceAssigned the entity to save.
     * @return the persisted entity.
     */
    public ResourceAssigned save(ResourceAssigned resourceAssigned) {
        log.debug("Request to save ResourceAssigned : {}", resourceAssigned);
        return resourceAssignedRepository.save(resourceAssigned);
    }

    /**
     * Update a resourceAssigned.
     *
     * @param resourceAssigned the entity to save.
     * @return the persisted entity.
     */
    public ResourceAssigned update(ResourceAssigned resourceAssigned) {
        log.debug("Request to update ResourceAssigned : {}", resourceAssigned);
        return resourceAssignedRepository.save(resourceAssigned);
    }

    /**
     * Partially update a resourceAssigned.
     *
     * @param resourceAssigned the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ResourceAssigned> partialUpdate(ResourceAssigned resourceAssigned) {
        log.debug("Request to partially update ResourceAssigned : {}", resourceAssigned);

        return resourceAssignedRepository
            .findById(resourceAssigned.getId())
            .map(existingResourceAssigned -> {
                if (resourceAssigned.getCallRecievedTime() != null) {
                    existingResourceAssigned.setCallRecievedTime(resourceAssigned.getCallRecievedTime());
                }
                if (resourceAssigned.getOnSceneTime() != null) {
                    existingResourceAssigned.setOnSceneTime(resourceAssigned.getOnSceneTime());
                }
                if (resourceAssigned.getLeftSceneTime() != null) {
                    existingResourceAssigned.setLeftSceneTime(resourceAssigned.getLeftSceneTime());
                }
                if (resourceAssigned.getArrivedHospitalTime() != null) {
                    existingResourceAssigned.setArrivedHospitalTime(resourceAssigned.getArrivedHospitalTime());
                }
                if (resourceAssigned.getClearHospitalTime() != null) {
                    existingResourceAssigned.setClearHospitalTime(resourceAssigned.getClearHospitalTime());
                }
                if (resourceAssigned.getGreenTime() != null) {
                    existingResourceAssigned.setGreenTime(resourceAssigned.getGreenTime());
                }
                if (resourceAssigned.getUnAssignedTime() != null) {
                    existingResourceAssigned.setUnAssignedTime(resourceAssigned.getUnAssignedTime());
                }

                return existingResourceAssigned;
            })
            .map(resourceAssignedRepository::save);
    }

    /**
     * Get all the resourceAssigneds.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ResourceAssigned> findAll() {
        log.debug("Request to get all ResourceAssigneds");
        return resourceAssignedRepository.findAll();
    }

    /**
     * Get one resourceAssigned by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ResourceAssigned> findOne(Long id) {
        log.debug("Request to get ResourceAssigned : {}", id);
        return resourceAssignedRepository.findById(id);
    }

    /**
     * Delete the resourceAssigned by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete ResourceAssigned : {}", id);
        resourceAssignedRepository.deleteById(id);
    }
}
