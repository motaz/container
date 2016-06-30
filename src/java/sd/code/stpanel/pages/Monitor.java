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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sd.code.stpanel.common.General;
import sd.code.stpanel.common.Web;

/**
 *
 * @author motaz
 */
@WebServlet(name = "Monitor", urlPatterns = {"/Monitor"})
public class Monitor extends HttpServlet {

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
                  Web.setHeader(true, request, response, out, "advanced", "monitor");
                  out.println("<h2>Monitor</h2>");

		  String url = General.getConfigurationParameter("url", "", pbxfile);
		  
		  Date now = new Date();
		  out.println(now.toString());
		  out.println("<a href='Monitor' class=button >Refresh</a>");
		  out.println("<br/><br/>");
		  
		  executeShell("Server time", "date", url, out);
		  
		  out.println("</br/>");
		  executeShell("Processors count", "cat /proc/cpuinfo | grep processor | wc -l", url, out);
		  
		  out.println("</br/>");
		  executeShell("Uptime", "uptime", url, out);
		  
		  out.println("<br/>");
		  executeShell("Memory (Mega)", "free -m", url, out);
		  
		  out.println("<br/>");
		  executeShell("Disk usage", "df -h", url, out);
		  
		  out.println("<script type=\"text/javascript\">\n" +
			      "  var timeout = setTimeout(\"location.reload(true);\",50000);\n" +
			      "</script>");
		  
		  Web.setFooter(out);

              }
              else {
                  response.sendRedirect("Login");
              }
            }
            catch (Exception ex){
                out.println(ex.toString());
            }
    }

    private void executeShell(String title, String command, String url, PrintWriter out) throws ParseException, IOException {
	JSONObject obj = new JSONObject();
	
	obj.put("command", command);
	
	String requestText = obj.toJSONString();
	
	String resultText = General.restCallURL(url + "Shell", requestText);
	JSONParser parser = new JSONParser();
	JSONObject resObj = (JSONObject) parser.parse(resultText);
	
	String content = resObj.get("result").toString();
	out.println(title);
	if (content != null){
	    out.println("<pre>" + content + "</pre>");
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
