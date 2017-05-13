/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.andonsystem.v1.services;


import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import in.andonsystem.v1.models.Contact;
import in.andonsystem.v1.models.Pair;
import in.andonsystem.v1.models.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Md Zahid Raza
 */
public class UserService {
    //authUser()
    private final String query1 = "SELECT * FROM user WHERE user_id = ? AND password = ? AND level = ?";
    //getUser()
    private final String query2 = "SELECT * FROM user WHERE user_id = ?";
    //addUser()
    private final String query3 = "INSERT INTO user (user_id,username,email,password,level,desgn_id,mobile) VALUES(?,?,?,?,?,?,?)";


    Connection conn;
    
    public UserService(Connection conn){
        this.conn = conn;
    }
    
    public Boolean authUser(int userId,String password,int level) throws SQLException{
        Boolean status = false;
        
        PreparedStatement ps = conn.prepareStatement(query1);
        ps.setInt(1, userId);
        ps.setString(2, Password.encrypt(password));
        ps.setInt(3, level);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            status = true;
        }  
        
        return status;
    }
    
    public int authUser(String authToken) throws SQLException{
        int userId = 0;
        
        String sql = "SELECT user_id FROM user WHERE auth_token = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, authToken);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            userId = rs.getInt("user_id");
        }  
        
        return userId;
    }
    
    public User authUser(int userId,String password) throws SQLException{
        User user = null;
        DesignationService dService = new DesignationService(conn);
        
        String sql = "SELECT * FROM user WHERE user_id = ? AND password = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, userId);
        ps.setString(2, Password.encrypt(password));

        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            user = new User(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getInt("level"),
                    rs.getInt("desgn_id"),
                    rs.getString("mobile")
            );
        }
        //If auth successful
        if(user != null){
            RandomString random = new RandomString(20);
            String authToken = random.nextString();
            //System.out.println(authToken);

            sql = "UPDATE user SET auth_token = ? WHERE user_id = ?";
            PreparedStatement ps2 = conn.prepareStatement(sql);
            ps2.setString(1, authToken);
            ps2.setInt(2, userId);
            ps2.executeUpdate();

            user.setAuthToken(authToken);
        }

        return user;
    }
    
    public Boolean findUser(int user_id) throws SQLException{
        Boolean status = false;
        
        PreparedStatement ps = conn.prepareStatement(query2);
        ps.setInt(1, user_id);

        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            status = true;
        }

        return status;
    }
    
    public User getUser(int user_id) throws SQLException{
        User result = null;
        
        PreparedStatement ps = conn.prepareStatement(query2);
        ps.setInt(1, user_id);

        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            result = new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getInt("level"),
                        rs.getInt("desgn_id"),
                        rs.getString("mobile")
            );
        }
        
        return result;
    }
   
    public List<User> getUsers() throws SQLException{
        List<User> users = new ArrayList<>();
        
        String sql = "SELECT user_id,username,desgn_id,mobile,level FROM user";
        PreparedStatement ps = conn.prepareStatement(sql);

        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            users.add(new User(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getInt("desgn_id"),
                    rs.getString("mobile"),
                    rs.getInt("level")
                )
            ); 
        }
        
        return users;
    }
    
    public List<Pair<Integer,String> > getUsers(int desgnId) throws SQLException{
        List<Pair<Integer,String> > users = new ArrayList<>();
        
        String sql = "SELECT user_id,username FROM user where desgn_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, desgnId);
        ResultSet rs = ps.executeQuery();

        while(rs.next()){
            users.add(new Pair(rs.getInt("user_id"),rs.getString("username"))); 
        }
        
        return users;
    }
    
    
    public String getUserName(int user_id) throws SQLException{
        String name = null;
        
        String sql = "SELECT username FROM user WHERE user_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, user_id);

        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            name =rs.getString("username"); 
        }
        
        return name;
    }
    
    public String getUserMobile(int user_id) throws SQLException{
        String mobile = null;
        
        String sql = "SELECT mobile FROM user WHERE user_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, user_id);

        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            mobile =rs.getString("mobile"); 
        }
        
        return mobile;
    }
    
    public List<String> getUserMobiles(String desgnIds) throws SQLException{
        List<String> mobiles = new ArrayList<>();
        
        String sql = "SELECT mobile FROM user WHERE desgn_id IN " + desgnIds;
        PreparedStatement ps = conn.prepareStatement(sql);

        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            mobiles.add(rs.getString("mobile"));
        }
       
        return mobiles;
    }
    
    public List<Contact> getContacts(int desgnId) throws SQLException{
        List<Contact> contacts = new ArrayList<>();
       
        String sql;
        ResultSet rs;
        if(desgnId == 0){       //All Contacts
            sql = "SELECT username,mobile FROM user";
            PreparedStatement ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
        }else{      //Only contacts of specific designation
            sql = "SELECT username,mobile FROM user WHERE desgn_id = ?" ;
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,desgnId);
            rs = ps.executeQuery();
        }
        while(rs.next()){
            contacts.add(new Contact(rs.getString("username"),rs.getString("mobile")));
        }
        
        return contacts;
    }
    
    
    public int addUser(User user) throws SQLException{
        int status = 0;
        try{
            PreparedStatement ps = conn.prepareStatement(query3);
            ps.setInt(1, user.getUserId());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getEmail());
            ps.setString(4, Password.encrypt(user.getMobile()));
            ps.setInt(5, user.getLevel());            
            ps.setInt(6, user.getDesgnId());
            ps.setString(7, user.getMobile());
           
            ps.executeUpdate();
            //user.setPassword(password);
            //new MailService().userRegistrationMail(user);
            status = 1;
            
        }catch(MySQLIntegrityConstraintViolationException ex){
            ex.printStackTrace();
            status = 2; 
        }
       
        return status;
    }
    
    public Boolean changeEmail(String authToken,String email) throws SQLException{
        Boolean status = false;
        
        String sql = "UPDATE user SET email = ? WHERE auth_token = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, email);
        ps.setString(2, authToken);
        int count = ps.executeUpdate();
        if(count > 0){
            status = true;
        }
        
        return status;
    }
    
    public Boolean changeMobile(String authToken,String mobile) throws SQLException{
        Boolean status = false;
        
        String sql = "UPDATE user SET mobile = ? WHERE auth_token = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, mobile);
        ps.setString(2, authToken);
        int count = ps.executeUpdate();
        if(count > 0){
            status = true;
        }
        
        return status;
    }
    
    public Boolean changePassword(String authToken,String currPasswd,String newPasswd) throws SQLException{
        Boolean status = false;
        
        String sql = "UPDATE user SET password = ? WHERE auth_token = ? AND password = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, Password.encrypt(newPasswd));
        ps.setString(2, authToken);
        ps.setString(3, Password.encrypt(currPasswd));
        int count = ps.executeUpdate();
        if(count > 0){
            status = true;
        }
        
        return status;
    }
    
    public Boolean changePassword(int userId,String newPasswd) throws SQLException{
        Boolean status = false;
        
        String sql = "UPDATE user SET password = ? WHERE user_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, Password.encrypt(newPasswd));
        ps.setInt(2, userId);
        int count = ps.executeUpdate();
        if(count > 0){
            status = true;
        }
        
        return status;
    }
    
    //To remove a User, he must be first removed from user_problem and user_line table
    public Boolean removeUser(int user_id) throws SQLException{
        Boolean status = false;
        
        String sql = "DELETE FROM user WHERE user_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, user_id);
        ps.executeUpdate();
        status = true;
       
        return status;
    }
    
    public Boolean editUsername(int userId,String name) throws SQLException{
        Boolean status = false;
        
        String sql = "UPDATE user SET username = ? WHERE user_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, name);
        ps.setInt(2, userId);
        int count = ps.executeUpdate();
        if(count > 0){
            status = true;
        }
        
        return status;
    }
    
    public Boolean editEmail(int userId,String email) throws SQLException{
        Boolean status = false;
        
        String sql = "UPDATE user SET email = ? WHERE user_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, email);
        ps.setInt(2, userId);
        int count = ps.executeUpdate();
        if(count > 0){
            status = true;
        }
        
        return status;
    }
    
    public Boolean editMobile(int userId,String mobile) throws SQLException{
        Boolean status = false;
        
        String sql = "UPDATE user SET mobile = ? WHERE user_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, mobile);
        ps.setInt(2, userId);
        int count = ps.executeUpdate();
        if(count > 0){
            status = true;
        }
        
        return status;
    }
    
    public Boolean ResetPassword(int userId,String username,String mobile) throws SQLException{
        Boolean status = false;
        RandomString random = new RandomString(8);
        String password = random.nextString();
        
        String sql = "UPDATE user SET password = ? WHERE user_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, Password.encrypt(password));
        ps.setInt(2, userId);
        int count = ps.executeUpdate();
        if(count > 0){
            String message = "Username:" + username + "\nNew Password:" + password;
            SMSService.sendSMS(mobile, message);
            status = true;
        }
        
        return status;
    }
    
    public Boolean ResetPassword(int userId) throws SQLException{
        Boolean status = false;
        RandomString random = new RandomString(8);
        String password = random.nextString();
        
        String sql = "UPDATE user SET password = ? WHERE user_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, Password.encrypt(password));
        ps.setInt(2, userId);
        int count = ps.executeUpdate();
        if(count > 0){
            String mobile = null,username = null;
            sql = "SELECT username,mobile FROM user WHERE user_id = ?";
            PreparedStatement ps2 = conn.prepareStatement(sql);
            ps2.setInt(1, userId);
            ResultSet rs = ps2.executeQuery();
            if(rs.next()){
                mobile =rs.getString("mobile"); 
                username = rs.getString("username");
            }
            String message = "Username:" + username + "\nNew Password:" + password;
            SMSService.sendSMS(mobile, message);
            status = true;
        }
        
        return status;
    }
   
    
}
