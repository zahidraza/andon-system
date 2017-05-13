package in.andonsystem.v2.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by razamd on 3/31/2017.
 */

@Entity
public class Buyer {

    @Id
    private Long id;

    private String name;

    private String team;

    @Generated(hash = 1455264066)
    public Buyer(Long id, String name, String team) {
        this.id = id;
        this.name = name;
        this.team = team;
    }

    @Generated(hash = 2117874565)
    public Buyer() {
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

    public String getTeam() {
        return this.team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Buyer)) return false;

        Buyer buyer = (Buyer) o;

        return getId().equals(buyer.getId());

    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
