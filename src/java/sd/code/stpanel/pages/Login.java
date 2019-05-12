/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.stpanel.pages;

import sd.code.stpanel.common.General;
import sd.code.stpanel.common.Web;
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
@WebServlet(name = "Login", urlPatterns = {"/Login"})
public class Login extends HttpServlet {

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
            Web.setHeader(false, request, response, out, "", "");
            
            if (request.getParameter("log") != null) {
                
                String password = request.getParameter("pass");
                
                String configUser = General.getConfigurationParameter("login", "", null);
                String configPass = General.getConfigurationParameter("pass", "", null);
                
                boolean valid = configUser.toLowerCase().equals(request.getParameter("login").toLowerCase());
                
                if (valid){
                    valid = configPass.equals(General.getMD5(password));
                }
                
                if (valid) {
                    
                    Cookie coo = new Cookie("user", configUser);
                    coo.setMaxAge(60 * 60 * 24 * 7);                
                    response.addCookie(coo);


                    String spices;
                    String remoteAddress = request.getRemoteAddr();
                    String userAgent = request.getHeader("user-agent");
                    spices = General.getMD5(General.getMD5(userAgent + "7n1" + General.getMD5(password) +
                      remoteAddress + "77") + "0066");
           
                    Cookie coo2 = new Cookie("spices", spices);
                    coo2.setMaxAge(60 * 60 * 24 * 7);                
                    response.addCookie(coo2);
                    
                    response.sendRedirect("Home");
                }
                else {
                    out.println("<p class=errormessage>Invalid login/password</p>");
                }
            }
            
            String user = General.getConfigurationParameter("login", null, null);
            if (user == null) {
                out.println("<p class=warnmessage>Configuration file is missing<br/>");  
                response.sendRedirect("Setup");
            }
            else {
                out.println("<h2>Login</h2>");
                out.println("<form method=POST>");
                out.println("<table><tr>");
                out.println("<td>Login </td><td><input type=text name=login id=login /></td></tr>");
                out.println("<tr><td>Password </td><td><input type=password name=pass /></td></tr>");
                out.println("<tr><td><input type=submit name=log value=Login /></td></tr>");
                out.println("</table></form>");
                out.println("<script>document.getElementById('login').focus();</script>");            
            }
            Web.setFooter(request, response);
	    out.close();
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
