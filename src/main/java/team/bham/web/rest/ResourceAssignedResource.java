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
import team.bham.domain.ResourceAssigned;
import team.bham.repository.ResourceAssignedRepository;
import team.bham.service.ResourceAssignedQueryService;
import team.bham.service.ResourceAssignedService;
import team.bham.service.criteria.ResourceAssignedCriteria;
import team.bham.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link team.bham.domain.ResourceAssigned}.
 */
@RestController
@RequestMapping("/api")
public class ResourceAssignedResource {

    private final Logger log = LoggerFactory.getLogger(ResourceAssignedResource.class);

    private static final String ENTITY_NAME = "resourceAssigned";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ResourceAssignedService resourceAssignedService;

    private final ResourceAssignedRepository resourceAssignedRepository;

    private final ResourceAssignedQueryService resourceAssignedQueryService;

    public ResourceAssignedResource(
        ResourceAssignedService resourceAssignedService,
        ResourceAssignedRepository resourceAssignedRepository,
        ResourceAssignedQueryService resourceAssignedQueryService
    ) {
        this.resourceAssignedService = resourceAssignedService;
        this.resourceAssignedRepository = resourceAssignedRepository;
        this.resourceAssignedQueryService = resourceAssignedQueryService;
    }

    /**
     * {@code POST  /resource-assigneds} : Create a new resourceAssigned.
     *
     * @param resourceAssigned the resourceAssigned to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new resourceAssigned, or with status {@code 400 (Bad Request)} if the resourceAssigned has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/resource-assigneds")
    public ResponseEntity<ResourceAssigned> createResourceAssigned(@Valid @RequestBody ResourceAssigned resourceAssigned)
        throws URISyntaxException {
        log.debug("REST request to save ResourceAssigned : {}", resourceAssigned);
        if (resourceAssigned.getId() != null) {
            throw new BadRequestAlertException("A new resourceAssigned cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ResourceAssigned result = resourceAssignedService.save(resourceAssigned);
        return ResponseEntity
            .created(new URI("/api/resource-assigneds/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /resource-assigneds/:id} : Updates an existing resourceAssigned.
     *
     * @param id the id of the resourceAssigned to save.
     * @param resourceAssigned the resourceAssigned to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated resourceAssigned,
     * or with status {@code 400 (Bad Request)} if the resourceAssigned is not valid,
     * or with status {@code 500 (Internal Server Error)} if the resourceAssigned couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/resource-assigneds/{id}")
    public ResponseEntity<ResourceAssigned> updateResourceAssigned(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ResourceAssigned resourceAssigned
    ) throws URISyntaxException {
        log.debug("REST request to update ResourceAssigned : {}, {}", id, resourceAssigned);
        if (resourceAssigned.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, resourceAssigned.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!resourceAssignedRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ResourceAssigned result = resourceAssignedService.update(resourceAssigned);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, resourceAssigned.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /resource-assigneds/:id} : Partial updates given fields of an existing resourceAssigned, field will ignore if it is null
     *
     * @param id the id of the resourceAssigned to save.
     * @param resourceAssigned the resourceAssigned to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated resourceAssigned,
     * or with status {@code 400 (Bad Request)} if the resourceAssigned is not valid,
     * or with status {@code 404 (Not Found)} if the resourceAssigned is not found,
     * or with status {@code 500 (Internal Server Error)} if the resourceAssigned couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/resource-assigneds/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ResourceAssigned> partialUpdateResourceAssigned(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ResourceAssigned resourceAssigned
    ) throws URISyntaxException {
        log.debug("REST request to partial update ResourceAssigned partially : {}, {}", id, resourceAssigned);
        if (resourceAssigned.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, resourceAssigned.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!resourceAssignedRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ResourceAssigned> result = resourceAssignedService.partialUpdate(resourceAssigned);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, resourceAssigned.getId().toString())
        );
    }

    /**
     * {@code GET  /resource-assigneds} : get all the resourceAssigneds.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of resourceAssigneds in body.
     */
    @GetMapping("/resource-assigneds")
    public ResponseEntity<List<ResourceAssigned>> getAllResourceAssigneds(ResourceAssignedCriteria criteria) {
        log.debug("REST request to get ResourceAssigneds by criteria: {}", criteria);
        List<ResourceAssigned> entityList = resourceAssignedQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /resource-assigneds/count} : count all the resourceAssigneds.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/resource-assigneds/count")
    public ResponseEntity<Long> countResourceAssigneds(ResourceAssignedCriteria criteria) {
        log.debug("REST request to count ResourceAssigneds by criteria: {}", criteria);
        return ResponseEntity.ok().body(resourceAssignedQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /resource-assigneds/:id} : get the "id" resourceAssigned.
     *
     * @param id the id of the resourceAssigned to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the resourceAssigned, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/resource-assigneds/{id}")
    public ResponseEntity<ResourceAssigned> getResourceAssigned(@PathVariable Long id) {
        log.debug("REST request to get ResourceAssigned : {}", id);
        Optional<ResourceAssigned> resourceAssigned = resourceAssignedService.findOne(id);
        return ResponseUtil.wrapOrNotFound(resourceAssigned);
    }

    /**
     * {@code DELETE  /resource-assigneds/:id} : delete the "id" resourceAssigned.
     *
     * @param id the id of the resourceAssigned to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/resource-assigneds/{id}")
    public ResponseEntity<Void> deleteResourceAssigned(@PathVariable Long id) {
        log.debug("REST request to delete ResourceAssigned : {}", id);
        resourceAssignedService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
