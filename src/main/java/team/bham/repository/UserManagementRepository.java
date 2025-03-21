package team.bham.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import team.bham.domain.UserManagement;

/**
 * Spring Data JPA repository for the UserManagement entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserManagementRepository extends JpaRepository<UserManagement, Long>, JpaSpecificationExecutor<UserManagement> {}
