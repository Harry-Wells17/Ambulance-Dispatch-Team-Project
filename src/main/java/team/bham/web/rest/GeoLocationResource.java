package team.bham.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.bham.domain.GeoLocation;
import team.bham.repository.GeoLocationRepository;
import team.bham.service.GeoLocationQueryService;
import team.bham.service.GeoLocationService;
import team.bham.service.criteria.GeoLocationCriteria;
import team.bham.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link team.bham.domain.GeoLocation}.
 */
@RestController
@RequestMapping("/api")
public class GeoLocationResource {

    private final Logger log = LoggerFactory.getLogger(GeoLocationResource.class);

    private static final String ENTITY_NAME = "geoLocation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GeoLocationService geoLocationService;

    private final GeoLocationRepository geoLocationRepository;

    private final GeoLocationQueryService geoLocationQueryService;

    public GeoLocationResource(
        GeoLocationService geoLocationService,
        GeoLocationRepository geoLocationRepository,
        GeoLocationQueryService geoLocationQueryService
    ) {
        this.geoLocationService = geoLocationService;
        this.geoLocationRepository = geoLocationRepository;
        this.geoLocationQueryService = geoLocationQueryService;
    }

    /**
     * {@code POST  /geo-locations} : Create a new geoLocation.
     *
     * @param geoLocation the geoLocation to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new geoLocation, or with status {@code 400 (Bad Request)} if the geoLocation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/geo-locations")
    public ResponseEntity<GeoLocation> createGeoLocation(@Valid @RequestBody GeoLocation geoLocation) throws URISyntaxException {
        log.debug("REST request to save GeoLocation : {}", geoLocation);
        if (geoLocation.getId() != null) {
            throw new BadRequestAlertException("A new geoLocation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        GeoLocation result = geoLocationService.save(geoLocation);
        return ResponseEntity
            .created(new URI("/api/geo-locations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /geo-locations/:id} : Updates an existing geoLocation.
     *
     * @param id the id of the geoLocation to save.
     * @param geoLocation the geoLocation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated geoLocation,
     * or with status {@code 400 (Bad Request)} if the geoLocation is not valid,
     * or with status {@code 500 (Internal Server Error)} if the geoLocation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/geo-locations/{id}")
    public ResponseEntity<GeoLocation> updateGeoLocation(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody GeoLocation geoLocation
    ) throws URISyntaxException {
        log.debug("REST request to update GeoLocation : {}, {}", id, geoLocation);
        if (geoLocation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, geoLocation.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!geoLocationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        GeoLocation result = geoLocationService.update(geoLocation);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, geoLocation.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /geo-locations/:id} : Partial updates given fields of an existing geoLocation, field will ignore if it is null
     *
     * @param id the id of the geoLocation to save.
     * @param geoLocation the geoLocation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated geoLocation,
     * or with status {@code 400 (Bad Request)} if the geoLocation is not valid,
     * or with status {@code 404 (Not Found)} if the geoLocation is not found,
     * or with status {@code 500 (Internal Server Error)} if the geoLocation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/geo-locations/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<GeoLocation> partialUpdateGeoLocation(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody GeoLocation geoLocation
    ) throws URISyntaxException {
        log.debug("REST request to partial update GeoLocation partially : {}, {}", id, geoLocation);
        if (geoLocation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, geoLocation.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!geoLocationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<GeoLocation> result = geoLocationService.partialUpdate(geoLocation);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, geoLocation.getId().toString())
        );
    }

    /**
     * {@code GET  /geo-locations} : get all the geoLocations.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of geoLocations in body.
     */
    @GetMapping("/geo-locations")
    public ResponseEntity<List<GeoLocation>> getAllGeoLocations(GeoLocationCriteria criteria) {
        log.debug("REST request to get GeoLocations by criteria: {}", criteria);
        List<GeoLocation> entityList = geoLocationQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /geo-locations/count} : count all the geoLocations.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/geo-locations/count")
    public ResponseEntity<Long> countGeoLocations(GeoLocationCriteria criteria) {
        log.debug("REST request to count GeoLocations by criteria: {}", criteria);
        return ResponseEntity.ok().body(geoLocationQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /geo-locations/:id} : get the "id" geoLocation.
     *
     * @param id the id of the geoLocation to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the geoLocation, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/geo-locations/{id}")
    public ResponseEntity<GeoLocation> getGeoLocation(@PathVariable Long id) {
        log.debug("REST request to get GeoLocation : {}", id);
        Optional<GeoLocation> geoLocation = geoLocationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(geoLocation);
    }

    /**
     * {@code DELETE  /geo-locations/:id} : delete the "id" geoLocation.
     *
     * @param id the id of the geoLocation to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/geo-locations/{id}")
    public ResponseEntity<Void> deleteGeoLocation(@PathVariable Long id) {
        log.debug("REST request to delete GeoLocation : {}", id);
        geoLocationService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
