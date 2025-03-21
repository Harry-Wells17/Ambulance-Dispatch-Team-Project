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
import team.bham.domain.ResourceAssigned;
import team.bham.repository.ResourceAssignedRepository;
import team.bham.service.criteria.ResourceAssignedCriteria;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ResourceAssigned} entities in the database.
 * The main input is a {@link ResourceAssignedCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ResourceAssigned} or a {@link Page} of {@link ResourceAssigned} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ResourceAssignedQueryService extends QueryService<ResourceAssigned> {

    private final Logger log = LoggerFactory.getLogger(ResourceAssignedQueryService.class);

    private final ResourceAssignedRepository resourceAssignedRepository;

    public ResourceAssignedQueryService(ResourceAssignedRepository resourceAssignedRepository) {
        this.resourceAssignedRepository = resourceAssignedRepository;
    }

    /**
     * Return a {@link List} of {@link ResourceAssigned} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ResourceAssigned> findByCriteria(ResourceAssignedCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ResourceAssigned> specification = createSpecification(criteria);
        return resourceAssignedRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link ResourceAssigned} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ResourceAssigned> findByCriteria(ResourceAssignedCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ResourceAssigned> specification = createSpecification(criteria);
        return resourceAssignedRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ResourceAssignedCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ResourceAssigned> specification = createSpecification(criteria);
        return resourceAssignedRepository.count(specification);
    }

    /**
     * Function to convert {@link ResourceAssignedCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ResourceAssigned> createSpecification(ResourceAssignedCriteria criteria) {
        Specification<ResourceAssigned> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ResourceAssigned_.id));
            }
            if (criteria.getCallRecievedTime() != null) {
                specification =
                    specification.and(buildRangeSpecification(criteria.getCallRecievedTime(), ResourceAssigned_.callRecievedTime));
            }
            if (criteria.getOnSceneTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getOnSceneTime(), ResourceAssigned_.onSceneTime));
            }
            if (criteria.getLeftSceneTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLeftSceneTime(), ResourceAssigned_.leftSceneTime));
            }
            if (criteria.getArrivedHospitalTime() != null) {
                specification =
                    specification.and(buildRangeSpecification(criteria.getArrivedHospitalTime(), ResourceAssigned_.arrivedHospitalTime));
            }
            if (criteria.getClearHospitalTime() != null) {
                specification =
                    specification.and(buildRangeSpecification(criteria.getClearHospitalTime(), ResourceAssigned_.clearHospitalTime));
            }
            if (criteria.getGreenTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getGreenTime(), ResourceAssigned_.greenTime));
            }
            if (criteria.getUnAssignedTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUnAssignedTime(), ResourceAssigned_.unAssignedTime));
            }
            if (criteria.getResourceId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getResourceId(),
                            root -> root.join(ResourceAssigned_.resource, JoinType.LEFT).get(Resource_.id)
                        )
                    );
            }
            if (criteria.getEmergencyCallId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getEmergencyCallId(),
                            root -> root.join(ResourceAssigned_.emergencyCall, JoinType.LEFT).get(EmergencyCall_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
