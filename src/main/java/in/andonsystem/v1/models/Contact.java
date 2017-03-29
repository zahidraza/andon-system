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
public class Contact {
    private String name;
    private String mobile;

    public Contact(String name, String mobile) {
        this.name = name;
        this.mobile = mobile;
    }

    public Contact() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    
}
