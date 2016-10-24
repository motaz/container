/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.stpanel.pages;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import sd.code.stpanel.common.General;
import sd.code.stpanel.common.Web;
import sd.code.stpanel.types.Operation;

/**
 *
 * @author motaz
 */
@WebServlet(name = "PlaySound", urlPatterns = {"/PlaySound"})
public class PlaySound extends HttpServlet {

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
	String user = Web.getCookieValue(request, "user");
	try {
	    if (Web.checkSession(request, user)) {
	       String pbxfile = General.getPBXsDir()  + Web.getCookieValue(request, "file");
	       String url = General.getConfigurationParameter("url", "", pbxfile);	      
		   
	       String contenttype = "audio/wav";
	       String filename = request.getParameter("filename");
	       response.setContentType(contenttype);
	       JSONObject obj = new JSONObject();
	       obj.put("filename", filename);
	       obj.put("contenttype", contenttype);
	       String requestText = obj.toJSONString();
	       OutputStream output = response.getOutputStream();
	       File file = new File(filename);
	       
	       System.out.println(url);
	       System.out.println(requestText);

	       response.setHeader("Content-Disposition", "attachment;filename=" + file.getName());
	       Operation op = General.downloadFile(url + "DownloadFile", requestText, contenttype, output);
	       response.setContentLength((int)op.size);		    
		   
	    }
	   } catch (Exception ex){
		
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
