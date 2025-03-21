package team.bham.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import team.bham.domain.ResourceAssigned;

/**
 * Spring Data JPA repository for the ResourceAssigned entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ResourceAssignedRepository extends JpaRepository<ResourceAssigned, Long>, JpaSpecificationExecutor<ResourceAssigned> {}
