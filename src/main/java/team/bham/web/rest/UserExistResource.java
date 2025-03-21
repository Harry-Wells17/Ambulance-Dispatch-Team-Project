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
import team.bham.domain.UserExist;
import team.bham.repository.UserExistRepository;
import team.bham.service.UserExistQueryService;
import team.bham.service.UserExistService;
import team.bham.service.criteria.UserExistCriteria;
import team.bham.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link team.bham.domain.UserExist}.
 */
@RestController
@RequestMapping("/api")
public class UserExistResource {

    private final Logger log = LoggerFactory.getLogger(UserExistResource.class);

    private static final String ENTITY_NAME = "userExist";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserExistService userExistService;

    private final UserExistRepository userExistRepository;

    private final UserExistQueryService userExistQueryService;

    public UserExistResource(
        UserExistService userExistService,
        UserExistRepository userExistRepository,
        UserExistQueryService userExistQueryService
    ) {
        this.userExistService = userExistService;
        this.userExistRepository = userExistRepository;
        this.userExistQueryService = userExistQueryService;
    }

    /**
     * {@code POST  /user-exists} : Create a new userExist.
     *
     * @param userExist the userExist to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userExist, or with status {@code 400 (Bad Request)} if the userExist has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-exists")
    public ResponseEntity<UserExist> createUserExist(@RequestBody UserExist userExist) throws URISyntaxException {
        log.debug("REST request to save UserExist : {}", userExist);
        if (userExist.getId() != null) {
            throw new BadRequestAlertException("A new userExist cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserExist result = userExistService.save(userExist);
        return ResponseEntity
            .created(new URI("/api/user-exists/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /user-exists/:id} : Updates an existing userExist.
     *
     * @param id the id of the userExist to save.
     * @param userExist the userExist to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userExist,
     * or with status {@code 400 (Bad Request)} if the userExist is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userExist couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-exists/{id}")
    public ResponseEntity<UserExist> updateUserExist(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UserExist userExist
    ) throws URISyntaxException {
        log.debug("REST request to update UserExist : {}, {}", id, userExist);
        if (userExist.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userExist.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userExistRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UserExist result = userExistService.update(userExist);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userExist.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /user-exists/:id} : Partial updates given fields of an existing userExist, field will ignore if it is null
     *
     * @param id the id of the userExist to save.
     * @param userExist the userExist to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userExist,
     * or with status {@code 400 (Bad Request)} if the userExist is not valid,
     * or with status {@code 404 (Not Found)} if the userExist is not found,
     * or with status {@code 500 (Internal Server Error)} if the userExist couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/user-exists/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserExist> partialUpdateUserExist(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UserExist userExist
    ) throws URISyntaxException {
        log.debug("REST request to partial update UserExist partially : {}, {}", id, userExist);
        if (userExist.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userExist.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userExistRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserExist> result = userExistService.partialUpdate(userExist);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userExist.getId().toString())
        );
    }

    /**
     * {@code GET  /user-exists} : get all the userExists.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userExists in body.
     */
    @GetMapping("/user-exists")
    public ResponseEntity<List<UserExist>> getAllUserExists(UserExistCriteria criteria) {
        log.debug("REST request to get UserExists by criteria: {}", criteria);
        List<UserExist> entityList = userExistQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /user-exists/count} : count all the userExists.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/user-exists/count")
    public ResponseEntity<Long> countUserExists(UserExistCriteria criteria) {
        log.debug("REST request to count UserExists by criteria: {}", criteria);
        return ResponseEntity.ok().body(userExistQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /user-exists/:id} : get the "id" userExist.
     *
     * @param id the id of the userExist to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userExist, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-exists/{id}")
    public ResponseEntity<UserExist> getUserExist(@PathVariable Long id) {
        log.debug("REST request to get UserExist : {}", id);
        Optional<UserExist> userExist = userExistService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userExist);
    }

    /**
     * {@code DELETE  /user-exists/:id} : delete the "id" userExist.
     *
     * @param id the id of the userExist to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-exists/{id}")
    public ResponseEntity<Void> deleteUserExist(@PathVariable Long id) {
        log.debug("REST request to delete UserExist : {}", id);
        userExistService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
