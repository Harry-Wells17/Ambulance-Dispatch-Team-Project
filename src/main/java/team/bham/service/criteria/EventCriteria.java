package team.bham.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link team.bham.domain.Event} entity. This class is used
 * in {@link team.bham.web.rest.EventResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /events?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EventCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter created;

    private StringFilter eventName;

    private LongFilter resourcesId;

    private LongFilter systemCallsId;

    private LongFilter systemLogId;

    private Boolean distinct;

    public EventCriteria() {}

    public EventCriteria(EventCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.created = other.created == null ? null : other.created.copy();
        this.eventName = other.eventName == null ? null : other.eventName.copy();
        this.resourcesId = other.resourcesId == null ? null : other.resourcesId.copy();
        this.systemCallsId = other.systemCallsId == null ? null : other.systemCallsId.copy();
        this.systemLogId = other.systemLogId == null ? null : other.systemLogId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public EventCriteria copy() {
        return new EventCriteria(this);
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

    public StringFilter getCreated() {
        return created;
    }

    public StringFilter created() {
        if (created == null) {
            created = new StringFilter();
        }
        return created;
    }

    public void setCreated(StringFilter created) {
        this.created = created;
    }

    public StringFilter getEventName() {
        return eventName;
    }

    public StringFilter eventName() {
        if (eventName == null) {
            eventName = new StringFilter();
        }
        return eventName;
    }

    public void setEventName(StringFilter eventName) {
        this.eventName = eventName;
    }

    public LongFilter getResourcesId() {
        return resourcesId;
    }

    public LongFilter resourcesId() {
        if (resourcesId == null) {
            resourcesId = new LongFilter();
        }
        return resourcesId;
    }

    public void setResourcesId(LongFilter resourcesId) {
        this.resourcesId = resourcesId;
    }

    public LongFilter getSystemCallsId() {
        return systemCallsId;
    }

    public LongFilter systemCallsId() {
        if (systemCallsId == null) {
            systemCallsId = new LongFilter();
        }
        return systemCallsId;
    }

    public void setSystemCallsId(LongFilter systemCallsId) {
        this.systemCallsId = systemCallsId;
    }

    public LongFilter getSystemLogId() {
        return systemLogId;
    }

    public LongFilter systemLogId() {
        if (systemLogId == null) {
            systemLogId = new LongFilter();
        }
        return systemLogId;
    }

    public void setSystemLogId(LongFilter systemLogId) {
        this.systemLogId = systemLogId;
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
        final EventCriteria that = (EventCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(created, that.created) &&
            Objects.equals(eventName, that.eventName) &&
            Objects.equals(resourcesId, that.resourcesId) &&
            Objects.equals(systemCallsId, that.systemCallsId) &&
            Objects.equals(systemLogId, that.systemLogId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, created, eventName, resourcesId, systemCallsId, systemLogId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EventCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (created != null ? "created=" + created + ", " : "") +
            (eventName != null ? "eventName=" + eventName + ", " : "") +
            (resourcesId != null ? "resourcesId=" + resourcesId + ", " : "") +
            (systemCallsId != null ? "systemCallsId=" + systemCallsId + ", " : "") +
            (systemLogId != null ? "systemLogId=" + systemLogId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
