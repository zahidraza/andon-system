

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <title>ANDON SYSTEM</title>
        <%@include file="include/common.jsp" %>
        
        <link rel="stylesheet" type="text/css" href="index.css" />
    </head>
    <body>
        <%@include file="include/header.jsp" %>
        <div class="wrapper">
            <div class="content">
                <p class="red-color">Un-authorized Access. Login with appropriate privilege. </p>
            </div>
        </div>
        <%@include file="include/footer.jsp" %>
    </body>
</html>
