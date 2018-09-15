/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.stpanel.pages;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.annotation.WebServlet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sd.code.stpanel.common.General;
import sd.code.stpanel.common.Web;
/**
 *
 * @author ameenah
 */
@WebServlet(name = "CompareFiles", urlPatterns = {"/CompareFiles"})
public class CompareFiles extends HttpServlet {
    
    protected void processRequest (HttpServletRequest request , HttpServletResponse response ) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        try(PrintWriter out = response.getWriter()){
            String user = Web.getCookieValue(request, "user");
            String pbxfile = General.getPBXsDir() + Web.getCookieValue(request, "file");
            
            try {
                String  fileNames [] = request.getParameterValues("backupfilename");

                if(Web.checkSession(request, user)){
                    Web.setHeader(true, request, response, out,"advanced", "tools");
                                         
                        if (fileNames != null) {
                            
                            JSONObject obj = new JSONObject();
                            obj.put("filename", "/etc/asterisk/backup/" + fileNames[0]);
                            String requestText = obj.toJSONString();  
                            
                            String url = General.getConfigurationParameter("url", "", pbxfile);
                            
                            String resultText = General.restCallURL(url + "GetFile", requestText);
                            JSONParser parser = new JSONParser();
                            JSONObject resObj = (JSONObject) parser.parse(resultText);
   
                          
                            //out.println("<h2>"+resObj.toJSONString()+"</h2>");
                            
                            displayCompareFile(out, resObj, fileNames[0]);
                            
                        }
                   
                    Web.setFooter(out);
                }else{
                    response.sendRedirect("Login");
                }
                
            }catch (Exception ex ){
                out.println(ex.toString());
                General.writeEvent("Error: in Compare Files: " + ex.toString());
            }
            
        }
        
    }
    private void displayCompareFile(final PrintWriter out, JSONObject resObj, String fileName) {
        
        
        if (Boolean.valueOf(resObj.get("success").toString())) {
            String content = resObj.get("content").toString();

            out.println("<br>");
            
            out.println("<textarea cols=50 rows = 60 font name=content >");
            out.print(content);
            out.println("</textarea><br/>");

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
