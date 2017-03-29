<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${sessionScope.user == null}" >
    <jsp:forward page="login.jsp" />
</c:if>
<!DOCTYPE html>
<html>
    <head>
        <title>Report</title>
        <%@include file="include/common.jsp" %>
        <link rel="stylesheet" type="text/css" href="index.css" />
        <link rel="stylesheet" href="//code.jquery.com/ui/1.12.0/themes/base/jquery-ui.css">
        <link rel="stylesheet" href="/resources/demos/style.css">
        <script src="https://code.jquery.com/jquery-1.12.4.js"></script>
        <script src="https://code.jquery.com/ui/1.12.0/jquery-ui.js"></script>
        <script>
            $( function() {
              $( "#datepicker1" ).datepicker();
            } );
            $( function() {
              $( "#datepicker2" ).datepicker();
            } );
        </script>
        <style>
            input{
                display:inline-block;
                margin: 5px auto;
                padding: 2px;
                font-size: 15px;
                width: 250px;
            }
            select{
                display:inline-block;
                margin: 5px auto;
                padding: 4px;
                font-size: 15px;
                width: 250px;
            }
            .report-container{
                height: 410px;
                overflow-x: scroll;
                overflow-y: scroll;
            }
            th{
                text-align: center;
            }
        </style>
    </head>
    <body>
        <%@include file="include/header.jsp" %>
        <div class="wrapper">
            <div class="content">
                <div>
                    <form action="web_report">
                       
                        Start Date:<input type="text" name="from" value="${requestScope.from != null ? requestScope.from : sessionScope.today}" id="datepicker1">
                        End Date:<input type="text" name="to" value="${requestScope.to != null ? requestScope.to : sessionScope.today}" id="datepicker2"><br>
                        <select name="line" >
                            <option value="0" ${requestScope.line == 0 ? 'selected' : ''} >All Lines</option>
                            <c:forEach var="line" begin="1" end="${applicationScope.lines}" step="1">
                                <option value="${line}" ${requestScope.line == line ? 'selected' : ''} >Line ${line}</option>
                            </c:forEach>   
                        </select>
                        <select name="secId" >
                            <option value="0" ${requestScope.secId == 0 ? 'selected' : ''}>All Sections</option>
                            <c:forEach items="${applicationScope.sections}" var="sec">
                                <option value="${sec.id}"  ${requestScope.secId == sec.id ? 'selected' : ''}>${sec.name}</option>
                            </c:forEach>   
                        </select>
                        <select name="deptId" >
                            <option value="0" ${requestScope.deptId == 0 ? 'selected' : ''}>All Departments</option>
                            <c:forEach items="${applicationScope.depts}" var="dept">
                                <option value="${dept.id}" ${requestScope.deptId == dept.id ? 'selected' : ''} >${dept.name}</option>
                            </c:forEach>   
                        </select>
                        <select name="critical" >
                            <option value="" ${requestScope.critical == '' ? 'selected' : ''}>Critical/Non-Critical</option>
                            <option value="YES" ${requestScope.critical == 'YES' ? 'selected' : ''}>Critical</option>
                            <option value="NO" ${requestScope.critical == 'NO' ? 'selected' : ''}>Non-Critical</option>
                            
                        </select>
                        <input type="text" name="operatorNo" value="${requestScope.operatorNo}"/><br>
                        <input type="submit" name="applyFilter" value="Apply Filter" />
                        <input type="submit" name="download" value="Download as Excel" />
                    </form>
                </div>
                <div class="report-container">
                    <table class="table-bordered" id="bordered-table" style="width: 2000px">
                        <tr>
                            <th width="100px">Date</th>
                            <th width="80px">Line</th>
                            <th width="100px">Section</th>
                            <th width="170px">Department</th>
                            <th width="180px">Problem</th>
                            <th width="100px">Critical</th>
                            <th width="100px">Operator No.</th>
                            <th width="250px">Remarks</th>
                            <th width="100px">Raised at</th>
                            <th width="150px">Raised by</th>
                            <th width="100px">Ack at</th>
                            <th width="150px">Ack by</th>
                            <th width="100px">Fixed at</th>
                            <th width="150px">fixed by</th>
                            <th width="90px">Downtime    (in minutes)</th>
                        </tr>
                        <c:forEach items="${requestScope.report}" var="data">
                            <tr>
                                <td>${data.date}</td>
                                <td>${data.line}</td>
                                <td>${data.section}</td>
                                <td>${data.dept}</td>
                                <td>${data.problem}</td>
                                <td>${data.critical}</td>
                                <td>${data.operatorNo}</td>
                                <td>${data.remarks}</td>
                                <td>${data.raisedAt}</td>
                                <td>${data.raisedBy}</td>
                                <td>${data.ackAt}</td>
                                <td>${data.ackBy}</td>
                                <td>${data.fixedAt}</td>
                                <td>${data.fixedBy}</td>
                                <td>${data.downtime}</td>
                            </tr>
                        </c:forEach>
                    </table>
                    
                </div>
            </div>
        </div>
        <%@include file="include/footer.jsp" %>
    </body>
</html>

