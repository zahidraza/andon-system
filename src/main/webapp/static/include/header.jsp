<nav class="navbar navbar-inverse navbar-fixed-top">
    <div class="container-fluid">
        <div class="navbar-header">
            <a class="navbar-brand" href="index.jsp">Andon System</a>
        </div>
            <ul class="nav navbar-nav">
                <li class="${pageContext.request.requestURI eq '/andon_system/index.jsp' ? 'active' : ''}"><a href="index.jsp">Home</a></li>
                <!-- If User is Admin-->
                <c:if test="${sessionScope.user.level == 4}" >              
                    <li class="${pageContext.request.requestURI eq '/andon_system/report.jsp' ? 'active' : ''}">
                        <a href="report.jsp" >Report</a>
                    </li>
                    <li class="${pageContext.request.requestURI eq '/andon_system/user_view.jsp' ? 'active' : ''}">
                        <a href="user_view.jsp" >Users</a>
                    </li>                                   
                </c:if>              
                 <!-- Mangement Level User-->
                <c:if test="${sessionScope.user.level == 3}" >
                    <li class="${pageContext.request.requestURI eq '/andon_system/report.jsp' ? 'active' : ''}">
                        <a href="report.jsp" >Report</a>
                    </li>                 
                </c:if>
                
                <li class="${pageContext.request.requestURI eq '/andon_system/section.jsp' ? 'active' : ''}">
                    <a href="section.jsp">Sections</a>
                </li>
                <li class="${pageContext.request.requestURI eq '/andon_system/department.jsp' ? 'active' : ''}">
                    <a href="department.jsp" >Departments</a>
                </li>
                <li class="${pageContext.request.requestURI eq '/andon_system/problem.jsp' ? 'active' : ''}">
                    <a href="problem.jsp" >Problems</a> 
                </li>
                <li >
                    <a href="download.jsp" >Downloads</a>
                </li>
                <li class="${pageContext.request.requestURI eq '/andon_system/faq.jsp' ? 'active' : ''}">
                    <a href="faq.jsp">FAQ</a>
                </li>
                <li class="${pageContext.request.requestURI eq '/andon_system/about.jsp' ? 'active' : ''}">
                    <a href="about.jsp">About</a>
                </li> 
          </ul>
          <ul class="nav navbar-nav navbar-right">
            
            <c:choose>
                <c:when test="${sessionScope.user != null}">
                    <li><a href="#" ><span class="glyphicon glyphicon-user"></span> ${sessionScope.user.username} </a></li>
                    <li><a href="logout"><span class="glyphicon glyphicon-log-out"></span> Logout&nbsp;&nbsp;&nbsp;&nbsp;</a></li>
                </c:when>
                <c:otherwise>
                    
                    <li><a href="login.jsp"><span class="glyphicon glyphicon-log-in"></span>  Login&nbsp;&nbsp;</a></li>
                </c:otherwise>
            </c:choose>
          </ul>
    </div>
</nav>