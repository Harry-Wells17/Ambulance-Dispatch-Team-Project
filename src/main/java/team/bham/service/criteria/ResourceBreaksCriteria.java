package team.bham.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link team.bham.domain.ResourceBreaks} entity. This class is used
 * in {@link team.bham.web.rest.ResourceBreaksResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /resource-breaks?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ResourceBreaksCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private ZonedDateTimeFilter lastBreak;

    private BooleanFilter breakRequested;

    private ZonedDateTimeFilter startedBreak;

    private BooleanFilter onBreak;

    private Boolean distinct;

    public ResourceBreaksCriteria() {}

    public ResourceBreaksCriteria(ResourceBreaksCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.lastBreak = other.lastBreak == null ? null : other.lastBreak.copy();
        this.breakRequested = other.breakRequested == null ? null : other.breakRequested.copy();
        this.startedBreak = other.startedBreak == null ? null : other.startedBreak.copy();
        this.onBreak = other.onBreak == null ? null : other.onBreak.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ResourceBreaksCriteria copy() {
        return new ResourceBreaksCriteria(this);
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

    public ZonedDateTimeFilter getLastBreak() {
        return lastBreak;
    }

    public ZonedDateTimeFilter lastBreak() {
        if (lastBreak == null) {
            lastBreak = new ZonedDateTimeFilter();
        }
        return lastBreak;
    }

    public void setLastBreak(ZonedDateTimeFilter lastBreak) {
        this.lastBreak = lastBreak;
    }

    public BooleanFilter getBreakRequested() {
        return breakRequested;
    }

    public BooleanFilter breakRequested() {
        if (breakRequested == null) {
            breakRequested = new BooleanFilter();
        }
        return breakRequested;
    }

    public void setBreakRequested(BooleanFilter breakRequested) {
        this.breakRequested = breakRequested;
    }

    public ZonedDateTimeFilter getStartedBreak() {
        return startedBreak;
    }

    public ZonedDateTimeFilter startedBreak() {
        if (startedBreak == null) {
            startedBreak = new ZonedDateTimeFilter();
        }
        return startedBreak;
    }

    public void setStartedBreak(ZonedDateTimeFilter startedBreak) {
        this.startedBreak = startedBreak;
    }

    public BooleanFilter getOnBreak() {
        return onBreak;
    }

    public BooleanFilter onBreak() {
        if (onBreak == null) {
            onBreak = new BooleanFilter();
        }
        return onBreak;
    }

    public void setOnBreak(BooleanFilter onBreak) {
        this.onBreak = onBreak;
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
        final ResourceBreaksCriteria that = (ResourceBreaksCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(lastBreak, that.lastBreak) &&
            Objects.equals(breakRequested, that.breakRequested) &&
            Objects.equals(startedBreak, that.startedBreak) &&
            Objects.equals(onBreak, that.onBreak) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lastBreak, breakRequested, startedBreak, onBreak, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ResourceBreaksCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (lastBreak != null ? "lastBreak=" + lastBreak + ", " : "") +
            (breakRequested != null ? "breakRequested=" + breakRequested + ", " : "") +
            (startedBreak != null ? "startedBreak=" + startedBreak + ", " : "") +
            (onBreak != null ? "onBreak=" + onBreak + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
