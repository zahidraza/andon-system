/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.andonsystem.v1.services;


import in.andonsystem.v1.models.Desgn;
import in.andonsystem.v1.models.KeyValue;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Md Zahid Raza
 */
public class DesignationService {
    
    private Connection conn;
   
    public DesignationService(Connection conn){
        this.conn = conn;
    }
    
    public int saveDesignation(String name,int level) throws SQLException{
        int desgnId = 0;

        String sql = "INSERT INTO designation VALUES(NULL,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, name);
        ps.setInt(2, level);
        ps.executeUpdate();

        sql = "SELECT desgn_id FROM designation WHERE name = ?";
        PreparedStatement ps2 = conn.prepareStatement(sql);
        ps2.setString(1, name);
        ResultSet rs = ps2.executeQuery();
        rs.next();
        desgnId = rs.getInt("desgn_id");
        
        return desgnId;
    }
    
    public List<String> getAllDesgn() throws SQLException {
        List<String> list = new ArrayList<>();

        String sql = "SELECT name FROM designation";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            list.add(rs.getString("name"));
        }
        
        return list;
    }
    
    public List<Desgn> getDesgns() throws SQLException{
        List<Desgn> list = new ArrayList<>();

        String sql = "SELECT desgn_id,name FROM designation ORDER BY name";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            list.add(new Desgn(rs.getInt("desgn_id"),rs.getString("name")));
        }
        
        return list;
    }
    
    public String getDesgnName(int desgnId) throws SQLException{
        String name = null;

        String sql = "SELECT name FROM designation WHERE desgn_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, desgnId);
        ResultSet rs = ps.executeQuery();
        rs.next();
        name = rs.getString("name");
        
        return name;
    }
    
    public List<Integer> getDesgnConcerned(int line,int probId,int level) throws SQLException{
        List<Integer> list = new ArrayList<>();
        String sql,desgnIds = "";

        sql = "SELECT desgn_id FROM desgn_problem WHERE prob_id IN ( 0, ?)";
        PreparedStatement ps1 = conn.prepareStatement(sql);
        ps1.setInt(1, probId);
        ResultSet rs = ps1.executeQuery();
        rs.next();
        desgnIds += rs.getInt("desgn_id");
        while(rs.next()){
            desgnIds += "," + rs.getInt("desgn_id");
        }

        sql = "SELECT desgn_id FROM desgn_line WHERE line IN (0,?) AND desgn_id IN(" + desgnIds+ ")";
        PreparedStatement ps2 = conn.prepareStatement(sql);
        ps2.setInt(1, line);
        ResultSet rs2 = ps2.executeQuery();
        rs2.next();
        desgnIds = "" + rs2.getInt("desgn_id");
        while(rs2.next()){
            desgnIds += "," + rs2.getInt("desgn_id");
        }

        sql = "SELECT desgn_id FROM designation WHERE level = ? AND desgn_id IN (" + desgnIds + ")";
        PreparedStatement ps3 = conn.prepareStatement(sql);
        ps3.setInt(1, level);
        ResultSet rs3 = ps3.executeQuery();
        while(rs3.next()){
            list.add(rs3.getInt("desgn_id"));
        }
        return list;
    }
    
    public int getDesgnId(String name) throws SQLException{
        int desgnId = 0;

        String sql = "SELECT desgn_id FROM designation WHERE name = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();
        rs.next();
        desgnId = rs.getInt("desgn_id");
       
        return desgnId;
    }
    
    public int getDesgnLevel(int desgnId) throws SQLException{
        int level = -1;

        String sql = "SELECT level FROM designation WHERE desgn_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, desgnId);
        ResultSet rs = ps.executeQuery();
        rs.next();
        level = rs.getInt("level");
        
        return level;
    }
    
    public void mapLine(int desgnId,int line) throws SQLException{
        

            String sql = "INSERT INTO desgn_line VALUES(?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, desgnId);
            ps.setInt(2, line);
            ps.executeUpdate();
        
    }
    
    public void mapProblem(int desgnId,int probId) throws SQLException{

        String sql = "INSERT INTO desgn_problem VALUES(?,?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, desgnId);
        ps.setInt(2, probId);
        ps.executeUpdate();
    
    }
   
    
    public List<KeyValue> getDesgnLineMapping() throws SQLException{
        List<KeyValue> list = new ArrayList<>();

        String sql = "SELECT * FROM desgn_line";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while(rs.next()){
            list.add(new KeyValue(rs.getInt("desgn_id"),rs.getInt("line")));
        }
        
        return list;
    }
    
    public List<KeyValue> getDesgnProblemMapping() throws SQLException{
        List<KeyValue> list = new ArrayList<>();

        String sql = "SELECT * FROM desgn_problem";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while(rs.next()){
            list.add(new KeyValue(rs.getInt("desgn_id"),rs.getInt("prob_id")));
        }
        
        return list;
    }
    
}
