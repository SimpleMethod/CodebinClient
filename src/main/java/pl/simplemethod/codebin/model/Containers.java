package pl.simplemethod.codebin.model;

import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "containers")
public class Containers implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_container")
    private Integer id;

    @NonNull
    @Column
    private String name;

    @NonNull
    @OneToOne
    private Images image;

    @NonNull
    @Column(name = "exposed_ports")
    private Integer exposedPorts;

    @NonNull
    @Column(name = "host_ports")
    private Integer hostPorts;

    @NonNull
    @Column(name = "ram_memory")
    private Long ramMemory;

    @NonNull
    @Column(name = "disk_quota")
    private Long diskQuota;

    @NonNull
    @Column(name = "create_time")
    private Long createTime;

    @NonNull
    @Column(name = "premium_status")
    private Boolean premiumStatus;

    @NonNull
    @Column
    private String type;

    @NonNull
    @Column(name = "status")
    private Integer status;

    @NonNull
    @Column(name = "docker_id")
    private String docker_id;

    public Containers(String name, Images image, Integer exposedPorts, Integer hostPorts, Long ramMemory, Long diskQuota, Long createTime, Boolean premiumStatus, String type, Integer status, String docker_id) {
        this.name = name;
        this.image = image;
        this.exposedPorts = exposedPorts;
        this.hostPorts = hostPorts;
        this.ramMemory = ramMemory;
        this.diskQuota = diskQuota;
        this.createTime = createTime;
        this.premiumStatus = premiumStatus;
        this.type = type;
        this.status = status;
        this.docker_id = docker_id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Images getImage() {
        return image;
    }

    public void setImage(Images image) {
        this.image = image;
    }

    public Integer getExposedPorts() {
        return exposedPorts;
    }

    public void setExposedPorts(Integer exposedPorts) {
        this.exposedPorts = exposedPorts;
    }

    public Integer getHostPorts() {
        return hostPorts;
    }

    public void setHostPorts(Integer hostPorts) {
        this.hostPorts = hostPorts;
    }

    public Long getRamMemory() {
        return ramMemory;
    }

    public void setRamMemory(Long ramMemory) {
        this.ramMemory = ramMemory;
    }

    public Long getDiskQuota() {
        return diskQuota;
    }

    public void setDiskQuota(Long diskQuota) {
        this.diskQuota = diskQuota;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Boolean getPremiumStatus() {
        return premiumStatus;
    }

    public void setPremiumStatus(Boolean premiumStatus) {
        this.premiumStatus = premiumStatus;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDocker_id() {
        return docker_id;
    }

    public void setDocker_id(String docker_id) {
        this.docker_id = docker_id;
    }
}
