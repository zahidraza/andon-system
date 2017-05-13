/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.andonsystem.v1.services;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This Service Class contains service methods for creating table structure in database
 * @author Md Zahid Raza
 */
public class DBTableService {
    /**
     * Connection object used by all methods.
     */
    private Connection conn;

    public DBTableService(Connection conn) {
        this.conn = conn;
    }
    /**
     * Creates user table.
     * <p>
     * Structure is:<br>
     * user_id          INTEGER     Primary Key<br>
     * username         VARCHAR<br>
     * email            VARCHAR<br>
     * password         VARCHAR<br>
     * level            INTEGER<br>
     * desgn_id         INTEGER<br>
     * mobile           VARCHAR<br>
     * auth_token       VARCHAR<br>
     * instance_token   VARCHAR<br>
     */
    public void createUserTable() throws SQLException{

        String sql =    "CREATE TABLE IF NOT EXISTS user(\n" +
                        "	user_id INTEGER NOT NULL,\n" +
                        "	username VARCHAR(255) NOT NULL,\n" +
                        "	email VARCHAR(255) NOT NULL,\n" +
                        "	password VARCHAR(255) NOT NULL,\n" +
                        "	level INTEGER(5) NOT NULL,\n" +
                        "	desgn_id INTEGER NOT NULL,\n" +
                        "	mobile VARCHAR(50) NOT NULL,\n" +                           
                        "	auth_token VARCHAR(255),\n" +
                        "	instance_token VARCHAR(255),\n" +
                        "	Primary Key(user_id)\n" +
                        ")";

        Statement stmt = conn.createStatement();
        stmt.execute(sql);

        sql = "SELECT COUNT(*) AS count FROM user";

        ResultSet rs = stmt.executeQuery(sql);
        rs.next();

        if( rs.getInt("count") == 0 ){
            sql = "INSERT INTO user VALUES(55555,'Md Zahid Raza','zahid7292@gmail.com','" + Password.encrypt("admin") + "',4,43,'8083237421',NULL,NULL)";
            stmt.executeUpdate(sql);
        }

    }   
    /**
     * Creates forgot_password table.
     * <p>
     * Structure is:<br>
     * user_id          INTEGER<br>
     * otp            INTEGER<br>
     * time         DATETIME<br>
     */
    public void createForgotPasswordTable() throws SQLException{

        String sql = "CREATE TABLE IF NOT EXISTS forgot_password (\n" +                    
                    "  user_id INTEGER NOT NULL,\n" + 
                    "  otp INTEGER NOT NULL,\n" +                    
                    "  time datetime DEFAULT NULL\n" +                     
                    ") ";

        Statement stmt = conn.createStatement();
        stmt.execute(sql);
   
    }
    /**
     *  Creates forgot_pass_event. This event runs every day at 11:00 AM and deletes all entry
     *  which are older than a day
     */
    public void createForgotPasswordEvent() throws SQLException {
        Statement stmt = conn.createStatement();
        String sql= "CREATE EVENT IF NOT EXISTS forgot_pass_event " +
                    "ON SCHEDULE EVERY 1 DAY STARTS '2016-06-24 11:00:00.000000'  " +
                    "DO     " +
                    "	DELETE FROM forgot_password WHERE DATEDIFF(NOW(),time) > 1";
        stmt.execute(sql);
    }
    /**
     * Creates section table.
     * <p>
     * Structure is:<br>
     * sec_id          INTEGER     Primary Key, Auto Incrementing<br>
     * name         VARCHAR  Section name.
     */
    public void createSectionTable() throws SQLException{
        String sql =    "CREATE TABLE IF NOT EXISTS section(\n" +
                        "	sec_id INTEGER NOT NULL AUTO_INCREMENT,\n" + 
                        "	name VARCHAR(255) NOT NULL,\n" + 
                        "	Primary Key(sec_id)\n" +
                        ")";

        Statement stmt = conn.createStatement();
        stmt.execute(sql);       
    }
    /**
     * @deprecated 
     */
    public void addSections() throws SQLException{
        String section = "INSERT INTO section VALUES"+
                          "(NULL,'Front'),(NULL,'Back'),(NULL,'Sleeve'),(NULL,'Cuff'),(NULL,'Assembly')";
        Statement stmt = conn.createStatement();

        String sql = "SELECT COUNT(*) as count FROM section";
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        if(rs.getInt("count") == 0){
            stmt.executeUpdate(section);
        }  
    }
    /**
     * @deprecated 
     */
    public void addDepartments() throws SQLException{
        Statement stmt = conn.createStatement();
        String department = "INSERT INTO department VALUES"
                                + "(NULL,'Cutting'),(NULL,'Trims'),(NULL,'Maintenance'),(NULL,'Industrial Engineering'),(NULL,'Quality'),(NULL,'Human Resource'),(NULL,'Lean')";
        String sql = "SELECT COUNT(*) as count FROM department";
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        if(rs.getInt("count") == 0){
            stmt.executeUpdate(department);
        }
    }
    /**
     * Creates department table.
     * <p>
     * Structure is:<br>
     * dept_id          INTEGER     Primary Key, Auto Incrementing<br>
     * name         VARCHAR  Department name.
     */
    public void createDeptTable() throws SQLException{
        String sql =    "CREATE TABLE IF NOT EXISTS department(\n" +
                        "	dept_id INTEGER NOT NULL AUTO_INCREMENT,\n" +      
                        "	name VARCHAR(255) NOT NULL,\n" + 
                        "	Primary Key(dept_id)\n" +
                        ")";

        Statement stmt = conn.createStatement();
        stmt.execute(sql);
    }
    /**
     * Creates problem table.
     * <p>
     * Structure is:<br>
     * prob_id          INTEGER     Primary Key, Auto Incrementing<br>
     * dept_id          INTEGER     Department of Problem<br>
     * name         VARCHAR  Problem name.
     */
    public void createProblemTable() throws SQLException{
        String sql =    "CREATE TABLE IF NOT EXISTS problem(\n" +
                        "	prob_id INTEGER NOT NULL AUTO_INCREMENT,\n" + 
                        "	dept_id INTEGER NOT NULL references department(dept_id),\n" +
                        "	name VARCHAR(255) NOT NULL,\n" +                     
                        "   FOREIGN KEY fk_problem(dept_id) \n"+
                        "   REFERENCES department(dept_id), \n"+
                        "	Primary Key(prob_id)\n" +
                        ")";

        Statement stmt = conn.createStatement();
        stmt.execute(sql);
    }
    /**
     * Creates designation table.
     * <p>
     * Structure is:<br>
     * desgn_id          INTEGER     Primary Key, Auto Incrementing<br>
     * name         VARCHAR  Designation name.<br>
     * level INTEGER Level of Designation
     */
    public void createDesignationTable() throws SQLException{
        String sql =    "CREATE TABLE IF NOT EXISTS designation(\n" +
                        "	desgn_id INTEGER NOT NULL AUTO_INCREMENT ,\n" +
                        "   name VARCHAR(255) NOT NULL,\n"+
                        "   level INTEGER(2) NOT NULL,\n"+
                        "   PRIMARY KEY(desgn_id)\n"+
                        ")";

       Statement stmt = conn.createStatement();
       stmt.execute(sql);
    }
    /**
     * Creates desgn_line table.This table contains the mapping of Designation and Line
     * <p>
     * Structure is:<br>
     * desgn_id   INTEGER<br>
     * line       INTEGER 
     */
    public void createDesignationLineTable() throws SQLException{
            String sql =    "CREATE TABLE IF NOT EXISTS desgn_line(\n" +
                            "	desgn_id INTEGER NOT NULL ,\n" +
                            "   line INTEGER(2) NOT NULL\n"+
                            ")";

           Statement stmt = conn.createStatement();
           stmt.execute(sql);
    }
    /**
     * Creates desgn_problem table.This table contains the mapping of Designation and Problem
     * <p>
     * Structure is:<br>
     * desgn_id   INTEGER<br>
     * prob_id       INTEGER 
     */
    public void createDesignationProblemTable() throws SQLException{
        String sql =    "CREATE TABLE IF NOT EXISTS desgn_problem(\n" +
                        "	desgn_id INTEGER NOT NULL ,\n" +
                        "   prob_id INTEGER NOT NULL\n"+
                        ")";

       Statement stmt = conn.createStatement();
       stmt.execute(sql);
    }
    /**
     * Creates issue table.Contains all the details a specific Issue Raised.
     * <p>
     * Structure is:<br>
     * issue_id         INTEGER     Primary Key<br>
     * line             INTEGER<br>
     * sec_id           INTEGER<br>
     * dept_id          INTEGER<br>
     * prob_id          INTEGER<br>
     * critical         INTEGER<br>
     * operator_no      INTEGER<br>
     * description      CHAR(3)<br>
     * raised_at        VARCHAR<br>
     * ack_at           TIMESTAMP<br>
     * fix_at           TIMESTAMP<br>
     * mod_at           TIMESTAMP<br>
     * raised_by        TIMESTAMP<br>
     * ack_by           INTEGER<br>
     * fix_by           INTEGER<br>
     * processing_at    INTEGER, Level to which notification has been delivered<br> 
     * status           INTEGER, values: 1 = open, 0 = closed<br>
     * seek_help        INTEGER, User level which has seeked help. Values: 0 = No one, 1 = Level 1, 2 = Level 2<br>
     */
    public void createIssueTable() throws SQLException{
        String sql =    "CREATE TABLE IF NOT EXISTS issue(\n" +
                        "	issue_id INTEGER NOT NULL AUTO_INCREMENT,\n" +
                        "	line INTEGER(2) NOT NULL,\n" +
                        "	sec_id INTEGER(3) NOT NULL,\n" +
                        "	dept_id INTEGER(3) NOT NULL,\n" +
                        "	prob_id INTEGER(3) NOT NULL,\n" +
                        "	critical CHAR(3) ,\n" +  // VALUES: Y/N
                        "	operator_no VARCHAR(255),\n" +

                        "	description TEXT,\n" +
                        "   raised_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                        "	ack_at TIMESTAMP NULL,\n" +
                        "	fix_at TIMESTAMP NULL,\n" +
                        "   mod_at TIMESTAMP NOT NULL,\n" + //modified at
                        "	raised_by INTEGER,\n" +  //user_id of USER
                        "	ack_by INTEGER,\n" +  //user_id of USER
                        "	fix_by INTEGER,\n" +  //user_id of USER
                        "	processing_at INTEGER(2),\n" +  //Level to which notification has been sent
                        "   status INTEGER NOT NULL,\n" +  // VALUES: 0 = closed 1 = open
                        "   seek_help INTEGER(1) DEFAULT 0,\n"+
                        "	Primary Key(issue_id)\n" +
                        ")";

        Statement stmt = conn.createStatement();
        stmt.execute(sql);    
    }
    
}
