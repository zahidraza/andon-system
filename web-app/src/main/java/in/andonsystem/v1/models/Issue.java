/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.andonsystem.v1.models;

/**
 *
 * @author Md Jawed Akhtar
 */
public class Issue {
    private int id;
    private int line;
    private int secId;
    private int deptId;
    private int probId;
    private String critical;
    private int operatorNo;
    private String desc;
    private long raisedAt;
    private long ackAt;
    private long fixAt;
    private int raisedBy;
    private int ackBy;
    private int fixBy;
    private int processingAt; //values: 1,2,3 for processing level 4 for fixed issues
    private int status;
    private int seekHelp;
    
    
    
    public Issue(){}

    public Issue(int id, int line, int secId, int deptId, int probId, String critical, int operatorNo, String desc, long raisedAt, long ackAt, long fixAt, int raisedBy, int ackBy,int fixBy,int processingAt, int status, int seekHelp) {
        this.id = id;
        this.line = line;
        this.secId = secId;
        this.deptId = deptId;
        this.probId = probId;
        this.critical = critical;
        this.operatorNo = operatorNo;
        this.desc = desc;
        this.raisedAt = raisedAt;
        this.ackAt = ackAt;
        this.fixAt = fixAt;
        this.raisedBy = raisedBy;
        this.ackBy = ackBy;
        this.fixBy = fixBy;
        this.processingAt = processingAt;
        this.status = status;
        this.seekHelp = seekHelp;
    }

    public int getProcessingAt() {
        return processingAt;
    }

    public void setProcessingAt(int processingAt) {
        this.processingAt = processingAt;
    }
  
    
    
    public int getSeekHelp() {
        return seekHelp;
    }

    public void setSeekHelp(int seekHelp) {
        this.seekHelp = seekHelp;
    }

    public int getRaisedBy() {
        return raisedBy;
    }

    public void setRaisedBy(int raisedBy) {
        this.raisedBy = raisedBy;
    }

    public int getFixBy() {
        return fixBy;
    }

    public void setFixBy(int fixBy) {
        this.fixBy = fixBy;
    }

    
    public long getFixAt() {
        return fixAt;
    }

    public void setFixAt(long fixAt) {
        this.fixAt = fixAt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getSecId() {
        return secId;
    }

    public void setSecId(int secId) {
        this.secId = secId;
    }

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public int getProbId() {
        return probId;
    }

    public void setProbId(int probId) {
        this.probId = probId;
    }

    public String getCritical() {
        return critical;
    }

    public void setCritical(String critical) {
        this.critical = critical;
    }

    public int getOperatorNo() {
        return operatorNo;
    }

    public void setOperatorNo(int operatorNo) {
        this.operatorNo = operatorNo;
    }

   

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public long getRaisedAt() {
        return raisedAt;
    }

    public void setRaisedAt(long raisedAt) {
        this.raisedAt = raisedAt;
    }

    public long getAckAt() {
        return ackAt;
    }

    public void setAckAt(long ackAt) {
        this.ackAt = ackAt;
    }

    public int getAckBy() {
        return ackBy;
    }

    public void setAckBy(int ackBy) {
        this.ackBy = ackBy;
    }
 
    
}
