package in.andonsystem.v1.dto;

import in.andonsystem.v1.entity.Problem;
import in.andonsystem.v2.entity.User;
import in.andonsystem.validation.Fixed;
import in.andonsystem.validation.SectionFixedValue;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by mdzahidraza on 16/06/17.
 */
public class IssueDto {

    private Long id;

    @NotNull
    @DecimalMax(value = "8")
    @DecimalMin(value = "1")
    private Integer line;

    @NotEmpty
    @Fixed(fixClass = SectionFixedValue.class)
    private String section;

    @NotNull
    private Long problemId;

    private String critical;

    private String operatorNo;

    private String description;

    @NotNull
    private Long raisedBy;

    private Long ackBy;

    private Long fixBy;

    private Long raisedAt;

    private Long ackAt;

    private Long fixAt;

    private Integer processingAt;

    private Integer seekHelp;

    private Boolean deleted;

    private Long lastModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLine() {
        return line;
    }

    public void setLine(Integer line) {
        this.line = line;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public Long getProblemId() {
        return problemId;
    }

    public void setProblemId(Long problemId) {
        this.problemId = problemId;
    }

    public String getCritical() {
        return critical;
    }

    public void setCritical(String critical) {
        this.critical = critical;
    }

    public String getOperatorNo() {
        return operatorNo;
    }

    public void setOperatorNo(String operatorNo) {
        this.operatorNo = operatorNo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getRaisedBy() {
        return raisedBy;
    }

    public void setRaisedBy(Long raisedBy) {
        this.raisedBy = raisedBy;
    }

    public Long getAckBy() {
        return ackBy;
    }

    public void setAckBy(Long ackBy) {
        this.ackBy = ackBy;
    }

    public Long getFixBy() {
        return fixBy;
    }

    public void setFixBy(Long fixBy) {
        this.fixBy = fixBy;
    }

    public Long getRaisedAt() {
        return raisedAt;
    }

    public void setRaisedAt(Long raisedAt) {
        this.raisedAt = raisedAt;
    }

    public Long getAckAt() {
        return ackAt;
    }

    public void setAckAt(Long ackAt) {
        this.ackAt = ackAt;
    }

    public Long getFixAt() {
        return fixAt;
    }

    public void setFixAt(Long fixAt) {
        this.fixAt = fixAt;
    }

    public Integer getProcessingAt() {
        return processingAt;
    }

    public void setProcessingAt(Integer processingAt) {
        this.processingAt = processingAt;
    }

    public Integer getSeekHelp() {
        return seekHelp;
    }

    public void setSeekHelp(Integer seekHelp) {
        this.seekHelp = seekHelp;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Long getLastModified() {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }
}
