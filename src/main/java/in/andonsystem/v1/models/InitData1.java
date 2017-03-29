/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.andonsystem.v1.models;

import java.util.List;

/**
 *
 * @author Administrator
 */
public class InitData1 {
    private long launchTime;
    private long issueSync;
    private int timeAck;
    private int timeLevel1;
    private int timeLevel2;
    private int lines;
    private List<Section> sections;
    private List<Dept> departments;
    private List<Problem> problems;
    private List<Issue1> issues;
    private List<User> users;
    private List<Desgn> desgns;
    private List<KeyValue> desgnLine;
    private List<KeyValue> desgnProblem;
    
    
    public InitData1(){}

    public InitData1(long launchTime, long issueSync, int timeAck, int timeLevel1, int timeLevel2, int lines, List<Section> sections, List<Dept> departments, List<Problem> problems, List<Issue1> issues, List<User> users, List<Desgn> desgns, List<KeyValue> desgnLine, List<KeyValue> desgnProblem) {
        this.launchTime = launchTime;
        this.issueSync = issueSync;
        this.timeAck = timeAck;
        this.timeLevel1 = timeLevel1;
        this.timeLevel2 = timeLevel2;
        this.lines = lines;
        this.sections = sections;
        this.departments = departments;
        this.problems = problems;
        this.issues = issues;
        this.users = users;
        this.desgns = desgns;
        this.desgnLine = desgnLine;
        this.desgnProblem = desgnProblem;
    }

    

    public List<KeyValue> getDesgnLine() {
        return desgnLine;
    }

    public void setDesgnLine(List<KeyValue> desgnLine) {
        this.desgnLine = desgnLine;
    }

    public List<KeyValue> getDesgnProblem() {
        return desgnProblem;
    }

    public void setDesgnProblem(List<KeyValue> desgnProblem) {
        this.desgnProblem = desgnProblem;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Desgn> getDesgns() {
        return desgns;
    }

    public void setDesgns(List<Desgn> desgns) {
        this.desgns = desgns;
    }

    

    public long getIssueSync() {
        return issueSync;
    }

    public void setIssueSync(long issueSync) {
        this.issueSync = issueSync;
    }
    
    

    public int getLines() {
        return lines;
    }

    public void setLines(int lines) {
        this.lines = lines;
    } 

    public long getLaunchTime() {
        return launchTime;
    }

    public void setLaunchTime(long launchTime) {
        this.launchTime = launchTime;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Dept> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Dept> departments) {
        this.departments = departments;
    }

    public List<Problem> getProblems() {
        return problems;
    }

    public void setProblems(List<Problem> problems) {
        this.problems = problems;
    }

    public List<Issue1> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue1> issues) {
        this.issues = issues;
    }

    public int getTimeAck() {
        return timeAck;
    }

    public void setTimeAck(int timeAck) {
        this.timeAck = timeAck;
    }

    public int getTimeLevel1() {
        return timeLevel1;
    }

    public void setTimeLevel1(int timeLevel1) {
        this.timeLevel1 = timeLevel1;
    }

    public int getTimeLevel2() {
        return timeLevel2;
    }

    public void setTimeLevel2(int timeLevel2) {
        this.timeLevel2 = timeLevel2;
    }
    
    
      
}
