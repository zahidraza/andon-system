/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.andonsystem.v1.services;


import in.andonsystem.v1.models.Section;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class SectionService {
    private Connection conn;

    public SectionService(Connection conn){
        this.conn = conn;
    }
    
    public List<Section> getSections() throws SQLException{
        List<Section> list = new ArrayList<>();
        
        String sql = "SELECT * FROM section";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            list.add(new Section(rs.getInt("sec_id"),rs.getString("name")));
        }
         
        return list;
    }
    
     public List<String> getSectionNames() throws SQLException{
        List<String> sections = new ArrayList<>();
        
        String sql = "SELECT name FROM section";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while(rs.next()){
            sections.add(rs.getString("name"));
        }
       
        return sections;
    }
     
    public String getSectionName(int secId) throws SQLException{
        String section = null;
        
        String sql = "SELECT name FROM section WHERE sec_id = " + secId;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        if(rs.next()){
            section = rs.getString("name");
        }
        
        return section;
    }
    
    public Boolean addSection(String name) throws SQLException{
        Boolean result = false;
        
        String sql = "INSERT INTO section VALUES(NULL,?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, name);
        ps.executeUpdate();
        result = true;
        
        return result;
    }
   
}
