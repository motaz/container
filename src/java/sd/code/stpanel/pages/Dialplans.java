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
import sd.code.stpanel.common.General;
import sd.code.stpanel.common.Web;

/**
 *
 * @author motaz
 */
@WebServlet(name = "Dialplans", urlPatterns = {"/Dialplans"})
public class Dialplans extends HttpServlet {

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
           try{ 
	       
              Web.setHeader(true, request, response, out, "pbx", "dialplans");
            
              String user = Web.getCookieValue(request, "user");
            
	      String action = request.getParameter("action");
	      if (action == null){
		  action ="";
	      }
	      boolean isDisplayAdd = action.equals("displayadd");
	      out.println("<h3>Dialplans</h3>");
	      if (isDisplayAdd){
		    out.println("<table><tr><td>");
		    displayAdd(out);
	      }
	      else
	      {
		out.println("<a href='Dialplans?action=displayadd'>Add new dialplan wizard</a>");
		  
	      }
	      addNewContext(request, out);
	      if (isDisplayAdd) {
	        out.println("</td><td>");
	      }
	      Web.displayDialplans(request, user, out);
	      if (isDisplayAdd){
	        out.println("</td></tr></table>");
	      }
	      Web.setFooter(out);
	    }
	    catch (Exception ex){
		out.println(ex.toString());      
	   }
  
	
    }

    private void addNewContext(HttpServletRequest request, final PrintWriter out) throws IOException, ServletException {
	
	if (request.getParameter("addcontext") != null) {
	    
	    try {
	    String contextname = "[" + request.getParameter("contextname") + "]";
	    String digits = "_X.";
	    
	    String aDisgits = request.getParameter("digits");
	    
	    if (aDisgits.equals("any")){
		    digits = "_X.";
	    }
	    else if (aDisgits.equals("fixed")){
		    digits = request.getParameter("fixedvalue");
	    }
	    else if (aDisgits.equals("pattern")){
		    digits = request.getParameter("paternvalue");
		    
	    }
	    
	    // Plan
	    String content = "exten => " + digits + ",1,NoOp()\n";
	    String answer = request.getParameter("answer");
	    if ((answer != null) && (answer.equals("1"))) {
		content = content + "  same => n,answer()\n";
	    }
	    
	    String play = request.getParameter("play");
	    if ((play != null) && (play.equals("1"))) {
		content = content +"  same => n,playback(" + request.getParameter("recording") + ")\n";
	    }
	    
	    String dial = request.getParameter("dial");
	    if ((dial != null) && (dial.equals("1"))) {
		content = content + "  same => n,dial(" + request.getParameter("dialto") + ")\n";
	    }
	    content = content + "  same => n,hangup()\n";
	    if (request.getParameter("preview") != null){ // Preview only
		out.println("<br/><pre>");
		out.println(contextname);
		out.println(content);
		out.println("</pre><br/>");
	    }
	    else
	    {	    
	      String pbxfile = General.getPBXsDir()  + Web.getCookieValue(request, "file");
	    
	      Web.addNewNode(pbxfile, request, "extensions.conf", contextname, content, out);
	    }
	}
	catch (Exception ex){
	  out.println(ex.toString());
	}
	    
    }
  }

    private void displayAdd(final PrintWriter out) {
	
	out.println("<h4>Add new dialplan context</h4>");
	out.println("<form method=POST>");
	
	out.println("<input type=checkbox name=preview value=1 />");
	out.println("Preview only (Don't create extension)<br/>");
		
	displaySubtitle(out, "Context name");
	out.println("<input type=text name=contextname size = 12 /><br/><br/>");
	
	// digits match
	displaySubtitle(out, "Match Digits");
	align(out, true);
	out.println("<input type=radio name=digits value=any checked /> Any digits<br/>");
	align(out, false);
	out.println("<input type=radio name=digits value=fixed /> Fixed digits ");
	out.println("<input type=text name=fixedvalue size = 5 /><br/>");
	align(out, false);
	out.println("<input type=radio name=digits value=pattern /> pattern");
	out.println("<input type=text name=patternvalue size=5 /><br/><br/>");
	
	// steps
	displaySubtitle(out, "Context plan");
	align(out, true);
	out.println("<input type=checkbox name=answer value=1 />Answer");
	align(out, true);
	out.println("<input type=checkbox name=play value=1 />Play recording ");
	out.println("<input type=text name=recording />");
	
	align(out, true);
	out.println("<input type=checkbox name=dial value=1 />Dial ");
	out.println("<input type=text name=dialto value = 'SIP/${EXTEN},,120'/><br/>");
	
	out.println("<br/><input type=submit name=addcontext value='Add Context'  />");
	out.println("</form>");
    }

    private void align(final PrintWriter out, boolean newLine) {
      if (newLine) {
	  out.print("<br/>");
      }
      out.print("&emsp;&emsp;");
    }

    private void displaySubtitle(final PrintWriter out, String title) {
	out.println("<font color=navy><b>" + title + "</b></font>");
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
