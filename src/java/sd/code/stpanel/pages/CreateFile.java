/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.stpanel.pages;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import sd.code.stpanel.common.General;
import sd.code.stpanel.common.Web;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author ameenah
 */
@WebServlet(name = "CreateFile", urlPatterns = {"/CreateFile"})
public class CreateFile  extends HttpServlet {
 
    protected void processRequest(HttpServletRequest request , HttpServletResponse response ) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        
        try (PrintWriter out = response.getWriter()){
            String user = Web.getCookieValue(request, "user");
            String pbxfile = General.getPBXsDir()  + Web.getCookieValue(request, "file");
            
            try {
                if (Web.checkSession(request, user)){
                    Web.setHeader(true, request, response, out, "advanced", "tools");
                    
                    String url = General.getConfigurationParameter("url", "", pbxfile);
                    
                    doSave(request, response , url, out);
                    displayCreateFileform(out);  
                   
                    
                                                     
                    Web.setFooter(request, response);
                } else {
                   response.sendRedirect("Login");
                }
            }
            catch (Exception ex){
		out.println(ex.toString());
		General.writeEvent("Error: in Create  File : " + ex.toString());
	    }
        }
    
    }
    
    private void displayCreateFileform(final PrintWriter out) {
        
        out.println("<br><p> Adding new File </p>");
        out.println("<form method=POST>");        
        out.println("File Name: <input type=text name=filename /><br/>");
        out.println("File Content :  <br>");
        out.println("<textarea cols=100 rows = 60 font name=filecontent >");
        out.println("</textarea><br/>");    
        out.println("<input type=submit name=save value='Save file'  /> <br>");
        out.println("</form>");
        
    }
        
     private void doSave(HttpServletRequest request, HttpServletResponse response ,String  url, final PrintWriter out) {
        
        try {
        if (request.getParameter("save") != null) {
            
            String fileName = request.getParameter("filename");
            String fileContent = request.getParameter("filecontent");
            
            
            JSONObject fileCreateObj = new JSONObject();
            fileCreateObj.put("command", "touch /etc/asterisk/"+ fileName);
            String requestText = fileCreateObj.toJSONString();
            String resultText = General.restCallURL(url + "Shell", requestText);
            
            JSONParser resultParser = new JSONParser();
            JSONObject resultObj = (JSONObject) resultParser.parse(resultText);
            boolean res = (Boolean.valueOf(resultObj.get("success").toString()));
            
            JSONObject filePerObj = new JSONObject();
            filePerObj.put("command", "chown asterisk:asterisk /etc/asterisk/" + fileName);
            requestText = filePerObj.toJSONString();
            String PermResultText = General.restCallURL(url + "Shell", requestText);           
    
            resultParser = new JSONParser();
            JSONObject PermresultObj = (JSONObject) resultParser.parse(PermResultText);
            boolean PermRes = (Boolean.valueOf(PermresultObj.get("success").toString()));
            
            if (res && PermRes) {
                JSONObject fileContentObj = new JSONObject();
                fileContentObj.put("filename", fileName);
                fileContentObj.put("content", fileContent);
                requestText = fileContentObj.toJSONString();
                resultText = General.restCallURL(url + "ModifyFile", requestText);  
 
                
                
                resultParser = new JSONParser();
                resultObj = (JSONObject) resultParser.parse(resultText);
                res = (Boolean.valueOf(resultObj.get("success").toString()));
                
                out.println("<br>");
                   
                if (res) {
                    response.sendRedirect("Files?file="+fileName);
                }
                else {
                    out.println("<p class=errormessage>Error: " + resultObj.get("message").toString() + "</p>");
                }
                
            }
        }
        }catch (Exception ex){
                out.println("<p class=errormessage>Error: " + ex.toString() + "</p>");
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
