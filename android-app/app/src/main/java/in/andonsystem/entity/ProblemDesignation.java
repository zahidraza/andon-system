package in.andonsystem.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by mdzahidraza on 18/06/17.
 */

@Entity
public class ProblemDesignation {

    @Id(autoincrement = true)
    private Long id;
    private Long problemId;
    private Long desgnId;


    @Generated(hash = 2049678383)
    public ProblemDesignation(Long id, Long problemId, Long desgnId) {
        this.id = id;
        this.problemId = problemId;
        this.desgnId = desgnId;
    }

    @Generated(hash = 1419113674)
    public ProblemDesignation() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProblemId() {
        return problemId;
    }

    public void setProblemId(Long problemId) {
        this.problemId = problemId;
    }

    public Long getDesgnId() {
        return desgnId;
    }

    public void setDesgnId(Long desgnId) {
        this.desgnId = desgnId;
    }
}
