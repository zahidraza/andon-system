/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.andonsystem.v1.models;

/**
 *
 * @author Md Zahid Raza
 */
public class User{
    
    private int userId;
    private String username;
    private String email;
    private String password;
    private int level;
    private int desgnId;
    private String mobile;
    private String authToken;

    public User(){}
    
    public User(int userId, String password, int level) {
        this.userId = userId;
        this.password = password;
        this.level = level;
    }

    public User(int userId, String username, int desgnId, String mobile,int level) {
        this.userId = userId;
        this.username = username;
        this.desgnId = desgnId;
        this.mobile = mobile;
        this.level = level;
    }

    public User(int userId, String username, String email,int level, int desgnId,String mobile,String authToken) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.level = level;
        this.desgnId = desgnId;
        this.mobile = mobile;
        this.authToken = authToken;
    }

    public User(int userId, String username, String email, int level, int desgnId, String mobile) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.level = level;
        this.desgnId = desgnId;
        this.mobile = mobile;
    }
   

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
    
    
    

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getDesgnId() {
        return desgnId;
    }

    public void setDesgnId(int desgnId) {
        this.desgnId = desgnId;
    }

    
    
}
