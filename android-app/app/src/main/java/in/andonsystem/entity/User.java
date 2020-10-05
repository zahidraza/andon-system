package in.andonsystem.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinEntity;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.ToOne;

/**
 * Created by razamd on 3/31/2017.
 */

@Entity
public class User {
    @Id
    private Long id;

    @NotNull
    @Property(nameInDb = "user_name")
    private String name;

    private String email;

    private String mobile;

    private String role;

    @Property(nameInDb = "user_type")
    private String userType;

    private String level;

    private Long desgnId;

    @ToOne(joinProperty = "desgnId")
    private Designation designation;

    @ToMany
    @JoinEntity(
            entity = UserBuyer.class,
            sourceProperty = "userId",
            targetProperty = "buyerId"
    )
    private List<Buyer> buyers;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1507654846)
    private transient UserDao myDao;

    @Generated(hash = 1433887650)
    private transient Long designation__resolvedKey;

    public User(Long id, @NotNull String name, String email, String mobile,
                String role, String userType, String level) {
        this.id = id;
        this.name = name;
        this.email = email != null ? email.toLowerCase() : null;
        this.mobile = mobile;
        this.role = role;
        this.userType = userType;
        this.level = level;
    }

    @Keep
    public User(Long id, @NotNull String name, String email, String mobile, String role,
            String userType, String level, Long desgnId) {
        this.id = id;
        this.name = name;
        this.email = email != null ? email.toLowerCase() : null;
        this.mobile = mobile;
        this.role = role;
        this.userType = userType;
        this.level = level;
        this.desgnId = desgnId;
    }

    @Generated(hash = 586692638)
    public User() {
    }



    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.toLowerCase() : null;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserType() {
        return this.userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getLevel() {
        return this.level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Long getDesgnId() {
        return this.desgnId;
    }

    public void setDesgnId(Long desgnId) {
        this.desgnId = desgnId;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 266990763)
    public List<Buyer> getBuyers() {
        if (buyers == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            BuyerDao targetDao = daoSession.getBuyerDao();
            List<Buyer> buyersNew = targetDao._queryUser_Buyers(id);
            synchronized (this) {
                if (buyers == null) {
                    buyers = buyersNew;
                }
            }
        }
        return buyers;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 718118335)
    public synchronized void resetBuyers() {
        buyers = null;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id.equals(user.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1683235924)
    public Designation getDesignation() {
        Long __key = this.desgnId;
        if (designation__resolvedKey == null || !designation__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DesignationDao targetDao = daoSession.getDesignationDao();
            Designation designationNew = targetDao.load(__key);
            synchronized (this) {
                designation = designationNew;
                designation__resolvedKey = __key;
            }
        }
        return designation;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1194385037)
    public void setDesignation(Designation designation) {
        synchronized (this) {
            this.designation = designation;
            desgnId = designation == null ? null : designation.getId();
            designation__resolvedKey = desgnId;
        }
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2059241980)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getUserDao() : null;
    }


}
