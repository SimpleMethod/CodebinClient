package pl.simplemethod.codebin.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(unique = true, nullable = false)
    private String token;

    @Column(unique = true)
    private String subscription;

    @OneToMany
    List<Containers> containers = new ArrayList<>();
}
