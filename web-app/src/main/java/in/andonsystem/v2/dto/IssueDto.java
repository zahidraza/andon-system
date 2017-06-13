package in.andonsystem.v2.dto;

import in.andonsystem.v2.validation.Fixed;
import in.andonsystem.v2.validation.ProblemFixedValue;
import javax.validation.constraints.NotNull;

/**
 * Created by razamd on 3/30/2017.
 */
public class IssueDto {

    private Long id;

    @NotNull
    private Long buyerId;

    @NotNull
    @Fixed(fixClass = ProblemFixedValue.class)
    private String problem;

    @NotNull
    private String description;

    @NotNull
    private Long raisedBy;

    private Long ackBy;

    private Long fixBy;

    private Long raisedAt;

    private Long ackAt;

    private Long fixAt;

    private Integer processingAt;

    private Boolean deleted;

    private Long lastModified;

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
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

    public Long getLastModified() {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
