/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.andonsystem.v1.models;

/**
 *
 * @author Administrator
 */
public class AuthData {
    private String code;
    private User data;
    
    public AuthData(){}

    public AuthData(String code, User data) {
        this.code = code;
        this.data = data;
    }
    
    

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }
    
    
}
