package team.bham.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import team.bham.domain.ResourceBreaks;

/**
 * Spring Data JPA repository for the ResourceBreaks entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ResourceBreaksRepository extends JpaRepository<ResourceBreaks, Long>, JpaSpecificationExecutor<ResourceBreaks> {}
