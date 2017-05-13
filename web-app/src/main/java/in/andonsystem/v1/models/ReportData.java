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
public class ReportData {
    
    private int probId;
    private int deptId;
    private int line;
    private int downtime;
    
    public ReportData(){}

    public ReportData(int probId, int deptId,int line, int downtime) {
        this.probId = probId;
        this.deptId = deptId;
        this.line = line;
        this.downtime = downtime;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
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

    public int getDowntime() {
        return downtime;
    }

    public void setDowntime(int downtime) {
        this.downtime = downtime;
    }
    
    
    
}
