package team.bham.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link team.bham.domain.UserManagement} entity. This class is used
 * in {@link team.bham.web.rest.UserManagementResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /user-managements?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserManagementCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter userRoleId;

    private LongFilter userPermsId;

    private Boolean distinct;

    public UserManagementCriteria() {}

    public UserManagementCriteria(UserManagementCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.userRoleId = other.userRoleId == null ? null : other.userRoleId.copy();
        this.userPermsId = other.userPermsId == null ? null : other.userPermsId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public UserManagementCriteria copy() {
        return new UserManagementCriteria(this);
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

    public LongFilter getUserRoleId() {
        return userRoleId;
    }

    public LongFilter userRoleId() {
        if (userRoleId == null) {
            userRoleId = new LongFilter();
        }
        return userRoleId;
    }

    public void setUserRoleId(LongFilter userRoleId) {
        this.userRoleId = userRoleId;
    }

    public LongFilter getUserPermsId() {
        return userPermsId;
    }

    public LongFilter userPermsId() {
        if (userPermsId == null) {
            userPermsId = new LongFilter();
        }
        return userPermsId;
    }

    public void setUserPermsId(LongFilter userPermsId) {
        this.userPermsId = userPermsId;
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
        final UserManagementCriteria that = (UserManagementCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(userRoleId, that.userRoleId) &&
            Objects.equals(userPermsId, that.userPermsId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userRoleId, userPermsId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserManagementCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (userRoleId != null ? "userRoleId=" + userRoleId + ", " : "") +
            (userPermsId != null ? "userPermsId=" + userPermsId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
