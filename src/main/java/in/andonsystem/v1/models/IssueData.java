/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.andonsystem.v1.models;

import java.util.List;

/**
 *
 * @author Md Jawed Akhtar
 */
public class IssueData {
    private long issueSync;
    private List<Issue> issues;
    
    public IssueData(){}

    public IssueData(long issueSync, List<Issue> issues) {
        this.issueSync = issueSync;
        this.issues = issues;
    }

    public long getIssueSync() {
        return issueSync;
    }

    public void setIssueSync(long issueSync) {
        this.issueSync = issueSync;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }
    
    
    
}
