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
public class Problem {
    private int probId;
    private int deptId;
    private String name;
    
    Problem(){}

    public Problem(int probId, int deptId, String name) {
        this.probId = probId;
        this.deptId = deptId;
        this.name = name;
    }

    public int getProbId() {
        return probId;
    }

    public void setProbId(int probId) {
        this.probId = probId;
    }

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
}
