package team.bham.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import team.bham.domain.enumeration.Role;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link team.bham.domain.UserRole} entity. This class is used
 * in {@link team.bham.web.rest.UserRoleResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /user-roles?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserRoleCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Role
     */
    public static class RoleFilter extends Filter<Role> {

        public RoleFilter() {}

        public RoleFilter(RoleFilter filter) {
            super(filter);
        }

        @Override
        public RoleFilter copy() {
            return new RoleFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private RoleFilter role;

    private LongFilter userManagementId;

    private Boolean distinct;

    public UserRoleCriteria() {}

    public UserRoleCriteria(UserRoleCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.role = other.role == null ? null : other.role.copy();
        this.userManagementId = other.userManagementId == null ? null : other.userManagementId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public UserRoleCriteria copy() {
        return new UserRoleCriteria(this);
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

    public RoleFilter getRole() {
        return role;
    }

    public RoleFilter role() {
        if (role == null) {
            role = new RoleFilter();
        }
        return role;
    }

    public void setRole(RoleFilter role) {
        this.role = role;
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
        final UserRoleCriteria that = (UserRoleCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(role, that.role) &&
            Objects.equals(userManagementId, that.userManagementId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, role, userManagementId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserRoleCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (role != null ? "role=" + role + ", " : "") +
            (userManagementId != null ? "userManagementId=" + userManagementId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
