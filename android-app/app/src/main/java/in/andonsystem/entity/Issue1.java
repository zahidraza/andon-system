package in.andonsystem.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * Created by mdzahidraza on 18/06/17.
 */

@Entity
public class Issue1 {
    @Id
    private Long id;

    private Integer line;

    private String section;

    private Long problemId;

    @ToOne(joinProperty = "problemId")
    private Problem problem;

    private String critical;

    private String operatorNo;

    private String description;

    private Long raisedBy;

    @ToOne(joinProperty = "raisedBy")
    private User raisedByUser;

    private Long ackBy;

    @ToOne(joinProperty = "ackBy")
    private User ackByUser;

    private Long fixBy;

    @ToOne(joinProperty = "fixBy")
    private User fixByUser;

    private Date raisedAt;

    private Date ackAt;

    private Date fixAt;

    private Integer processingAt;

    private Integer seekHelp;

    private Boolean deleted;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 118865237)
    private transient Issue1Dao myDao;

    @Generated(hash = 21907614)
    public Issue1(Long id, Integer line, String section, Long problemId,
            String critical, String operatorNo, String description, Long raisedBy,
            Long ackBy, Long fixBy, Date raisedAt, Date ackAt, Date fixAt,
            Integer processingAt, Integer seekHelp, Boolean deleted) {
        this.id = id;
        this.line = line;
        this.section = section;
        this.problemId = problemId;
        this.critical = critical;
        this.operatorNo = operatorNo;
        this.description = description;
        this.raisedBy = raisedBy;
        this.ackBy = ackBy;
        this.fixBy = fixBy;
        this.raisedAt = raisedAt;
        this.ackAt = ackAt;
        this.fixAt = fixAt;
        this.processingAt = processingAt;
        this.seekHelp = seekHelp;
        this.deleted = deleted;
    }

    public Issue1(Long id, Integer line, String section, Long problemId, String critical, String operatorNo, String description,
                  Long raisedBy, Date raisedAt, Integer processingAt, Integer seekHelp, Boolean deleted) {
        this.id = id;
        this.line = line;
        this.section = section;
        this.problemId = problemId;
        this.critical = critical;
        this.operatorNo = operatorNo;
        this.description = description;
        this.raisedBy = raisedBy;
        this.raisedAt = raisedAt;
        this.processingAt = processingAt;
        this.seekHelp = seekHelp;
        this.deleted = deleted;
    }

    @Generated(hash = 1510195716)
    public Issue1() {
    }

    @Generated(hash = 1128556837)
    private transient Long problem__resolvedKey;

    @Generated(hash = 321767753)
    private transient Long raisedByUser__resolvedKey;

    @Generated(hash = 1045371704)
    private transient Long ackByUser__resolvedKey;

    @Generated(hash = 1617456049)
    private transient Long fixByUser__resolvedKey;

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

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1726272046)
    public Problem getProblem() {
        Long __key = this.problemId;
        if (problem__resolvedKey == null || !problem__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ProblemDao targetDao = daoSession.getProblemDao();
            Problem problemNew = targetDao.load(__key);
            synchronized (this) {
                problem = problemNew;
                problem__resolvedKey = __key;
            }
        }
        return problem;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 402020458)
    public void setProblem(Problem problem) {
        synchronized (this) {
            this.problem = problem;
            problemId = problem == null ? null : problem.getId();
            problem__resolvedKey = problemId;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1997197928)
    public User getRaisedByUser() {
        Long __key = this.raisedBy;
        if (raisedByUser__resolvedKey == null
                || !raisedByUser__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserDao targetDao = daoSession.getUserDao();
            User raisedByUserNew = targetDao.load(__key);
            synchronized (this) {
                raisedByUser = raisedByUserNew;
                raisedByUser__resolvedKey = __key;
            }
        }
        return raisedByUser;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1092426669)
    public void setRaisedByUser(User raisedByUser) {
        synchronized (this) {
            this.raisedByUser = raisedByUser;
            raisedBy = raisedByUser == null ? null : raisedByUser.getId();
            raisedByUser__resolvedKey = raisedBy;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1844316738)
    public User getAckByUser() {
        Long __key = this.ackBy;
        if (ackByUser__resolvedKey == null
                || !ackByUser__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserDao targetDao = daoSession.getUserDao();
            User ackByUserNew = targetDao.load(__key);
            synchronized (this) {
                ackByUser = ackByUserNew;
                ackByUser__resolvedKey = __key;
            }
        }
        return ackByUser;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 642713081)
    public void setAckByUser(User ackByUser) {
        synchronized (this) {
            this.ackByUser = ackByUser;
            ackBy = ackByUser == null ? null : ackByUser.getId();
            ackByUser__resolvedKey = ackBy;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1372295253)
    public User getFixByUser() {
        Long __key = this.fixBy;
        if (fixByUser__resolvedKey == null
                || !fixByUser__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserDao targetDao = daoSession.getUserDao();
            User fixByUserNew = targetDao.load(__key);
            synchronized (this) {
                fixByUser = fixByUserNew;
                fixByUser__resolvedKey = __key;
            }
        }
        return fixByUser;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 657129114)
    public void setFixByUser(User fixByUser) {
        synchronized (this) {
            this.fixByUser = fixByUser;
            fixBy = fixByUser == null ? null : fixByUser.getId();
            fixByUser__resolvedKey = fixBy;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    @Override
    public String toString() {
        return "Issue1{" +
                "id=" + id +
                ", line=" + line +
                ", section='" + section + '\'' +
                ", problemId=" + problemId +
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
                ", deleted=" + deleted +
                '}';
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 844710448)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getIssue1Dao() : null;
    }
}
