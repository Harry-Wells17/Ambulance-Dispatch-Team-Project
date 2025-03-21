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
import team.bham.domain.UserManagement;
import team.bham.repository.UserManagementRepository;
import team.bham.service.criteria.UserManagementCriteria;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link UserManagement} entities in the database.
 * The main input is a {@link UserManagementCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link UserManagement} or a {@link Page} of {@link UserManagement} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class UserManagementQueryService extends QueryService<UserManagement> {

    private final Logger log = LoggerFactory.getLogger(UserManagementQueryService.class);

    private final UserManagementRepository userManagementRepository;

    public UserManagementQueryService(UserManagementRepository userManagementRepository) {
        this.userManagementRepository = userManagementRepository;
    }

    /**
     * Return a {@link List} of {@link UserManagement} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<UserManagement> findByCriteria(UserManagementCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<UserManagement> specification = createSpecification(criteria);
        return userManagementRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link UserManagement} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<UserManagement> findByCriteria(UserManagementCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<UserManagement> specification = createSpecification(criteria);
        return userManagementRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(UserManagementCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<UserManagement> specification = createSpecification(criteria);
        return userManagementRepository.count(specification);
    }

    /**
     * Function to convert {@link UserManagementCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<UserManagement> createSpecification(UserManagementCriteria criteria) {
        Specification<UserManagement> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), UserManagement_.id));
            }
            if (criteria.getUserRoleId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getUserRoleId(),
                            root -> root.join(UserManagement_.userRole, JoinType.LEFT).get(UserRole_.id)
                        )
                    );
            }
            if (criteria.getUserPermsId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getUserPermsId(),
                            root -> root.join(UserManagement_.userPerms, JoinType.LEFT).get(UserPerms_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
