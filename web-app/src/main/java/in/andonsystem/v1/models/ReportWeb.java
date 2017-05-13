/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.andonsystem.v1.models;

import java.util.Date;

/**
 *
 * @author Administrator
 */
public class ReportWeb {
    
    private MyDate date;
    private int line;
    private String section;
    private String dept;
    private String problem;
    private int operatorNo;
    private String critical;
    private String remarks;
    private String raisedAt;
    private String raisedBy;
    private String ackAt;
    private String ackBy;
    private String fixedAt;
    private String fixedBy;
    private int downtime;

    public ReportWeb(){}
    
    public ReportWeb(MyDate date, int line, String section, String dept, String problem, int operatorNo, String critical, String remarks, String raisedAt, String raisedBy, String ackAt, String ackBy, String fixedAt, String fixedBy, int downtime) {
        this.date = date;
        this.line = line;
        this.section = section;
        this.dept = dept;
        this.problem = problem;
        this.operatorNo = operatorNo;
        this.critical = critical;
        this.remarks = remarks;
        this.raisedAt = raisedAt;
        this.raisedBy = raisedBy;
        this.ackAt = ackAt;
        this.ackBy = ackBy;
        this.fixedAt = fixedAt;
        this.fixedBy = fixedBy;
        this.downtime = downtime;
    }

    public MyDate getDate() {
        return date;
    }

    public void setDate(MyDate date) {
        this.date = date;
    }

    

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public int getOperatorNo() {
        return operatorNo;
    }

    public void setOperatorNo(int operatorNo) {
        this.operatorNo = operatorNo;
    }

    public String getCritical() {
        return critical;
    }

    public void setCritical(String critical) {
        this.critical = critical;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getRaisedAt() {
        return raisedAt;
    }

    public void setRaisedAt(String raisedAt) {
        this.raisedAt = raisedAt;
    }

    public String getRaisedBy() {
        return raisedBy;
    }

    public void setRaisedBy(String raisedBy) {
        this.raisedBy = raisedBy;
    }

    public String getAckAt() {
        return ackAt;
    }

    public void setAckAt(String ackAt) {
        this.ackAt = ackAt;
    }

    public String getAckBy() {
        return ackBy;
    }

    public void setAckBy(String ackBy) {
        this.ackBy = ackBy;
    }

    public String getFixedAt() {
        return fixedAt;
    }

    public void setFixedAt(String fixedAt) {
        this.fixedAt = fixedAt;
    }

    public String getFixedBy() {
        return fixedBy;
    }

    public void setFixedBy(String fixedBy) {
        this.fixedBy = fixedBy;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getDowntime() {
        return downtime;
    }

    public void setDowntime(int downtime) {
        this.downtime = downtime;
    }


    
   
}
