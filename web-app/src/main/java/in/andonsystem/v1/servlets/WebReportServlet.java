/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.andonsystem.v1.servlets;


import in.andonsystem.v1.models.ReportWeb;
import in.andonsystem.v1.models.User;
import in.andonsystem.v1.services.IssueService;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import in.andonsystem.v1.util.ConnectionPool;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author Administrator
 */
@WebServlet(name = "WebReportServlet", urlPatterns = { "/web_report" })
public class WebReportServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (!(user.getLevel() == 4 || user.getLevel() == 3)) {
            response.sendRedirect("login.jsp");
        }

        Connection conn = null;
        try {
            ConnectionPool
                    .getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String applyFilter = request.getParameter("applyFilter");
        String download = request.getParameter("download");

        String from = request.getParameter("from");
        String to = request.getParameter("to");
        int line = Integer.parseInt(request.getParameter("line"));
        int secId = Integer.parseInt(request.getParameter("secId"));
        int deptId = Integer.parseInt(request.getParameter("deptId"));
        String critical = request.getParameter("critical");
        String opNo = request.getParameter("operatorNo");
        int operatorNo = -1;
        if (!opNo.equals("")) {
            operatorNo = Integer.parseInt(opNo);
        }
        try {
            IssueService iService = new IssueService(conn);
            List<ReportWeb> report = iService.getWebReport(from, to, line, secId, deptId, operatorNo, critical);

            if (applyFilter != null) {
                request.setAttribute("report", report);
                request.setAttribute("from", from);
                request.setAttribute("to", to);
                request.setAttribute("line", line);
                request.setAttribute("secId", secId);
                request.setAttribute("deptId", deptId);
                request.setAttribute("critical", critical);
                request.setAttribute("operatorNo", opNo);

                RequestDispatcher rd = request.getRequestDispatcher("report.jsp");
                rd.forward(request, response);
            }

            if (download != null) {
                String filename = null;

                DateFormat df1 = new SimpleDateFormat("MM/dd/yyyy");
                DateFormat df2 = new SimpleDateFormat("ddMM");

                StringBuilder sb = new StringBuilder("Report_");
                sb.append(df2.format(df1.parse(from)));
                sb.append("_");
                sb.append(df2.format(df1.parse(to)));
                sb.append("_");
                sb.append(line);
                sb.append(secId);
                sb.append(deptId);
                sb.append(".xlsx");
                filename = sb.toString();

                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader("Content-Disposition", "attachment; filename=" + filename);
                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet sheet = workbook.createSheet("Sheet 1");

                XSSFCreationHelper helper = workbook.getCreationHelper();
                XSSFCellStyle styleDate = workbook.createCellStyle();
                styleDate.setDataFormat(helper.createDataFormat().getFormat("dd MMMM yyyy"));

                XSSFCellStyle styleAlignCenter = workbook.createCellStyle();
                styleAlignCenter.setAlignment(HorizontalAlignment.CENTER);

                //Add Lebels
                XSSFRow row = sheet.createRow(0);
                //row.setRowStyle(styleRowBAckground);
                setLabel(row, workbook);

                int rowId = 1;
                XSSFCell cell;
                for (ReportWeb rep : report) {
                    row = sheet.createRow(rowId);

                    cell = row.createCell(0);
                    cell.setCellValue(rep.getDate().getDate());
                    cell.setCellStyle(styleDate);

                    cell = row.createCell(1);
                    cell.setCellValue(rep.getLine());
                    cell.setCellStyle(styleAlignCenter);

                    cell = row.createCell(2);
                    cell.setCellValue(rep.getSection());

                    cell = row.createCell(3);
                    cell.setCellValue(rep.getDept());

                    cell = row.createCell(4);
                    cell.setCellValue(rep.getProblem());

                    cell = row.createCell(5);
                    cell.setCellValue(rep.getCritical());
                    cell.setCellStyle(styleAlignCenter);

                    cell = row.createCell(6);
                    cell.setCellValue(rep.getOperatorNo());
                    cell.setCellStyle(styleAlignCenter);

                    cell = row.createCell(7);
                    cell.setCellValue(rep.getRemarks());

                    cell = row.createCell(8);
                    cell.setCellValue(rep.getRaisedAt());
                    cell.setCellStyle(styleAlignCenter);

                    cell = row.createCell(9);
                    cell.setCellValue(rep.getRaisedBy());

                    cell = row.createCell(10);
                    cell.setCellValue(rep.getAckAt());
                    cell.setCellStyle(styleAlignCenter);

                    cell = row.createCell(11);
                    cell.setCellValue(rep.getAckBy());

                    cell = row.createCell(12);
                    cell.setCellValue(rep.getFixedAt());
                    cell.setCellStyle(styleAlignCenter);

                    cell = row.createCell(13);
                    cell.setCellValue(rep.getFixedBy());

                    cell = row.createCell(14);
                    cell.setCellValue(rep.getDowntime());

                    rowId++;
                }

                sheet.setColumnWidth(4, 5000);
                sheet.setColumnWidth(7, 8000);
                sheet.setColumnWidth(9, 4000);
                sheet.setColumnWidth(11, 4000);
                sheet.setColumnWidth(13, 4000);

                for (int i = 0; i <= 14; i++) {
                    if (i == 1 || i == 7 || i == 4 || i == 9 || i == 11 || i == 13) {
                        continue;
                    }
                    sheet.autoSizeColumn(i);
                }

                OutputStream out = response.getOutputStream();
                workbook.write(out);
                out.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


    }

    private void setLabel(XSSFRow row, XSSFWorkbook workbook) {
        XSSFFont boldFont = workbook.createFont();
        boldFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        XSSFCellStyle boldOnly = workbook.createCellStyle();
        boldOnly.setFont(boldFont);

        XSSFCellStyle centerAndBold = workbook.createCellStyle();
        centerAndBold.setAlignment(HorizontalAlignment.CENTER);
        centerAndBold.setFont(boldFont);

        Cell cell;
        cell = row.createCell(0);
        cell.setCellValue("Date");
        cell.setCellStyle(centerAndBold);

        cell = row.createCell(1);
        cell.setCellValue("Line");
        cell.setCellStyle(centerAndBold);


        cell = row.createCell(2);
        cell.setCellValue("Section");
        cell.setCellStyle(boldOnly);

        cell = row.createCell(3);
        cell.setCellValue("Department");
        cell.setCellStyle(boldOnly);

        cell = row.createCell(4);
        cell.setCellValue("Problem");
        cell.setCellStyle(boldOnly);

        cell = row.createCell(5);
        cell.setCellValue("Critical");
        cell.setCellStyle(centerAndBold);

        cell = row.createCell(6);
        cell.setCellValue("Operator No.");
        cell.setCellStyle(centerAndBold);

        cell = row.createCell(7);
        cell.setCellValue("Remarks");
        cell.setCellStyle(boldOnly);

        cell = row.createCell(8);
        cell.setCellValue("Raised at");
        cell.setCellStyle(centerAndBold);

        cell = row.createCell(9);
        cell.setCellValue("Raised by");
        cell.setCellStyle(boldOnly);

        cell = row.createCell(10);
        cell.setCellValue("Ack at");
        cell.setCellStyle(centerAndBold);

        cell = row.createCell(11);
        cell.setCellValue("Ack by");
        cell.setCellStyle(boldOnly);

        cell = row.createCell(12);
        cell.setCellValue("Fixed at");
        cell.setCellStyle(centerAndBold);

        cell = row.createCell(13);
        cell.setCellValue("Fixed by");
        cell.setCellStyle(boldOnly);

        cell = row.createCell(14);
        cell.setCellValue("Downtime (in min)");
        cell.setCellStyle(centerAndBold);

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
