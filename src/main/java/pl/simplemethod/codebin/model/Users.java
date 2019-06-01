package pl.simplemethod.codebin.model;

import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class Users implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(unique = true, nullable = false)
    private String token;
    @Column(unique = true)
    private String subscription;
    @Column(name = "id_github", unique = true, nullable = false)
    private Integer github;
    @NonNull
    @Column(name = "role")
    private String role;

    @OneToMany
    private List<Containers> containers = new ArrayList<>();

    public Users() {
    }

    public Users(String token, Integer github,  String role, String subscription, List<Containers> containers) {
        this.containers = containers;
        this.token = token;
        this.subscription = subscription;
        this.github = github;
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Users{" +
                "containers=" + containers +
                ", id=" + id +
                ", token='" + token + '\'' +
                ", subscription='" + subscription + '\'' +
                ", github=" + github +
                ", role='" + role + '\'' +
                '}';
    }

    public List<Containers> getContainers() {
        return containers;
    }

    public void setContainers(List<Containers> containers) {
        this.containers = containers;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSubscription() {
        return subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    public Integer getGithub() {
        return github;
    }

    public void setGithub(Integer github) {
        this.github = github;
    }
}
