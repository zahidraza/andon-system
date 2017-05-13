package in.andonsystem.v2.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * Created by razamd on 3/31/2017.
 */

@Entity
public class Issue implements Comparable<Issue>{

    @Id
    private Long id;

    private Long buyerId;

    @ToOne(joinProperty = "buyerId")
    private Buyer buyer;

    private String problem;

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

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 724440415)
    private transient IssueDao myDao;


    @Generated(hash = 336750181)
    public Issue(Long id, Long buyerId, String problem, String description, Long raisedBy, Long ackBy, Long fixBy,
            Date raisedAt, Date ackAt, Date fixAt, Integer processingAt) {
        this.id = id;
        this.buyerId = buyerId;
        this.problem = problem;
        this.description = description;
        this.raisedBy = raisedBy;
        this.ackBy = ackBy;
        this.fixBy = fixBy;
        this.raisedAt = raisedAt;
        this.ackAt = ackAt;
        this.fixAt = fixAt;
        this.processingAt = processingAt;
    }

    public Issue(Long id, Long buyerId, String problem, String description, Date raisedAt, Date ackAt, Date fixAt, Integer processingAt) {
        this.id = id;
        this.buyerId = buyerId;
        this.problem = problem;
        this.description = description;
        this.raisedAt = raisedAt;
        this.ackAt = ackAt;
        this.fixAt = fixAt;
        this.processingAt = processingAt;
    }

    @Generated(hash = 596101413)
    public Issue() {
    }


    @Generated(hash = 1856800855)
    private transient Long buyer__resolvedKey;

    @Generated(hash = 321767753)
    private transient Long raisedByUser__resolvedKey;

    @Generated(hash = 1045371704)
    private transient Long ackByUser__resolvedKey;

    @Generated(hash = 1617456049)
    private transient Long fixByUser__resolvedKey;

    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public Long getBuyerId() {
        return this.buyerId;
    }


    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }


    public String getProblem() {
        return this.problem;
    }


    public void setProblem(String problem) {
        this.problem = problem;
    }


    public String getDescription() {
        return this.description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public Long getRaisedBy() {
        return this.raisedBy;
    }


    public void setRaisedBy(Long raisedBy) {
        this.raisedBy = raisedBy;
    }


    public Long getAckBy() {
        return this.ackBy;
    }


    public void setAckBy(Long ackBy) {
        this.ackBy = ackBy;
    }


    public Long getFixBy() {
        return this.fixBy;
    }


    public void setFixBy(Long fixBy) {
        this.fixBy = fixBy;
    }


    public Date getRaisedAt() {
        return this.raisedAt;
    }


    public void setRaisedAt(Date raisedAt) {
        this.raisedAt = raisedAt;
    }


    public Date getAckAt() {
        return this.ackAt;
    }


    public void setAckAt(Date ackAt) {
        this.ackAt = ackAt;
    }


    public Date getFixAt() {
        return this.fixAt;
    }


    public void setFixAt(Date fixAt) {
        this.fixAt = fixAt;
    }


    /** To-one relationship, resolved on first access. */
    @Generated(hash = 2133095854)
    public Buyer getBuyer() {
        Long __key = this.buyerId;
        if (buyer__resolvedKey == null || !buyer__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            BuyerDao targetDao = daoSession.getBuyerDao();
            Buyer buyerNew = targetDao.load(__key);
            synchronized (this) {
                buyer = buyerNew;
                buyer__resolvedKey = __key;
            }
        }
        return buyer;
    }


    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1404955818)
    public void setBuyer(Buyer buyer) {
        synchronized (this) {
            this.buyer = buyer;
            buyerId = buyer == null ? null : buyer.getId();
            buyer__resolvedKey = buyerId;
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

    /**
     * flag = 0, raised state
     * flag = 1, acknowledged state
     * flag = 2, fixed state
     * @param other
     * @return
     */
    @Override
    public int compareTo(Issue other) {
        int flag1 = 0;
        if(this.fixAt != null) flag1 = 2;
        else if(this.ackAt != null) flag1 = 1;

        int flag2 = 0;
        if(other.fixAt != null) flag2 = 2;
        else if(other.ackAt != null) flag2 = 1;

        int result = flag1 - flag2;
        if(result == 0){
            result = (int)(this.getId() - other.getId());
        }
        return result;
    }

    public Integer getProcessingAt() {
        return this.processingAt;
    }

    public void setProcessingAt(Integer processingAt) {
        this.processingAt = processingAt;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 884668014)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getIssueDao() : null;
    }
}
