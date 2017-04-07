package in.andonsystem.v2.entity;

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
import java.io.Serializable;
import java.util.Date;

/**
 * Created by razamd on 3/30/2017.
 */
@Entity
public class Issue2 implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "issue_id", nullable = false)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "buyer_id")
    private Buyer buyer;

    @Column(name = "problem", nullable = false)
    private String problem;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "raised_by")
    private User raisedBy;

    @ManyToOne
    @JoinColumn(name = "ack_by")
    private User ackBy;

    @ManyToOne
    @JoinColumn(name = "fix_by")
    private User fixBy;

    @Column(name = "raised_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date raisedAt;

    @Column(name = "ack_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ackAt;

    @Column(name = "fix_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fixAt;

    @Column(name = "processing_at")
    private Integer processingAt;

    @Version
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modified")
    private Date lastModified;

    public Issue2() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
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

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Issue1{" +
               "id=" + id +
               ", buyer=" + buyer +
               ", problem='" + problem + '\'' +
               ", description='" + description + '\'' +
               ", raisedBy=" + raisedBy +
               ", ackBy=" + ackBy +
               ", fixBy=" + fixBy +
               ", raisedAt=" + raisedAt +
               ", ackAt=" + ackAt +
               ", fixAt=" + fixAt +
               ", processingAt=" + processingAt +
               ", lastModified=" + lastModified +
               '}';
    }
}
