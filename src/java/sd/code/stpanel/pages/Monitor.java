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
import org.json.simple.JSONArray;
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
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
	response.setContentType("text/html;charset=UTF-8");
	
        PrintWriter out = response.getWriter();
        try {
            String user = Web.getCookieValue(request, "user");
            String pbxfile = General.getPBXsDir()  + Web.getCookieValue(request, "file");
            String url = General.getConfigurationParameter("url", "", pbxfile);	      
            if (Web.checkSession(request, user)) {
                Web.setHeader(true, request, response, out, "pbx", "monitor");
                out.println("<h2>Monitor</h2>");

                String function = request.getParameter("function");
                if (function == null){
                    function = "system";
                }
                   
                String selectedColor = "bgcolor=#FFFFcc";

                out.println("<table><tr bgcolor=#eeeecc>");
                out.println("<td ");
                if (function.equals("system")) {
                    out.println(selectedColor);
                }
                out.println("><a href='Monitor?function=system'>System</a></td>");

                out.println("<td ");
                if (function.equals("calls")) {
                    out.println(selectedColor);
                }
                out.println("><a href='Monitor?function=calls'>Active Channels</a></td>");

                out.println("<td ");
                if (function.equals("cdr")) {
                    out.println(selectedColor);
                }
                out.println("><a href='Monitor?function=cdr'>Last CDRs</a></td>");

                out.println("</tr></table>");

                out.println("<a href='Monitor?function=" + function + "' class=btn >Refresh</a>");

                Date now = new Date();
                out.println("<font size=2>" + now.toString() + "</font>");
                out.println("<br/><br/>");

                if (function.equals("system")){
                    displaySystemStatus(url, out);
                }
                else if (function.equals("calls")) {

                   displayActiveChannels(pbxfile, url, out);
                }
                else if (function.equals("cdr")){
                   displayCDR(url, out);
                }

                out.println("<script type=\"text/javascript\">\n" +
                        "  var timeout = setTimeout(\"location.reload(true);\", 50000);\n" +
                        "</script>");

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

    private void displayActiveChannels(String pbxfile, String url, PrintWriter out) throws IOException, ParseException {
	
	String text = Web.callAMICommand(pbxfile, "core show channels concise");
	    
	if (text.length() < 150){
	    if (text.contains("Privilege")){
		out.println("<p class=infomessage>No active channels</p>");
	    }
	    else {
	       out.println("<p class=infomessage>" + text + "</p>");
	    }
	}
	String lines[] = text.split("\n");
	    
	out.println("<b><lable id='channels'></lablel></b> Active channels");
	out.println("<table class=tform><tr ><th>ID</th><th>Caller ID</th><th>Extension</th>");
	out.println("<th>Duration</th><th>Application</th></tr>");
	int count=0;
	for (String line: lines) {
		if (line.contains("!")) {
		   String callid = line.substring(0, line.indexOf("!")).trim();
		   count++;
		   // Get details call info
		   String info[] = General.getCallInfo(pbxfile, callid);
		   if ((info != null) && (info.length > 30)){
		   String callerID = getFieldValue("Caller ID:", info);
		   String id = getFieldValue("UniqueID:", info);
		   String extension = getFieldValue("Connected Line ID:", info);
		   String duration = getFieldValue("Elapsed Time:", info);
		   String application = getFieldValue("Application:", info);
		   out.println("<tr>");
		   out.println("<td>" + id + "</td>");
		   out.println("<td>" + callerID + "</td>");
		   out.println("<td>" + extension + "</td>");
		   out.println("<td>" + duration + "</td>");
		   out.println("<td>" + application + "</td>");
		   
		   out.println("</tr>");
		   }
		}
	    }
	    out.println("</table>");
	    out.println("<script>document.getElementById('channels').innerHTML='" + count + "'</script>");
	
    }
    
    private void displayCDR(String url, PrintWriter out) throws IOException, ParseException {
	
	try {
	     String resultText = General.restCallURL(url + "GetLastCDR", "");
	
	     out.println("<h2>Last CDRs</h2>");
	     JSONParser parser = new JSONParser();
	     JSONObject obj = (JSONObject) parser.parse(resultText);
	     boolean success = Boolean.valueOf(obj.get("success").toString());

	     if (success) {
		 out.println("<table class=tform><tr>");
		
		 JSONObject result = (JSONObject) obj.get("result");
		 JSONArray header = (JSONArray) result.get("header");
		 JSONArray data = (JSONArray) result.get("data");
		 
		 
		 // Table header
		 if (header.size() > 0) {
		     for (int i=0; i < header.size(); i++){
			out.print("<th>" + header.get(i).toString() + "</th>");
		     }
		     
		 }
		 out.println("</tr>");
		 
		 // Records
		 
		 for (int i=0; i < data.size(); i++) {
		     JSONArray record = (JSONArray)data.get(i);
		     out.print("<tr>");
		     for (int j=0; j < record.size(); j++){
			 out.print("<td>" + record.get(j).toString() + "</td>");
		     }
		     out.println("</tr>");
		 }
	         out.println("</table>");
	     
	     

	     }
	}
	catch (Exception ex){
	    out.println(ex.toString());
	}
    }
    
 
    private void displaySystemStatus(String url, PrintWriter out) {
	
	// CPU Utilization
	String loadStr = General.executeShell("uptime", url);
	String cpuloadStr = loadStr.substring(loadStr.indexOf("load average:"), loadStr.length());
	cpuloadStr = cpuloadStr.substring(cpuloadStr.indexOf(":") + 1, cpuloadStr.indexOf(",")).trim();
	double cpuload = Double.parseDouble(cpuloadStr);
	String result = General.executeShell("cat /proc/cpuinfo | grep processor | wc -l", url);
	int procCount = Integer.parseInt(result.trim());
	
	double utilization = (cpuload * 100) / procCount;
	String bgcolor = "#AAFFAA";
	if (utilization > 100) {
	    bgcolor = "#990000";
	}
	else if (utilization > 90) {
	    bgcolor = "#FF5555";
	}
	else if (utilization > 50) {
	    bgcolor = "#FFFFaa";
	}
	if (utilization > 100) {
	    utilization = 100;
	}
	else if (utilization == 0) {
	    bgcolor = "#FFFFFF";
	}
	
	out.println("<table class=dtable><tr>");;
	out.println("<td>CPU Usage</td>");
	out.println("<td bgcolor=" + bgcolor +">" + String.format("%.1f", utilization) + " %</td></tr>");
	
	out.println("</table><br/>");
	
	out.println("Server time");
	result = General.executeShell( "date", url);
	out.println("<pre>" + result + "</pre>");
	
	out.println("<br/>");
	
	out.println("Processors count");
	
	out.println("<pre>" + procCount + "</pre>");
	
	out.println("<br/>");
	out.println("Uptime");
	
	
	out.println("<pre>" + loadStr + "</pre>");
	
	out.println("<br/>");
	out.println("Memory (In Megabytes)");
	result = General.executeShell("free -m", url);
	out.println("<pre>" + result + "</pre>");
	
	out.println("<br/>");
	out.println("Disk usage");
	result = General.executeShell("df -h", url);
	String lines[] = result.split("\n");
	out.println("<pre>");
	for (String line: lines){
	    if (line.contains("/") && line.contains("%")) {
		String usageStr = line.substring(line.indexOf(" "), line.indexOf("%")).trim();
		while (usageStr.contains(" ")) {
		    usageStr = usageStr.substring(usageStr.indexOf(" "), usageStr.length()).trim();
		}
		double usage = Double.parseDouble(usageStr);
		if (usage > 80) {
		    line = "<font color=brown><b>" + line + "</b></font>";
		}
		else if (usage > 60) {
		    line = "<font color=#ee7766><b>" + line + "</b></font>";
		}
	    }
	    out.println(line);
	}
	out.println("</pre>");
	
    }
    
    private String getFieldValue(String key, String lines []){
        
        for (String line: lines){
            if (line.contains(key)){
                line = line.trim();
                line = line.substring(line.indexOf(":") + 1, line.length()).trim();
                return line;
            }
        }
        return "";
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
