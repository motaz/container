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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sd.code.stpanel.common.General;
import sd.code.stpanel.common.Web;

/**
 *
 * @author motaz
 */
@WebServlet(name = "Functions", urlPatterns = {"/Functions"})
public class Functions extends HttpServlet {

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
          String pbxfile = General.getPBXsDir() + Web.getCookieValue(request, "file");
            try {
                if (Web.checkSession(request, user)) {
                    Web.setHeader(true, request, response, out, "advanced", "functions");
                    String url = General.getConfigurationParameter("url", "", pbxfile);

                    out.println("<h2>Functions</h2>");
		    String function = request.getParameter("function");
		    if (function == null){
			function = "active";
		    }
                    out.println("<table><tr>");
                    out.println("<td ");
		    if (function.equals("active")) {
  		      out.println("bgcolor=#AAAADD");
		    }
		    out.println("><a href='Functions?function=active'>Queue-Active</a></td>");
		    
                    out.println("<td ");
  		    if (function.equals("paused")) {
  		      out.println("bgcolor=#AAAADD");
	 	    }
		    out.println("><a href='Functions?function=paused'>Queue-Paused</a></td>");
		    
                    out.println("<td ");
  		    if (function.equals("busy")) {
  		      out.println("bgcolor=#AAAADD");
	 	    }
		    out.println("><a href='Functions?function=busy'>Queue-Busy</a></td>");
		    
                    out.println("</tr></table>");

		    pauseUnpause(request, url, out);
		    
		    if (function.equals("paused")) {
			out.println("<h2>Paused</h2>");
		    	displayStatusOf(url, out, true, "paused");
		    }
		    else if (function.equals("active")) {
			out.println("<h2>Active</h2>");
		    	displayStatusOf(url, out, false, "paused");
		    }
		    else if (function.equals("busy")) {
			out.println("<h2>Busy</h2>");
		    	displayStatusOf(url, out, true, "Busy");
		    }
                    
		}
	    }
	    catch (Exception ex){
		out.println(ex.toString());
	    }
	    Web.setFooter(out);
	}
    }

    private void displayStatusOf(String url, final PrintWriter out, boolean has, String keyword) throws IOException, ParseException {
	
	JSONObject obj = new JSONObject();
	obj.put("command", "queue show");
	String requestText = obj.toJSONString();
	
	String resultText = General.restCallURL(url + "Command", requestText);
	JSONParser parser = new JSONParser();
	JSONObject resObj = (JSONObject) parser.parse(resultText);
	if (Boolean.valueOf(resObj.get("success").toString())) {
	    String text = resObj.get("result").toString();
	    
	    String lines[] = text.split("\n");
	    int count = 0;
	    for (String line: lines) {
		if (line.contains("holdtime")){
		    String queue = line.substring(0, line.trim().indexOf(" ")).trim();
		    out.println("<h2>Queue " + queue + "</h2>");
		}
		if (line.contains("Agent/") && 
			(((has &&line.contains(keyword))) || (! has && !line.contains(keyword)))) {
		    count++;
		    String member = line.substring(0, line.indexOf("(") - 1).trim();
		    out.println("<b>" + member + "</b> " + line);
		    out.println("<form method=post>");
		    out.println("<input type=hidden name=member value='" + member + "' />");
		    if (has && keyword.equals("paused")) {
		        out.println("<input type=submit name=unpause value='Unpause' />");
		    }
		    else if (! has && keyword.equals("paused")) {
		        out.println("<input type=submit name=pause value='Pause' />");
		    }
		    out.println("</form>");
		}
	    }
	    if (count == 0) {
		out.println("There is no members with status (" + keyword + ")");
	    }
	    else {
		out.println("<font color=green><b>" + count + "</b> Members</font>");
	    }
	    
	}
    }

    private void pauseUnpause(HttpServletRequest request, String url, final PrintWriter out) throws IOException, ServletException {
	
        String command = null;
        if (request.getParameter("unpause") != null) {
  	  command = "unpause";
        }
        else if (request.getParameter("pause") != null) {
	    command = "pause";
        }
      
        if (command != null){
	    String memb = request.getParameter("member");
	    JSONObject obj = new JSONObject();
	    obj.put("command", "queue " + command + " member " + memb);
	    String requestText = obj.toJSONString();
	    String resultText = General.restCallURL(url + "Command", requestText);
	    out.println("<p class=infomessage>"  + resultText + "</p>");
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
