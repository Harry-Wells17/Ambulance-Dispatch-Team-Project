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
import team.bham.domain.ResourceBreaks;
import team.bham.repository.ResourceBreaksRepository;
import team.bham.service.criteria.ResourceBreaksCriteria;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ResourceBreaks} entities in the database.
 * The main input is a {@link ResourceBreaksCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ResourceBreaks} or a {@link Page} of {@link ResourceBreaks} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ResourceBreaksQueryService extends QueryService<ResourceBreaks> {

    private final Logger log = LoggerFactory.getLogger(ResourceBreaksQueryService.class);

    private final ResourceBreaksRepository resourceBreaksRepository;

    public ResourceBreaksQueryService(ResourceBreaksRepository resourceBreaksRepository) {
        this.resourceBreaksRepository = resourceBreaksRepository;
    }

    /**
     * Return a {@link List} of {@link ResourceBreaks} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ResourceBreaks> findByCriteria(ResourceBreaksCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ResourceBreaks> specification = createSpecification(criteria);
        return resourceBreaksRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link ResourceBreaks} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ResourceBreaks> findByCriteria(ResourceBreaksCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ResourceBreaks> specification = createSpecification(criteria);
        return resourceBreaksRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ResourceBreaksCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ResourceBreaks> specification = createSpecification(criteria);
        return resourceBreaksRepository.count(specification);
    }

    /**
     * Function to convert {@link ResourceBreaksCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ResourceBreaks> createSpecification(ResourceBreaksCriteria criteria) {
        Specification<ResourceBreaks> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ResourceBreaks_.id));
            }
            if (criteria.getLastBreak() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastBreak(), ResourceBreaks_.lastBreak));
            }
            if (criteria.getBreakRequested() != null) {
                specification = specification.and(buildSpecification(criteria.getBreakRequested(), ResourceBreaks_.breakRequested));
            }
            if (criteria.getStartedBreak() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStartedBreak(), ResourceBreaks_.startedBreak));
            }
            if (criteria.getOnBreak() != null) {
                specification = specification.and(buildSpecification(criteria.getOnBreak(), ResourceBreaks_.onBreak));
            }
        }
        return specification;
    }
}
