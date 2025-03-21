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
import team.bham.domain.EmergencyCall;
import team.bham.repository.EmergencyCallRepository;
import team.bham.service.EmergencyCallQueryService;
import team.bham.service.EmergencyCallService;
import team.bham.service.criteria.EmergencyCallCriteria;
import team.bham.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link team.bham.domain.EmergencyCall}.
 */
@RestController
@RequestMapping("/api")
public class EmergencyCallResource {

    private final Logger log = LoggerFactory.getLogger(EmergencyCallResource.class);

    private static final String ENTITY_NAME = "emergencyCall";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EmergencyCallService emergencyCallService;

    private final EmergencyCallRepository emergencyCallRepository;

    private final EmergencyCallQueryService emergencyCallQueryService;

    public EmergencyCallResource(
        EmergencyCallService emergencyCallService,
        EmergencyCallRepository emergencyCallRepository,
        EmergencyCallQueryService emergencyCallQueryService
    ) {
        this.emergencyCallService = emergencyCallService;
        this.emergencyCallRepository = emergencyCallRepository;
        this.emergencyCallQueryService = emergencyCallQueryService;
    }

    /**
     * {@code POST  /emergency-calls} : Create a new emergencyCall.
     *
     * @param emergencyCall the emergencyCall to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new emergencyCall, or with status {@code 400 (Bad Request)} if the emergencyCall has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/emergency-calls")
    public ResponseEntity<EmergencyCall> createEmergencyCall(@Valid @RequestBody EmergencyCall emergencyCall) throws URISyntaxException {
        log.debug("REST request to save EmergencyCall : {}", emergencyCall);
        if (emergencyCall.getId() != null) {
            throw new BadRequestAlertException("A new emergencyCall cannot already have an ID", ENTITY_NAME, "idexists");
        }
        EmergencyCall result = emergencyCallService.save(emergencyCall);
        return ResponseEntity
            .created(new URI("/api/emergency-calls/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /emergency-calls/:id} : Updates an existing emergencyCall.
     *
     * @param id the id of the emergencyCall to save.
     * @param emergencyCall the emergencyCall to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated emergencyCall,
     * or with status {@code 400 (Bad Request)} if the emergencyCall is not valid,
     * or with status {@code 500 (Internal Server Error)} if the emergencyCall couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/emergency-calls/{id}")
    public ResponseEntity<EmergencyCall> updateEmergencyCall(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EmergencyCall emergencyCall
    ) throws URISyntaxException {
        log.debug("REST request to update EmergencyCall : {}, {}", id, emergencyCall);
        if (emergencyCall.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, emergencyCall.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!emergencyCallRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        EmergencyCall result = emergencyCallService.update(emergencyCall);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, emergencyCall.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /emergency-calls/:id} : Partial updates given fields of an existing emergencyCall, field will ignore if it is null
     *
     * @param id the id of the emergencyCall to save.
     * @param emergencyCall the emergencyCall to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated emergencyCall,
     * or with status {@code 400 (Bad Request)} if the emergencyCall is not valid,
     * or with status {@code 404 (Not Found)} if the emergencyCall is not found,
     * or with status {@code 500 (Internal Server Error)} if the emergencyCall couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/emergency-calls/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<EmergencyCall> partialUpdateEmergencyCall(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EmergencyCall emergencyCall
    ) throws URISyntaxException {
        log.debug("REST request to partial update EmergencyCall partially : {}, {}", id, emergencyCall);
        if (emergencyCall.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, emergencyCall.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!emergencyCallRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<EmergencyCall> result = emergencyCallService.partialUpdate(emergencyCall);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, emergencyCall.getId().toString())
        );
    }

    /**
     * {@code GET  /emergency-calls} : get all the emergencyCalls.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of emergencyCalls in body.
     */
    @GetMapping("/emergency-calls")
    public ResponseEntity<List<EmergencyCall>> getAllEmergencyCalls(EmergencyCallCriteria criteria) {
        log.debug("REST request to get EmergencyCalls by criteria: {}", criteria);
        List<EmergencyCall> entityList = emergencyCallQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /emergency-calls/count} : count all the emergencyCalls.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/emergency-calls/count")
    public ResponseEntity<Long> countEmergencyCalls(EmergencyCallCriteria criteria) {
        log.debug("REST request to count EmergencyCalls by criteria: {}", criteria);
        return ResponseEntity.ok().body(emergencyCallQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /emergency-calls/:id} : get the "id" emergencyCall.
     *
     * @param id the id of the emergencyCall to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the emergencyCall, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/emergency-calls/{id}")
    public ResponseEntity<EmergencyCall> getEmergencyCall(@PathVariable Long id) {
        log.debug("REST request to get EmergencyCall : {}", id);
        Optional<EmergencyCall> emergencyCall = emergencyCallService.findOne(id);
        return ResponseUtil.wrapOrNotFound(emergencyCall);
    }

    /**
     * {@code DELETE  /emergency-calls/:id} : delete the "id" emergencyCall.
     *
     * @param id the id of the emergencyCall to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/emergency-calls/{id}")
    public ResponseEntity<Void> deleteEmergencyCall(@PathVariable Long id) {
        log.debug("REST request to delete EmergencyCall : {}", id);
        emergencyCallService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
