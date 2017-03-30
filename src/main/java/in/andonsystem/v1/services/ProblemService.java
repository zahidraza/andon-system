/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.andonsystem.v1.services;


import in.andonsystem.v1.models.Pair;
import in.andonsystem.v1.models.Problem;
import in.andonsystem.v1.models.Tuple;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Md Zahid Raza
 */
public class ProblemService {
    
    private final String query1 = "SELECT user_id,username,designation FROM user WHERE level = ? AND user_id NOT IN(\n" +
                                "        SELECT user_id FROM user_problem WHERE prob_id = ?)  ";
    private final String query2 = "SELECT user_id,username,designation,level FROM user WHERE user_id  IN(\n" +
                                "        SELECT user_id FROM user_problem WHERE prob_id = ?) ORDER BY level ";
    
    Connection conn;

    public ProblemService(Connection conn){
        this.conn = conn;
    }
    
    //Get a Specific Problem
    public Problem getProblem(int prob_id) throws SQLException{
        Problem prob = null;
        
        String sql = "SELECT * FROM problem WHERE prob_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, prob_id);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            prob = new Problem(prob_id,rs.getInt("dept_id"),rs.getString("name"));
        }
        
        return prob;
    }
    //Get problems of a Department
    public List<Pair<String,List<String> > > getProbs() throws SQLException{
        DeptService dService = new DeptService(conn);
        List<Integer> deptIds = dService.getDeptIds();
        
        List<Pair<String, List<String> > > list = new ArrayList<>();
        
        String sql,deptName; 
        int deptId;
        PreparedStatement ps = null;
        Statement stmt = conn.createStatement();
        ResultSet rs;


        Iterator<Integer> itr = deptIds.iterator();
        while(itr.hasNext()){
            deptId = itr.next();
            deptName = dService.getDeptName(deptId);

            sql = "SELECT name FROM problem WHERE dept_id = " + deptId;
            rs = stmt.executeQuery(sql);

            List<String> probs = new ArrayList<>();
            while(rs.next()){
                probs.add(rs.getString("name"));
            }
            list.add(new Pair(deptName,probs));
        }

        return list;
    }
    //Get all Problems
    public List<Problem> getProblems() throws SQLException{
        List<Problem> list = new ArrayList<>();
        
        String sql = "SELECT * FROM problem ";
        PreparedStatement ps = conn.prepareStatement(sql);

        ResultSet rs = ps.executeQuery();

        while(rs.next()){
            list.add(new Problem(rs.getInt("prob_id"),rs.getInt("dept_id"),rs.getString("name")));
        }
        
        return list;
    }
   
    public int addProblem(int dept_id,String name) throws SQLException{
        int prob_id = 0;
        
        String sql = "INSERT INTO problem (dept_id,name) VALUES(?,?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, dept_id);
        ps.setString(2, name);
        ps.executeUpdate();

        sql = "SELECT prob_id FROM problem WHERE name = ?";
        PreparedStatement ps2 = conn.prepareStatement(sql);
        ps2.setString(1, name);
        ResultSet rs = ps2.executeQuery();
        if(rs.next()){
           prob_id = rs.getInt("prob_id");
        }
        
        return prob_id;
    }
    
    public String getProblemName(int prob_id) throws SQLException{
        String result = null;
        
            String sql = "SELECT name FROM problem WHERE prob_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, prob_id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                result = rs.getString("name");
            }

        return result;
    }
    
    public Boolean updateProblem(int prob_id,String name) throws SQLException{
        Boolean status = false;
        
        String sql = "UPDATE  problem SET name = ? WHERE prob_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, name);
        ps.setInt(2, prob_id);
        ps.executeUpdate();

        status = true;
        
        return status;
    }
    
    public Boolean mapProblem(int user_id,int prob_id) throws SQLException{
        Boolean status = false;
        
        String sql = "INSERT INTO user_problem VALUES(?,?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, user_id);
        ps.setInt(2, prob_id);
        ps.executeUpdate();

        status = true;
        
        return status;
    }
    
    public Boolean unmapProblem(int user_id,int prob_id)throws SQLException{
        Boolean status = false;
        
        String sql = "DELETE FROM user_problem WHERE user_id = ? AND prob_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, user_id);
        ps.setInt(2, prob_id);
        ps.executeUpdate();

        status = true;
        
        return status;
    }
    
    //Get unmapped Users to Problem of Particular Level
    public List<Tuple<Integer,String,String,String> > getUsersUnmapped(int level, int prob_id) throws SQLException{
        
        List<Tuple<Integer,String,String,String> > list = new ArrayList<Tuple<Integer,String,String,String> >();
        
        PreparedStatement ps = conn.prepareStatement(query1);
        ps.setInt(1, level);
        ps.setInt(2,prob_id);

        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            list.add(new Tuple(rs.getInt("user_id"),rs.getString("username"),rs.getString("designation"),null));
        }
        
        return list;
    }
    
    //Get all mapped users to Problem
    public List<Tuple<Integer,String,String,String> > getUsersMapped(int prob_id) throws SQLException{
        
        List<Tuple<Integer,String,String,String> > list = new ArrayList<Tuple<Integer,String,String,String> >();
        
        PreparedStatement ps = conn.prepareStatement(query2);
        ps.setInt(1,prob_id);

        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            list.add(new Tuple(rs.getInt("user_id"),rs.getString("username"),rs.getString("designation"),rs.getString("level")));
        }
       
        return list;
    }
    //To Remove a Problem, it must be first removed from child table user_problem, else will fail
    public Boolean removeProblem(int prob_id) throws SQLException{
        Boolean status = false;
        
        String sql = "DELETE FROM user_problem WHERE prob_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, prob_id);
        ps.executeUpdate();

        sql = "DELETE FROM problem WHERE prob_id = ?";
        PreparedStatement ps2 = conn.prepareStatement(sql);
        ps2.setInt(1, prob_id);
        ps2.executeUpdate();

        status = true;
        
        return status;
    }
    
    /*
    
    
    public List<Pair<Integer,String> > getProblem(int dept_id,int sec_id){
        
        List<Pair<Integer,String> > list = new ArrayList<Pair<Integer,String> >();
        
            String sql = "SELECT prob_id,name FROM problem WHERE dept_id = ? AND sec_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, dept_id);
            ps.setInt(2, sec_id);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                list.add(new Pair(rs.getInt("prob_id"),rs.getString("name")));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        
        return list;
    }
    
    public List<Problem> getTeams(int dept_id,int sec_id){
        
        List<Problem> list = new ArrayList<Problem>();
        
            String sql = "SELECT * FROM problem WHERE dept_id = ? AND sec_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, dept_id);
            ps.setInt(2, sec_id);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                list.add(new Problem(
                            rs.getInt("prob_id"),
                            rs.getInt("sec_id"),
                            rs.getInt("dept_id"),
                            rs.getString("name")
                        )
                );
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        
        return list;
    }
    
    public String getProblemName(int prob_id){
        String result = null;
        
            String sql = "SELECT name FROM problem WHERE prob_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, prob_id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                result = rs.getString("name");
            }
           
        }catch(SQLException e){
            e.printStackTrace();
        }
        return result;
    }
    
    
    
    public Boolean updateProblem(int prob_id,String name){
        Boolean status = false;
        
            String sql = "UPDATE  problem SET name = ? WHERE prob_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setInt(2, prob_id);
            ps.executeUpdate();
            
            status = true;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return status;
    }
    
    public List<Pair<Integer,String> > getProblemsForUser(int dept_id,int sec_id,int user_id){
        
        List<Pair<Integer,String> > list = new ArrayList<Pair<Integer,String> >();
        
            String sql =    "SELECT prob_id,name FROM problem \n" +
                            "WHERE dept_id = ? AND sec_id = ? AND prob_id NOT IN(\n" +
                            "	SELECT prob_id \n" +
                            "       FROM user_problem_rel \n" +
                            "       WHERE user_id = ?\n" +
                            ") ";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, dept_id);
            ps.setInt(2, sec_id);
            ps.setInt(3, user_id);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                list.add(new Pair(rs.getInt("prob_id"),rs.getString("name")));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        
        return list;
    }
    
    public List<Tuple<String,String,String,Integer> > getProblemsUserLinkedto(int user_id){
        
        List<Tuple<String,String,String,Integer> > list = new ArrayList<Tuple<String,String,String,Integer> >();
        
            String sql =    "SELECT d.name AS dept,s.name AS sec,p.name AS prob, p.prob_id \n" +
                            "FROM department d,section s,problem p ,user_problem_rel u\n" +
                            "WHERE p.prob_id = u.prob_id AND p.dept_id = d.dept_id AND p.sec_id = s.sec_id AND u.user_id = ?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, user_id);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                list.add(new Tuple(rs.getString("dept"),rs.getString("sec"),rs.getString("prob"),rs.getInt("prob_id")));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        
        return list;
    }
    
    public List<Tuple<String,String,String,Integer> > getProblemsUserNotLinkedto(int user_id){
        
        List<Tuple<String,String,String,Integer> > list = new ArrayList<Tuple<String,String,String,Integer> >();
        
            String sql =    "SELECT s.name as section,d.name as department,p.name as problem,p.prob_id "+
                            "FROM problem p,section s,department d\n" +
                            "WHERE p.sec_id = s.sec_id AND p.dept_id = d.dept_id AND prob_id NOT IN (\n" +
                            "       SELECT prob_id FROM user_problem_rel WHERE user_id = ?\n" +
                            ");";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, user_id);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                list.add(new Tuple(rs.getString("department"),rs.getString("section"),rs.getString("problem"),rs.getInt("prob_id")));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        
        return list;
    }
    
    public Boolean linkProblem(int user_id,int prob_id){
        Boolean status = false;
        
            String sql = "INSERT INTO user_problem_rel VALUES(?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, user_id);
            ps.setInt(2, prob_id);
            ps.executeUpdate();
            
            status = true;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return status;
    }
    
    public Boolean unLinkProblem(int user_id,int prob_id){
        Boolean status = false;
        
            String sql = "DELETE FROM user_problem_rel WHERE user_id = ? AND prob_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, user_id);
            ps.setInt(2, prob_id);
            ps.executeUpdate();
            
            status = true;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return status;
    }
    */
    
    
}
