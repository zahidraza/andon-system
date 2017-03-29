package in.andonsystem.v2.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "USER")
public class User implements Serializable{
    @Id
    @Column(name = "USER_ID", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USERNAME", nullable = false)
    private String name;

    @Column(name = "EMAIL", nullable = true, unique = true)
    private String email;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "ROLE", nullable = false)
    private String role;

    @Column(name = "MOBILE", nullable = true)
    private String mobile;

    public User() {
    }

    public User(String name, String email, String password, String role, String mobile) {
        this.name = name;
        this.email = email;
        setPassword(password);
        this.role = role;
        this.mobile = mobile;
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

    public Role getRole() {
        return Role.parse(role);
    }

    public void setRole(Role role) {
        this.role = role.getValue();
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", name=" + name + ", email=" + email + ", role=" + role + ", mobile=" + mobile + '}';
    }

}
