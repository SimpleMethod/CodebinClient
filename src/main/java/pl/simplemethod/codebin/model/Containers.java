package pl.simplemethod.codebin.model;

import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "containers")
public class Containers implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NonNull
    @Column
    private String name;

    @NonNull
    @Column(name = "id_docker")
    private String idDocker;

    @NonNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "images_id", referencedColumnName = "id")
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
    @Column(name = "status")
    private Integer status;

    @NonNull
    @Column(name = "create_time")
    private Long createTime;

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

    public String getIdDocker() {
        return idDocker;
    }

    public void setIdDocker(String idDocker) {
        this.idDocker = idDocker;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Containers(String name, String idDocker, Images image, Integer exposedPorts, Integer hostPorts, Long ramMemory, Long diskQuota, Integer status, Long createTime) {
        this.name = name;
        this.idDocker = idDocker;
        this.image = image;
        this.exposedPorts = exposedPorts;
        this.hostPorts = hostPorts;
        this.ramMemory = ramMemory;
        this.diskQuota = diskQuota;
        this.status = status;
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Containers{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", docker_id='" + idDocker + '\'' +
                ", image=" + image +
                ", exposedPorts=" + exposedPorts +
                ", hostPorts=" + hostPorts +
                ", ramMemory=" + ramMemory +
                ", diskQuota=" + diskQuota +
                ", status=" + status +
                ", createTime=" + createTime +
                '}';
    }
}
