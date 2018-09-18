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
                            
                            String url = General.getConfigurationParameter("url", "", pbxfile);
                            
                            JSONObject obj1 = new JSONObject();                            
                            obj1.put("filename", "/etc/asterisk/backup/" + fileNames[0]);
                            String requestText = obj1.toJSONString();                                                          
                            String firstResultText = General.restCallURL(url + "GetFile", requestText);
       
                            JSONParser parser = new JSONParser();
                            JSONObject resObj1 = (JSONObject) parser.parse(firstResultText);                            
                            
                            JSONObject obj2 = new JSONObject(); 
                            obj2.put("filename", "/etc/asterisk/backup/" + fileNames[1]);
                            requestText = obj2.toJSONString();                                                          
                            String secondResultText = General.restCallURL(url + "GetFile", requestText);                          
                            
                            JSONObject resObj2 = (JSONObject) parser.parse(secondResultText);      
   
                            
                            String command = "diff -c "+ "/etc/asterisk/backup/" + fileNames[0] +" "+"/etc/asterisk/backup/" +fileNames[1] ;
                            JSONObject diffobj = new JSONObject(); 
                            diffobj.put("command", command );
                            requestText = diffobj.toJSONString();                                                          
                            String resultText = General.restCallURL(url + "Shell", requestText); 
   
                             out.println("<p>"+resultText +"</p>");
                            
                            //out.println("<h2>"+resObj.toJSONString()+"</h2>");                            
                            displayCompareFile(out, resObj1,resObj2 ,  fileNames[0] , fileNames[1]);                                                       
                            
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
    private void displayCompareFile(final PrintWriter out, JSONObject firstResObj,JSONObject secondResObj,  String firstFileName , String secondFileName) {
        
        
        if (Boolean.valueOf(firstResObj.get("success").toString())) {
            String content = firstResObj.get("content").toString();

            
            out.println("<div style='float:left ; width:50%' >");
            out.println("<br>");            
            out.println("<textarea wrap='off'  cols=50 rows = 30 font name=content >");
            out.print(content);
            out.println("</textarea><br/>");
            out.println("</div>");
            
        }

        if (Boolean.valueOf(secondResObj.get("success").toString())) {
            String content = secondResObj.get("content").toString();
           
            out.println("<br>");  
            out.println("<div>");
            out.println("<textarea wrap='off' cols=50 rows = 30 font name=content >");
            out.print(content);
            out.println("</textarea><br/>");
            out.println("</div>");
         
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
