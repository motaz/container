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
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
	response.setContentType("text/html;charset=UTF-8");
	try {
            PrintWriter out = response.getWriter();
            try {
              String user = Web.getCookieValue(request, "user");
              String pbxfile = General.getPBXsDir()  + Web.getCookieValue(request, "file");
              if (Web.checkSession(request, user)) {
                  Web.setHeader(true, request, response, out, "advanced", "monitor");
                  out.println("<h2>Monitor</h2>");

		  String function = request.getParameter("function");
		  if (function == null){
		      function = "system";
		  }
		  
                  out.println("<table><tr>");
                  out.println("<td><a href='Monitor?function=system'>System</a></td>");
                  out.println("<td><a href='Monitor?function=calls'>Active Channels</a></td>");
		  out.println("</tr></table>");
		  
		  String url = General.getConfigurationParameter("url", "", pbxfile);
		  
		  Date now = new Date();
		  out.println(now.toString());
		  out.println("<a href='Monitor?function=" + function + "' class=button >Refresh</a>");
		  out.println("<br/><br/>");
		  
		  if (function.equals("system")){
		     displaySystemStatus(url, out);
		  }
		  else if (function.equals("calls")) {
		      
		  
		    displayCalls(pbxfile, url, out);
		  }
		  
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
	} finally {
	}
    }

    private void displayCalls(String pbxfile, String url, PrintWriter out) throws IOException, ParseException {
	
	String text = Web.callAMICommand(pbxfile, "core show channels concise");
	    
	String lines[] = text.split("\n");
	    
	out.println("<table><tr><th>ID</th><th>Caller ID</th><th>Extension</th><th>Duration</th></tr>");
	int count=0;
	for (String line: lines) {
		if (line.contains("!")) {
		   String callid = line.substring(0, line.indexOf("!")).trim();
		   count++;
		   // Get details call info
		   String info[] = getCallInfo(pbxfile, callid);
		   if ((info != null) && (info.length > 30)){
		   String callerID = getValue(info[7]);
		   String id = getValue(info[5]);
		   String extension = getValue(info[31]);
		   String duration = getValue(info[26]);
		   out.println("<tr>");
		   out.println("<td>" + id + "</td>");
		   out.println("<td>" + callerID + "</td>");
		   out.println("<td>" + extension + "</td>");
		   out.println("<td>" + duration + "</td>");
		   
		   out.println("</tr>");
		   }
		}
	    }
	    out.println("</table>");
	    out.println("<b>" + count + "</b> Channels");
	
    }

    private String getValue(String text){
     
	return text.substring(text.indexOf(":") + 1, text.length()).trim();
    }
    
    private String[] getCallInfo(String pbxfile, String callid) throws IOException, ParseException{
	

	String text = Web.callAMICommand(pbxfile, "core show channel " + callid);
	String lines[];
	    
	lines = text.split("\n");
	
        
	return lines;
    }
    
    private void displaySystemStatus(String url, PrintWriter out) {
	
	out.println("Server time");
	String result = General.executeShell( "date", url);
	out.println("<pre>" + result + "</pre>");
	
	out.println("</br/>");
	
	out.println("Processors count");
	result = General.executeShell("cat /proc/cpuinfo | grep processor | wc -l", url);
	
	out.println("<pre>" + result + "</pre>");
	
	out.println("</br/>");
	out.println("Uptime");
	result = General.executeShell("uptime", url);
	out.println("<pre>" + result + "</pre>");
	
	out.println("<br/>");
	out.println("Memory (In Megabytes)");
	result = General.executeShell("free -m", url);
	out.println("<pre>" + result + "</pre>");
	
	out.println("<br/>");
	out.println("Disk usage");
	result = General.executeShell("df -h", url);
	out.println("<pre>" + result + "</pre>");
	
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
