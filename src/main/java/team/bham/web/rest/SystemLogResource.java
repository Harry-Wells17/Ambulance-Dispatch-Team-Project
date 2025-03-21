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
import team.bham.domain.SystemLog;
import team.bham.repository.SystemLogRepository;
import team.bham.service.SystemLogQueryService;
import team.bham.service.SystemLogService;
import team.bham.service.criteria.SystemLogCriteria;
import team.bham.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link team.bham.domain.SystemLog}.
 */
@RestController
@RequestMapping("/api")
public class SystemLogResource {

    private final Logger log = LoggerFactory.getLogger(SystemLogResource.class);

    private static final String ENTITY_NAME = "systemLog";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SystemLogService systemLogService;

    private final SystemLogRepository systemLogRepository;

    private final SystemLogQueryService systemLogQueryService;

    public SystemLogResource(
        SystemLogService systemLogService,
        SystemLogRepository systemLogRepository,
        SystemLogQueryService systemLogQueryService
    ) {
        this.systemLogService = systemLogService;
        this.systemLogRepository = systemLogRepository;
        this.systemLogQueryService = systemLogQueryService;
    }

    /**
     * {@code POST  /system-logs} : Create a new systemLog.
     *
     * @param systemLog the systemLog to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new systemLog, or with status {@code 400 (Bad Request)} if the systemLog has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/system-logs")
    public ResponseEntity<SystemLog> createSystemLog(@Valid @RequestBody SystemLog systemLog) throws URISyntaxException {
        log.debug("REST request to save SystemLog : {}", systemLog);
        if (systemLog.getId() != null) {
            throw new BadRequestAlertException("A new systemLog cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SystemLog result = systemLogService.save(systemLog);
        return ResponseEntity
            .created(new URI("/api/system-logs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /system-logs/:id} : Updates an existing systemLog.
     *
     * @param id the id of the systemLog to save.
     * @param systemLog the systemLog to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated systemLog,
     * or with status {@code 400 (Bad Request)} if the systemLog is not valid,
     * or with status {@code 500 (Internal Server Error)} if the systemLog couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/system-logs/{id}")
    public ResponseEntity<SystemLog> updateSystemLog(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SystemLog systemLog
    ) throws URISyntaxException {
        log.debug("REST request to update SystemLog : {}, {}", id, systemLog);
        if (systemLog.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, systemLog.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!systemLogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SystemLog result = systemLogService.update(systemLog);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, systemLog.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /system-logs/:id} : Partial updates given fields of an existing systemLog, field will ignore if it is null
     *
     * @param id the id of the systemLog to save.
     * @param systemLog the systemLog to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated systemLog,
     * or with status {@code 400 (Bad Request)} if the systemLog is not valid,
     * or with status {@code 404 (Not Found)} if the systemLog is not found,
     * or with status {@code 500 (Internal Server Error)} if the systemLog couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/system-logs/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SystemLog> partialUpdateSystemLog(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SystemLog systemLog
    ) throws URISyntaxException {
        log.debug("REST request to partial update SystemLog partially : {}, {}", id, systemLog);
        if (systemLog.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, systemLog.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!systemLogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SystemLog> result = systemLogService.partialUpdate(systemLog);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, systemLog.getId().toString())
        );
    }

    /**
     * {@code GET  /system-logs} : get all the systemLogs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of systemLogs in body.
     */
    @GetMapping("/system-logs")
    public ResponseEntity<List<SystemLog>> getAllSystemLogs(SystemLogCriteria criteria) {
        log.debug("REST request to get SystemLogs by criteria: {}", criteria);
        List<SystemLog> entityList = systemLogQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /system-logs/count} : count all the systemLogs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/system-logs/count")
    public ResponseEntity<Long> countSystemLogs(SystemLogCriteria criteria) {
        log.debug("REST request to count SystemLogs by criteria: {}", criteria);
        return ResponseEntity.ok().body(systemLogQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /system-logs/:id} : get the "id" systemLog.
     *
     * @param id the id of the systemLog to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the systemLog, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/system-logs/{id}")
    public ResponseEntity<SystemLog> getSystemLog(@PathVariable Long id) {
        log.debug("REST request to get SystemLog : {}", id);
        Optional<SystemLog> systemLog = systemLogService.findOne(id);
        return ResponseUtil.wrapOrNotFound(systemLog);
    }

    /**
     * {@code DELETE  /system-logs/:id} : delete the "id" systemLog.
     *
     * @param id the id of the systemLog to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/system-logs/{id}")
    public ResponseEntity<Void> deleteSystemLog(@PathVariable Long id) {
        log.debug("REST request to delete SystemLog : {}", id);
        systemLogService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
