/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.andonsystem.v1.services;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Md Zahid Raza
 */
public class ForgotPasswordService {
    private Connection conn;
   
    public ForgotPasswordService(Connection conn){
        this.conn = conn;
    }
    
    public Boolean recordForgotPassword(int otp,int userId) throws SQLException{
        Boolean status = false;
        
        String sql = "INSERT INTO forgot_password VALUES( ?, ?, NOW())";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, userId);
        ps.setInt(2, otp);
        ps.executeUpdate();
        status = true;
 
        return status;
    }
    
    public Boolean verifyOTP(int userId,int otp) throws SQLException{
        Boolean status = false;
        
        String sql = "SELECT * FROM forgot_password WHERE user_id = ? AND otp = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, userId);
        ps.setInt(2, otp);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            status = true;
        }
        
        return status;
    }
    
}

