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
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import sd.code.stpanel.common.General;
import sd.code.stpanel.common.Web;

/**
 *
 * @author motaz
 */
@WebServlet(name = "UploadSound", urlPatterns = {"/UploadSound"})
public class UploadSound extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException
	     {
	response.setContentType("text/html;charset=UTF-8");
	try (PrintWriter out = response.getWriter()) {
	    String user = Web.getCookieValue(request, "user");
	    try {
		if (Web.checkSession(request, user)) {
		    Web.setHeader(true, request, response, out, "advanced", "tools");
		    String pbxfile = General.getPBXsDir()  + Web.getCookieValue(request, "file");
		    String url = General.getConfigurationParameter("url", "", pbxfile);	      
		    
		    out.println("<h2>Sound files</h2>");
		    String dir = request.getParameter("dir");
		    if (dir == null) {
			dir = "/usr/share/asterisk/sounds";
		    }
		    
		    // Goto Subdirectory script
		    out.println("<form method=POST>");
		    out.println("<script>");
		    out.println("function setSubdirectory(avalue){");
		    out.println("  document.getElementById('directory').value = avalue; }");

		    out.println("function setMaindirectory(avalue){");
		    out.println("  document.getElementById('dir').value = avalue; }");
		    out.println("</script>");
		    out.println("<input type=hidden id=directory name=directory />");

		    // directory
		    dir = General.addSlash(dir);
		    String directory = request.getParameter("directory");
		    if (directory != null) {
			out.println("<button onclick=setMaindirectory('" + dir + "') >Up</button><br/>");
			dir = General.addSlash(dir + directory);
		    }
		    
			    
		    out.println("<input type=text name=dir id=dir value=" + dir + " size = 50 />");

		    out.println("<table><tr><th>File name</th><th></th></tr>");
		    String filesText = General.listFiles(url, dir);
		    try {
		         JSONParser parser = new JSONParser();
			 
			 JSONArray files = (JSONArray)parser.parse(filesText);
			 
			 for (int i=0; i < files.size(); i++){
			     
			     String fileName = files.get(i).toString();
			     out.println("<tr><td>" + fileName + "</td>");
			     if (fileName.indexOf(".")>0) {
				 out.print("<td><a href='PlaySound?filename=" + dir + fileName +"'>Play</a></td>");
			     }
			     else {
				 out.print("<td><button onclick = setSubdirectory('" + fileName + 
                                         "' class='btn'  )>Enter</button></td>");
			     }
			     out.println("</tr>");
			     
			 }
			 
		    } catch (Exception ex){
			out.println(ex.toString());
		    }
		    out.println("</table>");
		    out.println("</form>");
		    
		    // Upload sound form
		    out.println("<form method=POST action='UploadSoundFile' enctype='multipart/form-data'>");
		    //out.println("<form method=POST action='http://localhost:8084/STAgent/UploadFile' enctype='multipart/form-data'>");
		    //out.println("<form method=POST action='http://localhost:9090' enctype='multipart/form-data'>");
		    out.println("<br/>Upload file");
		    out.println("<input type=hidden name=dir value='" + dir + "' />");
		    out.println("<input type=file name=file />");
		    out.println("<input type=submit name=uploadfile value='Upload' class='button'  />");
		    out.println("</form>");
		    
		    Web.setFooter(request, response);
		}
	    }
	    catch (Exception ex){
		out.println(ex.toString());
		General.writeEvent("Error: in UploadSound: " + ex.toString());
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
