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
import team.bham.domain.GeoLocation;
import team.bham.repository.GeoLocationRepository;
import team.bham.service.criteria.GeoLocationCriteria;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link GeoLocation} entities in the database.
 * The main input is a {@link GeoLocationCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link GeoLocation} or a {@link Page} of {@link GeoLocation} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class GeoLocationQueryService extends QueryService<GeoLocation> {

    private final Logger log = LoggerFactory.getLogger(GeoLocationQueryService.class);

    private final GeoLocationRepository geoLocationRepository;

    public GeoLocationQueryService(GeoLocationRepository geoLocationRepository) {
        this.geoLocationRepository = geoLocationRepository;
    }

    /**
     * Return a {@link List} of {@link GeoLocation} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<GeoLocation> findByCriteria(GeoLocationCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<GeoLocation> specification = createSpecification(criteria);
        return geoLocationRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link GeoLocation} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<GeoLocation> findByCriteria(GeoLocationCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<GeoLocation> specification = createSpecification(criteria);
        return geoLocationRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(GeoLocationCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<GeoLocation> specification = createSpecification(criteria);
        return geoLocationRepository.count(specification);
    }

    /**
     * Function to convert {@link GeoLocationCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<GeoLocation> createSpecification(GeoLocationCriteria criteria) {
        Specification<GeoLocation> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), GeoLocation_.id));
            }
            if (criteria.getLatitude() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLatitude(), GeoLocation_.latitude));
            }
            if (criteria.getLongitude() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLongitude(), GeoLocation_.longitude));
            }
        }
        return specification;
    }
}
