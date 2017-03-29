/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.andonsystem.v1.services;


import in.andonsystem.v1.models.Dept;
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
public class DeptService {
    private Connection conn;

    public DeptService(Connection conn) {
        this.conn = conn;
    }
    
    public List<Dept> getDepartments() throws SQLException{
        List<Dept> list = new ArrayList<>();
        String sql = "SELECT * FROM department";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            list.add(new Dept(rs.getInt("dept_id"),rs.getString("name")));
        }
        return list;
    }
    
    public List<String> getDeptNames() throws SQLException{
        List<String> deptNames = new ArrayList<>();
        String sql = "SELECT name FROM department";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while(rs.next()){
            deptNames.add(rs.getString("name"));
        }
        return deptNames;
    }
    
    public List<Integer> getDeptIds() throws SQLException{
        List<Integer> deptIds = new ArrayList<>();
        
        String sql = "SELECT dept_id FROM department";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while(rs.next()){
            deptIds.add(rs.getInt("dept_id"));
        }
        
        return deptIds;
    }
    
    public String getDeptName(int dept_id) throws SQLException{
        String result = null;
        String sql = "SELECT name FROM department WHERE dept_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, dept_id);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            result = rs.getString("name");
        }
        return result;
    }
    
}
