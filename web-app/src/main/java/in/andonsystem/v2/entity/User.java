package in.andonsystem.v2.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import in.andonsystem.v1.entity.Designation;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "USERS")
public class User implements Serializable {
    @Id
    @Column(name = "USER_ID", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "EMAIL", unique = true)
    private String email;

    @JsonIgnore
    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "MOBILE", nullable = false)
    private String mobile;

    @Column(name = "ROLE", nullable = false)
    private String role;

    @Column(name = "USER_TYPE", nullable = false)
    private String userType;

    @Column(name = "LEVEL", nullable = true)
    private String level;

    //@JsonIgnore
    @ManyToOne(optional = true)
    @JoinColumn(name = "DESGN_ID")
    private Designation designation;

    @Column(name = "OTP")
    private String otp;

    @Column(name = "retry_count")
    private Integer retryCount;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "USER_BUYER",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "BUYER_ID")
    )
    private Set<Buyer> buyers = new HashSet<>();


    @Version
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModified;

    public User() {
    }

    public User(Long id, String name, String mobile, String role, String userType, String level) {
        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.role = role;
        this.userType = userType;
        this.level = level;
    }

    public User(Long id, String name, String mobile, String role, String userType, String level, Boolean active) {
        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.role = role;
        this.userType = userType;
        this.level = level;
        this.active = active;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public User(Long id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        this.password = encoder.encode(password);
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Set<Buyer> getBuyers() {
        return buyers;
    }

    public void setBuyers(Set<Buyer> buyers) {
        this.buyers = buyers;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public Designation getDesignation() {
        return designation;
    }

    public void setDesignation(Designation designation) {
        this.designation = designation;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", email='" + email + '\'' +
               ", password='" + password + '\'' +
               ", mobile='" + mobile + '\'' +
               ", role='" + role + '\'' +
               ", userType='" + userType + '\'' +
               ", level='" + level + '\'' +
               '}';
    }
}
