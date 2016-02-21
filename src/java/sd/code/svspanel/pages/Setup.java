/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.svspanel.pages;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sd.code.svspanel.common.General;
import sd.code.svspanel.common.Web;

/**
 *
 * @author motaz
 */
@WebServlet(name = "Setup", urlPatterns = {"/Setup"})
public class Setup extends HttpServlet {

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
        try (PrintWriter out = response.getWriter()) {
            Web.setHeader(false, request, response, out, "", "");
            String currentLogin = General.getConfigurationParameter("login", null, null);
            if (request.getParameter("login") != null){
                try {
                    General.setConfigurationParameter("login", request.getParameter("login"), "");
                    String pass = General.getMD5(request.getParameter("pass"));
                    General.setConfigurationParameter("pass", pass, "");
                    response.sendRedirect("Login");
                }
                catch (Exception ex){
                    out.println("<p class=errormessage>" + ex.toString() + "</p>");
                }
                
            }
            if (currentLogin == null){
                out.println("<h3>SimpleTrunk Panel setup");
                out.println("<h3><font color=blue>Create New Admin Login</font></h3>");
                out.println("<form method=POST>");
                out.println("<table><tr>");
                out.println("<td>Login </td><td><input type=text name=login id=login /></td></tr>");
                out.println("<tr><td>Password </td><td><input type=password name=pass /></td></tr>");
                out.println("<tr><td><input type=submit name=log value=Login /></td></tr>");
                out.println("</table></form>");
                out.println("<script>document.getElementById('login').focus();</script>");            
            }
            Web.setFooter(out);

        }
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
