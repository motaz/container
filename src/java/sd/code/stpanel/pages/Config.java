/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.stpanel.pages;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sd.code.stpanel.common.Web;

/**
 *
 * @author motaz
 */
@WebServlet(name = "Config", urlPatterns = {"/Config"})
public class Config extends HttpServlet {

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
	    String user = Web.getCookieValue(request, "user");
	    try {
		if (Web.checkSession(request, user)) {
		    Web.setHeader(true, request, response, out, "advanced", "config");
		    
		    out.println("<h2>Configurations</h2>");
                    
                    String command = request.getParameter("command");
                    
                    String tab1Color = "";
                    String tab2Color =  "";
                    String tab3Color = "";
                    String tab4Color = "";
                    
                    if (command != null) {
                        
                        if (command.equals("configbackup")){
                                tab1Color = "bgcolor=#AAAADD";
			}
			else if (command.equals("peers")){
                                tab2Color = "bgcolor=#AAAADD";
			}
                        else if (command.equals("ami")){
                            tab3Color = "bgcolor=#AAAADD";
                        }
                        else if (command.equals("cdr")){
                            tab4Color = "bgcolor=#AAAADD";
                        }
                    }

                    
                    
                    
                    out.println("<table><tr>");
                    out.println("<td " + tab1Color + "><a href='Backup'>Configuration Backup</a></td>");
                    out.println("<td " + tab2Color + "><a href='UploadSound?command=peers'>Sound files</a></td>");
                    out.println("<td " + tab3Color + "><a href='AMIConfig'>Config AMI</a></td>");
                    out.println("<td " + tab4Color + "><a href='CDRConfig'>Config CDR</a></td>");
                    out.println("</tr></table>");
                                        
		    
		    Web.setFooter(out);
		}
	    }
	    catch (Exception ex){
		out.println(ex.toString());
	    }
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
