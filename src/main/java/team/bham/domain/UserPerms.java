package team.bham.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import team.bham.domain.enumeration.Perms;

/**
 * A UserPerms.
 */
@Entity
@Table(name = "user_perms")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserPerms implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "perms", nullable = false)
    private Perms perms;

    @OneToMany(mappedBy = "userPerms")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "userRole", "userPerms" }, allowSetters = true)
    private Set<UserManagement> userManagements = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserPerms id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Perms getPerms() {
        return this.perms;
    }

    public UserPerms perms(Perms perms) {
        this.setPerms(perms);
        return this;
    }

    public void setPerms(Perms perms) {
        this.perms = perms;
    }

    public Set<UserManagement> getUserManagements() {
        return this.userManagements;
    }

    public void setUserManagements(Set<UserManagement> userManagements) {
        if (this.userManagements != null) {
            this.userManagements.forEach(i -> i.setUserPerms(null));
        }
        if (userManagements != null) {
            userManagements.forEach(i -> i.setUserPerms(this));
        }
        this.userManagements = userManagements;
    }

    public UserPerms userManagements(Set<UserManagement> userManagements) {
        this.setUserManagements(userManagements);
        return this;
    }

    public UserPerms addUserManagement(UserManagement userManagement) {
        this.userManagements.add(userManagement);
        userManagement.setUserPerms(this);
        return this;
    }

    public UserPerms removeUserManagement(UserManagement userManagement) {
        this.userManagements.remove(userManagement);
        userManagement.setUserPerms(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserPerms)) {
            return false;
        }
        return id != null && id.equals(((UserPerms) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserPerms{" +
            "id=" + getId() +
            ", perms='" + getPerms() + "'" +
            "}";
    }
}
