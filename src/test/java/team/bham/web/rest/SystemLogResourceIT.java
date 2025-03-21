package team.bham.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static team.bham.web.rest.TestUtil.sameInstant;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import team.bham.IntegrationTest;
import team.bham.domain.EmergencyCall;
import team.bham.domain.Event;
import team.bham.domain.SystemLog;
import team.bham.domain.User;
import team.bham.domain.enumeration.LogType;
import team.bham.repository.SystemLogRepository;
import team.bham.service.SystemLogService;
import team.bham.service.criteria.SystemLogCriteria;

/**
 * Integration tests for the {@link SystemLogResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SystemLogResourceIT {

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final LogType DEFAULT_LOG_TYPE = LogType.CONTROLROOM;
    private static final LogType UPDATED_LOG_TYPE = LogType.CALL;

    private static final String DEFAULT_MESSAGE_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE_CONTENT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/system-logs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SystemLogRepository systemLogRepository;

    @Mock
    private SystemLogRepository systemLogRepositoryMock;

    @Mock
    private SystemLogService systemLogServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSystemLogMockMvc;

    private SystemLog systemLog;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SystemLog createEntity(EntityManager em) {
        SystemLog systemLog = new SystemLog()
            .createdAt(DEFAULT_CREATED_AT)
            .logType(DEFAULT_LOG_TYPE)
            .messageContent(DEFAULT_MESSAGE_CONTENT);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        systemLog.setCreatedBy(user);
        return systemLog;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SystemLog createUpdatedEntity(EntityManager em) {
        SystemLog systemLog = new SystemLog()
            .createdAt(UPDATED_CREATED_AT)
            .logType(UPDATED_LOG_TYPE)
            .messageContent(UPDATED_MESSAGE_CONTENT);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        systemLog.setCreatedBy(user);
        return systemLog;
    }

    @BeforeEach
    public void initTest() {
        systemLog = createEntity(em);
    }

    @Test
    @Transactional
    void createSystemLog() throws Exception {
        int databaseSizeBeforeCreate = systemLogRepository.findAll().size();
        // Create the SystemLog
        restSystemLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(systemLog)))
            .andExpect(status().isCreated());

        // Validate the SystemLog in the database
        List<SystemLog> systemLogList = systemLogRepository.findAll();
        assertThat(systemLogList).hasSize(databaseSizeBeforeCreate + 1);
        SystemLog testSystemLog = systemLogList.get(systemLogList.size() - 1);
        assertThat(testSystemLog.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testSystemLog.getLogType()).isEqualTo(DEFAULT_LOG_TYPE);
        assertThat(testSystemLog.getMessageContent()).isEqualTo(DEFAULT_MESSAGE_CONTENT);
    }

    @Test
    @Transactional
    void createSystemLogWithExistingId() throws Exception {
        // Create the SystemLog with an existing ID
        systemLog.setId(1L);

        int databaseSizeBeforeCreate = systemLogRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSystemLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(systemLog)))
            .andExpect(status().isBadRequest());

        // Validate the SystemLog in the database
        List<SystemLog> systemLogList = systemLogRepository.findAll();
        assertThat(systemLogList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = systemLogRepository.findAll().size();
        // set the field null
        systemLog.setCreatedAt(null);

        // Create the SystemLog, which fails.

        restSystemLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(systemLog)))
            .andExpect(status().isBadRequest());

        List<SystemLog> systemLogList = systemLogRepository.findAll();
        assertThat(systemLogList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLogTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = systemLogRepository.findAll().size();
        // set the field null
        systemLog.setLogType(null);

        // Create the SystemLog, which fails.

        restSystemLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(systemLog)))
            .andExpect(status().isBadRequest());

        List<SystemLog> systemLogList = systemLogRepository.findAll();
        assertThat(systemLogList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSystemLogs() throws Exception {
        // Initialize the database
        systemLogRepository.saveAndFlush(systemLog);

        // Get all the systemLogList
        restSystemLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(systemLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].logType").value(hasItem(DEFAULT_LOG_TYPE.toString())))
            .andExpect(jsonPath("$.[*].messageContent").value(hasItem(DEFAULT_MESSAGE_CONTENT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSystemLogsWithEagerRelationshipsIsEnabled() throws Exception {
        when(systemLogServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSystemLogMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(systemLogServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSystemLogsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(systemLogServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSystemLogMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(systemLogRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSystemLog() throws Exception {
        // Initialize the database
        systemLogRepository.saveAndFlush(systemLog);

        // Get the systemLog
        restSystemLogMockMvc
            .perform(get(ENTITY_API_URL_ID, systemLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(systemLog.getId().intValue()))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)))
            .andExpect(jsonPath("$.logType").value(DEFAULT_LOG_TYPE.toString()))
            .andExpect(jsonPath("$.messageContent").value(DEFAULT_MESSAGE_CONTENT.toString()));
    }

    @Test
    @Transactional
    void getSystemLogsByIdFiltering() throws Exception {
        // Initialize the database
        systemLogRepository.saveAndFlush(systemLog);

        Long id = systemLog.getId();

        defaultSystemLogShouldBeFound("id.equals=" + id);
        defaultSystemLogShouldNotBeFound("id.notEquals=" + id);

        defaultSystemLogShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSystemLogShouldNotBeFound("id.greaterThan=" + id);

        defaultSystemLogShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSystemLogShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSystemLogsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        systemLogRepository.saveAndFlush(systemLog);

        // Get all the systemLogList where createdAt equals to DEFAULT_CREATED_AT
        defaultSystemLogShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the systemLogList where createdAt equals to UPDATED_CREATED_AT
        defaultSystemLogShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllSystemLogsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        systemLogRepository.saveAndFlush(systemLog);

        // Get all the systemLogList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultSystemLogShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the systemLogList where createdAt equals to UPDATED_CREATED_AT
        defaultSystemLogShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllSystemLogsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        systemLogRepository.saveAndFlush(systemLog);

        // Get all the systemLogList where createdAt is not null
        defaultSystemLogShouldBeFound("createdAt.specified=true");

        // Get all the systemLogList where createdAt is null
        defaultSystemLogShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllSystemLogsByCreatedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        systemLogRepository.saveAndFlush(systemLog);

        // Get all the systemLogList where createdAt is greater than or equal to DEFAULT_CREATED_AT
        defaultSystemLogShouldBeFound("createdAt.greaterThanOrEqual=" + DEFAULT_CREATED_AT);

        // Get all the systemLogList where createdAt is greater than or equal to UPDATED_CREATED_AT
        defaultSystemLogShouldNotBeFound("createdAt.greaterThanOrEqual=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllSystemLogsByCreatedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        systemLogRepository.saveAndFlush(systemLog);

        // Get all the systemLogList where createdAt is less than or equal to DEFAULT_CREATED_AT
        defaultSystemLogShouldBeFound("createdAt.lessThanOrEqual=" + DEFAULT_CREATED_AT);

        // Get all the systemLogList where createdAt is less than or equal to SMALLER_CREATED_AT
        defaultSystemLogShouldNotBeFound("createdAt.lessThanOrEqual=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllSystemLogsByCreatedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        systemLogRepository.saveAndFlush(systemLog);

        // Get all the systemLogList where createdAt is less than DEFAULT_CREATED_AT
        defaultSystemLogShouldNotBeFound("createdAt.lessThan=" + DEFAULT_CREATED_AT);

        // Get all the systemLogList where createdAt is less than UPDATED_CREATED_AT
        defaultSystemLogShouldBeFound("createdAt.lessThan=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllSystemLogsByCreatedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        systemLogRepository.saveAndFlush(systemLog);

        // Get all the systemLogList where createdAt is greater than DEFAULT_CREATED_AT
        defaultSystemLogShouldNotBeFound("createdAt.greaterThan=" + DEFAULT_CREATED_AT);

        // Get all the systemLogList where createdAt is greater than SMALLER_CREATED_AT
        defaultSystemLogShouldBeFound("createdAt.greaterThan=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllSystemLogsByLogTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        systemLogRepository.saveAndFlush(systemLog);

        // Get all the systemLogList where logType equals to DEFAULT_LOG_TYPE
        defaultSystemLogShouldBeFound("logType.equals=" + DEFAULT_LOG_TYPE);

        // Get all the systemLogList where logType equals to UPDATED_LOG_TYPE
        defaultSystemLogShouldNotBeFound("logType.equals=" + UPDATED_LOG_TYPE);
    }

    @Test
    @Transactional
    void getAllSystemLogsByLogTypeIsInShouldWork() throws Exception {
        // Initialize the database
        systemLogRepository.saveAndFlush(systemLog);

        // Get all the systemLogList where logType in DEFAULT_LOG_TYPE or UPDATED_LOG_TYPE
        defaultSystemLogShouldBeFound("logType.in=" + DEFAULT_LOG_TYPE + "," + UPDATED_LOG_TYPE);

        // Get all the systemLogList where logType equals to UPDATED_LOG_TYPE
        defaultSystemLogShouldNotBeFound("logType.in=" + UPDATED_LOG_TYPE);
    }

    @Test
    @Transactional
    void getAllSystemLogsByLogTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        systemLogRepository.saveAndFlush(systemLog);

        // Get all the systemLogList where logType is not null
        defaultSystemLogShouldBeFound("logType.specified=true");

        // Get all the systemLogList where logType is null
        defaultSystemLogShouldNotBeFound("logType.specified=false");
    }

    @Test
    @Transactional
    void getAllSystemLogsByCreatedByIsEqualToSomething() throws Exception {
        User createdBy;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            systemLogRepository.saveAndFlush(systemLog);
            createdBy = UserResourceIT.createEntity(em);
        } else {
            createdBy = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(createdBy);
        em.flush();
        systemLog.setCreatedBy(createdBy);
        systemLogRepository.saveAndFlush(systemLog);
        Long createdById = createdBy.getId();

        // Get all the systemLogList where createdBy equals to createdById
        defaultSystemLogShouldBeFound("createdById.equals=" + createdById);

        // Get all the systemLogList where createdBy equals to (createdById + 1)
        defaultSystemLogShouldNotBeFound("createdById.equals=" + (createdById + 1));
    }

    @Test
    @Transactional
    void getAllSystemLogsByEmergencyCallIsEqualToSomething() throws Exception {
        EmergencyCall emergencyCall;
        if (TestUtil.findAll(em, EmergencyCall.class).isEmpty()) {
            systemLogRepository.saveAndFlush(systemLog);
            emergencyCall = EmergencyCallResourceIT.createEntity(em);
        } else {
            emergencyCall = TestUtil.findAll(em, EmergencyCall.class).get(0);
        }
        em.persist(emergencyCall);
        em.flush();
        systemLog.setEmergencyCall(emergencyCall);
        systemLogRepository.saveAndFlush(systemLog);
        Long emergencyCallId = emergencyCall.getId();

        // Get all the systemLogList where emergencyCall equals to emergencyCallId
        defaultSystemLogShouldBeFound("emergencyCallId.equals=" + emergencyCallId);

        // Get all the systemLogList where emergencyCall equals to (emergencyCallId + 1)
        defaultSystemLogShouldNotBeFound("emergencyCallId.equals=" + (emergencyCallId + 1));
    }

    @Test
    @Transactional
    void getAllSystemLogsByEventIsEqualToSomething() throws Exception {
        Event event;
        if (TestUtil.findAll(em, Event.class).isEmpty()) {
            systemLogRepository.saveAndFlush(systemLog);
            event = EventResourceIT.createEntity(em);
        } else {
            event = TestUtil.findAll(em, Event.class).get(0);
        }
        em.persist(event);
        em.flush();
        systemLog.setEvent(event);
        systemLogRepository.saveAndFlush(systemLog);
        Long eventId = event.getId();

        // Get all the systemLogList where event equals to eventId
        defaultSystemLogShouldBeFound("eventId.equals=" + eventId);

        // Get all the systemLogList where event equals to (eventId + 1)
        defaultSystemLogShouldNotBeFound("eventId.equals=" + (eventId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSystemLogShouldBeFound(String filter) throws Exception {
        restSystemLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(systemLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].logType").value(hasItem(DEFAULT_LOG_TYPE.toString())))
            .andExpect(jsonPath("$.[*].messageContent").value(hasItem(DEFAULT_MESSAGE_CONTENT.toString())));

        // Check, that the count call also returns 1
        restSystemLogMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSystemLogShouldNotBeFound(String filter) throws Exception {
        restSystemLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSystemLogMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSystemLog() throws Exception {
        // Get the systemLog
        restSystemLogMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSystemLog() throws Exception {
        // Initialize the database
        systemLogRepository.saveAndFlush(systemLog);

        int databaseSizeBeforeUpdate = systemLogRepository.findAll().size();

        // Update the systemLog
        SystemLog updatedSystemLog = systemLogRepository.findById(systemLog.getId()).get();
        // Disconnect from session so that the updates on updatedSystemLog are not directly saved in db
        em.detach(updatedSystemLog);
        updatedSystemLog.createdAt(UPDATED_CREATED_AT).logType(UPDATED_LOG_TYPE).messageContent(UPDATED_MESSAGE_CONTENT);

        restSystemLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSystemLog.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSystemLog))
            )
            .andExpect(status().isOk());

        // Validate the SystemLog in the database
        List<SystemLog> systemLogList = systemLogRepository.findAll();
        assertThat(systemLogList).hasSize(databaseSizeBeforeUpdate);
        SystemLog testSystemLog = systemLogList.get(systemLogList.size() - 1);
        assertThat(testSystemLog.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testSystemLog.getLogType()).isEqualTo(UPDATED_LOG_TYPE);
        assertThat(testSystemLog.getMessageContent()).isEqualTo(UPDATED_MESSAGE_CONTENT);
    }

    @Test
    @Transactional
    void putNonExistingSystemLog() throws Exception {
        int databaseSizeBeforeUpdate = systemLogRepository.findAll().size();
        systemLog.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSystemLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, systemLog.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(systemLog))
            )
            .andExpect(status().isBadRequest());

        // Validate the SystemLog in the database
        List<SystemLog> systemLogList = systemLogRepository.findAll();
        assertThat(systemLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSystemLog() throws Exception {
        int databaseSizeBeforeUpdate = systemLogRepository.findAll().size();
        systemLog.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSystemLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(systemLog))
            )
            .andExpect(status().isBadRequest());

        // Validate the SystemLog in the database
        List<SystemLog> systemLogList = systemLogRepository.findAll();
        assertThat(systemLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSystemLog() throws Exception {
        int databaseSizeBeforeUpdate = systemLogRepository.findAll().size();
        systemLog.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSystemLogMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(systemLog)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SystemLog in the database
        List<SystemLog> systemLogList = systemLogRepository.findAll();
        assertThat(systemLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSystemLogWithPatch() throws Exception {
        // Initialize the database
        systemLogRepository.saveAndFlush(systemLog);

        int databaseSizeBeforeUpdate = systemLogRepository.findAll().size();

        // Update the systemLog using partial update
        SystemLog partialUpdatedSystemLog = new SystemLog();
        partialUpdatedSystemLog.setId(systemLog.getId());

        partialUpdatedSystemLog.logType(UPDATED_LOG_TYPE).messageContent(UPDATED_MESSAGE_CONTENT);

        restSystemLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSystemLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSystemLog))
            )
            .andExpect(status().isOk());

        // Validate the SystemLog in the database
        List<SystemLog> systemLogList = systemLogRepository.findAll();
        assertThat(systemLogList).hasSize(databaseSizeBeforeUpdate);
        SystemLog testSystemLog = systemLogList.get(systemLogList.size() - 1);
        assertThat(testSystemLog.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testSystemLog.getLogType()).isEqualTo(UPDATED_LOG_TYPE);
        assertThat(testSystemLog.getMessageContent()).isEqualTo(UPDATED_MESSAGE_CONTENT);
    }

    @Test
    @Transactional
    void fullUpdateSystemLogWithPatch() throws Exception {
        // Initialize the database
        systemLogRepository.saveAndFlush(systemLog);

        int databaseSizeBeforeUpdate = systemLogRepository.findAll().size();

        // Update the systemLog using partial update
        SystemLog partialUpdatedSystemLog = new SystemLog();
        partialUpdatedSystemLog.setId(systemLog.getId());

        partialUpdatedSystemLog.createdAt(UPDATED_CREATED_AT).logType(UPDATED_LOG_TYPE).messageContent(UPDATED_MESSAGE_CONTENT);

        restSystemLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSystemLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSystemLog))
            )
            .andExpect(status().isOk());

        // Validate the SystemLog in the database
        List<SystemLog> systemLogList = systemLogRepository.findAll();
        assertThat(systemLogList).hasSize(databaseSizeBeforeUpdate);
        SystemLog testSystemLog = systemLogList.get(systemLogList.size() - 1);
        assertThat(testSystemLog.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testSystemLog.getLogType()).isEqualTo(UPDATED_LOG_TYPE);
        assertThat(testSystemLog.getMessageContent()).isEqualTo(UPDATED_MESSAGE_CONTENT);
    }

    @Test
    @Transactional
    void patchNonExistingSystemLog() throws Exception {
        int databaseSizeBeforeUpdate = systemLogRepository.findAll().size();
        systemLog.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSystemLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, systemLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(systemLog))
            )
            .andExpect(status().isBadRequest());

        // Validate the SystemLog in the database
        List<SystemLog> systemLogList = systemLogRepository.findAll();
        assertThat(systemLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSystemLog() throws Exception {
        int databaseSizeBeforeUpdate = systemLogRepository.findAll().size();
        systemLog.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSystemLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(systemLog))
            )
            .andExpect(status().isBadRequest());

        // Validate the SystemLog in the database
        List<SystemLog> systemLogList = systemLogRepository.findAll();
        assertThat(systemLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSystemLog() throws Exception {
        int databaseSizeBeforeUpdate = systemLogRepository.findAll().size();
        systemLog.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSystemLogMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(systemLog))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SystemLog in the database
        List<SystemLog> systemLogList = systemLogRepository.findAll();
        assertThat(systemLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSystemLog() throws Exception {
        // Initialize the database
        systemLogRepository.saveAndFlush(systemLog);

        int databaseSizeBeforeDelete = systemLogRepository.findAll().size();

        // Delete the systemLog
        restSystemLogMockMvc
            .perform(delete(ENTITY_API_URL_ID, systemLog.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SystemLog> systemLogList = systemLogRepository.findAll();
        assertThat(systemLogList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
