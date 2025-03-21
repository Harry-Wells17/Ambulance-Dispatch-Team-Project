package team.bham.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.bham.domain.UserManagement;
import team.bham.repository.UserManagementRepository;
import team.bham.service.UserManagementQueryService;
import team.bham.service.UserManagementService;
import team.bham.service.criteria.UserManagementCriteria;
import team.bham.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link team.bham.domain.UserManagement}.
 */
@RestController
@RequestMapping("/api")
public class UserManagementResource {

    private final Logger log = LoggerFactory.getLogger(UserManagementResource.class);

    private static final String ENTITY_NAME = "userManagement";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserManagementService userManagementService;

    private final UserManagementRepository userManagementRepository;

    private final UserManagementQueryService userManagementQueryService;

    public UserManagementResource(
        UserManagementService userManagementService,
        UserManagementRepository userManagementRepository,
        UserManagementQueryService userManagementQueryService
    ) {
        this.userManagementService = userManagementService;
        this.userManagementRepository = userManagementRepository;
        this.userManagementQueryService = userManagementQueryService;
    }

    /**
     * {@code POST  /user-managements} : Create a new userManagement.
     *
     * @param userManagement the userManagement to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userManagement, or with status {@code 400 (Bad Request)} if the userManagement has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-managements")
    public ResponseEntity<UserManagement> createUserManagement(@RequestBody UserManagement userManagement) throws URISyntaxException {
        log.debug("REST request to save UserManagement : {}", userManagement);
        if (userManagement.getId() != null) {
            throw new BadRequestAlertException("A new userManagement cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserManagement result = userManagementService.save(userManagement);
        return ResponseEntity
            .created(new URI("/api/user-managements/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /user-managements/:id} : Updates an existing userManagement.
     *
     * @param id the id of the userManagement to save.
     * @param userManagement the userManagement to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userManagement,
     * or with status {@code 400 (Bad Request)} if the userManagement is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userManagement couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-managements/{id}")
    public ResponseEntity<UserManagement> updateUserManagement(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UserManagement userManagement
    ) throws URISyntaxException {
        log.debug("REST request to update UserManagement : {}, {}", id, userManagement);
        if (userManagement.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userManagement.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userManagementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UserManagement result = userManagementService.update(userManagement);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userManagement.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /user-managements/:id} : Partial updates given fields of an existing userManagement, field will ignore if it is null
     *
     * @param id the id of the userManagement to save.
     * @param userManagement the userManagement to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userManagement,
     * or with status {@code 400 (Bad Request)} if the userManagement is not valid,
     * or with status {@code 404 (Not Found)} if the userManagement is not found,
     * or with status {@code 500 (Internal Server Error)} if the userManagement couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/user-managements/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserManagement> partialUpdateUserManagement(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UserManagement userManagement
    ) throws URISyntaxException {
        log.debug("REST request to partial update UserManagement partially : {}, {}", id, userManagement);
        if (userManagement.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userManagement.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userManagementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserManagement> result = userManagementService.partialUpdate(userManagement);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userManagement.getId().toString())
        );
    }

    /**
     * {@code GET  /user-managements} : get all the userManagements.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userManagements in body.
     */
    @GetMapping("/user-managements")
    public ResponseEntity<List<UserManagement>> getAllUserManagements(UserManagementCriteria criteria) {
        log.debug("REST request to get UserManagements by criteria: {}", criteria);
        List<UserManagement> entityList = userManagementQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /user-managements/count} : count all the userManagements.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/user-managements/count")
    public ResponseEntity<Long> countUserManagements(UserManagementCriteria criteria) {
        log.debug("REST request to count UserManagements by criteria: {}", criteria);
        return ResponseEntity.ok().body(userManagementQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /user-managements/:id} : get the "id" userManagement.
     *
     * @param id the id of the userManagement to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userManagement, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-managements/{id}")
    public ResponseEntity<UserManagement> getUserManagement(@PathVariable Long id) {
        log.debug("REST request to get UserManagement : {}", id);
        Optional<UserManagement> userManagement = userManagementService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userManagement);
    }

    /**
     * {@code DELETE  /user-managements/:id} : delete the "id" userManagement.
     *
     * @param id the id of the userManagement to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-managements/{id}")
    public ResponseEntity<Void> deleteUserManagement(@PathVariable Long id) {
        log.debug("REST request to delete UserManagement : {}", id);
        userManagementService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
