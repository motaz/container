/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.svspanel.pages;

import sd.code.svspanel.common.General;
import sd.code.svspanel.common.Web;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author motaz
 */
@WebServlet(name = "ChangePassword", urlPatterns = {"/ChangePassword"})
public class ChangePassword extends HttpServlet {

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
            Web.setHeader(true, request, response, out, "myadmin");
            
            String user = Web.getCookieValue(request, "user");

            if (Web.checkSession(request, user)){
                // Web.displayConfigTabs(out, "ChangePassword", userID);
                displayChangePassword(request, out, user);
                
                String reverse = request.getParameter("reverse");
                if ( reverse != null){
                        Cookie co = new Cookie("reverse", reverse);
                        co.setMaxAge(60 * 60 * 24 * 7);
                        response.addCookie(co);
                    }
                else {
                    reverse = Web.getCookieValue(request, "reverse");
                }
                
                if (reverse == null || reverse.equals("no")) {
                
                  out.println("<a href='ChangePassword?reverse=yes' class=btn>Reverse SIP/Dialplan orders</a>");
                }
                else
                {
                   out.println("<a href='ChangePassword?reverse=no' class=btn>Unreverse SIP/Dialplan orders</a>");
                }
            }
            else
            {
                response.sendRedirect("Login");
            }
                  
              
           Web.setFooter(out);
        }
    
    }
    private void displayChangePassword(HttpServletRequest request, PrintWriter out, String user) {
        try {
           if (request.getParameter("resetpassword") != null) {
             if (request.getParameter("newpassword").isEmpty()) {
                out.print("<p class=errormessage>Empty password</p>");
             }
             else  if (!request.getParameter("newpassword").equals(request.getParameter("confirmpassword"))) {
                out.print("<p class=errormessage>Passwords do not match</p>");
            }
            else // Change password
            {
                 
                 String configPass = General.getConfigurationParameter("pass", "", null);
                 
                 if (! configPass.equals(General.getMD5(request.getParameter("oldpassword")))) {
                     out.println("<p class=errormessage>Invalid password</p>");
                 }
                 else {
                     boolean res = General.setConfigurationParameter("pass", General.getMD5(request.getParameter("newpassword")), null);
                     
                     if (res){
                         out.println("<p class=infomessage>Password has been changed, you need to relogin</p>");
                     }
                     else {
                         out.println("<p class=errormessage>Unable to change password</p>");
                     }
                 }

                
            }
                
          }
            
        
          out.println("<h3>Change my password</h3>");
          out.println("<form method=post>");
          out.println("<table>");
          out.println("<tr><td>Old password </td><td><input type=password name=oldpassword /></rd></tr>");
          out.println("<tr><td>New password </td><td><input type=password name=newpassword /></td></tr>");
          out.println("<tr><td>Confirm password </td><td><input type=password name=confirmpassword /></td></tr>");
          out.println("<tr><td><input type=submit name=resetpassword value=Change class=button /></td></tr>");
          out.println("</table></form>");
          out.println("<hr>");
          
        } catch (Exception ex) {
            General.writeEvent(ex.toString(), "");
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
