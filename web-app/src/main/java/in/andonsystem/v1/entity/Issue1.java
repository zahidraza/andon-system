package in.andonsystem.v1.entity;

import in.andonsystem.v2.entity.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import java.util.Date;

/**
 * Created by razamd on 4/5/2017.
 */
@Entity
@Table(name = "issue1")
public class Issue1 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ISSUE_ID")
    private Long id;

    @Column(name = "LINE", nullable = false)
    private Integer line;

    @ManyToOne(optional = false)
    @JoinColumn(name = "PROB_ID")
    private Problem problem;

    @Column(name = "CRITICAL", length = 3)
    private String critical;

    @Column(name = "OPERATOR_NO", length = 10)
    private String operatorNo;

    @Column(name = "DESCRIPTION")
    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "RAISED_BY")
    private User raisedBy;

    @ManyToOne
    @JoinColumn(name = "ACK_BY")
    private User ackBy;

    @ManyToOne
    @JoinColumn(name = "FIX_BY")
    private User fixBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "RAISED_AT")
    private Date raisedAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ACK_AT")
    private Date ackAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FIX_AT")
    private Date fixAt;

    @Column(name = "PROCESSING_AT")
    private Integer processingAt;

    @Column(name = "SEEK_HELP")
    private Integer seekHelp;

    @Column(name = "deleted",nullable = false)
    private Boolean deleted;

    @Version
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_MODIFIED")
    private Date lastModified;

    public Issue1() {
    }

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

    public Integer getLine() {
        return line;
    }

    public void setLine(Integer line) {
        this.line = line;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
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

    public User getRaisedBy() {
        return raisedBy;
    }

    public void setRaisedBy(User raisedBy) {
        this.raisedBy = raisedBy;
    }

    public User getAckBy() {
        return ackBy;
    }

    public void setAckBy(User ackBy) {
        this.ackBy = ackBy;
    }

    public User getFixBy() {
        return fixBy;
    }

    public void setFixBy(User fixBy) {
        this.fixBy = fixBy;
    }

    public Date getRaisedAt() {
        return raisedAt;
    }

    public void setRaisedAt(Date raisedAt) {
        this.raisedAt = raisedAt;
    }

    public Date getAckAt() {
        return ackAt;
    }

    public void setAckAt(Date ackAt) {
        this.ackAt = ackAt;
    }

    public Date getFixAt() {
        return fixAt;
    }

    public void setFixAt(Date fixAt) {
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

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public String toString() {
        return "Issue1{" +
               "id=" + id +
               ", line=" + line +
               ", problem=" + problem +
               ", critical='" + critical + '\'' +
               ", operatorNo='" + operatorNo + '\'' +
               ", description='" + description + '\'' +
               ", raisedBy=" + raisedBy +
               ", ackBy=" + ackBy +
               ", fixBy=" + fixBy +
               ", raisedAt=" + raisedAt +
               ", ackAt=" + ackAt +
               ", fixAt=" + fixAt +
               ", processingAt=" + processingAt +
               ", seekHelp=" + seekHelp +
               ", lastModified=" + lastModified +
               '}';
    }
}
