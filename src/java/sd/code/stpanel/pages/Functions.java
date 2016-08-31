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
                    Web.setHeader(true, request, response, out, "pbx", "functions");
                    String url = General.getConfigurationParameter("url", "", pbxfile);

                    out.println("<h2>Queues</h2>");
		    String function = request.getParameter("function");
		    if (function == null){
			function = "active";
		    }

		  
		    out.println("<script type='text/javascript'>\n" +
			        "var timeout = setTimeout('location.reload(true);', 50000);\n" +
			        "</script>");
		    Date now = new Date();
		    out.println(now.toString());
		    
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
  		    if (function.equals("talk")) {
  		      out.println("bgcolor=#AAAADD");
	 	    }
		    out.println("><a href='Functions?function=talk'>Talking/Waiting</a></td>");
		   		    
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
	
		    else if (function.equals("talk")) {
			out.println("<h2>Talking/Waiting</h2>");
			out.println("<table><tr>");
			out.println("<td>");
		        displayStatusOf(url, out, true, "Busy");
			out.println("</td><td>");

		    	displayWaiting(pbxfile, url, out);
			out.println("</td><tr></table>");
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
	boolean isBusy  = keyword.equals("Busy");
	if (isBusy){
	    out.println("<h3>Talking</h3>");
	}
	
	String resultText = General.restCallURL(url + "Command", requestText);
	JSONParser parser = new JSONParser();
	JSONObject resObj = (JSONObject) parser.parse(resultText);
	if (Boolean.valueOf(resObj.get("success").toString())) {
	    String text = resObj.get("result").toString();
	    out.println("<font color=green><b><label id=count></label></b></font> Members");
	    out.println("<table class=tform>");
	    out.println("<tr><th>Queue</th><th>Agent</th><th>Status</th><th>Info</th>");
	    if (!isBusy) { 
		out.println("<th>Action</th>");
	    }
	    out.println("</tr>");
	    String queue = "";
	    String lines[] = text.split("\n");
	    int count = 0;
	    for (String line: lines) {
		if (line.contains("holdtime")){
		    queue = line.substring(0, line.trim().indexOf(" ")).trim();
		    out.println("<tr><td><b>" + queue + "</b></td>");
		    
		}
		
		if ((line.contains("Agent/") || (line.contains("SIP/"))) && 
			(((has &&line.contains(keyword))) || (! has && !line.contains(keyword)))) {
		    count++;
		    String member = line.substring(0, line.indexOf("(") - 1).trim();
		    if (queue.isEmpty()) {
			out.println("<tr><td>-</td>");
			
		    }

		    queue = "";
		    
		    out.println("<td>" + member + "</td>");
		    line = line.substring(line.indexOf("("), line.length());
		    
		    // Option
		    //out.println("<td  style='font-size:12'>" + line.substring(0, line.indexOf(")") +1 ) + "</td>");
		    line = line.substring(line.indexOf(")") + 1, line.length());
		    
		    // Status
		    String status  = line.substring(0, line.indexOf(")") +1 );
		    line = line.substring(line.indexOf(")") + 1, line.length()).trim();
		    if (line.startsWith("(")){
			status = status + line.substring(0, line.indexOf(")") + 1);
			line = line.substring(line.indexOf(")") + 1, line.length());
		    }
		    
		    out.println("<td>" + status + "</td>");
		    
		    // Info
		    out.println("<td style='font-size:12'>" + line + "</td>");
		    if (! isBusy){
			out.println("<td><form method=post>");
			out.println("<input type=hidden name=member value='" + member + "' />");
			if (has && keyword.equals("paused")) {
			    out.println("<input type=submit name=unpause value='Unpause' />");
			}
			else if (! has && keyword.equals("paused")) {
			    out.println("<input type=submit name=pause value='Pause' />");
			}
			out.println("</form></td>");
		    }
		    out.println("</tr>");
		}
	    }
	 
	    out.println("</table>");
	    if (count == 0) {
		if (!has) {
		    keyword = "Un" + keyword;
		}
		out.println("There is no members with status (" + keyword + ")");
	    }
	    else {
		out.println("<script> document.getElementById('count').innerHTML = '" + count + "'</script>");
	    }
	    
	}
    }
    
    private void displayWaiting(String pbxfile, String url, final PrintWriter out) throws IOException, ParseException {
	
	JSONObject obj = new JSONObject();
	obj.put("command", "queue show");
	String requestText = obj.toJSONString();
	
	String resultText = General.restCallURL(url + "Command", requestText);
	JSONParser parser = new JSONParser();
	JSONObject resObj = (JSONObject) parser.parse(resultText);
	if (Boolean.valueOf(resObj.get("success").toString())) {
	    String text = resObj.get("result").toString();
	    out.println("<h3>Waiting</h3>");
	    out.println("<font color=green><b><label id=waitcount></label></b></font> Customers");
	    out.println("<table class=tform>");
	    out.println("<tr><th>Queue</th><th>Caller ID</th><th>Application</th><th>Info</th></tr>");
	    String queue = "";
	    
	    String lines[] = text.split("\n");
	    int count = 0;
	    boolean started = false;
	    String lastQueue = "";
	    for (String line: lines) {

		if (line.contains("holdtime")) {
		    queue = line.substring(0, line.indexOf(" ")).trim();
		}
		if (line.trim().isEmpty()) {
		    started = false;
		}
		if (started) {
		    String callid = line.substring(line.indexOf(".") + 1, line.indexOf("(")).trim();
 		    String info[] = General.getCallInfo(pbxfile, callid);
		    if ((info != null) && (info.length > 30)){
			String callerID = General.getValue(info[7]);
			String application = General.getValue(info[35]);
			 
			out.print("<tr><td><b>");
			if (!lastQueue.equals(queue)) {
			    out.print(queue);
			}
			out.println("</b></td>");
			lastQueue = queue;
			out.println("<td>" + callerID + "</td>");
			line = line.substring(line.indexOf("("), line.length());
			out.println("<td>" + application + "</td>");
			out.println("<td  style='font-size:12'>" + line + "</td>");
			out.println("</tr>");
			count++;

		   }
		}
		if (line.contains("Callers:")) {
		    
		    started = true;
		}
		out.println("</tr>");
	    
	    }
	    
	 
	    out.println("</table>");
	    if (count == 0) {
		out.println("There is no waiting customer");
	    }
	    else {
		out.println("<script> document.getElementById('waitcount').innerHTML = '" + count + "'</script>");
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
