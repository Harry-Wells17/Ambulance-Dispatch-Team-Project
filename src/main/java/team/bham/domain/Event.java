package team.bham.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

/**
 * A Event.
 */
@Entity
@Table(name = "event")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "created", nullable = false)
    private String created;

    @NotNull
    @Column(name = "event_name", nullable = false)
    private String eventName;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "event_description", nullable = false)
    private String eventDescription;

    @OneToMany(mappedBy = "event")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "resourceBreak", "event" }, allowSetters = true)
    private Set<Resource> resources = new HashSet<>();

    @OneToMany(mappedBy = "event")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "resourceAssigneds", "systemLogs", "createdBy", "event" }, allowSetters = true)
    private Set<EmergencyCall> systemCalls = new HashSet<>();

    @OneToMany(mappedBy = "event")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "createdBy", "emergencyCall", "event" }, allowSetters = true)
    private Set<SystemLog> systemLogs = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Event id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreated() {
        return this.created;
    }

    public Event created(String created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getEventName() {
        return this.eventName;
    }

    public Event eventName(String eventName) {
        this.setEventName(eventName);
        return this;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDescription() {
        return this.eventDescription;
    }

    public Event eventDescription(String eventDescription) {
        this.setEventDescription(eventDescription);
        return this;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public Set<Resource> getResources() {
        return this.resources;
    }

    public void setResources(Set<Resource> resources) {
        if (this.resources != null) {
            this.resources.forEach(i -> i.setEvent(null));
        }
        if (resources != null) {
            resources.forEach(i -> i.setEvent(this));
        }
        this.resources = resources;
    }

    public Event resources(Set<Resource> resources) {
        this.setResources(resources);
        return this;
    }

    public Event addResources(Resource resource) {
        this.resources.add(resource);
        resource.setEvent(this);
        return this;
    }

    public Event removeResources(Resource resource) {
        this.resources.remove(resource);
        resource.setEvent(null);
        return this;
    }

    public Set<EmergencyCall> getSystemCalls() {
        return this.systemCalls;
    }

    public void setSystemCalls(Set<EmergencyCall> emergencyCalls) {
        if (this.systemCalls != null) {
            this.systemCalls.forEach(i -> i.setEvent(null));
        }
        if (emergencyCalls != null) {
            emergencyCalls.forEach(i -> i.setEvent(this));
        }
        this.systemCalls = emergencyCalls;
    }

    public Event systemCalls(Set<EmergencyCall> emergencyCalls) {
        this.setSystemCalls(emergencyCalls);
        return this;
    }

    public Event addSystemCalls(EmergencyCall emergencyCall) {
        this.systemCalls.add(emergencyCall);
        emergencyCall.setEvent(this);
        return this;
    }

    public Event removeSystemCalls(EmergencyCall emergencyCall) {
        this.systemCalls.remove(emergencyCall);
        emergencyCall.setEvent(null);
        return this;
    }

    public Set<SystemLog> getSystemLogs() {
        return this.systemLogs;
    }

    public void setSystemLogs(Set<SystemLog> systemLogs) {
        if (this.systemLogs != null) {
            this.systemLogs.forEach(i -> i.setEvent(null));
        }
        if (systemLogs != null) {
            systemLogs.forEach(i -> i.setEvent(this));
        }
        this.systemLogs = systemLogs;
    }

    public Event systemLogs(Set<SystemLog> systemLogs) {
        this.setSystemLogs(systemLogs);
        return this;
    }

    public Event addSystemLog(SystemLog systemLog) {
        this.systemLogs.add(systemLog);
        systemLog.setEvent(this);
        return this;
    }

    public Event removeSystemLog(SystemLog systemLog) {
        this.systemLogs.remove(systemLog);
        systemLog.setEvent(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Event)) {
            return false;
        }
        return id != null && id.equals(((Event) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Event{" +
            "id=" + getId() +
            ", created='" + getCreated() + "'" +
            ", eventName='" + getEventName() + "'" +
            ", eventDescription='" + getEventDescription() + "'" +
            "}";
    }
}
