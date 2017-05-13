/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.andonsystem.v1.resources;


import in.andonsystem.v1.services.ForgotPasswordService;
import in.andonsystem.v1.services.OTP;
import in.andonsystem.v1.services.SMSService;
import in.andonsystem.v1.services.UserService;
import in.andonsystem.v1.util.ConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * @author Md Zahid Raza
 */
@Path("/forgot_passwd")
public class ForgotPasswordResource {

    @POST
    @Path("/send_otp")
    @Produces(MediaType.TEXT_PLAIN)
    public String sendOTP(@FormParam("userId") int userId) {
        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        UserService uService = new UserService(conn);
        ForgotPasswordService fService = new ForgotPasswordService(conn);
        Boolean status = false;
        try {
            Boolean result = uService.findUser(userId);

            if (result) {
                OTP otpObj = new OTP(6);
                String otp = otpObj.nextString();
                fService.recordForgotPassword(Integer.parseInt(otp), userId);
                String mobile = uService.getUserMobile(userId);

                //Send otp           
                String message = "OTP to reset password in ANDON SYSTEM APPLICATION is: " + otp;
                status = SMSService.sendSMS(mobile, message);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return (status? "success" : "fail");
    }

    @POST
    @Path("/verify_otp")
    @Produces(MediaType.TEXT_PLAIN)
    public String verifyOTP(@FormParam("userId") int userId, @FormParam("otp") int otp) {
        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ForgotPasswordService fService = new ForgotPasswordService(conn);
        Boolean result = false;
        try {
            result = fService.verifyOTP(userId, otp);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return (result? "success" : "fail");
    }

    @POST
    @Path("/change_password")
    @Produces(MediaType.TEXT_PLAIN)
    public String changePassword(@FormParam("userId") int userId, @FormParam("newPassword") String newPassword) {
        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        UserService uService = new UserService(conn);
        Boolean result = false;
        try {
            result = uService.changePassword(userId, newPassword);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return (result? "success" : "fail");
    }

}
