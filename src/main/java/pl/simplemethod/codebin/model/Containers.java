package pl.simplemethod.codebin.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "containers")
public class Containers implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(name = "id_docker", unique = true, nullable = false)
    private String idDocker;

    @OneToOne()
    @JoinColumn(name = "id_images", referencedColumnName = "id")
    private Images image;

    @Column(name = "exposed_ports", nullable = false)
    private Integer exposedPorts;

    @Column(name = "host_ports", nullable = false)
    private Integer hostPorts;

    @Column(name = "ram_memory", nullable = false)
    private Long ramMemory;

    @Column(name = "disk_quota", nullable = false)
    private Long diskQuota;

    @Column(name = "status", nullable = false)
    private Integer status = 0;

    @Column(name = "create_time", nullable = false)
    private Long createTime;
    @Column(name = "share_url", unique = true, nullable = false)
    private String shareUrl;

    public Containers(String name, String idDocker, Images image, Integer exposedPorts, Integer hostPorts, Long ramMemory, Long diskQuota, String shareUrl, Integer status, Long createTime) {
        this.name = name;
        this.idDocker = idDocker;
        this.image = image;
        this.exposedPorts = exposedPorts;
        this.hostPorts = hostPorts;
        this.ramMemory = ramMemory;
        this.diskQuota = diskQuota;
        this.shareUrl = shareUrl;
        this.status = status;
        this.createTime = createTime;
    }

    public Containers() {
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    @Override
    public String toString() {
        return "Containers{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", idDocker='" + idDocker + '\'' +
                ", image=" + image +
                ", exposedPorts=" + exposedPorts +
                ", hostPorts=" + hostPorts +
                ", ramMemory=" + ramMemory +
                ", diskQuota=" + diskQuota +
                ", status=" + status +
                ", createTime=" + createTime +
                ", shareUrl='" + shareUrl + '\'' +
                '}';
    }

}
