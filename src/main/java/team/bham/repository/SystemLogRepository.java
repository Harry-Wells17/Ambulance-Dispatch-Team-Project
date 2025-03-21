package team.bham.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import team.bham.domain.SystemLog;

/**
 * Spring Data JPA repository for the SystemLog entity.
 */
@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, Long>, JpaSpecificationExecutor<SystemLog> {
    @Query("select systemLog from SystemLog systemLog where systemLog.createdBy.login = ?#{principal.username}")
    List<SystemLog> findByCreatedByIsCurrentUser();

    default Optional<SystemLog> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<SystemLog> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<SystemLog> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct systemLog from SystemLog systemLog left join fetch systemLog.createdBy",
        countQuery = "select count(distinct systemLog) from SystemLog systemLog"
    )
    Page<SystemLog> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct systemLog from SystemLog systemLog left join fetch systemLog.createdBy")
    List<SystemLog> findAllWithToOneRelationships();

    @Query("select systemLog from SystemLog systemLog left join fetch systemLog.createdBy where systemLog.id =:id")
    Optional<SystemLog> findOneWithToOneRelationships(@Param("id") Long id);
}
