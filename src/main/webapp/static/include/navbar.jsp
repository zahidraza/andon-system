<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<nav id="nav" class="navbar navbar-inverse ">
    
    <div class="row">
        <div class="col-md-8">
            <ul class="nav navbar-nav">
                <li class="${pageContext.request.requestURI eq '/index.jsp' ? 'active' : ''}" >
                    <a href="index.jsp">Home</a>
                </li>
                
                <!-- If User is Admin-->
                <c:if test="${sessionScope.user.level == 4}" >              
                    <li >
                        <a href="report.jsp" >Report</a>
                    </li>
                    <li >
                        <a href="user_view.jsp" >Users</a>
                    </li>                                   
                </c:if>
                  
                 <!-- Mangement Level User-->
                <c:if test="${sessionScope.user.level == 3}" >
                    <li >
                        <a href="report.jsp" >Report</a>
                    </li>                 
                </c:if>
                
                <li>
                    <a href="section.jsp">Sections</a>
                </li>
                <li >
                    <a href="department.jsp" >Departments</a>
                </li>
                <li >
                    <a href="problem.jsp" >Problems</a> 
                </li>
                <li>
                    <a href="download.jsp" >Downloads</a>
                </li>
                
                <li >
                    <a href="#">FAQ</a>
                </li>
                <li >
                    <a href="#">About</a>
                </li>
            </ul>
        
        </div>
        <div class="col-md-4">
            <ul class="nav navbar-nav navbar-right">
                <c:choose>
                    <c:when test="${sessionScope.user != null}">
                        <li><a href="#" > Hello, ${sessionScope.user.username} </a></li>
                        <li><a href="logout">Logout&nbsp;&nbsp;&nbsp;&nbsp;</a></li>
                    </c:when>
                    <c:otherwise>
                        <li align="left"><a href="login.jsp">Login&nbsp;&nbsp;&nbsp;&nbsp;</a></li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
    </div>
</nav>


