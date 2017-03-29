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
public class IssueData1 {
    private long issueSync;
    private List<Issue1> issues;
    
    public IssueData1(){}

    public IssueData1(long issueSync, List<Issue1> issues) {
        this.issueSync = issueSync;
        this.issues = issues;
    }

    public long getIssueSync() {
        return issueSync;
    }

    public void setIssueSync(long issueSync) {
        this.issueSync = issueSync;
    }

    public List<Issue1> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue1> issues) {
        this.issues = issues;
    }
    
    
    
}
