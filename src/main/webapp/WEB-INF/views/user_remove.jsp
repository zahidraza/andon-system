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
        <title>Remove User</title>
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
                    <a role="button" class="btn btn-primary" href="user_add.jsp">Register User</a>
                    <a role="button" class="btn btn-primary active" href="user_remove.jsp">Remove User</a>
                </div>

                <div style="width: 41%; margin: 20px auto" >


                    <p id="message">${requestScope.message}</p>
                    <div>
                        <select name="desgnId" class="form-control" onchange="showUsersRemove(this.value)">
                            <option value="" >Select Designation</option>
                            <c:forEach items="${applicationScope.designations}" var="desgn">
                                <option value="${desgn.desgnId}" >${desgn.name}</option>
                            </c:forEach>   
                        </select>

                    </div>
                    <!-- Content updated by Ajax technology --> 
                    <div id="display_users">

                    </div>


                </div>
            </div>
        </div>
        <%@include file="include/footer.jsp" %>       
    </body>
</html>
