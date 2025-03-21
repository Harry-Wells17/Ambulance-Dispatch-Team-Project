package team.bham.service;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.bham.domain.GeoLocation;
import team.bham.repository.GeoLocationRepository;

/**
 * Service Implementation for managing {@link GeoLocation}.
 */
@Service
@Transactional
public class GeoLocationService {

    private final Logger log = LoggerFactory.getLogger(GeoLocationService.class);

    private final GeoLocationRepository geoLocationRepository;

    public GeoLocationService(GeoLocationRepository geoLocationRepository) {
        this.geoLocationRepository = geoLocationRepository;
    }

    /**
     * Save a geoLocation.
     *
     * @param geoLocation the entity to save.
     * @return the persisted entity.
     */
    public GeoLocation save(GeoLocation geoLocation) {
        log.debug("Request to save GeoLocation : {}", geoLocation);
        return geoLocationRepository.save(geoLocation);
    }

    /**
     * Update a geoLocation.
     *
     * @param geoLocation the entity to save.
     * @return the persisted entity.
     */
    public GeoLocation update(GeoLocation geoLocation) {
        log.debug("Request to update GeoLocation : {}", geoLocation);
        return geoLocationRepository.save(geoLocation);
    }

    /**
     * Partially update a geoLocation.
     *
     * @param geoLocation the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<GeoLocation> partialUpdate(GeoLocation geoLocation) {
        log.debug("Request to partially update GeoLocation : {}", geoLocation);

        return geoLocationRepository
            .findById(geoLocation.getId())
            .map(existingGeoLocation -> {
                if (geoLocation.getLatitude() != null) {
                    existingGeoLocation.setLatitude(geoLocation.getLatitude());
                }
                if (geoLocation.getLongitude() != null) {
                    existingGeoLocation.setLongitude(geoLocation.getLongitude());
                }

                return existingGeoLocation;
            })
            .map(geoLocationRepository::save);
    }

    /**
     * Get all the geoLocations.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<GeoLocation> findAll() {
        log.debug("Request to get all GeoLocations");
        return geoLocationRepository.findAll();
    }

    /**
     * Get one geoLocation by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<GeoLocation> findOne(Long id) {
        log.debug("Request to get GeoLocation : {}", id);
        return geoLocationRepository.findById(id);
    }

    /**
     * Delete the geoLocation by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete GeoLocation : {}", id);
        geoLocationRepository.deleteById(id);
    }
}
