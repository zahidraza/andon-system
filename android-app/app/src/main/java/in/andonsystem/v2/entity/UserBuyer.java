package in.andonsystem.v2.entity;

import android.util.Log;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by razamd on 4/7/2017.
 */
@Entity
public class UserBuyer {
    @Id(autoincrement = true)
    private Long id;
    private Long userId;
    private Long buyerId;
    @Generated(hash = 987854515)
    public UserBuyer(Long id, Long userId, Long buyerId) {
        this.id = id;
        this.userId = userId;
        this.buyerId = buyerId;
    }
    @Generated(hash = 1603178675)
    public UserBuyer() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getUserId() {
        return this.userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Long getBuyerId() {
        return this.buyerId;
    }
    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

}
