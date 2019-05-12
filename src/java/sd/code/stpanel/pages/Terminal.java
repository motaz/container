/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.stpanel.pages;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sd.code.stpanel.common.General;
import sd.code.stpanel.common.Web;

/**
 *
 * @author motaz
 */
@WebServlet(name = "Terminal", urlPatterns = {"/Terminal"})
public class Terminal extends HttpServlet {

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
        try {
            String user = Web.getCookieValue(request, "user");
            String pbxfile = General.getPBXsDir()  + Web.getCookieValue(request, "file");
            if (Web.checkSession(request, user)) {
                Web.setHeader(true, request, response, out, "advanced", "terminal");
                out.println("<h2>Terminal</h2>");


                String url = General.getConfigurationParameter("url", "", pbxfile);
                String commandText = request.getParameter("command");
                if (commandText == null){
                    commandText = "";
                }
                out.println("<br/><form method=POST >");
                out.println("Command <input type=text name=command size = 40 value = '" + commandText + "' /> &emsp;");
                out.println("<input type=submit name=execute value='Execute' class=btn />");
                out.println("</form>");

                if (request.getParameter("execute") != null) {
                  String result = General.executeShell(commandText, url);
                  out.println("<pre>");
                  out.println(result);
                  out.println("</pre>");
                }


                Web.setFooter(request, response);

            }
            else {
              response.sendRedirect("Login");
            }
        }
        catch (Exception ex){
            out.println(ex.toString());
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
