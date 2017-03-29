/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.andonsystem.v1.resources;


import in.andonsystem.v1.services.SMSService;
import in.andonsystem.v1.services.UserService;
import in.andonsystem.v1.util.ConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * @author Md Zahid Raza
 */
@Path("/style_changeover")
public class StyleChangeoverResource {

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public String styleChangeover(
            @FormParam("line") int line,
            @FormParam("from") String from,
            @FormParam("to") String to,
            @FormParam("remarks") String remarks,
            @FormParam("submitBy") String submitBy) {

        //First Floor desgnIds : 1,6,7,9,43
        //Second Floor desgnIds : 2,5,8,10,43,8
        Connection conn = null;
        try {
            conn = ConnectionPool
                    .getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        UserService uService = new UserService(conn);
        Boolean status = false;
        try {
            List<String> mobileList = null;
            if (line == 1 || line == 2 || line == 3 || line == 4) {
                mobileList = uService.getUserMobiles("(1,6,7,9,43)");
            }
            if (line == 5 || line == 6 || line == 7 || line == 8) {
                mobileList = uService.getUserMobiles("(2,5,8,10,43)");
            }
            String mobiles = "";
            if (mobileList.size() > 0) {
                mobiles += mobileList.get(0);

                for (int i = 1; i < mobileList.size(); i++) {
                    mobiles += "," + mobileList.get(i);
                }
            }

            String message = "Line: " + line + " Changeover from " + from + " to " + to + " Remarks: " + remarks +
                             " Submitted by: " + submitBy;
            //Send sms to these mobile numbers

            if (!mobiles.equals("")) {
                status = SMSService.sendSMS(mobiles, message);
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
        return (status? "success" : "Server Error or No mobiles to send SMS");
    }
}
