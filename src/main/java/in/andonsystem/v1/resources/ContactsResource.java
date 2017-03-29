/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.andonsystem.v1.resources;


import in.andonsystem.v1.models.Contact;
import in.andonsystem.v1.services.UserService;
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
@Path("/contacts")
public class ContactsResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Contact> getContacts(@QueryParam("desgnId") int desgnId) {
        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        UserService uService = new UserService(conn);
        List<Contact> contacts = null;
        try {
            contacts = uService.getContacts(desgnId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return contacts;
    }

}
