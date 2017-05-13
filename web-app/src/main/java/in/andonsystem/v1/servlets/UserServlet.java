package in.andonsystem.v1.servlets;


import in.andonsystem.v1.models.Pair;
import in.andonsystem.v1.models.Preferences;
import in.andonsystem.v1.models.User;
import in.andonsystem.v1.services.DesignationService;
import in.andonsystem.v1.util.MiscUtil;
import in.andonsystem.v1.services.UserService;
import in.andonsystem.v1.util.ConnectionPool;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "UserServlet", urlPatterns = { "/user" })
public class UserServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user.getLevel() != 4) {
            response.sendRedirect("login.jsp");
        }

        RequestDispatcher dispatcher = null;
        Connection conn = null;
        try {
            ConnectionPool
                    .getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {

            UserService uService = new UserService(conn);
            DesignationService desgnService = new DesignationService(conn);

            String action = request.getParameter("action");
            //Redirect to user_mapping page when hyperlink of particular user is clicked
            if (action.equals("view")) {
                int user_id = Integer.parseInt(request.getParameter("user_id"));
                user = uService.getUser(user_id);
                request.setAttribute("user", user);
                request.setAttribute("action", "user_details");
                dispatcher = request.getRequestDispatcher("user_view.jsp");
            }

            if (action.equals("add")) {
                //Set app relaunched to sync user data in android app
                MiscUtil.getInstance().setConfigProperty(
                        Preferences.APP_LAUNCH,
                        String.valueOf(System.currentTimeMillis())
                );

                int userId = Integer.parseInt(request.getParameter("user_id"));
                String username = request.getParameter("username");
                String email = request.getParameter("email");
                String mobile = request.getParameter("mobile");
                int desgnId = Integer.parseInt(request.getParameter("desgn"));
                int userLevel = desgnService.getDesgnLevel(desgnId);
                user = new User(userId, username, email, userLevel, desgnId, mobile);

                int result = uService.addUser(user);


                if (result == 1) {
                    request.setAttribute("message", "User Registered successfully!!!");
                } else if (result == 2) {
                    request.setAttribute("message", "User already exist!!!");
                } else {
                    request.setAttribute("message", "Unable Register User!!!");
                }

                dispatcher = request.getRequestDispatcher("user_add.jsp");
            }
            //Remove User
            if (action.equals("remove")) {
                int user_id = Integer.parseInt(request.getParameter("user_id"));
                Boolean result = uService.removeUser(user_id);
                if (result) {
                    request.setAttribute("message", "User removed Successfully!!!");
                } else {
                    request.setAttribute("message", "Unable to remove User.");
                }
                dispatcher = request.getRequestDispatcher("user_remove.jsp");
            }

            //Display list of users When a level is Selected using Ajax technology
            if (action.equals("showUsers")) {
                int desgnId = Integer.parseInt(request.getParameter("desgnId"));
                String page = request.getParameter("page"); // value: viewPgae/removePage

                List<Pair<Integer, String>> list = uService.getUsers(desgnId);
                String respText = "";
                if (page.equals("viewPage")) {
                    respText = "<table class=\"table-bordered\" id=\"bordered-table\" width=\"100%\" >" +
                               "<tr>\n" +
                               "    <th width=\"35%\" class=\"align-center\">Employee Id</th>\n" +
                               "    <th class=\"align-center\">User Name</th>\n" +
                               "</tr>";

                    for (Pair<Integer, String> item : list) {
                        respText += "<tr>\n" +
                                    "       <td>" + item.getKey() + "</td>" +
                                    "       <td><a href=\"user?action=view&user_id=" + item.getKey() + "\" >" +
                                    item.getValue() + "</a></td>" +
                                    "</tr>";
                    }

                    respText += "</table>";
                }
                if (page.contains("remove")) {
                    respText = "<table class=\"table-bordered\" id=\"bordered-table\" width=\"100%\" >" +
                               "<tr>\n" +
                               "    <th width=\"35%\" class=\"align-center\">Employee Id</th>\n" +
                               "    <th class=\"align-center\">User Name</th>\n" +
                               "    <th class=\"align-center\">Action</th>\n" +
                               "</tr>";

                    for (Pair<Integer, String> item : list) {
                        respText += "<tr>\n" +
                                    "       <td>" + item.getKey() + "</td>" +
                                    "       <td>" + item.getValue() + "</td>" +
                                    "       <td><a class=\"btn btn-danger\" href=\"user?action=remove&user_id=" +
                                    item.getKey() + "\" >Remove</a></td>" +
                                    "</tr>";
                    }

                    respText += "</table>";
                }
                PrintWriter out = response.getWriter();
                response.setContentType("text/plain");
                out.print(respText);
                return;
            }

            if (action.equals("editProfile")) {
                int userId = Integer.parseInt(request.getParameter("userId"));

                String editUsername = request.getParameter("editUsername");
                String editEmail = request.getParameter("editEmail");
                String editMobile = request.getParameter("editMobile");
                String resetPassword = request.getParameter("resetPassword");

                Boolean result = false;
                if (editUsername != null) {
                    String username = request.getParameter("username");
                    result = uService.editUsername(userId, username);
                }
                if (editEmail != null) {
                    String email = request.getParameter("email");
                    result = uService.editEmail(userId, email);
                }
                if (editMobile != null) {
                    String mobile = request.getParameter("mobile");
                    result = uService.editMobile(userId, mobile);
                }
                if (resetPassword != null) {
                    String username = request.getParameter("username");
                    String mobile = request.getParameter("mobile");
                    if (uService.ResetPassword(userId, username, mobile)) {
                        request.setAttribute("message", "New Password Sent to mobile number " + mobile);
                    } else {
                        request.setAttribute("message", "Unable reset password.");
                    }
                }
                if (result) {
                    request.setAttribute("message", "Change saved successfully!!!");
                } else {
                    if (resetPassword == null) {
                        request.setAttribute("message", "Unable save Change, Try later.");
                    }
                }

                user = uService.getUser(userId);
                request.setAttribute("user", user);
                request.setAttribute("action", "user_details");
                dispatcher = request.getRequestDispatcher("user_view.jsp");
            }

            dispatcher.forward(request, response);
        } catch (SQLException e) {
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
