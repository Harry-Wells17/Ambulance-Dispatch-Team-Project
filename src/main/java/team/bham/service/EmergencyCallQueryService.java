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
import team.bham.domain.EmergencyCall;
import team.bham.repository.EmergencyCallRepository;
import team.bham.service.criteria.EmergencyCallCriteria;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link EmergencyCall} entities in the database.
 * The main input is a {@link EmergencyCallCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link EmergencyCall} or a {@link Page} of {@link EmergencyCall} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EmergencyCallQueryService extends QueryService<EmergencyCall> {

    private final Logger log = LoggerFactory.getLogger(EmergencyCallQueryService.class);

    private final EmergencyCallRepository emergencyCallRepository;

    public EmergencyCallQueryService(EmergencyCallRepository emergencyCallRepository) {
        this.emergencyCallRepository = emergencyCallRepository;
    }

    /**
     * Return a {@link List} of {@link EmergencyCall} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<EmergencyCall> findByCriteria(EmergencyCallCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<EmergencyCall> specification = createSpecification(criteria);
        return emergencyCallRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link EmergencyCall} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<EmergencyCall> findByCriteria(EmergencyCallCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<EmergencyCall> specification = createSpecification(criteria);
        return emergencyCallRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(EmergencyCallCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<EmergencyCall> specification = createSpecification(criteria);
        return emergencyCallRepository.count(specification);
    }

    /**
     * Function to convert {@link EmergencyCallCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<EmergencyCall> createSpecification(EmergencyCallCriteria criteria) {
        Specification<EmergencyCall> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), EmergencyCall_.id));
            }
            if (criteria.getCreated() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreated(), EmergencyCall_.created));
            }
            if (criteria.getOpen() != null) {
                specification = specification.and(buildSpecification(criteria.getOpen(), EmergencyCall_.open));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildSpecification(criteria.getType(), EmergencyCall_.type));
            }
            if (criteria.getAge() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAge(), EmergencyCall_.age));
            }
            if (criteria.getSexAssignedAtBirth() != null) {
                specification = specification.and(buildSpecification(criteria.getSexAssignedAtBirth(), EmergencyCall_.sexAssignedAtBirth));
            }
            if (criteria.getHistory() != null) {
                specification = specification.and(buildStringSpecification(criteria.getHistory(), EmergencyCall_.history));
            }
            if (criteria.getInjuries() != null) {
                specification = specification.and(buildStringSpecification(criteria.getInjuries(), EmergencyCall_.injuries));
            }
            if (criteria.getCondition() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCondition(), EmergencyCall_.condition));
            }
            if (criteria.getLatitude() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLatitude(), EmergencyCall_.latitude));
            }
            if (criteria.getLongitude() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLongitude(), EmergencyCall_.longitude));
            }
            if (criteria.getClosed() != null) {
                specification = specification.and(buildSpecification(criteria.getClosed(), EmergencyCall_.closed));
            }
            if (criteria.getResourceAssignedId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getResourceAssignedId(),
                            root -> root.join(EmergencyCall_.resourceAssigneds, JoinType.LEFT).get(ResourceAssigned_.id)
                        )
                    );
            }
            if (criteria.getSystemLogId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getSystemLogId(),
                            root -> root.join(EmergencyCall_.systemLogs, JoinType.LEFT).get(SystemLog_.id)
                        )
                    );
            }
            if (criteria.getCreatedById() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getCreatedById(),
                            root -> root.join(EmergencyCall_.createdBy, JoinType.LEFT).get(User_.id)
                        )
                    );
            }
            if (criteria.getEventId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getEventId(), root -> root.join(EmergencyCall_.event, JoinType.LEFT).get(Event_.id))
                    );
            }
        }
        return specification;
    }
}
