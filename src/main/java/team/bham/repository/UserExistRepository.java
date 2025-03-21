package team.bham.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import team.bham.domain.UserExist;

/**
 * Spring Data JPA repository for the UserExist entity.
 */
@Repository
public interface UserExistRepository extends JpaRepository<UserExist, Long>, JpaSpecificationExecutor<UserExist> {
    @Query("select userExist from UserExist userExist where userExist.createdBy.login = ?#{principal.username}")
    List<UserExist> findByCreatedByIsCurrentUser();

    default Optional<UserExist> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<UserExist> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<UserExist> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct userExist from UserExist userExist left join fetch userExist.createdBy",
        countQuery = "select count(distinct userExist) from UserExist userExist"
    )
    Page<UserExist> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct userExist from UserExist userExist left join fetch userExist.createdBy")
    List<UserExist> findAllWithToOneRelationships();

    @Query("select userExist from UserExist userExist left join fetch userExist.createdBy where userExist.id =:id")
    Optional<UserExist> findOneWithToOneRelationships(@Param("id") Long id);
}
