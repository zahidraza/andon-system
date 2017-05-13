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
public class Desgn {
    private int desgnId;
    private String name;

    public Desgn(){}
    
    public Desgn(int desnId, String name) {
        this.desgnId = desnId;
        this.name = name;
    }
    
    public int getDesgnId() {
        return desgnId;
    }

    public void setDesgnId(int desnId) {
        this.desgnId = desnId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
    
}
