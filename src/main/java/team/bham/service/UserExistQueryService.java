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
import team.bham.domain.UserExist;
import team.bham.repository.UserExistRepository;
import team.bham.service.criteria.UserExistCriteria;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link UserExist} entities in the database.
 * The main input is a {@link UserExistCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link UserExist} or a {@link Page} of {@link UserExist} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class UserExistQueryService extends QueryService<UserExist> {

    private final Logger log = LoggerFactory.getLogger(UserExistQueryService.class);

    private final UserExistRepository userExistRepository;

    public UserExistQueryService(UserExistRepository userExistRepository) {
        this.userExistRepository = userExistRepository;
    }

    /**
     * Return a {@link List} of {@link UserExist} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<UserExist> findByCriteria(UserExistCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<UserExist> specification = createSpecification(criteria);
        return userExistRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link UserExist} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<UserExist> findByCriteria(UserExistCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<UserExist> specification = createSpecification(criteria);
        return userExistRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(UserExistCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<UserExist> specification = createSpecification(criteria);
        return userExistRepository.count(specification);
    }

    /**
     * Function to convert {@link UserExistCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<UserExist> createSpecification(UserExistCriteria criteria) {
        Specification<UserExist> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), UserExist_.id));
            }
            if (criteria.getExist() != null) {
                specification = specification.and(buildSpecification(criteria.getExist(), UserExist_.exist));
            }
            if (criteria.getCreatedById() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getCreatedById(), root -> root.join(UserExist_.createdBy, JoinType.LEFT).get(User_.id))
                    );
            }
        }
        return specification;
    }
}
