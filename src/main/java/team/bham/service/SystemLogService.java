package team.bham.service;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.bham.domain.SystemLog;
import team.bham.repository.SystemLogRepository;

/**
 * Service Implementation for managing {@link SystemLog}.
 */
@Service
@Transactional
public class SystemLogService {

    private final Logger log = LoggerFactory.getLogger(SystemLogService.class);

    private final SystemLogRepository systemLogRepository;

    public SystemLogService(SystemLogRepository systemLogRepository) {
        this.systemLogRepository = systemLogRepository;
    }

    /**
     * Save a systemLog.
     *
     * @param systemLog the entity to save.
     * @return the persisted entity.
     */
    public SystemLog save(SystemLog systemLog) {
        log.debug("Request to save SystemLog : {}", systemLog);
        return systemLogRepository.save(systemLog);
    }

    /**
     * Update a systemLog.
     *
     * @param systemLog the entity to save.
     * @return the persisted entity.
     */
    public SystemLog update(SystemLog systemLog) {
        log.debug("Request to update SystemLog : {}", systemLog);
        return systemLogRepository.save(systemLog);
    }

    /**
     * Partially update a systemLog.
     *
     * @param systemLog the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SystemLog> partialUpdate(SystemLog systemLog) {
        log.debug("Request to partially update SystemLog : {}", systemLog);

        return systemLogRepository
            .findById(systemLog.getId())
            .map(existingSystemLog -> {
                if (systemLog.getCreatedAt() != null) {
                    existingSystemLog.setCreatedAt(systemLog.getCreatedAt());
                }
                if (systemLog.getLogType() != null) {
                    existingSystemLog.setLogType(systemLog.getLogType());
                }
                if (systemLog.getMessageContent() != null) {
                    existingSystemLog.setMessageContent(systemLog.getMessageContent());
                }

                return existingSystemLog;
            })
            .map(systemLogRepository::save);
    }

    /**
     * Get all the systemLogs.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<SystemLog> findAll() {
        log.debug("Request to get all SystemLogs");
        return systemLogRepository.findAll();
    }

    /**
     * Get all the systemLogs with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<SystemLog> findAllWithEagerRelationships(Pageable pageable) {
        return systemLogRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one systemLog by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SystemLog> findOne(Long id) {
        log.debug("Request to get SystemLog : {}", id);
        return systemLogRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the systemLog by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete SystemLog : {}", id);
        systemLogRepository.deleteById(id);
    }
}
