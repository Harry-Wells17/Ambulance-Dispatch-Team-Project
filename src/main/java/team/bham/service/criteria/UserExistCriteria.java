package team.bham.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link team.bham.domain.UserExist} entity. This class is used
 * in {@link team.bham.web.rest.UserExistResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /user-exists?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserExistCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BooleanFilter exist;

    private LongFilter createdById;

    private Boolean distinct;

    public UserExistCriteria() {}

    public UserExistCriteria(UserExistCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.exist = other.exist == null ? null : other.exist.copy();
        this.createdById = other.createdById == null ? null : other.createdById.copy();
        this.distinct = other.distinct;
    }

    @Override
    public UserExistCriteria copy() {
        return new UserExistCriteria(this);
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

    public BooleanFilter getExist() {
        return exist;
    }

    public BooleanFilter exist() {
        if (exist == null) {
            exist = new BooleanFilter();
        }
        return exist;
    }

    public void setExist(BooleanFilter exist) {
        this.exist = exist;
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
        final UserExistCriteria that = (UserExistCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(exist, that.exist) &&
            Objects.equals(createdById, that.createdById) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, exist, createdById, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserExistCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (exist != null ? "exist=" + exist + ", " : "") +
            (createdById != null ? "createdById=" + createdById + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
