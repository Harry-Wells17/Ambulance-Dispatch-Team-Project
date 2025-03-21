package team.bham.service;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.bham.domain.EmergencyCall;
import team.bham.repository.EmergencyCallRepository;

/**
 * Service Implementation for managing {@link EmergencyCall}.
 */
@Service
@Transactional
public class EmergencyCallService {

    private final Logger log = LoggerFactory.getLogger(EmergencyCallService.class);

    private final EmergencyCallRepository emergencyCallRepository;

    public EmergencyCallService(EmergencyCallRepository emergencyCallRepository) {
        this.emergencyCallRepository = emergencyCallRepository;
    }

    /**
     * Save a emergencyCall.
     *
     * @param emergencyCall the entity to save.
     * @return the persisted entity.
     */
    public EmergencyCall save(EmergencyCall emergencyCall) {
        log.debug("Request to save EmergencyCall : {}", emergencyCall);
        return emergencyCallRepository.save(emergencyCall);
    }

    /**
     * Update a emergencyCall.
     *
     * @param emergencyCall the entity to save.
     * @return the persisted entity.
     */
    public EmergencyCall update(EmergencyCall emergencyCall) {
        log.debug("Request to update EmergencyCall : {}", emergencyCall);
        return emergencyCallRepository.save(emergencyCall);
    }

    /**
     * Partially update a emergencyCall.
     *
     * @param emergencyCall the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<EmergencyCall> partialUpdate(EmergencyCall emergencyCall) {
        log.debug("Request to partially update EmergencyCall : {}", emergencyCall);

        return emergencyCallRepository
            .findById(emergencyCall.getId())
            .map(existingEmergencyCall -> {
                if (emergencyCall.getCreated() != null) {
                    existingEmergencyCall.setCreated(emergencyCall.getCreated());
                }
                if (emergencyCall.getOpen() != null) {
                    existingEmergencyCall.setOpen(emergencyCall.getOpen());
                }
                if (emergencyCall.getType() != null) {
                    existingEmergencyCall.setType(emergencyCall.getType());
                }
                if (emergencyCall.getAge() != null) {
                    existingEmergencyCall.setAge(emergencyCall.getAge());
                }
                if (emergencyCall.getSexAssignedAtBirth() != null) {
                    existingEmergencyCall.setSexAssignedAtBirth(emergencyCall.getSexAssignedAtBirth());
                }
                if (emergencyCall.getHistory() != null) {
                    existingEmergencyCall.setHistory(emergencyCall.getHistory());
                }
                if (emergencyCall.getInjuries() != null) {
                    existingEmergencyCall.setInjuries(emergencyCall.getInjuries());
                }
                if (emergencyCall.getCondition() != null) {
                    existingEmergencyCall.setCondition(emergencyCall.getCondition());
                }
                if (emergencyCall.getLatitude() != null) {
                    existingEmergencyCall.setLatitude(emergencyCall.getLatitude());
                }
                if (emergencyCall.getLongitude() != null) {
                    existingEmergencyCall.setLongitude(emergencyCall.getLongitude());
                }
                if (emergencyCall.getClosed() != null) {
                    existingEmergencyCall.setClosed(emergencyCall.getClosed());
                }

                return existingEmergencyCall;
            })
            .map(emergencyCallRepository::save);
    }

    /**
     * Get all the emergencyCalls.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<EmergencyCall> findAll() {
        log.debug("Request to get all EmergencyCalls");
        return emergencyCallRepository.findAll();
    }

    /**
     * Get all the emergencyCalls with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<EmergencyCall> findAllWithEagerRelationships(Pageable pageable) {
        return emergencyCallRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one emergencyCall by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<EmergencyCall> findOne(Long id) {
        log.debug("Request to get EmergencyCall : {}", id);
        return emergencyCallRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the emergencyCall by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete EmergencyCall : {}", id);
        emergencyCallRepository.deleteById(id);
    }
}
