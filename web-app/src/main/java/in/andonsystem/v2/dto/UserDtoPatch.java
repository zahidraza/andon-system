package in.andonsystem.v2.dto;

import in.andonsystem.Level;
import in.andonsystem.Role;
import in.andonsystem.UserType;
import in.andonsystem.validation.StringEnum;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Created by razamd on 4/9/2017.
 */
public class UserDtoPatch {
    private Long id;

    @Size(min = 5, max = 100)
    private String name;

    @Pattern(regexp="^(?=.*[a-z])[a-z0-9_@\\.]{4,20}$", message="Username should contain lowercase letter, number and [_@.] only.")
    private String email;

    private String password;

    @Pattern(regexp="[0-9]{10}", message="Incorrect mobile")
    private String mobile;

    @StringEnum(enumClass = Role.class)
    private String role;

    @StringEnum(enumClass = UserType.class)
    private String userType;

    @StringEnum(enumClass = Level.class)
    private String level;

    public UserDtoPatch() {
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
        this.password = password;
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
}
