<%-- 
    Document   : admin_login
    Created on : 13 Jan, 2016, 4:41:53 PM
    Author     : Md Zahid Raza
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <title>User Login</title>
        <%@include file="include/common.jsp" %>
        
        <link rel="stylesheet" type="text/css" href="index.css" />
        <script type="text/javascript" src="index.js" ></script>
        
    </head>
    <body>
        <%@include file="include/header.jsp" %>
        
        <div class="wrapper">
            <div class="content">
                <div> 
                    <h4>Login Page</h4>
                    
                    <p id="errorBox"></p>
                    
                    <c:if test="${requestScope.loginMessage != null}" >
                        <p id="message"> ${requestScope.loginMessage} </p>
                    </c:if>
                    <form name="form" action="login" method="post" onsubmit="return Login()">  
                        
                        <table id="login_table">                       
                            <tr>
                                <td width=120><label id="log_label">User ID</label></td>
                                <td><input type="text" value="" name="user_id" placeholder="Enter Employee ID"></td>
                            </tr>
                            <tr>
                                <td ><label id="log_label">Password</label></td>
                                <td> <input type="password" value="" name="password" placeholder="Password"></td>
                            </tr>
                            <tr>
                                <td ><label id="log_label">User Role</label></td>
                                <td> 
                                    <SELECT name="user_level" style="width: 250px; padding: 3px; font-size: 16px;margin: 5px 0px;">
                                        <option value="" selected > Select Role </option>
                                        <option value="3">Management</option>                   
                                        <option value="4">Admin</option>
                                    </SELECT>
                                </td>
                            </tr>

                            <tr>
                                <td colspan=2 style="text-align: center;padding: 20px"> <a href="forgot_password.jsp">Forgot Password?</a></td>
                            </tr>
                            <tr>
                                <td align="center" colspan=2 >
                                    <button  type="submit" class="btn btn-primary" >Login</button>
                                </td>
                            </tr>
                        </table>
                    </form>
                </div>
                
            </div>
        </div>
        <%@include file="include/footer.jsp" %>
    </body>
</html>
