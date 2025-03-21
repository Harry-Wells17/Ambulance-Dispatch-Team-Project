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
import team.bham.domain.UserPerms;
import team.bham.repository.UserPermsRepository;
import team.bham.service.UserPermsQueryService;
import team.bham.service.UserPermsService;
import team.bham.service.criteria.UserPermsCriteria;
import team.bham.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link team.bham.domain.UserPerms}.
 */
@RestController
@RequestMapping("/api")
public class UserPermsResource {

    private final Logger log = LoggerFactory.getLogger(UserPermsResource.class);

    private static final String ENTITY_NAME = "userPerms";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserPermsService userPermsService;

    private final UserPermsRepository userPermsRepository;

    private final UserPermsQueryService userPermsQueryService;

    public UserPermsResource(
        UserPermsService userPermsService,
        UserPermsRepository userPermsRepository,
        UserPermsQueryService userPermsQueryService
    ) {
        this.userPermsService = userPermsService;
        this.userPermsRepository = userPermsRepository;
        this.userPermsQueryService = userPermsQueryService;
    }

    /**
     * {@code POST  /user-perms} : Create a new userPerms.
     *
     * @param userPerms the userPerms to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userPerms, or with status {@code 400 (Bad Request)} if the userPerms has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-perms")
    public ResponseEntity<UserPerms> createUserPerms(@Valid @RequestBody UserPerms userPerms) throws URISyntaxException {
        log.debug("REST request to save UserPerms : {}", userPerms);
        if (userPerms.getId() != null) {
            throw new BadRequestAlertException("A new userPerms cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserPerms result = userPermsService.save(userPerms);
        return ResponseEntity
            .created(new URI("/api/user-perms/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /user-perms/:id} : Updates an existing userPerms.
     *
     * @param id the id of the userPerms to save.
     * @param userPerms the userPerms to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userPerms,
     * or with status {@code 400 (Bad Request)} if the userPerms is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userPerms couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-perms/{id}")
    public ResponseEntity<UserPerms> updateUserPerms(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserPerms userPerms
    ) throws URISyntaxException {
        log.debug("REST request to update UserPerms : {}, {}", id, userPerms);
        if (userPerms.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userPerms.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userPermsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UserPerms result = userPermsService.update(userPerms);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userPerms.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /user-perms/:id} : Partial updates given fields of an existing userPerms, field will ignore if it is null
     *
     * @param id the id of the userPerms to save.
     * @param userPerms the userPerms to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userPerms,
     * or with status {@code 400 (Bad Request)} if the userPerms is not valid,
     * or with status {@code 404 (Not Found)} if the userPerms is not found,
     * or with status {@code 500 (Internal Server Error)} if the userPerms couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/user-perms/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserPerms> partialUpdateUserPerms(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserPerms userPerms
    ) throws URISyntaxException {
        log.debug("REST request to partial update UserPerms partially : {}, {}", id, userPerms);
        if (userPerms.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userPerms.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userPermsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserPerms> result = userPermsService.partialUpdate(userPerms);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userPerms.getId().toString())
        );
    }

    /**
     * {@code GET  /user-perms} : get all the userPerms.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userPerms in body.
     */
    @GetMapping("/user-perms")
    public ResponseEntity<List<UserPerms>> getAllUserPerms(UserPermsCriteria criteria) {
        log.debug("REST request to get UserPerms by criteria: {}", criteria);
        List<UserPerms> entityList = userPermsQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /user-perms/count} : count all the userPerms.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/user-perms/count")
    public ResponseEntity<Long> countUserPerms(UserPermsCriteria criteria) {
        log.debug("REST request to count UserPerms by criteria: {}", criteria);
        return ResponseEntity.ok().body(userPermsQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /user-perms/:id} : get the "id" userPerms.
     *
     * @param id the id of the userPerms to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userPerms, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-perms/{id}")
    public ResponseEntity<UserPerms> getUserPerms(@PathVariable Long id) {
        log.debug("REST request to get UserPerms : {}", id);
        Optional<UserPerms> userPerms = userPermsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userPerms);
    }

    /**
     * {@code DELETE  /user-perms/:id} : delete the "id" userPerms.
     *
     * @param id the id of the userPerms to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-perms/{id}")
    public ResponseEntity<Void> deleteUserPerms(@PathVariable Long id) {
        log.debug("REST request to delete UserPerms : {}", id);
        userPermsService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
