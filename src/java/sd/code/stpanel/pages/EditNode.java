/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.stpanel.pages;

import sd.code.stpanel.common.General;
import sd.code.stpanel.common.Web;
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

/**
 *
 * @author motaz
 */
@WebServlet(name = "EditNode", urlPatterns = {"/EditNode"})
public class EditNode extends HttpServlet {

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
	String user = Web.getCookieValue(request, "user");
	String pbxfile = General.getPBXsDir() + Web.getCookieValue(request, "file");
	try {
	    String tabName = "sip";
	    String fileName = request.getParameter("filename");
	    if (!fileName.contains("sip.")) {
	       tabName = "dialplan";
	    }
	    if (Web.checkSession(request, user)) {
		Web.setHeader(true, request, response, out, "advanced", tabName);
		String url = General.getConfigurationParameter("url", "", pbxfile);

		String nodename = request.getParameter("nodename");
		if ((nodename != null) && (! nodename.contains("["))) {
		    nodename = "[" + nodename + "]";
		}

		out.println("<h4>Configuration file: <b>[" + fileName + "]</b> </h4>");
		if (nodename == null) {
		   out.println("Adding new node");
		   out.println("<form method=POST>");
		   out.println("Node name<input type=text name=nodename /><br/>");
		   out.println("Text <br/><textarea cols=100 rows=10 name=content >");
		   out.println("</textarea></br>");
		   out.println("<input type=submit name=add value='Add Node' />");
		   out.println("</form>");
		}
		else {

		    if (request.getParameter("add") != null) {			
		       Web.addNewNode(pbxfile, request, fileName, nodename, request.getParameter("content"), out);
		    }
		    out.println("<h3>Edit Node: <b>" + nodename + "</b> </h3>");

		    doSave(request, fileName, nodename, url, out);

		    JSONObject obj = new JSONObject();
		    obj.put("filename", fileName);
		    String requestText = obj.toJSONString();


		    String resultText = General.restCallURL(url + "GetFile", requestText);
		    JSONParser parser = new JSONParser();
		    JSONObject resObj = (JSONObject) parser.parse(resultText);
		    String dataHeader = "<pre>";
		    String dataFooter = "</pre>";
		    boolean edit = (request.getParameter("edit") != null);
		    if (edit) {
			dataHeader = "<textarea cols=120 rows = 20 font name=content >";
			dataFooter = "</textarea>";
		    }

		    displayEditForm(out, resObj, nodename, dataHeader, dataFooter, edit);
		}
                
            }
            }catch (Exception ex){
                General.writeEvent("Error in EditNode: " + ex.toString());
            }
            
	out.close();
        
    }



    private void displayEditForm(final PrintWriter out, JSONObject resObj, String nodename, String dataHeader, String dataFooter, boolean edit) {
        
        out.println("<form method=POST>");
	displayEditSaveButton(edit, out);
        if (Boolean.valueOf(resObj.get("success").toString())) {
            String content = resObj.get("content").toString();
            String[] arr = content.split("\n");
            
            boolean started = false;
            boolean found = false;
            for (String line: arr) {
                line = line.trim();
                if (! found && line.contains(nodename)) {
                    started = true;
                    found = true;
                    out.println(dataHeader);
                }
                else if ((started) && (line.contains("[")) && (line.indexOf("[") < 5)) {
                    started = false;
                }
                
                if (started) {
                    if (! line.isEmpty()){
                        out.println(line);
                    }
                }
                
            }
            out.println(dataFooter);
            
        }
	displayEditSaveButton(edit, out);
        out.println("</form>");
    }

    private void displayEditSaveButton(boolean edit, final PrintWriter out) {
	if (edit) {
	    out.println("<br/><input type=submit value=Save name=save id=button />");
	    
	}else {
	    out.println("<input type=submit value=Edit name=edit />");
	}
	out.println("<br/>");
    }

    private void doSave(HttpServletRequest request, String fileName, String nodename, String url, final PrintWriter out) throws IOException, ParseException {
        if (request.getParameter("save") != null) {
            JSONObject saveobj = new JSONObject();
            saveobj.put("filename", fileName);
            saveobj.put("nodename", nodename);
            saveobj.put("content", request.getParameter("content"));
            String requestText = saveobj.toJSONString();
            String resultText = General.restCallURL(url + "ModifyNode", requestText);
            JSONParser saveparser = new JSONParser();
            JSONObject saveresObj = (JSONObject) saveparser.parse(resultText);
            boolean res = (Boolean.valueOf(saveresObj.get("success").toString()));
            if (res) {
                out.println("<p class=infomessage>Saved</p>");
            }
            else {
                out.println("<p class=errormessage>Error: " + saveresObj.get("message").toString() + "</p>");
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
