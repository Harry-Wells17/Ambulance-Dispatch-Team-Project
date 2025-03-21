package team.bham.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import team.bham.domain.enumeration.CallCategory;
import team.bham.domain.enumeration.Sex;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link team.bham.domain.EmergencyCall} entity. This class is used
 * in {@link team.bham.web.rest.EmergencyCallResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /emergency-calls?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EmergencyCallCriteria implements Serializable, Criteria {

    /**
     * Class for filtering CallCategory
     */
    public static class CallCategoryFilter extends Filter<CallCategory> {

        public CallCategoryFilter() {}

        public CallCategoryFilter(CallCategoryFilter filter) {
            super(filter);
        }

        @Override
        public CallCategoryFilter copy() {
            return new CallCategoryFilter(this);
        }
    }

    /**
     * Class for filtering Sex
     */
    public static class SexFilter extends Filter<Sex> {

        public SexFilter() {}

        public SexFilter(SexFilter filter) {
            super(filter);
        }

        @Override
        public SexFilter copy() {
            return new SexFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private ZonedDateTimeFilter created;

    private BooleanFilter open;

    private CallCategoryFilter type;

    private IntegerFilter age;

    private SexFilter sexAssignedAtBirth;

    private StringFilter history;

    private StringFilter injuries;

    private StringFilter condition;

    private FloatFilter latitude;

    private FloatFilter longitude;

    private BooleanFilter closed;

    private LongFilter resourceAssignedId;

    private LongFilter systemLogId;

    private LongFilter createdById;

    private LongFilter eventId;

    private Boolean distinct;

    public EmergencyCallCriteria() {}

    public EmergencyCallCriteria(EmergencyCallCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.created = other.created == null ? null : other.created.copy();
        this.open = other.open == null ? null : other.open.copy();
        this.type = other.type == null ? null : other.type.copy();
        this.age = other.age == null ? null : other.age.copy();
        this.sexAssignedAtBirth = other.sexAssignedAtBirth == null ? null : other.sexAssignedAtBirth.copy();
        this.history = other.history == null ? null : other.history.copy();
        this.injuries = other.injuries == null ? null : other.injuries.copy();
        this.condition = other.condition == null ? null : other.condition.copy();
        this.latitude = other.latitude == null ? null : other.latitude.copy();
        this.longitude = other.longitude == null ? null : other.longitude.copy();
        this.closed = other.closed == null ? null : other.closed.copy();
        this.resourceAssignedId = other.resourceAssignedId == null ? null : other.resourceAssignedId.copy();
        this.systemLogId = other.systemLogId == null ? null : other.systemLogId.copy();
        this.createdById = other.createdById == null ? null : other.createdById.copy();
        this.eventId = other.eventId == null ? null : other.eventId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public EmergencyCallCriteria copy() {
        return new EmergencyCallCriteria(this);
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

    public ZonedDateTimeFilter getCreated() {
        return created;
    }

    public ZonedDateTimeFilter created() {
        if (created == null) {
            created = new ZonedDateTimeFilter();
        }
        return created;
    }

    public void setCreated(ZonedDateTimeFilter created) {
        this.created = created;
    }

    public BooleanFilter getOpen() {
        return open;
    }

    public BooleanFilter open() {
        if (open == null) {
            open = new BooleanFilter();
        }
        return open;
    }

    public void setOpen(BooleanFilter open) {
        this.open = open;
    }

    public CallCategoryFilter getType() {
        return type;
    }

    public CallCategoryFilter type() {
        if (type == null) {
            type = new CallCategoryFilter();
        }
        return type;
    }

    public void setType(CallCategoryFilter type) {
        this.type = type;
    }

    public IntegerFilter getAge() {
        return age;
    }

    public IntegerFilter age() {
        if (age == null) {
            age = new IntegerFilter();
        }
        return age;
    }

    public void setAge(IntegerFilter age) {
        this.age = age;
    }

    public SexFilter getSexAssignedAtBirth() {
        return sexAssignedAtBirth;
    }

    public SexFilter sexAssignedAtBirth() {
        if (sexAssignedAtBirth == null) {
            sexAssignedAtBirth = new SexFilter();
        }
        return sexAssignedAtBirth;
    }

    public void setSexAssignedAtBirth(SexFilter sexAssignedAtBirth) {
        this.sexAssignedAtBirth = sexAssignedAtBirth;
    }

    public StringFilter getHistory() {
        return history;
    }

    public StringFilter history() {
        if (history == null) {
            history = new StringFilter();
        }
        return history;
    }

    public void setHistory(StringFilter history) {
        this.history = history;
    }

    public StringFilter getInjuries() {
        return injuries;
    }

    public StringFilter injuries() {
        if (injuries == null) {
            injuries = new StringFilter();
        }
        return injuries;
    }

    public void setInjuries(StringFilter injuries) {
        this.injuries = injuries;
    }

    public StringFilter getCondition() {
        return condition;
    }

    public StringFilter condition() {
        if (condition == null) {
            condition = new StringFilter();
        }
        return condition;
    }

    public void setCondition(StringFilter condition) {
        this.condition = condition;
    }

    public FloatFilter getLatitude() {
        return latitude;
    }

    public FloatFilter latitude() {
        if (latitude == null) {
            latitude = new FloatFilter();
        }
        return latitude;
    }

    public void setLatitude(FloatFilter latitude) {
        this.latitude = latitude;
    }

    public FloatFilter getLongitude() {
        return longitude;
    }

    public FloatFilter longitude() {
        if (longitude == null) {
            longitude = new FloatFilter();
        }
        return longitude;
    }

    public void setLongitude(FloatFilter longitude) {
        this.longitude = longitude;
    }

    public BooleanFilter getClosed() {
        return closed;
    }

    public BooleanFilter closed() {
        if (closed == null) {
            closed = new BooleanFilter();
        }
        return closed;
    }

    public void setClosed(BooleanFilter closed) {
        this.closed = closed;
    }

    public LongFilter getResourceAssignedId() {
        return resourceAssignedId;
    }

    public LongFilter resourceAssignedId() {
        if (resourceAssignedId == null) {
            resourceAssignedId = new LongFilter();
        }
        return resourceAssignedId;
    }

    public void setResourceAssignedId(LongFilter resourceAssignedId) {
        this.resourceAssignedId = resourceAssignedId;
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
        final EmergencyCallCriteria that = (EmergencyCallCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(created, that.created) &&
            Objects.equals(open, that.open) &&
            Objects.equals(type, that.type) &&
            Objects.equals(age, that.age) &&
            Objects.equals(sexAssignedAtBirth, that.sexAssignedAtBirth) &&
            Objects.equals(history, that.history) &&
            Objects.equals(injuries, that.injuries) &&
            Objects.equals(condition, that.condition) &&
            Objects.equals(latitude, that.latitude) &&
            Objects.equals(longitude, that.longitude) &&
            Objects.equals(closed, that.closed) &&
            Objects.equals(resourceAssignedId, that.resourceAssignedId) &&
            Objects.equals(systemLogId, that.systemLogId) &&
            Objects.equals(createdById, that.createdById) &&
            Objects.equals(eventId, that.eventId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            created,
            open,
            type,
            age,
            sexAssignedAtBirth,
            history,
            injuries,
            condition,
            latitude,
            longitude,
            closed,
            resourceAssignedId,
            systemLogId,
            createdById,
            eventId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EmergencyCallCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (created != null ? "created=" + created + ", " : "") +
            (open != null ? "open=" + open + ", " : "") +
            (type != null ? "type=" + type + ", " : "") +
            (age != null ? "age=" + age + ", " : "") +
            (sexAssignedAtBirth != null ? "sexAssignedAtBirth=" + sexAssignedAtBirth + ", " : "") +
            (history != null ? "history=" + history + ", " : "") +
            (injuries != null ? "injuries=" + injuries + ", " : "") +
            (condition != null ? "condition=" + condition + ", " : "") +
            (latitude != null ? "latitude=" + latitude + ", " : "") +
            (longitude != null ? "longitude=" + longitude + ", " : "") +
            (closed != null ? "closed=" + closed + ", " : "") +
            (resourceAssignedId != null ? "resourceAssignedId=" + resourceAssignedId + ", " : "") +
            (systemLogId != null ? "systemLogId=" + systemLogId + ", " : "") +
            (createdById != null ? "createdById=" + createdById + ", " : "") +
            (eventId != null ? "eventId=" + eventId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
