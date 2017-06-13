package in.andonsystem.v1.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import in.andonsystem.v2.entity.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by razamd on 4/4/2017.
 */
@Entity
@Table(name = "DESIGNATION")
public class Designation implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DESGN_ID")
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "LINE", nullable = false)
    private String lines;

    @Column(name = "level")
    private Integer level;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "DESIGNATION_PROBLEM",
            joinColumns = @JoinColumn(name = "DESGN_ID"),
            inverseJoinColumns = @JoinColumn(name = "PROB_ID")
    )
    private Set<Problem> problems = new HashSet<>();

    @OneToMany(mappedBy = "designation")
    private Set<User> users = new HashSet<>();

    public Designation() {
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
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

    public List<Integer> getLines() {
        List<Integer> result = new ArrayList<>();
        if(lines == null) return result;

        String[] line = lines.split(",");
        for (String l: line){
            result.add(Integer.parseInt(l));
        }
        return result;
    }

    public void setLines(String lines) {
        this.lines = lines;
    }

    public Set<Problem> getProblems() {
        return problems;
    }

    public void setProblems(Set<Problem> problems) {
        this.problems = problems;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
