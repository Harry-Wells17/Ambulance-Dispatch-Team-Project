package team.bham.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link team.bham.domain.ResourceAssigned} entity. This class is used
 * in {@link team.bham.web.rest.ResourceAssignedResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /resource-assigneds?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ResourceAssignedCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private ZonedDateTimeFilter callRecievedTime;

    private ZonedDateTimeFilter onSceneTime;

    private ZonedDateTimeFilter leftSceneTime;

    private ZonedDateTimeFilter arrivedHospitalTime;

    private ZonedDateTimeFilter clearHospitalTime;

    private ZonedDateTimeFilter greenTime;

    private ZonedDateTimeFilter unAssignedTime;

    private LongFilter resourceId;

    private LongFilter emergencyCallId;

    private Boolean distinct;

    public ResourceAssignedCriteria() {}

    public ResourceAssignedCriteria(ResourceAssignedCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.callRecievedTime = other.callRecievedTime == null ? null : other.callRecievedTime.copy();
        this.onSceneTime = other.onSceneTime == null ? null : other.onSceneTime.copy();
        this.leftSceneTime = other.leftSceneTime == null ? null : other.leftSceneTime.copy();
        this.arrivedHospitalTime = other.arrivedHospitalTime == null ? null : other.arrivedHospitalTime.copy();
        this.clearHospitalTime = other.clearHospitalTime == null ? null : other.clearHospitalTime.copy();
        this.greenTime = other.greenTime == null ? null : other.greenTime.copy();
        this.unAssignedTime = other.unAssignedTime == null ? null : other.unAssignedTime.copy();
        this.resourceId = other.resourceId == null ? null : other.resourceId.copy();
        this.emergencyCallId = other.emergencyCallId == null ? null : other.emergencyCallId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ResourceAssignedCriteria copy() {
        return new ResourceAssignedCriteria(this);
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

    public ZonedDateTimeFilter getCallRecievedTime() {
        return callRecievedTime;
    }

    public ZonedDateTimeFilter callRecievedTime() {
        if (callRecievedTime == null) {
            callRecievedTime = new ZonedDateTimeFilter();
        }
        return callRecievedTime;
    }

    public void setCallRecievedTime(ZonedDateTimeFilter callRecievedTime) {
        this.callRecievedTime = callRecievedTime;
    }

    public ZonedDateTimeFilter getOnSceneTime() {
        return onSceneTime;
    }

    public ZonedDateTimeFilter onSceneTime() {
        if (onSceneTime == null) {
            onSceneTime = new ZonedDateTimeFilter();
        }
        return onSceneTime;
    }

    public void setOnSceneTime(ZonedDateTimeFilter onSceneTime) {
        this.onSceneTime = onSceneTime;
    }

    public ZonedDateTimeFilter getLeftSceneTime() {
        return leftSceneTime;
    }

    public ZonedDateTimeFilter leftSceneTime() {
        if (leftSceneTime == null) {
            leftSceneTime = new ZonedDateTimeFilter();
        }
        return leftSceneTime;
    }

    public void setLeftSceneTime(ZonedDateTimeFilter leftSceneTime) {
        this.leftSceneTime = leftSceneTime;
    }

    public ZonedDateTimeFilter getArrivedHospitalTime() {
        return arrivedHospitalTime;
    }

    public ZonedDateTimeFilter arrivedHospitalTime() {
        if (arrivedHospitalTime == null) {
            arrivedHospitalTime = new ZonedDateTimeFilter();
        }
        return arrivedHospitalTime;
    }

    public void setArrivedHospitalTime(ZonedDateTimeFilter arrivedHospitalTime) {
        this.arrivedHospitalTime = arrivedHospitalTime;
    }

    public ZonedDateTimeFilter getClearHospitalTime() {
        return clearHospitalTime;
    }

    public ZonedDateTimeFilter clearHospitalTime() {
        if (clearHospitalTime == null) {
            clearHospitalTime = new ZonedDateTimeFilter();
        }
        return clearHospitalTime;
    }

    public void setClearHospitalTime(ZonedDateTimeFilter clearHospitalTime) {
        this.clearHospitalTime = clearHospitalTime;
    }

    public ZonedDateTimeFilter getGreenTime() {
        return greenTime;
    }

    public ZonedDateTimeFilter greenTime() {
        if (greenTime == null) {
            greenTime = new ZonedDateTimeFilter();
        }
        return greenTime;
    }

    public void setGreenTime(ZonedDateTimeFilter greenTime) {
        this.greenTime = greenTime;
    }

    public ZonedDateTimeFilter getUnAssignedTime() {
        return unAssignedTime;
    }

    public ZonedDateTimeFilter unAssignedTime() {
        if (unAssignedTime == null) {
            unAssignedTime = new ZonedDateTimeFilter();
        }
        return unAssignedTime;
    }

    public void setUnAssignedTime(ZonedDateTimeFilter unAssignedTime) {
        this.unAssignedTime = unAssignedTime;
    }

    public LongFilter getResourceId() {
        return resourceId;
    }

    public LongFilter resourceId() {
        if (resourceId == null) {
            resourceId = new LongFilter();
        }
        return resourceId;
    }

    public void setResourceId(LongFilter resourceId) {
        this.resourceId = resourceId;
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
        final ResourceAssignedCriteria that = (ResourceAssignedCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(callRecievedTime, that.callRecievedTime) &&
            Objects.equals(onSceneTime, that.onSceneTime) &&
            Objects.equals(leftSceneTime, that.leftSceneTime) &&
            Objects.equals(arrivedHospitalTime, that.arrivedHospitalTime) &&
            Objects.equals(clearHospitalTime, that.clearHospitalTime) &&
            Objects.equals(greenTime, that.greenTime) &&
            Objects.equals(unAssignedTime, that.unAssignedTime) &&
            Objects.equals(resourceId, that.resourceId) &&
            Objects.equals(emergencyCallId, that.emergencyCallId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            callRecievedTime,
            onSceneTime,
            leftSceneTime,
            arrivedHospitalTime,
            clearHospitalTime,
            greenTime,
            unAssignedTime,
            resourceId,
            emergencyCallId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ResourceAssignedCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (callRecievedTime != null ? "callRecievedTime=" + callRecievedTime + ", " : "") +
            (onSceneTime != null ? "onSceneTime=" + onSceneTime + ", " : "") +
            (leftSceneTime != null ? "leftSceneTime=" + leftSceneTime + ", " : "") +
            (arrivedHospitalTime != null ? "arrivedHospitalTime=" + arrivedHospitalTime + ", " : "") +
            (clearHospitalTime != null ? "clearHospitalTime=" + clearHospitalTime + ", " : "") +
            (greenTime != null ? "greenTime=" + greenTime + ", " : "") +
            (unAssignedTime != null ? "unAssignedTime=" + unAssignedTime + ", " : "") +
            (resourceId != null ? "resourceId=" + resourceId + ", " : "") +
            (emergencyCallId != null ? "emergencyCallId=" + emergencyCallId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
