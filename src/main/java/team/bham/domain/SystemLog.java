package team.bham.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import team.bham.domain.enumeration.LogType;

/**
 * A SystemLog.
 */
@Entity
@Table(name = "system_log")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SystemLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "log_type", nullable = false)
    private LogType logType;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "message_content", nullable = false)
    private String messageContent;

    @ManyToOne(optional = false)
    @NotNull
    private User createdBy;

    @ManyToOne
    @JsonIgnoreProperties(value = { "resourceAssigneds", "systemLogs", "createdBy", "event" }, allowSetters = true)
    private EmergencyCall emergencyCall;

    @ManyToOne
    @JsonIgnoreProperties(value = { "resources", "systemCalls", "systemLogs" }, allowSetters = true)
    private Event event;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SystemLog id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public SystemLog createdAt(ZonedDateTime createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LogType getLogType() {
        return this.logType;
    }

    public SystemLog logType(LogType logType) {
        this.setLogType(logType);
        return this;
    }

    public void setLogType(LogType logType) {
        this.logType = logType;
    }

    public String getMessageContent() {
        return this.messageContent;
    }

    public SystemLog messageContent(String messageContent) {
        this.setMessageContent(messageContent);
        return this;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    public SystemLog createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    public EmergencyCall getEmergencyCall() {
        return this.emergencyCall;
    }

    public void setEmergencyCall(EmergencyCall emergencyCall) {
        this.emergencyCall = emergencyCall;
    }

    public SystemLog emergencyCall(EmergencyCall emergencyCall) {
        this.setEmergencyCall(emergencyCall);
        return this;
    }

    public Event getEvent() {
        return this.event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public SystemLog event(Event event) {
        this.setEvent(event);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SystemLog)) {
            return false;
        }
        return id != null && id.equals(((SystemLog) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SystemLog{" +
            "id=" + getId() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", logType='" + getLogType() + "'" +
            ", messageContent='" + getMessageContent() + "'" +
            "}";
    }
}
