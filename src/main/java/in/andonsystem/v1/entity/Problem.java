package in.andonsystem.v1.entity;

import in.andonsystem.v2.validation.DepartmentFixedValue;
import in.andonsystem.v2.validation.Fixed;

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
 * Created by razamd on 4/4/2017.
 */
@Entity
@Table(name = "PROBLEM")
public class Problem implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROB_ID")
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @NotNull @Fixed(fixClass = DepartmentFixedValue.class)
    @Column(name = "DEPARTMENT", nullable = false)
    private String department;

    @ManyToMany(mappedBy = "problems")
    private Set<Designation> designations = new HashSet<>();

    public Problem() {
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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Set<Designation> getDesignations() {
        return designations;
    }

    public void setDesignations(Set<Designation> designations) {
        this.designations = designations;
    }

    @Override
    public String toString() {
        return "Problem{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", department='" + department + '\'' +
               '}';
    }
}
