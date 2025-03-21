package team.bham.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import team.bham.domain.enumeration.CallCategory;
import team.bham.domain.enumeration.Sex;

/**
 * A EmergencyCall.
 */
@Entity
@Table(name = "emergency_call")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EmergencyCall implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "created", nullable = false)
    private ZonedDateTime created;

    @Column(name = "open")
    private Boolean open;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private CallCategory type;

    @Column(name = "age")
    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(name = "sex_assigned_at_birth")
    private Sex sexAssignedAtBirth;

    @Column(name = "history")
    private String history;

    @Column(name = "injuries")
    private String injuries;

    @Column(name = "condition")
    private String condition;

    @NotNull
    @Column(name = "latitude", nullable = false)
    private Float latitude;

    @NotNull
    @Column(name = "longitude", nullable = false)
    private Float longitude;

    @Column(name = "closed")
    private Boolean closed;

    @OneToMany(mappedBy = "emergencyCall")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "resource", "emergencyCall" }, allowSetters = true)
    private Set<ResourceAssigned> resourceAssigneds = new HashSet<>();

    @OneToMany(mappedBy = "emergencyCall")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "createdBy", "emergencyCall", "event" }, allowSetters = true)
    private Set<SystemLog> systemLogs = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    private User createdBy;

    @ManyToOne
    @JsonIgnoreProperties(value = { "resources", "systemCalls", "systemLogs" }, allowSetters = true)
    private Event event;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public EmergencyCall id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getCreated() {
        return this.created;
    }

    public EmergencyCall created(ZonedDateTime created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public Boolean getOpen() {
        return this.open;
    }

    public EmergencyCall open(Boolean open) {
        this.setOpen(open);
        return this;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    public CallCategory getType() {
        return this.type;
    }

    public EmergencyCall type(CallCategory type) {
        this.setType(type);
        return this;
    }

    public void setType(CallCategory type) {
        this.type = type;
    }

    public Integer getAge() {
        return this.age;
    }

    public EmergencyCall age(Integer age) {
        this.setAge(age);
        return this;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Sex getSexAssignedAtBirth() {
        return this.sexAssignedAtBirth;
    }

    public EmergencyCall sexAssignedAtBirth(Sex sexAssignedAtBirth) {
        this.setSexAssignedAtBirth(sexAssignedAtBirth);
        return this;
    }

    public void setSexAssignedAtBirth(Sex sexAssignedAtBirth) {
        this.sexAssignedAtBirth = sexAssignedAtBirth;
    }

    public String getHistory() {
        return this.history;
    }

    public EmergencyCall history(String history) {
        this.setHistory(history);
        return this;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public String getInjuries() {
        return this.injuries;
    }

    public EmergencyCall injuries(String injuries) {
        this.setInjuries(injuries);
        return this;
    }

    public void setInjuries(String injuries) {
        this.injuries = injuries;
    }

    public String getCondition() {
        return this.condition;
    }

    public EmergencyCall condition(String condition) {
        this.setCondition(condition);
        return this;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Float getLatitude() {
        return this.latitude;
    }

    public EmergencyCall latitude(Float latitude) {
        this.setLatitude(latitude);
        return this;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return this.longitude;
    }

    public EmergencyCall longitude(Float longitude) {
        this.setLongitude(longitude);
        return this;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public Boolean getClosed() {
        return this.closed;
    }

    public EmergencyCall closed(Boolean closed) {
        this.setClosed(closed);
        return this;
    }

    public void setClosed(Boolean closed) {
        this.closed = closed;
    }

    public Set<ResourceAssigned> getResourceAssigneds() {
        return this.resourceAssigneds;
    }

    public void setResourceAssigneds(Set<ResourceAssigned> resourceAssigneds) {
        if (this.resourceAssigneds != null) {
            this.resourceAssigneds.forEach(i -> i.setEmergencyCall(null));
        }
        if (resourceAssigneds != null) {
            resourceAssigneds.forEach(i -> i.setEmergencyCall(this));
        }
        this.resourceAssigneds = resourceAssigneds;
    }

    public EmergencyCall resourceAssigneds(Set<ResourceAssigned> resourceAssigneds) {
        this.setResourceAssigneds(resourceAssigneds);
        return this;
    }

    public EmergencyCall addResourceAssigned(ResourceAssigned resourceAssigned) {
        this.resourceAssigneds.add(resourceAssigned);
        resourceAssigned.setEmergencyCall(this);
        return this;
    }

    public EmergencyCall removeResourceAssigned(ResourceAssigned resourceAssigned) {
        this.resourceAssigneds.remove(resourceAssigned);
        resourceAssigned.setEmergencyCall(null);
        return this;
    }

    public Set<SystemLog> getSystemLogs() {
        return this.systemLogs;
    }

    public void setSystemLogs(Set<SystemLog> systemLogs) {
        if (this.systemLogs != null) {
            this.systemLogs.forEach(i -> i.setEmergencyCall(null));
        }
        if (systemLogs != null) {
            systemLogs.forEach(i -> i.setEmergencyCall(this));
        }
        this.systemLogs = systemLogs;
    }

    public EmergencyCall systemLogs(Set<SystemLog> systemLogs) {
        this.setSystemLogs(systemLogs);
        return this;
    }

    public EmergencyCall addSystemLog(SystemLog systemLog) {
        this.systemLogs.add(systemLog);
        systemLog.setEmergencyCall(this);
        return this;
    }

    public EmergencyCall removeSystemLog(SystemLog systemLog) {
        this.systemLogs.remove(systemLog);
        systemLog.setEmergencyCall(null);
        return this;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    public EmergencyCall createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    public Event getEvent() {
        return this.event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public EmergencyCall event(Event event) {
        this.setEvent(event);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EmergencyCall)) {
            return false;
        }
        return id != null && id.equals(((EmergencyCall) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EmergencyCall{" +
            "id=" + getId() +
            ", created='" + getCreated() + "'" +
            ", open='" + getOpen() + "'" +
            ", type='" + getType() + "'" +
            ", age=" + getAge() +
            ", sexAssignedAtBirth='" + getSexAssignedAtBirth() + "'" +
            ", history='" + getHistory() + "'" +
            ", injuries='" + getInjuries() + "'" +
            ", condition='" + getCondition() + "'" +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            ", closed='" + getClosed() + "'" +
            "}";
    }
}
