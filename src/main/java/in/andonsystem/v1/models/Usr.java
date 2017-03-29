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
public class Usr {
    private int userId;
    private String username;
    private int desgnId;
    private String mobile;
    
    public Usr(){}

    public Usr(int userId, String username, int desgnId, String mobile) {
        this.userId = userId;
        this.username = username;
        this.desgnId = desgnId;
        this.mobile = mobile;
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

    public int getDesgnId() {
        return desgnId;
    }

    public void setDesgnId(int desgnId) {
        this.desgnId = desgnId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    
    
}
