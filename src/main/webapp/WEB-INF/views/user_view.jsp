<%@page import="java.sql.SQLException"%>
<%@page import="java.sql.Connection"%>
<%@page import="in.andonsystem.DBase"%>
<%@page import="in.andonsystem.services.DesignationService"%>
<%@page import="in.andonsystem.models.User"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${sessionScope.user == null}" >
    <jsp:forward page="login.jsp" />
</c:if>
<c:if test="${sessionScope.user.level != 4}" >
    <jsp:forward page="unauthorized.jsp" />
</c:if>

<!DOCTYPE html>
<html>
    <head>
        <title>View User</title>
        <%@include file="include/common.jsp" %>
        
        <link rel="stylesheet" type="text/css" href="css/user.css" />
        <script type="text/javascript" src="scripts/user_ajax.js"></script>
        <script type="text/javascript">
            function editUsername(){
                var userId = document.getElementById("username");
                var userbtn = document.getElementById("userbtn");
                var username = userId.innerHTML;
                userId.innerHTML = '<input type="text" name="username" value="'+username +'" class="form-control" />';
                userbtn.innerHTML = '<input type="submit" name="editUsername" value="Save" class="btn btn-default btn-block" />'
                return false;
            }
            function editEmail(){
                var emailId = document.getElementById("email");
                var emailbtn = document.getElementById("emailbtn");
                var email = emailId.innerHTML;
                emailId.innerHTML = '<input type="text" name="email" value="'+email +'" class="form-control" />';
                emailbtn.innerHTML = '<input type="submit" name="editEmail" value="Save" class="btn btn-default btn-block" />'
                return false;
            }
            function editMobile(){
                var mobileId = document.getElementById("mobile");
                var mobilebtn = document.getElementById("mobilebtn");
                var mobile = mobileId.innerHTML;
                mobileId.innerHTML = '<input type="text" name="mobile" value="'+mobile +'" class="form-control" />';
                mobilebtn.innerHTML = '<input type="submit" name="editMobile" value="Save" class="btn btn-default btn-block" />'
                return false;
            }
        </script>

    </head>
    <body>
        <%@include file="include/header.jsp" %>
        <div class="wrapper">
            <div class="content">
                <div class="btn-group">
                    <a role="button" class="btn btn-primary active" href="user_view.jsp">View User</a>
                    <a role="button" class="btn btn-primary" href="user_add.jsp">Register User</a>
                    <a role="button" class="btn btn-primary" href="user_remove.jsp">Remove User</a>
                </div>

                <div style="width: 41%; margin: 20px auto" >

                    <c:if test="${requestScope.action == null}">
                        <p id="message">${requestScope.message}</p>
                        <div>
                            <select name="desgnId" class="form-control" onchange="showUsersView(this.value)">
                                <option value="" >Select Designation</option>
                                <c:forEach items="${applicationScope.designations}" var="desgn">
                                    <option value="${desgn.desgnId}" >${desgn.name}</option>
                                </c:forEach>   
                            </select>

                        </div>
                        <!-- Content updated by Ajax technology --> 
                        <div id="display_users">

                        </div>
                    </c:if>
                    <c:if test="${requestScope.action eq 'user_details'}">
                        <form action="user" method="post">
                            <input type="hidden" name="action" value="editProfile" />
                            <input type="hidden" name="userId" value="${requestScope.user.userId}" />
                            <h3>User Details</h3>
                            <p id="message">${requestScope.message}</p>
                            
                            <table class="table-bordered" id="bordered-table"  style="width:100%;margin: 20px auto;text-align: left;">

                                <tr>
                                    <td width="25%" class="bold">User Name</td>
                                    <td width="60%" id="username">${requestScope.user.username}</td>
                                    <td width="15%" id="userbtn"><button onclick="return editUsername()" class="btn btn-default btn-block">edit</button></td>
                                </tr>
                                <tr>
                                    <td class="bold">Employee Id</td>
                                    <td>${requestScope.user.userId}</td>
                                    <td></td>
                                </tr>
                                <tr>
                                    <td class="bold">Email</td>
                                    <td id="email">${requestScope.user.email}</td>
                                    <td id="emailbtn"><button onclick="return editEmail()" class="btn btn-default btn-block">edit</button></td>
                                </tr>
                                <tr>
                                    <td class="bold">Mobile No.</td>
                                    <td id="mobile">${requestScope.user.mobile}</td>
                                    <td id="mobilebtn"><button onclick="return editMobile()" class="btn btn-default btn-block">edit</button></td>
                                </tr>
                                <tr>
                                    <td class="bold">User Level</td>
                                    <td>Level ${requestScope.user.level}</td>
                                    <td></td>
                                </tr>
                                <tr>
                                    <td class="bold">Designation</td>
                                    <td>
                                    <%
                                        Connection conn = DBase.getConn();
                                        User user = (User)request.getAttribute("user");
                                        DesignationService dService = new DesignationService(conn);
                                        String desgnName = null;
                                        try{
                                            desgnName = dService.getDesgnName(user.getDesgnId());
                                        }catch(SQLException e){
                                            e.printStackTrace();
                                        }finally{
                                            try{
                                                conn.close();
                                            }catch(Exception ex){
                                                ex.printStackTrace();
                                            }
                                        }
                                        out.print(desgnName);
                                    %>
                                    </td>
                                    <td></td>
                                </tr>
                            </table>
                            <a href="user?action=editProfile&resetPassword=NotNull&userId=${requestScope.user.userId}&username=${requestScope.user.username}&mobile=${requestScope.user.mobile}" class="btn btn-default">Reset Password</a>       
                            
                        </form>
                    </c:if>


                </div>
            </div>
        </div>
        <%@include file="include/footer.jsp" %>
    </body>
</html>
