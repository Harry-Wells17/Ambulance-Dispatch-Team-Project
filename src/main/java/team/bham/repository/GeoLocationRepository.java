package team.bham.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import team.bham.domain.GeoLocation;

/**
 * Spring Data JPA repository for the GeoLocation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GeoLocationRepository extends JpaRepository<GeoLocation, Long>, JpaSpecificationExecutor<GeoLocation> {}
