package team.bham.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ResourceAssigned.
 */
@Entity
@Table(name = "resource_assigned")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ResourceAssigned implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "call_recieved_time", nullable = false)
    private ZonedDateTime callRecievedTime;

    @Column(name = "on_scene_time")
    private ZonedDateTime onSceneTime;

    @Column(name = "left_scene_time")
    private ZonedDateTime leftSceneTime;

    @Column(name = "arrived_hospital_time")
    private ZonedDateTime arrivedHospitalTime;

    @Column(name = "clear_hospital_time")
    private ZonedDateTime clearHospitalTime;

    @Column(name = "green_time")
    private ZonedDateTime greenTime;

    @Column(name = "un_assigned_time")
    private ZonedDateTime unAssignedTime;

    @ManyToOne
    @JsonIgnoreProperties(value = { "resourceBreak", "event" }, allowSetters = true)
    private Resource resource;

    @ManyToOne
    @JsonIgnoreProperties(value = { "resourceAssigneds", "systemLogs", "createdBy", "event" }, allowSetters = true)
    private EmergencyCall emergencyCall;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ResourceAssigned id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getCallRecievedTime() {
        return this.callRecievedTime;
    }

    public ResourceAssigned callRecievedTime(ZonedDateTime callRecievedTime) {
        this.setCallRecievedTime(callRecievedTime);
        return this;
    }

    public void setCallRecievedTime(ZonedDateTime callRecievedTime) {
        this.callRecievedTime = callRecievedTime;
    }

    public ZonedDateTime getOnSceneTime() {
        return this.onSceneTime;
    }

    public ResourceAssigned onSceneTime(ZonedDateTime onSceneTime) {
        this.setOnSceneTime(onSceneTime);
        return this;
    }

    public void setOnSceneTime(ZonedDateTime onSceneTime) {
        this.onSceneTime = onSceneTime;
    }

    public ZonedDateTime getLeftSceneTime() {
        return this.leftSceneTime;
    }

    public ResourceAssigned leftSceneTime(ZonedDateTime leftSceneTime) {
        this.setLeftSceneTime(leftSceneTime);
        return this;
    }

    public void setLeftSceneTime(ZonedDateTime leftSceneTime) {
        this.leftSceneTime = leftSceneTime;
    }

    public ZonedDateTime getArrivedHospitalTime() {
        return this.arrivedHospitalTime;
    }

    public ResourceAssigned arrivedHospitalTime(ZonedDateTime arrivedHospitalTime) {
        this.setArrivedHospitalTime(arrivedHospitalTime);
        return this;
    }

    public void setArrivedHospitalTime(ZonedDateTime arrivedHospitalTime) {
        this.arrivedHospitalTime = arrivedHospitalTime;
    }

    public ZonedDateTime getClearHospitalTime() {
        return this.clearHospitalTime;
    }

    public ResourceAssigned clearHospitalTime(ZonedDateTime clearHospitalTime) {
        this.setClearHospitalTime(clearHospitalTime);
        return this;
    }

    public void setClearHospitalTime(ZonedDateTime clearHospitalTime) {
        this.clearHospitalTime = clearHospitalTime;
    }

    public ZonedDateTime getGreenTime() {
        return this.greenTime;
    }

    public ResourceAssigned greenTime(ZonedDateTime greenTime) {
        this.setGreenTime(greenTime);
        return this;
    }

    public void setGreenTime(ZonedDateTime greenTime) {
        this.greenTime = greenTime;
    }

    public ZonedDateTime getUnAssignedTime() {
        return this.unAssignedTime;
    }

    public ResourceAssigned unAssignedTime(ZonedDateTime unAssignedTime) {
        this.setUnAssignedTime(unAssignedTime);
        return this;
    }

    public void setUnAssignedTime(ZonedDateTime unAssignedTime) {
        this.unAssignedTime = unAssignedTime;
    }

    public Resource getResource() {
        return this.resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public ResourceAssigned resource(Resource resource) {
        this.setResource(resource);
        return this;
    }

    public EmergencyCall getEmergencyCall() {
        return this.emergencyCall;
    }

    public void setEmergencyCall(EmergencyCall emergencyCall) {
        this.emergencyCall = emergencyCall;
    }

    public ResourceAssigned emergencyCall(EmergencyCall emergencyCall) {
        this.setEmergencyCall(emergencyCall);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResourceAssigned)) {
            return false;
        }
        return id != null && id.equals(((ResourceAssigned) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ResourceAssigned{" +
            "id=" + getId() +
            ", callRecievedTime='" + getCallRecievedTime() + "'" +
            ", onSceneTime='" + getOnSceneTime() + "'" +
            ", leftSceneTime='" + getLeftSceneTime() + "'" +
            ", arrivedHospitalTime='" + getArrivedHospitalTime() + "'" +
            ", clearHospitalTime='" + getClearHospitalTime() + "'" +
            ", greenTime='" + getGreenTime() + "'" +
            ", unAssignedTime='" + getUnAssignedTime() + "'" +
            "}";
    }
}
