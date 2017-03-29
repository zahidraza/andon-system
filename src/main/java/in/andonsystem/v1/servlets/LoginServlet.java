/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.andonsystem.v1.servlets;


import in.andonsystem.v1.models.User;
import in.andonsystem.v1.util.MiscUtil;
import in.andonsystem.v1.services.UserService;
import in.andonsystem.v1.util.ConnectionPool;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Md Jawed Akhtar
 */
@WebServlet(name = "LoginServlet", urlPatterns = { "/login" })
public class LoginServlet extends HttpServlet {


    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Connection conn = null;
        try {
            ConnectionPool
                    .getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        UserService userService = new UserService(conn);
        RequestDispatcher dispatcher;
        try {
            int user_id = Integer.parseInt(request.getParameter("user_id"));
            String password = request.getParameter("password");
            int user_level = Integer.parseInt(request.getParameter("user_level"));

            Boolean result = userService.authUser(user_id, password, user_level);

            if (result) {
                //System.out.println("login Successfull!");
                User user = userService.getUser(user_id);
                request.getSession().setAttribute("user", user);
                DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                String today = df.format(new Date(System.currentTimeMillis()));
                request.getSession().setAttribute("today", today);
                dispatcher = request.getRequestDispatcher("index.jsp");
            } else {
                request.setAttribute("loginMessage", "Incorrect User ID or Password, Try again!!!");
                dispatcher = request.getRequestDispatcher("login.jsp");
            }

            dispatcher.forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
