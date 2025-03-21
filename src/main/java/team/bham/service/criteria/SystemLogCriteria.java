package team.bham.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import team.bham.domain.enumeration.LogType;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link team.bham.domain.SystemLog} entity. This class is used
 * in {@link team.bham.web.rest.SystemLogResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /system-logs?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SystemLogCriteria implements Serializable, Criteria {

    /**
     * Class for filtering LogType
     */
    public static class LogTypeFilter extends Filter<LogType> {

        public LogTypeFilter() {}

        public LogTypeFilter(LogTypeFilter filter) {
            super(filter);
        }

        @Override
        public LogTypeFilter copy() {
            return new LogTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private ZonedDateTimeFilter createdAt;

    private LogTypeFilter logType;

    private LongFilter createdById;

    private LongFilter emergencyCallId;

    private LongFilter eventId;

    private Boolean distinct;

    public SystemLogCriteria() {}

    public SystemLogCriteria(SystemLogCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.createdAt = other.createdAt == null ? null : other.createdAt.copy();
        this.logType = other.logType == null ? null : other.logType.copy();
        this.createdById = other.createdById == null ? null : other.createdById.copy();
        this.emergencyCallId = other.emergencyCallId == null ? null : other.emergencyCallId.copy();
        this.eventId = other.eventId == null ? null : other.eventId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public SystemLogCriteria copy() {
        return new SystemLogCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public ZonedDateTimeFilter getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTimeFilter createdAt() {
        if (createdAt == null) {
            createdAt = new ZonedDateTimeFilter();
        }
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTimeFilter createdAt) {
        this.createdAt = createdAt;
    }

    public LogTypeFilter getLogType() {
        return logType;
    }

    public LogTypeFilter logType() {
        if (logType == null) {
            logType = new LogTypeFilter();
        }
        return logType;
    }

    public void setLogType(LogTypeFilter logType) {
        this.logType = logType;
    }

    public LongFilter getCreatedById() {
        return createdById;
    }

    public LongFilter createdById() {
        if (createdById == null) {
            createdById = new LongFilter();
        }
        return createdById;
    }

    public void setCreatedById(LongFilter createdById) {
        this.createdById = createdById;
    }

    public LongFilter getEmergencyCallId() {
        return emergencyCallId;
    }

    public LongFilter emergencyCallId() {
        if (emergencyCallId == null) {
            emergencyCallId = new LongFilter();
        }
        return emergencyCallId;
    }

    public void setEmergencyCallId(LongFilter emergencyCallId) {
        this.emergencyCallId = emergencyCallId;
    }

    public LongFilter getEventId() {
        return eventId;
    }

    public LongFilter eventId() {
        if (eventId == null) {
            eventId = new LongFilter();
        }
        return eventId;
    }

    public void setEventId(LongFilter eventId) {
        this.eventId = eventId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SystemLogCriteria that = (SystemLogCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(logType, that.logType) &&
            Objects.equals(createdById, that.createdById) &&
            Objects.equals(emergencyCallId, that.emergencyCallId) &&
            Objects.equals(eventId, that.eventId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt, logType, createdById, emergencyCallId, eventId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SystemLogCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (createdAt != null ? "createdAt=" + createdAt + ", " : "") +
            (logType != null ? "logType=" + logType + ", " : "") +
            (createdById != null ? "createdById=" + createdById + ", " : "") +
            (emergencyCallId != null ? "emergencyCallId=" + emergencyCallId + ", " : "") +
            (eventId != null ? "eventId=" + eventId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
