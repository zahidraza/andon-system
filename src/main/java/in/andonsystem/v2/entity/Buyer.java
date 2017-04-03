package in.andonsystem.v2.entity;

import in.andonsystem.v2.validation.Fixed;
import in.andonsystem.v2.validation.TeamFixedValue;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by razamd on 3/30/2017.
 */
@Entity
@Table(name = "BUYER")
public class Buyer implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "buyer_id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull @Fixed(fixClass = TeamFixedValue.class)
    @Column(name = "team", nullable = false)
    private String team;

    @ManyToMany(mappedBy = "buyers")
    private Set<User> users = new HashSet<>();

    public Buyer() {
    }

    public Buyer(Long id) {
        this.id = id;
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

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "Buyer{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", team='" + team + '\'' +
               ", users=" + users +
               '}';
    }
}
