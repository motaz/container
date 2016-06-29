/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.stpanel.pages;

import sd.code.stpanel.common.General;
import sd.code.stpanel.common.Web;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author motaz
 */
@WebServlet(name = "AddPBX", urlPatterns = {"/AddPBX"})
public class AddPBX extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
           try{ 
              Web.setHeader(true, request, response, out, "", "");
            
              String user = Web.getCookieValue(request, "user");

              if (Web.checkSession(request, user)){
                
                 addNewPBX(request, out, response); 
                 out.println("<h2>Add new PBX for administration</h2>");
               
                 displayAdd(out);
              }
              else
              {
                 response.sendRedirect("Login");
              }
        
              Web.setFooter(out);
           
        } catch (Exception ex){
            out.println("<p class=errormessage>" + ex.toString() + "</p>");
        }
           
        
        
    }

    private void addNewPBX(HttpServletRequest request, final PrintWriter out, HttpServletResponse response) throws IOException {
        
        if (request.getParameter("add") != null) {
            String title = request.getParameter("title");
            String url = request.getParameter("url");
            String fileName = request.getParameter("file");
            
            if ((title.trim().isEmpty()) || (url.trim().isEmpty()) || 
                    (fileName.trim().isEmpty())) {
                out.println("<p class=errormessage>Empty parameter</p>");
            }
            else { // Create new file
                
                String dir = General.getPBXsDir();
            
                if (!fileName.contains(".")) {
                    fileName = fileName + ".stc";
                }
              
                fileName = dir + fileName;
                out.println(fileName);
                File configFile = new File(fileName);
                configFile.createNewFile();
                boolean success = General.setConfigurationParameter("url", url, fileName);
                if (success) {
                    success = General.setConfigurationParameter("title", title, fileName);
                }
                if (success) {
                    response.sendRedirect("Home");
                }
                else {
                    out.println("<p class=errormessage>Unable to write configuration</p>");
                }
                
            }
        }
    }

    private void displayAdd(final PrintWriter out) {
        
        out.println("<form method=POST>");
        out.println("<table>");
        out.println("<tr><td>Title </td><td><input type=text name=title /></td></td>");
        out.println("<tr><td>Config file name </td>");
        out.println("<td><input type=text name=file /></td></td>");
        out.println("<tr><td>ST Agent URL</td>");
        out.println("<td><input type=text name=url size=30 value='" +
                      "http://localhost:8080/STAgent/' /></td></tr>");
        out.println("<tr><td><input type=submit name=add value=Add /></td></tr>");
        out.println("</table>");
        out.println("</form>");
        
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
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
