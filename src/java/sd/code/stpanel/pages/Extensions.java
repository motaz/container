/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.stpanel.pages;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sd.code.stpanel.common.ContextParser;
import sd.code.stpanel.common.General;
import sd.code.stpanel.common.Web;
import sd.code.stpanel.types.NodeInfo;

/**
 *
 * @author motaz
 */
@WebServlet(name = "Extensions", urlPatterns = {"/Extensions"})
public class Extensions extends HttpServlet {

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
              String title = "Trunk";
              boolean isExten = false;
              String type = request.getParameter("type");
              if ((type == null) || (type.equals("ext"))){
                  type = "ext";
                  title = "Extension";
                  isExten = true;
              }
              String fileName = request.getParameter("file");
              if (fileName == null){
                  fileName = "sip.conf";
              }
              Web.setHeader(true, request, response, out, "pbx", type);
            
              String user = Web.getCookieValue(request, "user");
              

              String pbxfile = General.getPBXsDir()  + Web.getCookieValue(request, "file");
              if (Web.checkSession(request, user)) {

		  // Insert SIP node

		  doAddNode(request, out);
		  boolean isDisplayAdd = (request.getParameter("add") != null);
		  // Display add SIP link
		  if (! isDisplayAdd){
		      if (type.equals("ext")) {
                         out.println("<a href='Extensions?file=" + fileName + "&add=1'>Add new SIP Extension</a>");
		      }
		      else
		      {
                        out.println("<a href='Extensions?file=" + fileName + "&add=2&type=trunk'>Add new SIP Trunk</a>");
			  
		      }
		    
		  }  // Display add SIP form
		  else {
		        out.println("<table class=dtable><tr><td>");
		      	displayAddSIPNode(out, title, type, fileName);
			out.println("</td><td>");
		  }
		  
                  

                  displayExtensions(pbxfile, fileName, request, out, title, isExten, type);
		  if (isDisplayAdd){
		      out.println("</td></tr></table>");
		  }

                }
        
