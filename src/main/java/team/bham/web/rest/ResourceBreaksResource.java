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
import team.bham.domain.ResourceBreaks;
import team.bham.repository.ResourceBreaksRepository;
import team.bham.service.ResourceBreaksQueryService;
import team.bham.service.ResourceBreaksService;
import team.bham.service.criteria.ResourceBreaksCriteria;
import team.bham.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link team.bham.domain.ResourceBreaks}.
 */
@RestController
@RequestMapping("/api")
public class ResourceBreaksResource {

    private final Logger log = LoggerFactory.getLogger(ResourceBreaksResource.class);

    private static final String ENTITY_NAME = "resourceBreaks";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ResourceBreaksService resourceBreaksService;

    private final ResourceBreaksRepository resourceBreaksRepository;

    private final ResourceBreaksQueryService resourceBreaksQueryService;

    public ResourceBreaksResource(
        ResourceBreaksService resourceBreaksService,
        ResourceBreaksRepository resourceBreaksRepository,
        ResourceBreaksQueryService resourceBreaksQueryService
    ) {
        this.resourceBreaksService = resourceBreaksService;
        this.resourceBreaksRepository = resourceBreaksRepository;
        this.resourceBreaksQueryService = resourceBreaksQueryService;
    }

    /**
     * {@code POST  /resource-breaks} : Create a new resourceBreaks.
     *
     * @param resourceBreaks the resourceBreaks to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new resourceBreaks, or with status {@code 400 (Bad Request)} if the resourceBreaks has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/resource-breaks")
    public ResponseEntity<ResourceBreaks> createResourceBreaks(@Valid @RequestBody ResourceBreaks resourceBreaks)
        throws URISyntaxException {
        log.debug("REST request to save ResourceBreaks : {}", resourceBreaks);
        if (resourceBreaks.getId() != null) {
            throw new BadRequestAlertException("A new resourceBreaks cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ResourceBreaks result = resourceBreaksService.save(resourceBreaks);
        return ResponseEntity
            .created(new URI("/api/resource-breaks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /resource-breaks/:id} : Updates an existing resourceBreaks.
     *
     * @param id the id of the resourceBreaks to save.
     * @param resourceBreaks the resourceBreaks to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated resourceBreaks,
     * or with status {@code 400 (Bad Request)} if the resourceBreaks is not valid,
     * or with status {@code 500 (Internal Server Error)} if the resourceBreaks couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/resource-breaks/{id}")
    public ResponseEntity<ResourceBreaks> updateResourceBreaks(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ResourceBreaks resourceBreaks
    ) throws URISyntaxException {
        log.debug("REST request to update ResourceBreaks : {}, {}", id, resourceBreaks);
        if (resourceBreaks.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, resourceBreaks.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!resourceBreaksRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ResourceBreaks result = resourceBreaksService.update(resourceBreaks);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, resourceBreaks.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /resource-breaks/:id} : Partial updates given fields of an existing resourceBreaks, field will ignore if it is null
     *
     * @param id the id of the resourceBreaks to save.
     * @param resourceBreaks the resourceBreaks to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated resourceBreaks,
     * or with status {@code 400 (Bad Request)} if the resourceBreaks is not valid,
     * or with status {@code 404 (Not Found)} if the resourceBreaks is not found,
     * or with status {@code 500 (Internal Server Error)} if the resourceBreaks couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/resource-breaks/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ResourceBreaks> partialUpdateResourceBreaks(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ResourceBreaks resourceBreaks
    ) throws URISyntaxException {
        log.debug("REST request to partial update ResourceBreaks partially : {}, {}", id, resourceBreaks);
        if (resourceBreaks.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, resourceBreaks.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!resourceBreaksRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ResourceBreaks> result = resourceBreaksService.partialUpdate(resourceBreaks);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, resourceBreaks.getId().toString())
        );
    }

    /**
     * {@code GET  /resource-breaks} : get all the resourceBreaks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of resourceBreaks in body.
     */
    @GetMapping("/resource-breaks")
    public ResponseEntity<List<ResourceBreaks>> getAllResourceBreaks(ResourceBreaksCriteria criteria) {
        log.debug("REST request to get ResourceBreaks by criteria: {}", criteria);
        List<ResourceBreaks> entityList = resourceBreaksQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /resource-breaks/count} : count all the resourceBreaks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/resource-breaks/count")
    public ResponseEntity<Long> countResourceBreaks(ResourceBreaksCriteria criteria) {
        log.debug("REST request to count ResourceBreaks by criteria: {}", criteria);
        return ResponseEntity.ok().body(resourceBreaksQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /resource-breaks/:id} : get the "id" resourceBreaks.
     *
     * @param id the id of the resourceBreaks to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the resourceBreaks, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/resource-breaks/{id}")
    public ResponseEntity<ResourceBreaks> getResourceBreaks(@PathVariable Long id) {
        log.debug("REST request to get ResourceBreaks : {}", id);
        Optional<ResourceBreaks> resourceBreaks = resourceBreaksService.findOne(id);
        return ResponseUtil.wrapOrNotFound(resourceBreaks);
    }

    /**
     * {@code DELETE  /resource-breaks/:id} : delete the "id" resourceBreaks.
     *
     * @param id the id of the resourceBreaks to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/resource-breaks/{id}")
    public ResponseEntity<Void> deleteResourceBreaks(@PathVariable Long id) {
        log.debug("REST request to delete ResourceBreaks : {}", id);
        resourceBreaksService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
