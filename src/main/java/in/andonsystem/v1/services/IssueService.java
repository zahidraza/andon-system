/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.andonsystem.v1.services;

import in.andonsystem.v1.models.Issue;
import in.andonsystem.v1.models.Issue1;
import in.andonsystem.v1.models.MyDate;
import in.andonsystem.v1.models.ReportData;
import in.andonsystem.v1.models.ReportWeb;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 *
 * @author Md Jawed Akhtar
 */
public class IssueService {
    
    private Connection conn;
    
    public IssueService(Connection conn){
        this.conn = conn;
    }
    
    public int saveIssue(Issue issue) throws SQLException{
        int issueId = 0;
        Timestamp currTime = new Timestamp(System.currentTimeMillis());
        
        //String sql = "INSERT INTO issue VALUES(NULL,?,?,?,?,?,?,?,?,NULL,NULL,?,?,NULL,NULL,1,0)";
        String sql;
        StringBuilder sb = new StringBuilder("INSERT INTO issue ");
        sb.append(" VALUES(NULL,:line,:secId,:deptId,:probId,:critical,:operatorNo,:desc,:raisedAt,NULL,NULL,:modAt,:raisedBy,NULL,NULL,:processingAt,1,0)");
        sql = sb.toString();

        NamedParameterStatement nps = new NamedParameterStatement(conn, sql);

        //PreparedStatement ps = conn.prepareStatement(sql);
        nps.setInt("line",issue.getLine());
        nps.setInt("secId", issue.getSecId());
        nps.setInt("deptId",issue.getDeptId());
        nps.setInt("probId", issue.getProbId());
        nps.setString("critical", issue.getCritical());
        nps.setInt("operatorNo", issue.getOperatorNo());
        nps.setString("desc", issue.getDesc());
        nps.setTimestamp("raisedAt", currTime);
        nps.setTimestamp("modAt", currTime);
        nps.setInt("raisedBy", issue.getRaisedBy());
        nps.setInt("processingAt", 1);

        nps.executeUpdate();

        sql = "SELECT issue_id FROM issue WHERE operator_no = ? AND description = ? AND raised_by = ? ORDER BY issue_id DESC";
        PreparedStatement ps2 = conn.prepareStatement(sql);
        ps2.setInt(1, issue.getOperatorNo());
        ps2.setString(2, issue.getDesc());
        ps2.setInt(3, issue.getRaisedBy());

        ResultSet rs2 = ps2.executeQuery();
        if(rs2.next()){
            issueId = rs2.getInt("issue_id");
        }
        
        return issueId;
    }
    
    public Boolean saveSeekHelp(int issueId,int seekHelp) throws SQLException{
        Boolean status = false;
        long timeNow = System.currentTimeMillis();
        
        String sql = "UPDATE issue SET seek_help = ?,mod_at = ?,processing_at = ? WHERE issue_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, seekHelp);
        ps.setTimestamp(2, new Timestamp(timeNow));
        ps.setInt(3, (seekHelp+1));
        ps.setInt(4, issueId);

        ps.executeUpdate();

        status = true;
        
        return status;
    }
    
    public Boolean saveProcessingAt(int issueId,int level) throws SQLException{
        Boolean status = false;
        long timeNow = System.currentTimeMillis();
        
        String sql = "UPDATE issue SET processing_at = ?,mod_at = ? WHERE issue_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, level);
        ps.setTimestamp(2, new Timestamp(timeNow));
        ps.setInt(3, issueId);

        ps.executeUpdate();

        status = true;
        
        return status;
    }
    public Boolean saveIssueStatus(int issueId,int level) throws SQLException{
        Boolean status = false;
        long timeNow = System.currentTimeMillis();
        
        String sql = "UPDATE issue SET processing_at = ?, mod_at = ? WHERE issue_id = ? ";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, level);
        ps.setTimestamp(2, new Timestamp(timeNow));
        ps.setInt(3, issueId);
        ps.executeUpdate();
        