              Web.setFooter(request, response);
           
        } catch (Exception ex){
            out.println("<p class=errormessage>" + ex.toString() + "</p>");
        }
        out.close();
    }

    private void displayExtensions(String pbxfile, String fileName, HttpServletRequest request, 
            final PrintWriter out, String title, boolean isExten, String type) throws IOException, ParseException {
        
	String url = General.getConfigurationParameter("url", "", pbxfile);
	JSONObject obj = new JSONObject();

	obj.put("filename", fileName);
	String requestText = obj.toJSONString();
	
	String resultText = General.restCallURL(url + "GetFile", requestText);
        if (type == null){
            type = "Extensions";
        }
	
	JSONParser parser = new JSONParser();
	JSONObject resObj = (JSONObject) parser.parse(resultText);
	if (Boolean.valueOf(resObj.get("success").toString())) {
	    String content = resObj.get("content").toString();
	    String[] arr = content.split("\n");
	    
	    ContextParser cparser = new ContextParser(arr, fileName);
	    ArrayList<NodeInfo> nodes = cparser.getNodesWithInfo();
	    
	    String reverseStr = Web.getCookieValue(request, "reverse");
	    boolean reverse = (reverseStr != null) && (reverseStr.equals("yes"));
            out.println("</br>");
            Web.displayIncludedFiles(content, out, "Extensions?type=" + type + "&file=");
	    out.println("<table class=dtable><tr><th>" + title +"</th><th>User name</th>");
	    out.println("<th>Host</th><th>Context</th></tr>");
	    if (reverse) {
		for (int i= nodes.size() -1; i >= 0; i--) {
		    NodeInfo node = nodes.get(i);
		    displayNode(out, node, isExten, fileName);
		    
		}
	    }
	    else {
		for (NodeInfo node: nodes) {
		    displayNode(out, node, isExten, fileName);
		}
	    }
	    out.println("</table>");
	}
    }

    private void doAddNode(HttpServletRequest request, final PrintWriter out) throws ParseException, IOException {
	
        if (request.getParameter("addnode") != null) {
	    String pbxfile = General.getPBXsDir() + Web.getCookieValue(request, "file");
            String url = General.getConfigurationParameter("url", "", pbxfile);
	    
            JSONObject saveobj = new JSONObject();
            String fileName = request.getParameter("file");
            if (fileName == null){
                fileName = "sip.conf";
            }
            out.println(fileName);
            saveobj.put("filename", fileName);
	    String nodeName = "[" + request.getParameter("nodename") + "]";
            saveobj.put("nodename", nodeName);
	    
	    String content;
	    content = "username=" + request.getParameter("username") + "\n";
	    content = content + "type=" + request.getParameter("siptype") + "\n";
	    content = content + "host=" + request.getParameter("host") + "\n";
	    if (!request.getParameter("context").isEmpty()) {
	       content = content + "context=" + request.getParameter("context") + "\n";
	    }
	    
	    if (!request.getParameter("secret").isEmpty()) {
  	       content = content + "secret=" + request.getParameter("secret") + "\n";
	    }
	    if (!request.getParameter("additional").isEmpty()) {
	       content = content + request.getParameter("additional");
	    }
	    if (request.getParameter("preview") != null){ // Preview only
		out.println("<br/><pre>");
		out.println(nodeName);
		out.println(content);
		out.println("</pre><br/>");
	    }
	    else // Create actual SIP node
	    {
		saveobj.put("content", content);
		String requestText = saveobj.toJSONString();
		String resultText = General.restCallURL(url + "AddNode", requestText);
		JSONParser saveparser = new JSONParser();
		JSONObject saveresObj = (JSONObject) saveparser.parse(resultText);
		boolean res = (Boolean.valueOf(saveresObj.get("success").toString()));
		if (res) {
		    out.println("<p class=infomessage>New node " + nodeName + " has been added</p>");
		}
		else {
		    out.println("<p class=errormessage>Error: " + saveresObj.get("message").toString() + "</p>");
		}
	    }
        }
    }
    
    private void displayAddSIPNode(final PrintWriter out, String title, String type, String fileName) {
	
	out.println("<h3>Add new SIP " + title + "</h3>");
	out.println("<form method=POST action='Extensions?type=" + type + "'>");
        out.println("<input type=hidden name=file value='" + fileName + "' />");
	out.println("<table dtable><tr>");
	
	out.println("<td><input type=checkbox name=preview value=1 /></td>");
	out.println("<td>Preview only (Don't create extension)</td></tr>");
	
	out.println("<td>" + title + "</td>");
	out.println("<td><input type=text name=nodename /></td></tr>");
	
	out.println("<td>User name</td>");
	out.println("<td><input type=text name=username /></td></tr>");
	
	out.println("<td>type</td>");
	out.println("<td><input type=text name=siptype value=peer /></td></tr>");
	
	out.println("<td>host</td>");
	out.println("<td><input type=text name=host value=dynamic /></td></tr>");

	out.println("<td>context</td>");
	out.println("<td><input type=text name=context /></td></tr>");

	out.println("<td>Secret</td>");
	out.println("<td><input type=text name=secret /></td></tr>");
	
	out.println("<td>additional properties<br/>");
	out.println("<font color=gray>name=value</font></td>");
	out.println("<td><textarea rows = 5 cols=40 name=additional />");
	if (type.equals("trunk")) {
  	   out.println("trunk=yes");
	}
	out.println("qualify=yes");
	out.println("nat=force_rport,comedia");
	out.println("</textarea></td></tr>");

	out.println("<tr><td><input type=submit name=addnode value='Add " + title + "' /></td></tr>");
	out.println("</table>");
	out.println("</form>");
	
    }

    private void displayNode(final PrintWriter out, NodeInfo node, boolean displayExtension, String fileName) {
	
        if ((displayExtension &&node.isExtension()) || (! displayExtension && node.isTrunk())) {
            out.println("<tr>");
            out.println("<td><a href='EditNode?filename=" + fileName + "&nodename=[" + 
                    node.getNodeName() + "]'>" +
                    node.getNodeName() + "</a></td>");
            out.println("<td>" + node.getProperty("username") + "</td>");
            out.println("<td>" + node.getProperty("host") + "</td>");
            out.println("<td>" + node.getProperty("context") + "</td>");
            out.println("</tr>");
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
