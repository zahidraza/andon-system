/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.andonsystem.v1.servlets;


import in.andonsystem.v1.models.User;
import in.andonsystem.v1.services.DesignationService;
import in.andonsystem.v1.util.ConnectionPool;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Md Zahid Raza
 */
@WebServlet(name = "DesignationServlet", urlPatterns = { "/designation" })
public class DesignationServlet extends HttpServlet {

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
        User user = (User) request.getSession().getAttribute("user");
        if (user.getLevel() != 4) {
            response.sendRedirect("login.jsp");
        }

        Connection conn = null;
        try {
            ConnectionPool
                    .getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {

            DesignationService dService = new DesignationService(conn);

            String name = request.getParameter("name");
            int level = Integer.parseInt(request.getParameter("level"));

            //Save Designation
            int desgnId = dService.saveDesignation(name, level);

            String lineStr = request.getParameter("lines");
            String probStr = request.getParameter("probs");

            //If no lines means Level 0 user. need not be mapped
            if (lineStr != null) {
                String[] lines = lineStr.split(",");
                String[] probs = probStr.split(",");
                //map Problem
                for (String prob : probs) {
                    int probId = Integer.parseInt(prob);
                    dService.mapProblem(desgnId, probId);
                }

                //map Lines
                for (String l : lines) {
                    int line = Integer.parseInt(l);
                    dService.mapLine(desgnId, line);
                }

            }

            PrintWriter out = response.getWriter();
            out.println("Success");
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
