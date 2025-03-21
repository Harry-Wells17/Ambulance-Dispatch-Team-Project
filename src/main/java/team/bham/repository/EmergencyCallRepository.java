package team.bham.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import team.bham.domain.EmergencyCall;

/**
 * Spring Data JPA repository for the EmergencyCall entity.
 */
@Repository
public interface EmergencyCallRepository extends JpaRepository<EmergencyCall, Long>, JpaSpecificationExecutor<EmergencyCall> {
    @Query("select emergencyCall from EmergencyCall emergencyCall where emergencyCall.createdBy.login = ?#{principal.username}")
    List<EmergencyCall> findByCreatedByIsCurrentUser();

    default Optional<EmergencyCall> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<EmergencyCall> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<EmergencyCall> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct emergencyCall from EmergencyCall emergencyCall left join fetch emergencyCall.createdBy",
        countQuery = "select count(distinct emergencyCall) from EmergencyCall emergencyCall"
    )
    Page<EmergencyCall> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct emergencyCall from EmergencyCall emergencyCall left join fetch emergencyCall.createdBy")
    List<EmergencyCall> findAllWithToOneRelationships();

    @Query("select emergencyCall from EmergencyCall emergencyCall left join fetch emergencyCall.createdBy where emergencyCall.id =:id")
    Optional<EmergencyCall> findOneWithToOneRelationships(@Param("id") Long id);
}
