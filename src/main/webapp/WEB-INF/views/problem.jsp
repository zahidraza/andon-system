<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Problems</title>
        <%@include file="include/common.jsp" %>       
        <link rel="stylesheet" type="text/css" href="css/problem.css" />
        <script type="text/javascript" src="scripts/problem_ajax.js"></script>
    </head>
    <body>
        <%@include file="include/header.jsp" %>
        <div class="wrapper">
            <div class="content">
                <div >
                    <h4>Problems</h4>

                    <div style="width: 44%; margin: 20px auto" >
                    
                        
                        <c:forEach items="${applicationScope.problems}" var="dept">
                            <h4>Department : ${dept.key}</h4>
                            <table class="table-bordered" id="bordered-table" style="width: 75%;margin: auto">
                                <c:forEach items="${dept.value}" var="prob" varStatus="status">
                                    <tr>
                                        <td>${status.index + 1}</td>
                                        <td>${prob}</td>
                                    </tr>
                                </c:forEach>
                            </table>
                        </c:forEach>
                       
                    </div>
                </div>
            </div>
        </div>
        <%@include file="include/footer.jsp" %>
        
    </body>
</html>
