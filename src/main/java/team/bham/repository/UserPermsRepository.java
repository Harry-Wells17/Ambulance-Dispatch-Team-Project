package team.bham.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import team.bham.domain.UserPerms;

/**
 * Spring Data JPA repository for the UserPerms entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserPermsRepository extends JpaRepository<UserPerms, Long>, JpaSpecificationExecutor<UserPerms> {}
