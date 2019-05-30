package pl.simplemethod.codebin.model;

import org.springframework.lang.NonNull;

import javax.persistence.*;

@Entity
@Table(name = "images")
public class Images {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_images")
    private Integer id;

    @NonNull
    @Column(name = "name_with_tag")
    private String name;

    @NonNull
    @Column(name = "type")
    private String type;

    @NonNull
    @Column(name = "docker_id")
    private String docker_id;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDocker_id() {
        return docker_id;
    }

    public void setDocker_id(String docker_id) {
        this.docker_id = docker_id;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Images(String name, String type, String docker_id, Long createTime) {
        this.name = name;
        this.type = type;
        this.docker_id = docker_id;
        this.createTime = createTime;
    }
}
