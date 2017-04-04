package in.andonsystem.v2.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import in.andonsystem.v2.enums.Level;
import in.andonsystem.v2.enums.Role;
import in.andonsystem.v2.enums.UserType;
import in.andonsystem.v2.validation.StringEnum;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserDto {

    private Long id;

    @NotNull
    @Size(min = 5, max = 100)
    private String name;

    @NotNull
    @Pattern(regexp=".+@.+\\..+", message="Incorrect email!")
    private String email;

    @JsonIgnore
    private String password;

    @NotNull
    @Pattern(regexp="[0-9]{10}", message="Incorrect mobile")
    private String mobile;

    @NotNull
    @StringEnum(enumClass = Role.class)
    private String role;

    @NotNull
    @StringEnum(enumClass = UserType.class)
    private String userType;

    @NotNull
    @StringEnum(enumClass = Level.class)
    private String level;

    private Long lastModified;

    public UserDto() {
    }

    public UserDto(String name, String email, String role, String mobile, String userType, String level) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.mobile = mobile;
        this.userType = userType;
        this.level = level;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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

    public Long getLastModified() {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public String toString() {
        return "UserDto{" + "id=" + id + ", name=" + name + ", email=" + email + ", role=" + role + ", mobile=" + mobile + '}';
    }
}
