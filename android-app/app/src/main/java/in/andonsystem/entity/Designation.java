package in.andonsystem.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.JoinEntity;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

/**
 * Created by mdzahidraza on 18/06/17.
 */

@Entity
public class Designation {
    @Id
    private Long id;

    private String name;

    private String lines;

    private Integer level;

    @Generated(hash = 1951800963)
    public Designation(Long id, String name, String lines, Integer level) {
        this.id = id;
        this.name = name;
        this.lines = lines;
        this.level = level;
    }

    @Generated(hash = 608147116)
    public Designation() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLines() {
        return lines;
    }

    public void setLines(String lines) {
        this.lines = lines;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