        status = true;
        return status;
    }
    
    public Boolean acknowledgeIssue(int issueId,int ackBy) throws SQLException{
        Boolean status = false;
        long timeNow = System.currentTimeMillis();
        
        String sql = "UPDATE issue SET ack_at = ?,mod_at = ?,ack_by = ? WHERE issue_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setTimestamp(1, new Timestamp(timeNow));
        ps.setTimestamp(2, new Timestamp(timeNow));
        ps.setInt(3,ackBy);
        ps.setInt(4, issueId);
        int count = ps.executeUpdate();
        if(count > 0)   status = true;
        
        return status;
    }
    
    public Boolean fixIssue(int issueId,int fixBy) throws SQLException{
        Boolean status = false;
        long timeNow = System.currentTimeMillis();
        
        String sql = "UPDATE issue SET fix_at = ?,fix_by = ?,mod_at = ?,processing_at = 4 ,status = 0 WHERE issue_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setTimestamp(1, new Timestamp(timeNow));
        ps.setInt(2, fixBy);
        ps.setTimestamp(3, new Timestamp(timeNow));
        ps.setInt(4, issueId);
        int count = ps.executeUpdate();
        if(count > 0)   status = true;
        
        return status;
    }
    
    public int fixIssueAutomatic(String timeSolved) throws SQLException{
        int count = 0;
        long timeNow = System.currentTimeMillis();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
        String date = df.format(new Date(timeNow));
        String startTime = date + " 00:00:00";
        String endTime = date + " 23:59:59";
        
        String sql = "UPDATE issue SET fix_at = '"+timeSolved+"' ,ack_at = '"+ timeSolved+"',fix_by = 55554, ack_by = 55554 ,mod_at = ?,processing_at = 4,status = 0 "+
                "WHERE raised_at > '"+ startTime + "' AND raised_at < '"+endTime+"' AND  fix_at IS NULL";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setTimestamp(1, new Timestamp(timeNow));          
        count = ps.executeUpdate();     
        
        return count;
    }
    //Get all open issues Issues
    public List<Issue> getIssues() throws SQLException{
        List<Issue> list = new ArrayList<>();
        
        Long timeNow = System.currentTimeMillis();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
        String time = df.format(new Date(timeNow));
        
        time += " 00:00:00";

        String sql = "SELECT * FROM issue WHERE raised_at > '" + time +"'";
        PreparedStatement ps = conn.prepareStatement(sql);
        //ps.setTimestamp(1, prevTime);
        ResultSet rs = ps.executeQuery(sql);

        while(rs.next()){
            list.add(new Issue(
                    rs.getInt("issue_id"),
                    rs.getInt("line"),
                    rs.getInt("sec_id"),
                    rs.getInt("dept_id"),
                    rs.getInt("prob_id"),
                    rs.getString("critical"),
                    rs.getInt("operator_no"),
                    rs.getString("description"),
                    MiscService.getTime( rs.getTimestamp("raised_at") ),
                    MiscService.getTime( rs.getTimestamp("ack_at") ),
                    MiscService.getTime(rs.getTimestamp("fix_at")),
                    rs.getInt("raised_by"),
                    rs.getInt("ack_by"),
                    rs.getInt("fix_by"),
                    rs.getInt("processing_at"),
                    rs.getInt("status"),
                    rs.getInt("seek_help")
            ));
        }
        
        return list;
    }
    
    //Delete in final app
    public List<Issue1> getIssues1() throws SQLException{
        List<Issue1> list = new ArrayList<>();
        
        Long timeNow = System.currentTimeMillis();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
        String time = df.format(new Date(timeNow));
        
        time += " 00:00:00";

        String sql = "SELECT * FROM issue WHERE raised_at > '" + time +"'";
        PreparedStatement ps = conn.prepareStatement(sql);
        //ps.setTimestamp(1, prevTime);
        ResultSet rs = ps.executeQuery(sql);

        while(rs.next()){
            list.add(new Issue1(
                    rs.getInt("issue_id"),
                    rs.getInt("line"),
                    rs.getInt("sec_id"),
                    rs.getInt("dept_id"),
                    rs.getInt("prob_id"),
                    rs.getString("critical"),
                    String.valueOf(rs.getInt("operator_no")),
                    rs.getString("description"),
                    MiscService.getTime( rs.getTimestamp("raised_at") ),
                    MiscService.getTime( rs.getTimestamp("ack_at") ),
                    MiscService.getTime(rs.getTimestamp("fix_at")),
                    rs.getInt("raised_by"),
                    rs.getInt("ack_by"),
                    rs.getInt("fix_by"),
                    rs.getInt("processing_at"),
                    rs.getInt("status"),
                    rs.getInt("seek_help")
            ));
        }
        
        return list;
    }
    
    
    //Get all open Issues raised after 
    public List<Issue> getIssues(long after) throws SQLException{
        List<Issue> list = new ArrayList<>();
        
        String sql = "SELECT * FROM issue WHERE mod_at > ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setTimestamp(1, new Timestamp(after));

        ResultSet rs = ps.executeQuery();

        while(rs.next()){
            list.add(new Issue(
                    rs.getInt("issue_id"),
                    rs.getInt("line"),
                    rs.getInt("sec_id"),
                    rs.getInt("dept_id"),
                    rs.getInt("prob_id"),
                    rs.getString("critical"),
                    rs.getInt("operator_no"),
                    rs.getString("description"),
                    MiscService.getTime( rs.getTimestamp("raised_at") ),
                    MiscService.getTime( rs.getTimestamp("ack_at") ),
                    MiscService.getTime(rs.getTimestamp("fix_at")),
                    rs.getInt("raised_by"),
                    rs.getInt("ack_by"),
                    rs.getInt("fix_by"),
                    rs.getInt("processing_at"),
                    rs.getInt("status"),
                    rs.getInt("seek_help")
            ));
        }
       
        return list;
    }
    
     //Delete in final app
    public List<Issue1> getIssues1(long after) throws SQLException{
        List<Issue1> list = new ArrayList<>();
        
        String sql = "SELECT * FROM issue WHERE mod_at > ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setTimestamp(1, new Timestamp(after));

        ResultSet rs = ps.executeQuery();

        while(rs.next()){
            list.add(new Issue1(
                    rs.getInt("issue_id"),
                    rs.getInt("line"),
                    rs.getInt("sec_id"),
                    rs.getInt("dept_id"),
                    rs.getInt("prob_id"),
                    rs.getString("critical"),
                    String.valueOf(rs.getInt("operator_no")),
                    rs.getString("description"),
                    MiscService.getTime( rs.getTimestamp("raised_at") ),
                    MiscService.getTime( rs.getTimestamp("ack_at") ),
                    MiscService.getTime(rs.getTimestamp("fix_at")),
                    rs.getInt("raised_by"),
                    rs.getInt("ack_by"),
                    rs.getInt("fix_by"),
                    rs.getInt("processing_at"),
                    rs.getInt("status"),
                    rs.getInt("seek_help")
            ));
        }
        
        return list;
    }
    
    
    public Issue getIssue(int issueId) throws SQLException{
        Issue issue = null;
        
        String sql = "SELECT * FROM issue WHERE issue_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, issueId);

        ResultSet rs = ps.executeQuery();

        while(rs.next()){
            issue = new Issue(
                    rs.getInt("issue_id"),
                    rs.getInt("line"),
                    rs.getInt("sec_id"),
                    rs.getInt("dept_id"),
                    rs.getInt("prob_id"),
                    rs.getString("critical"),
                    rs.getInt("operator_no"),
                    rs.getString("description"),
                    MiscService.getTime( rs.getTimestamp("raised_at") ),
                    MiscService.getTime( rs.getTimestamp("ack_at") ),
                    MiscService.getTime(rs.getTimestamp("fix_at")),
                    rs.getInt("raised_by"),
                    rs.getInt("ack_by"),
                    rs.getInt("fix_by"),
                    rs.getInt("processing_at"),
                    rs.getInt("status"),
                    rs.getInt("seek_help")
            );
        }
        
        return issue;
    }
    
    public List<ReportData> getDowntimeReport(String date) throws Exception{
        List<ReportData> data = new ArrayList<>();
        SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat df2 = new SimpleDateFormat("yyyy:MM:dd");
        
        String time = df2.format(df1.parse(date));
        String timeStart = time + " 00:00:00";
        String timeEnd = time + " 23:59:59";

        String sql = "SELECT prob_id , dept_id ,line, TIMESTAMPDIFF(MINUTE,raised_at,fix_at) AS downtime FROM issue"+
                    "   WHERE fix_at IS NOT NULL AND raised_at > '" + timeStart +"'  AND raised_at < '" + timeEnd +"'";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while(rs.next()){
            data.add(new ReportData(rs.getInt("prob_id"),rs.getInt("dept_id"),rs.getInt("line"),rs.getInt("downtime")));
        }

        sql = "SELECT prob_id , dept_id,line FROM issue WHERE fix_at IS  NULL AND raised_at > '" + timeStart + "'  AND raised_at < '" + timeEnd + "'";
        ResultSet rs2 = stmt.executeQuery(sql);

        while(rs2.next()){
            data.add(new ReportData(rs2.getInt("prob_id"),rs2.getInt("dept_id"),rs2.getInt("line"),-1));
        }
        
        return data;
    }
    
    public Boolean isAcknowledged(int issueId) throws SQLException{
        Boolean status = false;
        
        String sql = "SELECT ack_at FROM issue WHERE issue_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, issueId);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            Timestamp ackAt = rs.getTimestamp("ack_at");           
            if(ackAt != null){
                status = true;
            }
        }
        
        return status;
    }
    
    public Boolean isSolved(int issueId)throws SQLException{
        Boolean status = false;
        
        String sql = "SELECT fix_at FROM issue WHERE issue_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, issueId);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            Timestamp fixAt = rs.getTimestamp("fix_at");           
            if(fixAt != null){
                status = true;
            }
        }
        
        return status;
    }
    
    public int getSeekHelp(int issueId) throws SQLException{
        int result = 0;
        
        String sql = "SELECT seek_help FROM issue WHERE issue_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, issueId);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            result = rs.getInt("seek_help");
        }
        
        return result;
    }
    
    public long getRaiseTime(int issueId) throws Exception{
        long result = 0L;
        
        String sql = "SELECT raised_at FROM issue WHERE issue_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, issueId);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            Timestamp raisedAt = rs.getTimestamp("raised_at");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            df.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
            result = df.parse(raisedAt.toString()).getTime();
        }
        
        return result;
    }
    
    public List<ReportWeb> getWebReport(String from,String to,int line,int secId,int deptId,int operatorNo,String critical) throws Exception{
        UserService uService = new UserService(conn);
        List<ReportWeb> report = new ArrayList<>();
        
        DateFormat df1 = new SimpleDateFormat("MM/dd/yyyy");
        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd ");
        DateFormat df3 = new SimpleDateFormat("hh:mm aa");
        DateFormat df4 = new SimpleDateFormat("dd/MM/yyyy");

        String fromDate = df2.format(df1.parse(from)) + "00:00:00";
        String toDate = df2.format(df1.parse(to)) + "23:59:59";

        StringBuilder sb = new StringBuilder("SELECT line, s.name as section, d.name as dept, p.name as problem, operator_no, critical, description, raised_at, raised_by, ack_at, ack_by, fix_at, fix_by, TIMESTAMPDIFF(MINUTE,raised_at,fix_at) AS downtime \n" +
                "FROM issue i, section s, department d, problem p\n" +
                "WHERE i.sec_id = s.sec_id AND i.dept_id = d.dept_id AND i.prob_id = p.prob_id\n");

        sb.append(" AND raised_at > '" + fromDate +"' " );
        sb.append(" AND raised_at < '" + toDate + "' " );
        if(line != 0){
            sb.append(" AND line = :line ");
        }
        if(secId != 0){
            sb.append(" AND i.sec_id = :secId ");
        }
        if(deptId != 0){
            sb.append(" AND i.dept_id = :deptId ");
        }
        if(operatorNo != -1){
            sb.append(" AND operator_no = :operatorNo ");
        }
        if(!critical.equals("")){
            sb.append(" AND critical = :critical ");
        }
        sb.append(" ORDER BY raised_at ASC");
        String sql = sb.toString();
        NamedParameterStatement nps = new NamedParameterStatement(conn, sql);  

        if(line != 0){
            nps.setInt("line", line);
        }
        if(secId != 0){
            nps.setInt("secId", secId);
        }
        if(deptId != 0){
            nps.setInt("deptId", deptId);
        }
        if(operatorNo != -1){
            nps.setInt("operatorNo", operatorNo);
        }
        if(!critical.equals("")){
            nps.setString("critical", critical);
        }

        ResultSet rs = nps.executeQuery();
        String raisedBy,ackBy,ackAt,fixedBy,fixAt;
        while(rs.next()){
            raisedBy = uService.getUserName(rs.getInt("raised_by"));
            ackBy = uService.getUserName(rs.getInt("ack_by"));
            fixedBy = uService.getUserName(rs.getInt("fix_by"));
            //fixedBy = rs.getInt("fix_by") == 0 ? "None" : uService.getUserName(rs.getInt("fix_by"));
            ackAt = (rs.getTimestamp("ack_at") != null ? df3.format(new Date(rs.getTimestamp("ack_at").getTime())) : "-" );
            fixAt = (rs.getTimestamp("fix_at") != null ? df3.format(new Date(rs.getTimestamp("fix_at").getTime())) : "-" );
            report.add(new ReportWeb(
                    new MyDate(new Date(rs.getTimestamp("raised_at").getTime())),
                    rs.getInt("line"),
                    rs.getString("section"),
                    rs.getString("dept"),
                    rs.getString("problem"),
                    rs.getInt("operator_no"),
                    (rs.getString("critical").equals("YES") ? "Critical" : "Non-Critical"),
                    rs.getString("description"),
                    df3.format(new Date(rs.getTimestamp("raised_at").getTime())),
                    raisedBy,
                    ackAt,
                    ackBy,
                    fixAt,
                    fixedBy,
                    rs.getInt("downtime")

            ));

        }
        
        return report;
    }
    
}
