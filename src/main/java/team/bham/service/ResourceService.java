package team.bham.service;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.bham.domain.Resource;
import team.bham.repository.ResourceRepository;

/**
 * Service Implementation for managing {@link Resource}.
 */
@Service
@Transactional
public class ResourceService {

    private final Logger log = LoggerFactory.getLogger(ResourceService.class);

    private final ResourceRepository resourceRepository;

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    /**
     * Save a resource.
     *
     * @param resource the entity to save.
     * @return the persisted entity.
     */
    public Resource save(Resource resource) {
        log.debug("Request to save Resource : {}", resource);
        return resourceRepository.save(resource);
    }

    /**
     * Update a resource.
     *
     * @param resource the entity to save.
     * @return the persisted entity.
     */
    public Resource update(Resource resource) {
        log.debug("Request to update Resource : {}", resource);
        return resourceRepository.save(resource);
    }

    /**
     * Partially update a resource.
     *
     * @param resource the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Resource> partialUpdate(Resource resource) {
        log.debug("Request to partially update Resource : {}", resource);

        return resourceRepository
            .findById(resource.getId())
            .map(existingResource -> {
                if (resource.getCreated() != null) {
                    existingResource.setCreated(resource.getCreated());
                }
                if (resource.getType() != null) {
                    existingResource.setType(resource.getType());
                }
                if (resource.getStatus() != null) {
                    existingResource.setStatus(resource.getStatus());
                }
                if (resource.getCallSign() != null) {
                    existingResource.setCallSign(resource.getCallSign());
                }
                if (resource.getLatitude() != null) {
                    existingResource.setLatitude(resource.getLatitude());
                }
                if (resource.getLongitude() != null) {
                    existingResource.setLongitude(resource.getLongitude());
                }

                return existingResource;
            })
            .map(resourceRepository::save);
    }

    /**
     * Get all the resources.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Resource> findAll() {
        log.debug("Request to get all Resources");
        return resourceRepository.findAll();
    }

    /**
     * Get one resource by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Resource> findOne(Long id) {
        log.debug("Request to get Resource : {}", id);
        return resourceRepository.findById(id);
    }

    /**
     * Delete the resource by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Resource : {}", id);
        resourceRepository.deleteById(id);
    }
}
