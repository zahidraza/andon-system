/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.andonsystem.v1.resources;


import in.andonsystem.v1.models.ReportData;
import in.andonsystem.v1.services.IssueService;
import in.andonsystem.v1.util.ConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * @author Md Zahid Raza
 */
@Path("/report")
public class ReportResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ReportData> getDowntimeReport(@QueryParam("date") String date) {
        Connection conn = null;
        try {
            conn = ConnectionPool
                    .getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        IssueService iService = new IssueService(conn);
        List<ReportData> data = null;
        try {
            data = iService.getDowntimeReport(date);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return data;
    }

}
