package team.bham.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A UserManagement.
 */
@Entity
@Table(name = "user_management")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserManagement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @JsonIgnoreProperties(value = { "userManagement" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private UserRole userRole;

    @ManyToOne
    @JsonIgnoreProperties(value = { "userManagements" }, allowSetters = true)
    private UserPerms userPerms;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserManagement id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserRole getUserRole() {
        return this.userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public UserManagement userRole(UserRole userRole) {
        this.setUserRole(userRole);
        return this;
    }

    public UserPerms getUserPerms() {
        return this.userPerms;
    }

    public void setUserPerms(UserPerms userPerms) {
        this.userPerms = userPerms;
    }

    public UserManagement userPerms(UserPerms userPerms) {
        this.setUserPerms(userPerms);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserManagement)) {
            return false;
        }
        return id != null && id.equals(((UserManagement) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserManagement{" +
            "id=" + getId() +
            "}";
    }
}
