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
        <title>Register User</title>
        <%@include file="include/common.jsp" %>
        
        <link rel="stylesheet" type="text/css" href="css/user.css" />
        <script type="text/javascript" src="scripts/user_ajax.js"></script>
    </head>
    <body>
        <%@include file="include/header.jsp" %>
        <div class="wrapper">
            <div class="content">
                <div class="btn-group">
                    <a role="button" class="btn btn-primary" href="user_view.jsp">View User</a>
                    <a role="button" class="btn btn-primary active" href="user_add.jsp">Register User</a>
                    <a role="button" class="btn btn-primary" href="user_remove.jsp">Remove User</a>
                </div>

                <div style="width: 50%; margin: 40px auto" >

                    <h4>User Registration Form</h4>

                    <p id="message">${requestScope.message}</p>
                    <form role="form" action="user" method="post">
                        <table id="add-tbl" style="width:60%;margin:20px auto">
                            <tr><td><input type="text" class="form-control" name="user_id" placeholder="Employee Id " /></td></tr>
                            <tr><td><input type="text" class="form-control" name="username" placeholder="Full Name " /></td></tr>
                            <tr><td><input type="text" class="form-control" name="email" placeholder="Email Id (optional) " /></td></tr>
                            <tr><td><input type="text" class="form-control" name="mobile" placeholder="Mobile number" /></td></tr>
                            <tr>
                                <td>
                                    <select name="desgn" class="form-control">
                                        <option value="" >Select Designation</option>
                                        <c:forEach items="${applicationScope.designations}" var="desgn">
                                            <option value="${desgn.desgnId}" >${desgn.name}</option>
                                        </c:forEach>   
                                    </select>
                                </td>
                            </tr>
                          
                            <tr><td><input type="submit" class="btn btn-block btn-success" name="add_btn" value="ADD USER" /></td></tr>

                            <tr><td><input type="hidden" name="action" value="add" /></td></tr>
                        </table>
                    </form>

                </div>
            </div>
        </div>
        <%@include file="include/footer.jsp" %>
    </body>
</html>
