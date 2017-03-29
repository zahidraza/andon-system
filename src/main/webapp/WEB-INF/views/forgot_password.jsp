<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <title>ANDON SYSTEM</title>
        <%@include file="include/common.jsp" %>
        
        <link rel="stylesheet" type="text/css" href="index.css" />
        <script type="text/javascript">
            function check(){
                var regex = /[0-9]{5}/;
                var userId = document.form.userId.value;
                if( userId==="" ){
                    document.form.userId.focus();
                    alert('Enter Employee Id');
                    return false;
                }else if(!regex.test(userId)){
                    document.form.userId.focus();
                    alert('Incorrect Employee Id. It should be numeric value of 5 digits ');
                    return false;
    }
            }
        </script>
    </head>
    <body>
        <%@include file="include/header.jsp" %>
        <div class="wrapper">
            <div class="content">
                <div style="width:30%;margin: 20px auto">
                    <h2>Password Reset</h2>
                    <p>${requestScope.message}</p>
                    <form name="form" action="forgot_password" onsubmit="return check()" >
                        <input type="text" name="userId" placeholder="Enter Employee Id"  style="width: 250px;padding: 5px;margin-top: 30px" />
                        <input type="submit" value="Submit" style="width: 100px;padding: 5px" />
                    </form>
                </div>
            </div>
        </div>
        <%@include file="include/footer.jsp" %>
        
    </body>
</html>
