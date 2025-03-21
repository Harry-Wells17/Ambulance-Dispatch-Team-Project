package team.bham.service;

import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.bham.domain.*; // for static metamodels
import team.bham.domain.SystemLog;
import team.bham.repository.SystemLogRepository;
import team.bham.service.criteria.SystemLogCriteria;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link SystemLog} entities in the database.
 * The main input is a {@link SystemLogCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SystemLog} or a {@link Page} of {@link SystemLog} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SystemLogQueryService extends QueryService<SystemLog> {

    private final Logger log = LoggerFactory.getLogger(SystemLogQueryService.class);

    private final SystemLogRepository systemLogRepository;

    public SystemLogQueryService(SystemLogRepository systemLogRepository) {
        this.systemLogRepository = systemLogRepository;
    }

    /**
     * Return a {@link List} of {@link SystemLog} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SystemLog> findByCriteria(SystemLogCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<SystemLog> specification = createSpecification(criteria);
        return systemLogRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link SystemLog} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SystemLog> findByCriteria(SystemLogCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<SystemLog> specification = createSpecification(criteria);
        return systemLogRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SystemLogCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<SystemLog> specification = createSpecification(criteria);
        return systemLogRepository.count(specification);
    }

    /**
     * Function to convert {@link SystemLogCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<SystemLog> createSpecification(SystemLogCriteria criteria) {
        Specification<SystemLog> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), SystemLog_.id));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), SystemLog_.createdAt));
            }
            if (criteria.getLogType() != null) {
                specification = specification.and(buildSpecification(criteria.getLogType(), SystemLog_.logType));
            }
            if (criteria.getCreatedById() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getCreatedById(), root -> root.join(SystemLog_.createdBy, JoinType.LEFT).get(User_.id))
                    );
            }
            if (criteria.getEmergencyCallId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getEmergencyCallId(),
                            root -> root.join(SystemLog_.emergencyCall, JoinType.LEFT).get(EmergencyCall_.id)
                        )
                    );
            }
            if (criteria.getEventId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getEventId(), root -> root.join(SystemLog_.event, JoinType.LEFT).get(Event_.id))
                    );
            }
        }
        return specification;
    }
}
