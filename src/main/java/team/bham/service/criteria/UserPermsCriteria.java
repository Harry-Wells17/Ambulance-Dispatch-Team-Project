package team.bham.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import team.bham.domain.enumeration.Perms;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link team.bham.domain.UserPerms} entity. This class is used
 * in {@link team.bham.web.rest.UserPermsResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /user-perms?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserPermsCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Perms
     */
    public static class PermsFilter extends Filter<Perms> {

        public PermsFilter() {}

        public PermsFilter(PermsFilter filter) {
            super(filter);
        }

        @Override
        public PermsFilter copy() {
            return new PermsFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private PermsFilter perms;

    private LongFilter userManagementId;

    private Boolean distinct;

    public UserPermsCriteria() {}

    public UserPermsCriteria(UserPermsCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.perms = other.perms == null ? null : other.perms.copy();
        this.userManagementId = other.userManagementId == null ? null : other.userManagementId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public UserPermsCriteria copy() {
        return new UserPermsCriteria(this);
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

    public PermsFilter getPerms() {
        return perms;
    }

    public PermsFilter perms() {
        if (perms == null) {
            perms = new PermsFilter();
        }
        return perms;
    }

    public void setPerms(PermsFilter perms) {
        this.perms = perms;
    }

    public LongFilter getUserManagementId() {
        return userManagementId;
    }

    public LongFilter userManagementId() {
        if (userManagementId == null) {
            userManagementId = new LongFilter();
        }
        return userManagementId;
    }

    public void setUserManagementId(LongFilter userManagementId) {
        this.userManagementId = userManagementId;
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
        final UserPermsCriteria that = (UserPermsCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(perms, that.perms) &&
            Objects.equals(userManagementId, that.userManagementId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, perms, userManagementId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserPermsCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (perms != null ? "perms=" + perms + ", " : "") +
            (userManagementId != null ? "userManagementId=" + userManagementId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
